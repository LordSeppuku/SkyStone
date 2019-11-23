package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState

class Intake {

    private lateinit var hardwareMap: HardwareMap

    private lateinit var leftServo: Servo

    private lateinit var rightServo: Servo

    private lateinit var leftMotor: DcMotor

    private lateinit var rightMotor: DcMotor

    private val intake = Button()
    private val exhaust = Button()
    private val popOff = Button()
    private val unPop = Button()

    fun init(hwMap: HardwareMap) {
        this.hardwareMap = hwMap

        leftServo = hardwareMap.servo["inSrvL"]
        rightServo = hardwareMap.servo["inSrvR"]

        leftMotor = hardwareMap.dcMotor["inDcL"]
        rightMotor = hardwareMap.dcMotor["inDcR"]

        rightMotor.direction = DcMotorSimple.Direction.REVERSE
        //leftServo.direction = Servo.Direction.REVERSE

        leftServo.position = -1.0
        rightServo.position = 1.0
    }

    fun update(gamepad: Gamepad, telemetry: Telemetry) {
        with(gamepad) {

            if (intake.update(b) != ButtonState.NOT_PRESSED) {
                leftMotor.power = -1.0
                rightMotor.power = -1.0

                telemetry.addLine("C O N S U M E")
            } else if (exhaust.update(a) != ButtonState.NOT_PRESSED) {
                leftMotor.power = 1.0
                rightMotor.power = 1.0

                telemetry.addLine("V O M I T")
            } else {
                leftMotor.power = 0.0
                rightMotor.power = 0.0
            }

            if (popOff.update(x) != ButtonState.NOT_PRESSED) {
                leftServo.position = -0.3
                rightServo.position = 0.3

                telemetry.addLine("P O P P I N  O F F")
            } else if (unPop.update(y) != ButtonState.NOT_PRESSED) {
                leftServo.position = -1.0
                rightServo.position = 1.0

                telemetry.addLine("U N E R E C T I N")
            } else {

            }

        }
    }

}