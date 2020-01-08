package org.firstinspires.ftc.teamcode.lib.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import org.firstinspires.ftc.teamcode.lib.components.CommandComponent

class CommandSystem : IteratingSystem(Family.all(CommandComponent::class.java).get()) {
    init {
        priority = 0
    }

    val commandMapper = ComponentMapper.getFor(CommandComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        commandMapper.get(entity).execute(entity!!)
    }
}