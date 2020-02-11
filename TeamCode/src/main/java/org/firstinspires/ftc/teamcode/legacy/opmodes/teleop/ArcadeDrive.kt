package org.firstinspires.ftc.teamcode.legacy.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.subsystems.Arm
import org.firstinspires.ftc.teamcode.legacy.subsystems.FoundationGripper
import org.firstinspires.ftc.teamcode.legacy.subsystems.Intake
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain
import kotlin.math.pow

@TeleOp(name = """
debunking the "balls have tastebuds"

lately, there's been a lot of fuss about someone claiming balls have tastebuds. They said that our genitals can sense sweetness. i saved you the shame.

in the name of science, i poured myself a cup of water and added two cubes of sugar. i stirred with the might of god, and unzipped my pants.

my skinny dip was shortlived. expectations were not met. i shoved my balls into the sweet, sugery water with hope, but my boys came out disappointed. not a taste. the only feeling was the cool water rushing and wirling against my two, hairy beasts.

here i sit, with shame and—worse—wet balls, writing to save my fellow men the utter humiliation curiousness leads too.

my hypotesis was wrong, but even if i just dipped my testicles in sugerified water, i did it for science. i did it for the betterment of humanity. you may laugh, but i will always smile, even past my grave, about how much i helped. i did my part. i can die, no longer restless, but in peace.

tl;dr: i shoved my balls in sugar water
""")
open class ArcadeDrive : LinearOpMode() {

    protected val drivetrain by lazy {
        MecanumDrivetrain(hardwareMap)
    }
    protected val arm by lazy {
        Arm(hardwareMap)
    }
    protected val intake by lazy {
        Intake(hardwareMap)
    }
    protected val foundationGripper by lazy {
        FoundationGripper(hardwareMap)
    }
    protected val runtime = ElapsedTime()

    override fun runOpMode() {

        drivetrain.init()
        arm.init()
        intake.init()
        foundationGripper.init()

        waitForStart()

        runtime.reset()

        while (opModeIsActive()) {

            telemetry.clear()

            with(gamepad1) {
                drivetrain.arcadeDrive(
                        -left_stick_y.toDouble().pow(3),
                        left_stick_x.toDouble().pow(3),
                        right_stick_x.toDouble().pow(3)
                )

                arm.update(gamepad1)
                intake.update(gamepad1, telemetry)
                foundationGripper.update(gamepad1)
            }

            telemetry.update()
        }

    }

}