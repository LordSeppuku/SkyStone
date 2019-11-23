package org.firstinspires.ftc.teamcode.legacy.opmodes.config

import android.app.Activity
import com.beust.klaxon.Klaxon
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@TeleOp(name = "Create/Modify AutoOP", group = "Configuration")
class AutonomousCreator: LinearOpMode() {

    private val introPrompts: List<String> = listOf(
            """
                Welcome to the Autonomous Creation Wizard!
                Please attach a controller to slot 1.
                For this application, 
                you will only need to use the D-pad
                buttons, the "A" button, and the "B" button.
                
                PRESS "A" TO CONTINUE
            """.trimIndent(),
            """
                What do YOU want to do today?
                PRESS THE RESPECTIVE BUTTON
                
                UP: Create new Autonomous
                DOWN: Modify existing Autonomous
            """.trimIndent()
    )

    private val creationPrompts: List<String> = listOf(
            """
                YOU HAVE SELECTED
                Create new Autonomous
               
                PRESS "A" TO CONTINUE
                PRESS "B" TO RETURN
            """.trimIndent(),
            """
                
            """.trimIndent()
    )

    private val modifyPrompts: List<String> = listOf(
            """
                YOU HAVE SELECTED
                Modify existing Autonomous
               
                PRESS "A" TO CONTINUE
                PRESS "B" TO RETURN
            """.trimIndent()
    )

    private val endPrompts: List<String> = listOf(

    )

    override fun runOpMode() {
        val files: MutableList<File> = mutableListOf()
        var wizardState: State = State.INTRO

        File("/FIRST/legacy/customauto/").walkTopDown().forEach {
            if (it.name.endsWith("created.json")) {
                files.add(it)
            }
        }

        waitForStart()

        telemetry.clear()

        while(opModeIsActive()) {
            telemetry.addLine(introPrompts[0])
            telemetry.update()


        }
    }

    private enum class State {
        INTRO,
        CREATE,
        MODIFY,
        END
    }

}