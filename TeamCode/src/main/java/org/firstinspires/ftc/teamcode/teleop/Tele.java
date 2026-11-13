package org.firstinspires.ftc.teamcode.teleop;

import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.api.Paths;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.subsystems.Robot;

//Jett was NOT here... omg omg he so tuff fr fr oml 6767 he the tuffest sigma losercore playlist listener
public class Tele extends OpMode {
    private final Alliances alliance;
    private Robot r;
    public Tele(Alliances alliance) {
        this.alliance = alliance;
    }

    private void telemetry() {
//        telemetry.addData("Pollen Shooter Speed", r.s.getPollenShooterVelocity());
//        telemetry.addData("Nectar Shooter Speed", r.s.getNectarShooterVelocity());
//        telemetry.addData("Pollen Hood Pos", r.s.getPollenHoodPos());
//        telemetry.addData("Nectar Hood Pos", r.s.getNectarHoodPos());
        telemetry.addData("X", r.d.getPose().x());
        telemetry.addData("Y", r.d.getPose().y());
        telemetry.addData("Heading", Math.toDegrees(r.d.getPose().heading()));
        telemetry.addData("can see upright cell", r.v.canSeeUprightAllianceCell(alliance));
        telemetry.addData("within deadzone", r.v.withinDeadzone());
        for(int i : r.v.getVisibleNumberIds()) {
            telemetry.addData("id", i);
        }
        telemetry.addData("vision ids", r.v.getVisibleNumberIds());
        telemetry.addData("tx", r.v.getTx());
        telemetry.addData("get fps", r.v.getStatus().getFps());
        telemetry.addLine("-------------");
        telemetry.addData("focused cell name", r.v.getFocusedCellName());
        telemetry.addData("focused cell estimated tx", r.v.getFocusedCellCenterTx());

//        telemetry.addData("Cluster", r.v.getSelectedCluster());
//        telemetry.addData("Visible Tags", r.v.getVisibleTagIds());
//        telemetry.addData("Estimated Center TX", r.v.getEstimatedCenterTx());
//        telemetry.addData("Centered", r.v.withinDeadzone());

        telemetry.update();
    }



    @Override
    public void init() {
        r = new Robot(hardwareMap, alliance);
    }

    @Override
    public void start() {
        r.v.startVision();
    }

    @Override
    public void loop() {
        r.periodic();
        r.d.drive(gamepad1);
        r.i.runIntake(gamepad1);
        r.s.runShooterPIDs();
        r.v.updateVision();

        if(gamepad1.xWasPressed()) {
            Scheduler.schedule(r.shoot());
        }
        if(gamepad1.aWasPressed()) {
            Scheduler.schedule(r.turnToGoal());
        }

        Scheduler.execute();
        telemetry();
    }

    @Override
    public void stop() {
        //optional for sake of battery preservation
        if(!r.v.isLimelightNull()) {
            r.v.stop();
        }
    }
}
