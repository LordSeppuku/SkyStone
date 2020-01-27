package org.firstinspires.ftc.teamcode.lib.systems.enactors

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Family
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.lib.components.DcMotorComponent
import org.firstinspires.ftc.teamcode.lib.systems.HardwareMapSystem

class MotorEnactor : HardwareMapSystem() {
    private val mapper = ComponentMapper.getFor(DcMotorComponent::class.java)

    override fun processHardware(hardwareMap: HardwareMap, deltaTime: Float) {
        for (entity in engine!!.getEntitiesFor(Family.all(DcMotorComponent::class.java).get())) {
            with(mapper[entity]) {
                val motor = hardwareMap.dcMotor.get(deviceString)
                motor.direction = direction
                motor.zeroPowerBehavior = zeroPowerBehavior
                motor.power = power
            }
        }
    }
}