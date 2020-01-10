package org.firstinspires.ftc.teamcode.lib.opmodes

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.lib.factory.EntityFactory
import org.firstinspires.ftc.teamcode.lib.systems.pollers.DcMotorPoller
import org.firstinspires.ftc.teamcode.lib.systems.pollers.EncoderPoller

/**
 * Barebones OpMode that exposes a preinitialized [Engine] instance as well as a [MutableList] that includes default systems of [RogueOpMode] for each type of System.
 *
 * TODO: Finish up all default Systems and create that [MutableList].
 */
abstract class AdvanceRogueOpMode : LinearOpMode() {

    /**
     * [Engine] instance used for entire OpMode.
     */
    protected val engine: Engine = Engine()

    /**
     * All default systems and entities contained here.
     */
    protected companion object {
        val defaultPollers = listOf<EntitySystem>(
                EncoderPoller(),
                DcMotorPoller()
        )
        val defaultLogic = listOf<EntitySystem>(

        )
        val defaultEnactors = listOf<EntitySystem>(

        )
        val defaultEntityFactories = listOf<EntityFactory>(

        )
    }

}