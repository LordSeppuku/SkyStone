package org.firstinspires.ftc.teamcode.lib.components

import com.badlogic.ashley.core.Component

data class WheelComponent(
        val radius: Double,
        val type: WheelType = WheelType.STANDARD,
        val gearRatio: Double = 1.0
) : Component {
    companion object {
        enum class WheelType {
            STANDARD, OMNI, MECANUM
        }
    }
}