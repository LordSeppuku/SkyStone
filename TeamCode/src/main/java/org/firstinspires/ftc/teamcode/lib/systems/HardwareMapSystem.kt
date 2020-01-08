package org.firstinspires.ftc.teamcode.lib.systems

import com.badlogic.ashley.core.*
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.lib.components.InternalHardwareMapComponent

/**
 * Simplifies processing hardware devices by ensuring access to a hardware map
 *
 * @property processHardware provides a [HardwareMap] for easy access for acquiring information
 */
abstract class HardwareMapSystem : EntitySystem() {

    private val hardwareMapMapper = ComponentMapper.getFor(InternalHardwareMapComponent::class.java)
    private lateinit var hardwareMapEntity: Entity

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        hardwareMapEntity = engine?.getEntitiesFor(Family.all(InternalHardwareMapComponent::class.java).get())!!.first()
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        processHardware(hardwareMapMapper.get(hardwareMapEntity).hardwareMap, deltaTime)
    }

    /**
     * Similar to regular [EntitySystem.update], but with a hardwareMap available.
     * Doesn't filter entities like regular [EntitySystem], that's up to you!
     *
     * @param
     */
    abstract fun processHardware(hardwareMap: HardwareMap, deltaTime: Float)
}