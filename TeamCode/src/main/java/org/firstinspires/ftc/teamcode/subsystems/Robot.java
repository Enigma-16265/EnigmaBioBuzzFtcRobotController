package org.firstinspires.ftc.teamcode.subsystems;

import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.teleop.OpModeStorage;

public class Robot {
    private Alliances alliance;
    public Drive d;
    public Intake i;
    public Shooter s;
    public Vision v;
    private Follower follower;

    public Robot(HardwareMap hardwareMap, Alliances alliance) {
        follower = Constants.create(hardwareMap);
        follower.setPose(OpModeStorage.autonomousEndPose);

        d = new Drive(hardwareMap);
        i = new Intake(hardwareMap);
        s = new Shooter(hardwareMap);
        v = new Vision(hardwareMap, alliance);

        this.alliance = alliance;
    }


    public Command shoot = sequential(

    );

    public void periodic() {
        d.periodic();
    }
}
