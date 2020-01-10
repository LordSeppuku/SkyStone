package org.firstinspires.ftc.teamcode.lib.systems.enactors

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.lib.components.DcMotorComponent
import org.firstinspires.ftc.teamcode.lib.systems.HardwareMapSystem

class MotorEnactor : HardwareMapSystem() {
    private lateinit var entities: ImmutableArray<Entity>
    private val mapper = ComponentMapper.getFor(DcMotorComponent::class.java)

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        entities = engine!!.getEntitiesFor(Family.all(DcMotorComponent::class.java).get())
    }

    override fun processHardware(hardwareMap: HardwareMap, deltaTime: Float) {
        for (entity in entities) {
            with(mapper[entity]) {
                val motor = hardwareMap.dcMotor.get(deviceString)
                motor.direction = direction
                motor.zeroPowerBehavior = zeroPowerBehavior
                motor.power = power
            }
        }
    }
}