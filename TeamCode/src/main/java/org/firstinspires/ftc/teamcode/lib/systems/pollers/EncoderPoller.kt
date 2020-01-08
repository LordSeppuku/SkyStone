package org.firstinspires.ftc.teamcode.lib.systems.pollers

import com.badlogic.ashley.core.Family
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.lib.components.EncoderComponent
import org.firstinspires.ftc.teamcode.lib.defaults.HardwareMappers
import org.firstinspires.ftc.teamcode.lib.systems.HardwareMapSystem

/**
 * Updates all encoder components with relevant encoder data.
 */
class EncoderPoller : HardwareMapSystem() {

    override fun processHardware(hardwareMap: HardwareMap, deltaTime: Float) {
        for (entity in engine.getEntitiesFor(Family.all(EncoderComponent::class.java).get())) {
            val component = HardwareMappers.encoder.get(entity)
            val encoder = hardwareMap.dcMotor.get(component.deviceString) as DcMotorEx
            component.apply {
                currentPosition = encoder.currentPosition
                targetPosition = encoder.targetPosition
                currentVelocity = encoder.velocity
                targetVelocity = component.targetVelocity
                direction = encoder.direction
                TICKS_PER_REV = component.TICKS_PER_REV
            }
        }
    }

}