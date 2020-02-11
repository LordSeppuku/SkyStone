package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState

class Intake(private val hwMap: HardwareMap) {

    private val leftServo: Servo by lazy {
        hwMap.servo["inSrvL"]
    }

    private val rightServo: Servo by lazy {
        hwMap.servo["inSrvR"]
    }

    private val leftMotor: DcMotor by lazy {
        hwMap.dcMotor["inDcL"]
    }

    private val rightMotor: DcMotor by lazy {
        hwMap.dcMotor["inDcR"]
    }

    private val intake = Button()
    private val exhaust = Button()
    private val popOff = Button()
    private val unPop = Button()

    var state: State = State.IN
        private set

    fun init() {
        rightMotor.direction = DcMotorSimple.Direction.REVERSE

        rightServo.direction = Servo.Direction.REVERSE

        leftServo.position = 0.0
        rightServo.position = 0.0
    }

    fun update(gamepad: Gamepad, telemetry: Telemetry) {
        with(gamepad) {
            if (intake.update(b) != ButtonState.NOT_PRESSED) {
                succ()
                telemetry.addLine("C O N S U M E")
            } else if (exhaust.update(a) != ButtonState.NOT_PRESSED) {
                vomit()
                telemetry.addLine("V O M I T")
            } else
                rest()


            if (popOff.update(x) != ButtonState.NOT_PRESSED) {
                popOff()
                telemetry.addLine("P O P P I N  O F F")
            } else if (unPop.update(y) != ButtonState.NOT_PRESSED) {
                popIn()
                telemetry.addLine("U N P O P P I N")
            } else {
            }
        }
    }

    fun popOff() {
        leftServo.position = 0.2
        rightServo.position = 0.2
    }

    fun popIn() {
        leftServo.position = 0.0
        rightServo.position = 0.0
    }

    fun rest() {
        leftMotor.power = 0.0
        rightMotor.power = 0.0
    }

    fun succ() {
        leftMotor.power = 1.0
        rightMotor.power = 1.0
    }

    fun vomit() {
        leftMotor.power = -0.5
        rightMotor.power = -0.5
    }

    enum class State {
        OUT, IN
    }

}