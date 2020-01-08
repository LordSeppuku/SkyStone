package org.firstinspires.ftc.teamcode.lib.components

import com.badlogic.ashley.core.Component
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlinx.serialization.Serializable
import org.firstinspires.ftc.robotcore.external.Func
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.lib.systems.TelemetryAssignmentSystem

/**
 * Provides access to opMode HardwareMap for Pollers and Enactors, really shouldn't be used in userland.
 *
 * @property hardwareMap currently running opMode HardwareMap
 */
data class InternalHardwareMapComponent(
        val hardwareMap: HardwareMap
) : Component

/**
 * Simple handler for states with [stateType] & [state] acting as concise representation of states in a Finite State Machine
 *
 * @property stateType string variable designating which state machine this component belongs to.
 * @property state number representing a state in the the state machine
 */
@Serializable
data class StateComponent(
        val stateType: String = "",
        var state: Int = 0
) : Component

/**
 * Extension of a component providing a name for purpose of acquiring actual device form HardwareMap.
 *
 * @property deviceString name of hardware device in configuration file
 */
interface HardwareComponent : Component {
    val deviceString: String
}

/**
 * Encapsulates a [Telemetry] instance akin to [InternalHardwareMapComponent], also shouldn't be used in userland.
 *
 * @property telemetry the current [Telemetry] instance of the current opMode
 */
data class InternalTelemetryComponent(
        val telemetry: Telemetry
) : Component

/**
 * Provides access to a [Telemetry.Line] instance provided by [TelemetryAssignmentSystem], DO NOT ATTEMPT TO ADD DATA UNLESS YOU'RE ACCESSING A LINE FROM A SYSTEM; WILL THROW ERROR.
 *
 * @property line instance of [Telemetry.Line]
 */
@Serializable
data class TelemetryLineComponent(
        val position: Int = 0,
        var line: Telemetry.Line = object : Telemetry.Line {
            override fun addData(caption: String?, format: String?, vararg args: Any?): Telemetry.Item {
                throw Exception("You shouldn't have called this.")
            }

            override fun addData(caption: String?, value: Any?): Telemetry.Item {
                throw Exception("You shouldn't have called this.")
            }

            override fun <T : Any?> addData(caption: String?, valueProducer: Func<T>?): Telemetry.Item {
                throw Exception("You shouldn't have called this.")
            }

            override fun <T : Any?> addData(caption: String?, format: String?, valueProducer: Func<T>?): Telemetry.Item {
                throw Exception("You shouldn't have called this.")
            }
        }
) : Component