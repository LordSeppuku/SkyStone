package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.legacy.lib.Distance
import org.firstinspires.ftc.teamcode.legacy.lib.DriveToPositionCommand
import org.firstinspires.ftc.teamcode.legacy.lib.Rotation
import org.firstinspires.ftc.teamcode.legacy.lib.Tensorflow

class Vision(private val hwMap: HardwareMap) {
    private val tensorflow: Tensorflow by lazy {
        Tensorflow(hwMap)
    }

    fun init() = tensorflow.start()

    fun discern(): SkystonePosition = with(tensorflow.acquireRecognitions()) {
        if (this?.size == 2) {

            var skystoneX: Float = (-1).toFloat()
            var stone1X: Float = (-1).toFloat()

            for (recognition in this) {
                when (recognition.label) {
                    "Skystone" -> {
                        skystoneX = recognition.left
                    }
                    "Stone" -> {
                        if (stone1X == -1.toFloat()) stone1X = recognition.left
                    }
                }
            }

            if (skystoneX < stone1X) return SkystonePosition.LEFT
            if (skystoneX == -1.toFloat()) return SkystonePosition.RIGHT
            if (skystoneX > stone1X) return SkystonePosition.CENTER
        } else if (this?.size == 3) {

            var skystoneX: Float = (-1).toFloat()
            var stone1X: Float = (-1).toFloat()
            var stone2X: Float = -1.0f

            for (recognition in this) {
                when (recognition.label) {
                    "Skystone" -> {
                        skystoneX = recognition.left
                    }
                    "Stone" -> {
                        if (stone1X == -1.toFloat()) stone1X = recognition.left
                        else stone2X = recognition.left
                    }
                }
            }

            if (skystoneX < stone1X) return SkystonePosition.LEFT
            if (skystoneX == -1.toFloat() || skystoneX > stone2X) return SkystonePosition.RIGHT
            if (skystoneX > stone1X || stone2X < skystoneX) return SkystonePosition.CENTER
        }

        SkystonePosition.UNKNOWN
    }

    fun shitdown() = tensorflow.shitdown()

    enum class SkystonePosition {

        RIGHT {
            override fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake, opmodeIsActive: () -> Boolean) {
                drivetrain.driveToPosition(DriveToPositionCommand(rotation = Rotation(90.0, UnnormalizedAngleUnit.DEGREES)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(horizontal = Distance(-14.0)))
                intake.update(Gamepad().apply {
                    x = true
                })
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(6.0)))(opmodeIsActive) {
                    intake.update(Gamepad().apply {
                        b = true
                    })
                }
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(-6.0)))(opmodeIsActive) {
                    intake.update(Gamepad().apply {
                        y = true
                    })
                }
                drivetrain.driveToPosition(DriveToPositionCommand(horizontal = Distance(14.0)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(rotation = Rotation(-90.0, UnnormalizedAngleUnit.DEGREES)))(opmodeIsActive) {}
            }
        },
        CENTER {
            override fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake, opmodeIsActive: () -> Boolean) {
                drivetrain.driveToPosition(DriveToPositionCommand(rotation = Rotation(90.0, UnnormalizedAngleUnit.DEGREES)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(-4.0)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(horizontal = Distance(-14.0)))
                intake.update(Gamepad().apply {
                    x = true
                })
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(6.0)))(opmodeIsActive) {
                    intake.update(Gamepad().apply {
                        b = true
                    })
                }
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(-6.0)))(opmodeIsActive) {
                    intake.update(Gamepad().apply {
                        y = true
                    })
                }
                drivetrain.driveToPosition(DriveToPositionCommand(horizontal = Distance(14.0)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(4.0)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(rotation = Rotation(-90.0, UnnormalizedAngleUnit.DEGREES)))(opmodeIsActive) {}
            }
        },
        LEFT {
            override fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake, opmodeIsActive: () -> Boolean) {
                drivetrain.driveToPosition(DriveToPositionCommand(rotation = Rotation(-90.0, UnnormalizedAngleUnit.DEGREES)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(-4.0)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(horizontal = Distance(14.0)))
                intake.update(Gamepad().apply {
                    x = true
                })
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(6.0)))(opmodeIsActive) {
                    intake.update(Gamepad().apply {
                        b = true
                    })
                }
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(-6.0)))(opmodeIsActive) {
                    intake.update(Gamepad().apply {
                        y = true
                    })
                }
                drivetrain.driveToPosition(DriveToPositionCommand(horizontal = Distance(-14.0)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(Distance(4.0)))(opmodeIsActive) {}
                drivetrain.driveToPosition(DriveToPositionCommand(rotation = Rotation(90.0, UnnormalizedAngleUnit.DEGREES)))(opmodeIsActive) {}
            }
        },
        UNKNOWN {
            override fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake, opmodeIsActive: () -> Boolean) {
                "Fuck if I know"
            }
        };

        abstract fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake, opmodeIsActive: () -> Boolean)
    }

}