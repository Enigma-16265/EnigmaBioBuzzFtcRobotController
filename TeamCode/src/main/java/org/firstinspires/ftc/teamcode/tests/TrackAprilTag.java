package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class TrackAprilTag extends OpMode {
    private Limelight3A limelight;
    private DcMotor backLeft, backRight, frontLeft, frontRight;

    private void initHardware() {
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    }

    @Override
    public void init() {
        initHardware();
    }

    @Override
    public void loop() {
        
    }
}
