package org.firstinspires.ftc.teamcode.legacy.opmodes.test

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.lib.components.*
import org.firstinspires.ftc.teamcode.lib.factory.EntityFactory
import org.firstinspires.ftc.teamcode.lib.factory.NoProducer
import org.firstinspires.ftc.teamcode.lib.opmodes.RogueOpMode
import org.firstinspires.ftc.teamcode.lib.systems.logic.DrivetrainSystem

@Disabled
@Autonomous(name = "Better Drivetrain Test", group = "Test")
class RedBullGivesYouWingsDos : RogueOpMode() {
    override val userLogic: List<EntitySystem> = listOf(
            DrivetrainSystem()
    )
    override val userEnactors: List<EntitySystem> = listOf(
            object : IteratingSystem(Family.all(DcMotorComponent::class.java, EncoderComponent::class.java, DrivetrainMotorComponent::class.java, WheelComponent::class.java).get()) {
                val encoderMapper = ComponentMapper.getFor(EncoderComponent::class.java)
                val wheelMapper = ComponentMapper.getFor(WheelComponent::class.java)
                val drivetrainMotorMapper = ComponentMapper.getFor(DrivetrainMotorComponent::class.java)

                override fun processEntity(entity: Entity?, deltaTime: Float) {
                    val robot = engine.getEntitiesFor(DrivetrainSystem.drivetrainFamily.get()).first()
                    with(entity!!) {
                        (hardwareMap[encoderMapper[this].deviceString] as DcMotorEx).let {
                            it.mode = DcMotor.RunMode.RUN_USING_ENCODER
                            it.velocity = robot.getComponent(KinematicComponent::class.java).run {
                                val work = kinematics.robotVelocityToWheelRotations(wheelMapper[this@with].radius,
                                        robot.getComponent(LocalizedMovementComponent::class.java).velocity)[drivetrainMotorMapper[this@with].position]

                                kinematics.wheelRotationsToTicks(work, wheelMapper[this@with], encoderMapper[this@with])
                            }
                        }
                    }
                }
            }
    )
    override val userEntityFactories: List<EntityFactory> = listOf(
            EntityFactory(NoProducer(3)) {
                val deviceString = when (it) {
                    0 -> "fl"
                    1 -> "fr"
                    2 -> "bl"
                    3 -> "br"
                    else -> "fl"
                }

                val direction = when (it) {
                    1 -> DcMotorSimple.Direction.REVERSE
                    else -> DcMotorSimple.Direction.FORWARD
                }

                {
                    with<DcMotorComponent> {
                        this.deviceString = deviceString
                        this.direction = direction
                    }
                    with<EncoderComponent> {
                        this.deviceString = deviceString
                        this.direction = direction
                    }
                }
            },
            EntityFactory {
                {
                    with<PathComponent> {
                        path = CatmullRomSpline<Vector2>(arrayOf(Vector2.X, Vector2.Y, Vector2.Zero), true)
                    }
                }
            },
            EntityFactory {
                /*Entity().apply {
                    add(MovementComponent())
                    add(RotationalMovementComponent())
                    add(TransformComponent())
                    add(LocalizedMovementComponent())
                    add(PurePursuitComponent())
                    add(KinematicComponent(MecanumKinematics(2.875, 5.25)))
                }*/
                {
                    with<MovementComponent>()
                    with<RotationalMovementComponent>()
                    with<TransformComponent>()
                    with<LocalizedMovementComponent>()
                    with<PurePursuitComponent>()
                    with<KinematicComponent> {
                        kinematics = MecanumKinematics(2.875, 5.25)
                    }
                }
            }
    )
}