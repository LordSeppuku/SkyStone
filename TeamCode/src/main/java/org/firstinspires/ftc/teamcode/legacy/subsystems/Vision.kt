package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.legacy.lib.Tensorflow

class Vision(private val hwMap: HardwareMap) {
    private val tensorflow: Tensorflow by lazy {
        Tensorflow(hwMap)
    }

    fun init() = tensorflow.start()

    fun discern(): SkystonePosition = with(tensorflow.acquireRecognitions()()) {
        if (this.size == 3) {

            var skystoneX: Float = (-1).toFloat()
            var stone1X: Float = (-1).toFloat()
            var stone2X: Float = (-1).toFloat()

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

            if (skystoneX < stone1X && skystoneX < stone2X) return SkystonePosition.LEFT
            if (skystoneX > stone1X && skystoneX > stone2X) return SkystonePosition.RIGHT
            if (skystoneX > stone1X && skystoneX < stone2X) return SkystonePosition.CENTER
        }

        SkystonePosition.UNKNOWN
    }

    fun shitdown() = tensorflow.shitdown()

    enum class SkystonePosition {

        LEFT {
            override fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake) {

            }
        },
        CENTER {
            override fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake) {

            }
        },
        RIGHT {
            override fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake) {

            }
        },
        UNKNOWN {
            override fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake) {
                "Fuck if I know"
            }
        };

        abstract fun acquireSkystone(drivetrain: MecanumDrivetrain, intake: Intake)
    }

}