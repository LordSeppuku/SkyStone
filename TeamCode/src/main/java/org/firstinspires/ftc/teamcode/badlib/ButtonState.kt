package org.firstinspires.ftc.teamcode.badlib

class Button() {

    var state: ButtonState = ButtonState.NOT_PRESSED
        private set(value) {
            state = value
        }

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
            PRESSED -> HELD
            HELD -> HELD
        }

        return NOT_PRESSED
    }
}