package org.firstinspires.ftc.teamcode.lib.systems.logic

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import ktx.ashley.mapperFor
import org.firstinspires.ftc.teamcode.lib.components.*

class DrivetrainSystem : EntitySystem() {
    companion object {
        val drivetrainFamily = Family.all(
                MovementComponent::class.java,
                RotationalMovementComponent::class.java,
                TransformComponent::class.java,
                LocalizedMovementComponent::class.java,
                PurePursuitComponent::class.java,
                KinematicComponent::class.java
        )
        val pathFamily = Family.all(PathComponent::class.java)

        val transformMapper = mapperFor<TransformComponent>()
        val rotationalMapper = mapperFor<RotationalMovementComponent>()
        val localizedMapper = mapperFor<LocalizedMovementComponent>()
        val pursuitMapper = mapperFor<PurePursuitComponent>()
        val pathMapper = mapperFor<PathComponent>()
        val kinematicMapper = mapperFor<KinematicComponent>()
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val ourEntity = engine!!.getEntitiesFor(drivetrainFamily.get()).first()
        engine.getEntitiesFor(pathFamily.get()).first()!!.run {
            val path = pathMapper[this].path
            val pursuit = pursuitMapper[ourEntity]
            val transform = transformMapper[ourEntity]
            val lookaheads = mutableListOf<Vector2>()
            for (i in 0..8) {
                val work = transform.position.rotate(transform.rotation.toFloat() + (45))
                lookaheads.add(path.valueAt(Vector2(), path.approximate(work.run {
                    setLength(len() + pursuit.lookAheadRadius.toFloat())
                })))
            }
            lookaheads.sortBy {
                it.dst(transform.position)
            }
            val kinematics = kinematicMapper[ourEntity]
            val targetVelocity = lookaheads.first().sub(transform.position).scl(0.5f)
            val targetHeading = (transform.position.angle(lookaheads.first()) - transform.rotation) * 0.5
            localizedMapper[ourEntity].velocity.set(kinematics.kinematics.linearVelocityToRobotVelocity(targetVelocity!!, transform.rotation))
            rotationalMapper[ourEntity].velocity = targetHeading
        }
    }

}