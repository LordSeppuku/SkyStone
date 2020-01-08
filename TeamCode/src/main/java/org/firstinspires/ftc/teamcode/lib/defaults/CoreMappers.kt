package org.firstinspires.ftc.teamcode.lib.defaults

import com.badlogic.ashley.core.ComponentMapper
import org.firstinspires.ftc.teamcode.lib.components.MovementComponent
import org.firstinspires.ftc.teamcode.lib.components.TransformComponent

object CoreMappers {
    val transform = ComponentMapper.getFor(TransformComponent::class.java)
    val movement = ComponentMapper.getFor(MovementComponent::class.java)

}