package org.firstinspires.ftc.teamcode.subsystems;

import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.utils.Timer;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.subsystems.dummies.DumberVision;
import org.firstinspires.ftc.teamcode.subsystems.dummies.DummyIntake;
import org.firstinspires.ftc.teamcode.subsystems.dummies.DummyShooter;
import org.firstinspires.ftc.teamcode.teleop.OpModeStorage;

public class Robot {
    private Alliances alliance;
    public Drive d;
    public DummyIntake i;
    public DummyShooter s;
    public Vision v;
    private Follower follower;
    private Timer turnTimer;

    public Robot(HardwareMap hardwareMap, Alliances alliance) {
        follower = Constants.create(hardwareMap);
        follower.setPose(OpModeStorage.autonomousEndPose);

        d = new Drive(hardwareMap);
        i = new DummyIntake(hardwareMap);
        s = new DummyShooter(hardwareMap);
        v = new Vision(hardwareMap, alliance);

        this.alliance = alliance;
    }
    public CommandBuilder shoot() {
        return sequential(
                s.openGates,
                waitMs(150),
                i.setFeeding,
                waitMs(450),
                i.stopIntake,
                s.closeGates
        );
    }

    public Command turnToGoal() {
        return Command.build()
                .setStart(() -> {
                    turnTimer = new Timer();
                    turnTimer.reset();
                })
                .setExecute(() -> {
                    if(v.canSeeUprightAllianceCell(alliance)) {
                        d.turnToHeadingInPlace(v);
                    }
                    else {
                        d.turnToHeadingInPlace(d.getHeadingToPoint(alliance));
                    }
                })
                .setDone(() ->
                        v.withinDeadzone() || turnTimer.milliseconds() >= 700 //|| !d.getFollower().isBusy()
                );
    }



    public void periodic() {
        d.periodic();
        s.setSpeedBasedOnDistance(v);
    }
}
