package org.firstinspires.ftc.teamcode.teleop.variants;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.teleop.Tele;

@TeleOp(name = "! Blue Tele")
public class BlueTeleOp extends Tele {
    public BlueTeleOp() {
        super(Alliances.BLUE);
    }
}
