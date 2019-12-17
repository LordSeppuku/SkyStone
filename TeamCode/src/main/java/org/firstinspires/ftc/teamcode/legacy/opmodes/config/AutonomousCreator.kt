package org.firstinspires.ftc.teamcode.legacy.opmodes.config

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.legacy.lib.DriveToPositionCommand
import java.io.File

@Disabled
@TeleOp(name = "Create/Modify AutoOP", group = "Configuration")
class AutonomousCreator: LinearOpMode() {

    private val creationPrompts: List<String> = listOf(
            """
                YOU HAVE SELECTED
                Create new Autonomous
                =====================
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
                ==========================
                PRESS "A" TO CONTINUE
                PRESS "B" TO RETURN
            """.trimIndent()
    )

    private val endPrompts: List<String> = listOf(

    )

    override fun runOpMode() {
        val files: MutableList<File> = mutableListOf()
        var wizardState: ConfigState = Intro()

        File(hardwareMap.appContext.filesDir.path).walkTopDown().forEach {
            if (it.name.endsWith("created.json")) {
                files.add(it)
            }
        }

        waitForStart()

        telemetry.clear()

        while(opModeIsActive()) {
            when (wizardState) {
                is Intro -> wizardState
            }
        }
    }
}

sealed class ModificationCommand
data class Insertion(val insertAfter: Int, val command: DriveToPositionCommand) : ModificationCommand()
data class Replacement(val replace: Int, val command: DriveToPositionCommand) : ModificationCommand()

sealed class ConfigState {
    //abstract val prompts: List<String>
}

class Intro : ConfigState() {
    /*override val prompts = listOf(
            """
                Welcome to the Autonomous Creation Wizard!
                Please attach a controller to slot 1.
                For this application, 
                you will only need to use the D-pad
                buttons, the "A" button, and the "B" button.
                ============================================
                PRESS "A" TO CONTINUE
                PRESS "B" TO EXIT
            """.trimIndent(),
            """
                What do YOU want to do today?
                =============================
                UP: Create new Autonomous
                DOWN: Modify existing Autonomous
            """.trimIndent()
    )*/
}

data class FileSelection(val files: List<File>) : ConfigState()
data class Create(val fileName: String,
                  val commands: MutableList<DriveToPositionCommand>) : ConfigState()

data class Modify(val fileName: String,
                  val existingCommands: MutableList<DriveToPositionCommand>,
                  val modifications: MutableList<ModificationCommand>) : ConfigState()

data class Delete(val fileName: String) : ConfigState()

data class Outro(val filesCreated: List<String>,
                 val filesModified: List<String>,
                 val filesDeleted: List<String>) : ConfigState()
