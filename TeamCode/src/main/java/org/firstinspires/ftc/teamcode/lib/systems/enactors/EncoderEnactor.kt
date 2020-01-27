package org.firstinspires.ftc.teamcode.lib.systems.enactors

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Family
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.lib.components.EncoderComponent
import org.firstinspires.ftc.teamcode.lib.systems.HardwareMapSystem

class EncoderEnactor : HardwareMapSystem() {
    val mapper = ComponentMapper.getFor(EncoderComponent::class.java)

    override fun processHardware(hardwareMap: HardwareMap, deltaTime: Float) {
        for (entity in engine!!.getEntitiesFor(Family.all(EncoderComponent::class.java).get())) {
            with(mapper[entity]) {
                val motor = hardwareMap.dcMotor.get(deviceString) as DcMotorEx
                motor.let {
                    it.velocity = this.targetVelocity
                    it.targetPosition = this.targetPosition
                    if (runToTargetPosition) it.mode = DcMotor.RunMode.RUN_TO_POSITION
                    else it.mode = DcMotor.RunMode.RUN_USING_ENCODER
                }
            }
        }
    }
}