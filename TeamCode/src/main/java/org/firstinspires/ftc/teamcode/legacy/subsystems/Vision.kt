package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.legacy.lib.Distance
import org.firstinspires.ftc.teamcode.legacy.lib.DriveToPositionCommand
import org.firstinspires.ftc.teamcode.legacy.lib.Rotation
import org.firstinspires.ftc.teamcode.legacy.lib.Tensorflow
import org.firstinspires.ftc.teamcode.legacy.opmodes.auto.Alliance
import org.firstinspires.ftc.teamcode.legacy.opmodes.auto.OurOpMode
import kotlin.math.abs

class Vision(private val hwMap: HardwareMap) {
    private val tensorflow: Tensorflow by lazy {
        Tensorflow(hwMap)
    }

    fun init() = tensorflow.start()

    fun averagedAcquisition(opModeIsActive: () -> Boolean): SkystonePosition {
        val discerns = mutableListOf<SkystonePosition>()
        for (i in 0..15) if (opModeIsActive()) discerns.add(discern())
        val grouped = discerns.groupingBy { it }.eachCount().toList().sortedByDescending { (key, value) -> value }
        return grouped.first().first
    }

    fun discern(): SkystonePosition = tensorflow.acquireRecognitions()?.filter { /*it.label == tensorflow.LABEL_FIRST_ELEMENT ||*/ it.label == tensorflow.LABEL_SECOND_ELEMENT }.run {

        if (this != null && this.isNotEmpty()) {
            val imageWidth = first().imageWidth
            val thirdWidth = imageWidth / 3
            val skystoneX = this.filter { it.label == tensorflow.LABEL_SECOND_ELEMENT }.run {
                if (isNotEmpty()) first().run { (left + right) / 2 } else -1f
            }
            return when {
                skystoneX > 0 && skystoneX < thirdWidth -> SkystonePosition.LEFT
                skystoneX > thirdWidth && skystoneX < (2 * thirdWidth) -> SkystonePosition.CENTER
                skystoneX > (2 * thirdWidth) -> SkystonePosition.RIGHT
                else -> SkystonePosition.UNKNOWN
            }
        }

        /*
        if (this?.size == 2) {

            var skystoneX: Float = -1f
            var stone1X: Float = -1f

            for (recognition in this) {
                when (recognition.label) {
                    "Skystone" -> {
                        skystoneX = recognition.left
                    }
                    "Stone" -> {
                        if (stone1X == -1f) stone1X = recognition.left
                    }
                }
            }

            if (skystoneX < stone1X) return SkystonePosition.LEFT
            else if (skystoneX > stone1X) return SkystonePosition.CENTER
            else if (skystoneX == -1f) return SkystonePosition.RIGHT
        } else if (this?.size == 3) {

            var skystoneX: Float = -1f
            var stone1X: Float = -1f
            var stone2X: Float = -1f

            for (recognition in this) {
                when (recognition.label) {
                    "Skystone" -> {
                        skystoneX = recognition.left
                    }
                    "Stone" -> {
                        if (stone1X == -1f) stone1X = recognition.left
                        else stone2X = recognition.left
                    }
                }
            }

            if (skystoneX < stone1X) return SkystonePosition.LEFT
            else if (skystoneX == -1f || skystoneX > stone2X) return SkystonePosition.RIGHT
            else if (skystoneX > stone1X || stone2X < skystoneX) return SkystonePosition.CENTER
        } else if (this?.size == 1) {

        }
         */

        SkystonePosition.UNKNOWN
    }

    fun shitdown() = tensorflow.shitdown()

    enum class SkystonePosition(val offset: Double = 0.0) {
        LEFT(6.0),
        CENTER(LEFT.succDistance + 8.0),
        RIGHT(CENTER.succDistance + 8.0),
        UNKNOWN;

        val succDistance = offset
        fun OurOpMode.driveAndSucccCommands() = if (alliance is Alliance.Red)
            listOf(
                    DriveToPositionCommand(rotation = Rotation(-90.0)),
                    DriveToPositionCommand(lateral = Distance(-succDistance), rotation = Rotation(-90.0)),
                    DriveToPositionCommand(horizontal = Distance(13.25), rotation = Rotation(-90.0)),
                    DriveToPositionCommand(lateral = Distance(succDistance), rotation = Rotation(-90.0)) {
                        intake.succ()
                    },
                    DriveToPositionCommand(horizontal = Distance(-13.25), rotation = Rotation(-90.0)) {
                        intake.rest()
                    }
            ) else
            listOf(
                    DriveToPositionCommand(rotation = Rotation(90.0)),
                    DriveToPositionCommand(lateral = Distance(abs(succDistance - 16.0)), rotation = Rotation(90.0)),
                    DriveToPositionCommand(horizontal = Distance(-13.25), rotation = Rotation(90.0)),
                    DriveToPositionCommand(lateral = Distance(-abs(succDistance - 16.0)), rotation = Rotation(90.0)) {
                        intake.succ()
                    },
                    DriveToPositionCommand(horizontal = Distance(13.25), rotation = Rotation(90.0)) {
                        intake.rest()
                    }
            )
    }

}