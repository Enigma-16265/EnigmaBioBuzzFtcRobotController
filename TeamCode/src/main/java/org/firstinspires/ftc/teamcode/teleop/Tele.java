package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.subsystems.Robot;

//Jett was NOT here... omg omg he so tuff fr fr oml 6767 he the tuffest sigma losercore playlist listener
public class Tele extends OpMode {
    private Alliances alliance;
    private Robot r;
    public Tele(Alliances alliance) {
        this.alliance = alliance;
    }

    private void telemetry() {

        telemetry.update();
    }

    @Override
    public void init() {
        r = new Robot(hardwareMap, alliance);
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        r.periodic();
        r.d.drive(gamepad1);

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
