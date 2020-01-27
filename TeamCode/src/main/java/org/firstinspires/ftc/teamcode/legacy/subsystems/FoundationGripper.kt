package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState

class FoundationGripper(private val hwMap: HardwareMap) {
    private val servo by lazy {
        hwMap.servo["fndSrv"]
    }

    private val down = 0.25
    private val up = 0.1

    var isDown = false
        private set

    private val button = Button()

    fun init() {
        servo.position = up
    }

    fun down() {
        if (!isDown) servo.position = down
        isDown = true
    }

    fun up() {
        if (isDown) servo.position = up
        isDown = false
    }

    fun update(gamepad: Gamepad) {
        if (button.update(gamepad.dpad_right) == ButtonState.PRESSED) {
            if (isDown) up()
            else down()
        }
    }
}