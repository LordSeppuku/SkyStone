package org.firstinspires.ftc.teamcode.lib.systems.logic

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import org.firstinspires.ftc.teamcode.lib.components.MovementComponent
import org.firstinspires.ftc.teamcode.lib.components.TransformComponent

class DrivetrainSystem : IteratingSystem(family.get()) {

    companion object {
        val family = Family.all(MovementComponent::class.java, TransformComponent::class.java)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        with(entity!!) {

        }
    }

}