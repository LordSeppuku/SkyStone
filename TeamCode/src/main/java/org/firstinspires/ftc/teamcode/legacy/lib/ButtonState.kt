package org.firstinspires.ftc.teamcode.legacy.lib

class Button {

    var state: ButtonState = ButtonState.NOT_PRESSED

    fun update(value: Boolean): ButtonState {
        state = state.update(button = value)
        return state
    }
}

enum class ButtonState {
    NOT_PRESSED,
    PRESSED,
    HELD;

    fun update(button: Boolean): ButtonState {
        if(button) when(this) {
            NOT_PRESSED -> return PRESSED
            PRESSED -> return HELD
            HELD -> return HELD
        }

        return NOT_PRESSED
    }
}