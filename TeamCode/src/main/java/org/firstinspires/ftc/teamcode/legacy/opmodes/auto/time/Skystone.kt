package org.firstinspires.ftc.teamcode.legacy.opmodes.auto.time

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.legacy.subsystems.Vision

@Autonomous(name = "Skystone Focused", group = "time")
class Skystone : TimeBasedAuto() {

    override fun blueProcedure() {
        drivetrain.drive {
            intake.update(Gamepad().apply {
                right_bumper = true
            }, telemetry)
            For(0.4, driveSpeed)
            when (whileTill(0.5, vision::discern)) {
                Vision.SkystonePosition.LEFT -> {
                    drivetrain.drive {
                        // Lift up to suck
                        whileTill(0.3) {
                            arm.update(Gamepad().apply {
                                dpad_up = true
                            })
                        }
                        // align left
                        For(0.5, horizontal = -driveSpeed)
                        // Drive and suck
                        ForAlso(0.6, driveSpeed) {
                            intake.update(Gamepad().apply {
                                x = true
                            }, telemetry)
                        }
                        // Drop arm
                        whileTill(0.3) {
                            arm.update(Gamepad().apply {
                                dpad_down = true
                            })
                        }
                        // Grasp
                        arm.update(Gamepad().apply {
                            right_bumper = true
                        })
                        // Back off
                        For(0.2, -driveSpeed)
                        For(0.75, horizontal = -driveSpeed)
                        For(0.5, rotation = driveSpeed)
                        whileTill(0.3) {
                            arm.update(Gamepad().apply {
                                dpad_up = true
                            })
                        }
                        whileTill(0.3) {
                            arm.update(Gamepad().apply {
                                right_trigger = 0.7f
                            })
                        }
                        whileTill(0.3) {
                            arm.update(Gamepad().apply {
                                dpad_down = true
                            })
                        }
                        For(0.5, horizontal = driveSpeed)
                    }

                }
                Vision.SkystonePosition.CENTER -> {

                }
                Vision.SkystonePosition.RIGHT -> {

                }
            }
        }
    }

    override fun redProcedure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}