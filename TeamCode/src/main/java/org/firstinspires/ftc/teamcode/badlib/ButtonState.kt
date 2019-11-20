package org.firstinspires.ftc.teamcode.badlib

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