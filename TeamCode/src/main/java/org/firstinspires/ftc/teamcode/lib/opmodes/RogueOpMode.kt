package org.firstinspires.ftc.teamcode.lib.opmodes

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.signals.Signal
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.lib.OpModeSingleton
import org.firstinspires.ftc.teamcode.lib.addSystemsFromList
import org.firstinspires.ftc.teamcode.lib.components.InternalHardwareMapComponent
import org.firstinspires.ftc.teamcode.lib.factory.EntityFactory
import org.firstinspires.ftc.teamcode.lib.removeAllSystems
import org.firstinspires.ftc.teamcode.lib.systems.TelemetryAssignmentSystem


/**
 * Provides simple access to an ECS implementation with a strong robotics focus, has all default systems setup.
 * Control loop has been designed with efficiency in mind because of a three-stage loop.
 * Three staged loop: Pollers (collect and interpret sensor data and puts said data into relevant components) -> Logic (acts upon data and modifies entity components again) -> Enactors (takes modified components and sends to respective hardware devices)
 * Use [AdvanceRogueOpMode] for an OpMode that only provides and [Engine] instance.
 */
open class RogueOpMode : AdvanceRogueOpMode() {

    val opModeState = Signal<OpModeSingleton.OpModeState>()

    /**
     * Use [EntityFactory] to create entities with components based on your own logic.
     */
    open val userEntityFactories: List<EntityFactory> = listOf()

    /**
     * Initialize all Poller systems required for OpMode here in this list.
     * Any systems listed here will have a priority of 2.
     */
    open val userPollers: List<EntitySystem> = listOf()

    /**
     * Initialize all Logic systems required for OpMode here in this list.
     * Any systems listed here will have a priority of 5.
     */
    open val userLogic: List<EntitySystem> = listOf()

    /**
     * Initialize all Enactor systems required for OpMode here in this list.
     * Any systems listed here will have a priority of 6.
     */
    open val userEnactors: List<EntitySystem> = listOf()

    /**
     * Code to run before main loop (i.e. where [Engine.update] is contained and executed)
     */
    open fun preLoopCode() {}

    /**
     * Code to run before [Engine.update] is called within main loop
     */
    open fun preUpdateCode() {}

    /**
     * Code to run after [Engine.update] is called within main loop
     */
    open fun postUpdateCode() {}

    /**
     * Code to run after exiting main loop (when this OpMode is declared to have ended, stopped automatically because of a stall, or stopped by Driver Station)
     */
    open fun postLoopCode() {}

    /**
     * Determine whether the OpMode should be active through user-defined behavior, should not contain side-effects.
     * TODO: Determine if this can be extracted to a system at reserved priority 3 (register after [userPollers]) or renamed better.
     */
    open fun userOpModeActiveness(): Boolean = true

    /**
     * [ElapsedTime] instance to measure Main Loop time between the start of loops.
     * NOTE: [deltaTime] returns a time in seconds for [Engine.update]
     */
    protected val deltaTime: ElapsedTime = ElapsedTime()

    override fun runOpMode() {

        opModeState.dispatch(OpModeSingleton.OpModeState.INIT)

        // Why add on its own instead of in with all the defaults. Just cuz'.
        engine.addEntity(Entity().add(
                InternalHardwareMapComponent(hardwareMap)
        ))

        defaultEntityFactories.map {
            it.produce(engine)
        }

        userEntityFactories.map {
            it.produce(engine)
        }

        // Add telemetry assignment system and assign telemetry lines NOW.
        engine.addSystem(TelemetryAssignmentSystem(telemetry))
        engine.update(0f)

        engine.addSystemsFromList(defaultPollers, 1)
        engine.addSystemsFromList(userPollers, 2)
        engine.addSystemsFromList(defaultLogic, 4)
        engine.addSystemsFromList(userLogic, 5)
        engine.addSystemsFromList(defaultEnactors, 7)
        engine.addSystemsFromList(userEnactors, 6)

        preLoopCode()

        waitForStart()

        opModeState.dispatch(OpModeSingleton.OpModeState.START)

        telemetry.clear()
        deltaTime.reset()

        while (opModeIsActive() && userOpModeActiveness()) {
            preUpdateCode() // User code
            engine.update(deltaTime.seconds().toFloat())
            postUpdateCode() // User code
            telemetry.update() // Why update here? If we put it in a system, it is vulnerable to being "mucked up" due to priority shenanigans.
            deltaTime.reset()
        }

        opModeState.dispatch(OpModeSingleton.OpModeState.STOP)

        postLoopCode()

        // Clean up all Systems and Entities.
        engine.removeAllSystems()
        engine.removeAllEntities()

    }

}