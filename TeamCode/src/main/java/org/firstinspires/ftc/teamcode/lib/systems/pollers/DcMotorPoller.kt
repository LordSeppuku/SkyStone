package org.firstinspires.ftc.teamcode.lib.systems.pollers

import com.badlogic.ashley.core.Family
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.lib.components.DcMotorComponent
import org.firstinspires.ftc.teamcode.lib.defaults.HardwareMappers
import org.firstinspires.ftc.teamcode.lib.systems.HardwareMapSystem

/**
 * Updates all motor components with motor data
 */

// TODO: Implement framework for RevExtensions Bulk Data for lower poll rate and propagate that bad boy.
class DcMotorPoller : HardwareMapSystem() {

    override fun processHardware(hardwareMap: HardwareMap, deltaTime: Float) {
        for (entity in engine.getEntitiesFor(Family.all(DcMotorComponent::class.java).get())) {
            val component = HardwareMappers.dcMotor.get(entity)
            val motor = hardwareMap.dcMotor.get(component.deviceString)
            component.apply {
                power = motor.power
                direction = motor.direction
                brakeBehavior = motor.zeroPowerBehavior
            }
        }
    }

}