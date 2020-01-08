package org.firstinspires.ftc.teamcode.lib.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.lib.components.TelemetryLineComponent

/**
 * Assigns a new [Telemetry.Line] instances to all [TelemetryLineComponent] on every update.
 * This is an core [EntitySystem] and should not be modified.
 *
 * @property telemetry self-explanatory, telemetry of currently running OpMode used to create [Telemetry.Line] instances
 */
class TelemetryAssignmentSystem(val telemetry: Telemetry) : EntitySystem() {
    init {
        priority = -1
    }

    private val lineMapper = ComponentMapper.getFor(TelemetryLineComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        telemetry.clear()
        engine?.getEntitiesFor(Family.all(TelemetryLineComponent::class.java).get()).apply {
            for (entry in this?.groupBy {
                lineMapper[it].position
            }!!.iterator()) {
                for (entity in entry.value) {
                    lineMapper[entity].line = telemetry.addLine()
                }
            }
        }
    }
}