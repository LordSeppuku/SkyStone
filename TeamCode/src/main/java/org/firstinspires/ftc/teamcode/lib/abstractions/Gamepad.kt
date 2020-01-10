package org.firstinspires.ftc.teamcode.lib.abstractions

import kotlinx.serialization.Serializable

@Serializable
data class Gamepad(
        val a: ButtonState = ButtonState.UNPRESSED,
        val b: ButtonState = ButtonState.UNPRESSED,
        val x: ButtonState = ButtonState.UNPRESSED,
        val y: ButtonState = ButtonState.UNPRESSED,
        val leftBumper: ButtonState = ButtonState.UNPRESSED,
        val rightBumper: ButtonState = ButtonState.UNPRESSED,
        val dpadUp: ButtonState = ButtonState.UNPRESSED,
        val dpadDown: ButtonState = ButtonState.UNPRESSED,
        val dpadRight: ButtonState = ButtonState.UNPRESSED,
        val dpadLeft: ButtonState = ButtonState.UNPRESSED,
        val leftStickButton: ButtonState = ButtonState.UNPRESSED,
        val rightStickButton: ButtonState = ButtonState.UNPRESSED,
        val leftStickX: Double = 0.0,
        val leftStickY: Double = 0.0,
        val rightStickX: Double = 0.0,
        val rightStickY: Double = 0.0,
        val leftTrigger: Double = 0.0,
        val rightTrigger: Double = 0.0
)

enum class ButtonState {
    UNPRESSED {
        override fun update(button: Boolean) =
                if (button) PRESSED
                else this
    },
    PRESSED {
        override fun update(button: Boolean) =
                if (button) HELD
                else UNPRESSED
    },
    HELD {
        override fun update(button: Boolean): ButtonState =
                if (button) HELD
                else UNPRESSED
    };

    abstract fun update(button: Boolean): ButtonState
}