package org.firstinspires.ftc.teamcode.legacy.opmodes.auto.time

import com.qualcomm.robotcore.eventloop.opmode.Autonomous

@Autonomous(name = "Foundation Focus", group = "time")
class Foundation : TimeBasedAuto() {

    override fun blueProcedure() {
        drivetrain.drive {
            For(1.0, horizontal = -driveSpeed)
            For(3.0, -driveSpeed)
            For(0.5, rotation = driveSpeed)
            foundationGripper.down()
            For(0.5, rotation = -driveSpeed)
            For(1.0, horizontal = driveSpeed)
            foundationGripper.up()
            For(2.7, driveSpeed)
        }
    }

    override fun redProcedure() {
        drivetrain.drive {
            For(1.0, horizontal = driveSpeed)
            For(3.0, -driveSpeed)
            For(0.5, rotation = -driveSpeed)
            foundationGripper.down()
            For(0.5, rotation = driveSpeed)
            For(1.0, horizontal = -driveSpeed)
            foundationGripper.up()
            For(2.7, driveSpeed)
        }
    }

}