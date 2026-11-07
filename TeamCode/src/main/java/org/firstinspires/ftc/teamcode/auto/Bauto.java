package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.commands.Commands;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.interpolator.Interpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.api.Paths.*;

import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Robot;
import org.firstinspires.ftc.teamcode.teleop.OpModeStorage;

public class Bauto extends OpMode {
    private Follower follower;
    private Robot r;
    private Alliances alliance;
    private boolean hasBalls = true;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final PoseFactory poseFactory = PoseFactory.degrees();

    // Poses
    private final Pose start = poseFactory.of(54.75, 7.75, 90);
    private final Pose strafeToShootPos = poseFactory.of(58.75, 7.75, 90);
    private final Pose pickupGarden = poseFactory.of(9.75, 8.75, -171.0274);
    private final Pose pickupGardenControl1 = poseFactory.of(47.75, 14.75, 0);
    private final Pose fromGardenToAudienceCellStart = poseFactory.of(9.75, 8.75, 185);
    private final Pose fromGardenToAudienceCell = poseFactory.of(58.75, 7.75, 90);
    private final Pose fromAudienceCellToWallCell = poseFactory.of(58.75, 133.75, 270);
    private final Pose fromAudienceCellToWallCellSegment1Heading = poseFactory.of(58.75, 133.75, 90);
    private final Pose fromAudienceCellToWallCellSegment2Start = poseFactory.of(58.75, 133.75, 90);
    private final Pose fromAudienceCellToWallCellSegment2End = poseFactory.of(58.75, 133.75, 270);
    private final Pose prepareTeammatePush = poseFactory.of(9.75, 60.75, 270);
    private final Pose prepareTeammatePushControl1 = poseFactory.of(10.75, 97.75, 0);
    private final Pose prepareTeammatePushControl2 = poseFactory.of(58.75, 57.75, 0);
    private final Pose pushTeammateAndPark = poseFactory.of(13.75, 109.75, -123.6901);
    private final Pose pushTeammateAndParkControl1 = poseFactory.of(1.75, 91.75, 0);

    public Path strafeToShootPos() {
        return Paths.line(start, strafeToShootPos).constant(strafeToShootPos);
    }

    public Path pickupGarden() {
        return Paths.curve(strafeToShootPos, pickupGardenControl1, pickupGarden).tangent();
    }

    public Path fromGardenToAudienceCell() {
        return Paths.line(fromGardenToAudienceCellStart, fromGardenToAudienceCell).linear(fromGardenToAudienceCellStart, fromGardenToAudienceCell);
    }

    public Path fromAudienceCellToWallCell() {
        return Paths.line(fromGardenToAudienceCell, fromAudienceCellToWallCell).heading(Interpolator.piecewise().until(0.75, Interpolator.constant(fromAudienceCellToWallCellSegment1Heading)).until(1, Interpolator.linear(fromAudienceCellToWallCellSegment2Start, fromAudienceCellToWallCellSegment2End)));
    }

    public Path prepareTeammatePush() {
        return Paths.curve(fromAudienceCellToWallCell, prepareTeammatePushControl1, prepareTeammatePushControl2, prepareTeammatePush).constant(prepareTeammatePush);
    }

    public Path pushTeammateAndPark() {
        return Paths.curve(prepareTeammatePush, pushTeammateAndParkControl1, pushTeammateAndPark).reverseTangent();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public Bauto(Alliances alliance) {
        if(alliance == Alliances.BLUE) {
            poseFactory.mirrorX(70.75);
        }

        this.alliance = alliance;
    }

    // Commands
    private Command setHasBalls(boolean b) {
        return Command.build()
                .setStart(() -> hasBalls = b)
                .setDone(() -> true);
    }
    private Command zilch() {
        return Command.build().setDone(() -> true);
    }

    private Command shoot() {
        return sequential(
                r.s.openGates,
                waitMs(150),
                r.i.setFeeding,
                waitMs(450),
                r.i.stopIntake,
                r.s.closeGates,
                setHasBalls(false)
        );
    }

    private Command autoRoutine() {
        //This is 1 - 1.5 tips
        return sequential(
                follow(follower, strafeToShootPos()),
                shoot(),
                follow(follower, pickupGarden()),
                follow(follower, fromGardenToAudienceCell()),
                conditional(() -> r.v.canSeeUprightAllianceCell(alliance), shoot(), zilch()),
                follow(follower, fromAudienceCellToWallCell()),
                conditional(() -> hasBalls, shoot(), zilch()),
                follow(follower, prepareTeammatePush()),
                follow(follower, pushTeammateAndPark())
        );
    }

    @Override
    public void init() {
        Scheduler.reset();
        r = new Robot(hardwareMap, alliance);

        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }

    @Override
    public void stop() {
        OpModeStorage.autonomousEndPose = follower.pose();
    }
}