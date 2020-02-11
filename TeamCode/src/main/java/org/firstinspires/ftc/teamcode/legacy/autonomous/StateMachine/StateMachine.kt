package org.firstinspires.ftc.teamcode.legacy.autonomous.StateMachine

import com.tinder.StateMachine
import org.firstinspires.ftc.teamcode.legacy.lib.DriveToPositionCommand
import org.firstinspires.ftc.teamcode.legacy.opmodes.auto.OurOpMode
import org.firstinspires.ftc.teamcode.legacy.subsystems.Vision

interface State
interface Event
interface SideEffect

sealed class AutoEvent : Event {
    data class GoToPose(val pose: Pose) : AutoEvent()
    object Discern : AutoEvent()
    object Acquire : AutoEvent()
    object Deposit : AutoEvent()
}

sealed class AutoSideEffect : SideEffect {
    data class OpModeCommand(val command: OurOpMode.() -> Unit) : AutoSideEffect()
    class DriveCommand(vararg val commands: DriveToPositionCommand) : AutoSideEffect()
    object Discern : AutoSideEffect()
}

data class AutoStateMachine(val opMode: OurOpMode) {
    var skystonePosition = Vision.SkystonePosition.UNKNOWN
        private set

    val stateMachine by lazy {
        StateMachine.create<State, Event, SideEffect> {
            initialState(opMode.startPose)
            base(Pose.StartAlpha)
            base(Pose.StartBeta)
            base(Pose.Discerning) {
                eventTransition<Pose.Discerning, AutoEvent.Discern>(Pose.Discerning, AutoSideEffect.Discern)
            }
            base(Pose.QuarryAlpha) {
                eventTransition<Pose.QuarryAlpha, AutoEvent.Acquire>(Pose.QuarryAlpha, AutoSideEffect.DriveCommand(*skystonePosition.run { opMode.driveAndSucccCommands() }.toTypedArray()))
            }
            base(Pose.QuarryBeta) {
                eventTransition<Pose.QuarryBeta, AutoEvent.Acquire>(Pose.QuarryBeta, AutoSideEffect.DriveCommand(*skystonePosition.run { opMode.driveAndSucccCommands() }.toTypedArray()))
            }
            base(Pose.ParkAlpha)
            base(Pose.ParkBeta)
            base(Pose.Crossways)
            base(Pose.FoundationLateral)
            base(Pose.FoundationWidth)

            onTransition {
                val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
                when (validTransition.sideEffect) {
                    is AutoSideEffect.OpModeCommand -> opMode.((validTransition.sideEffect as AutoSideEffect.OpModeCommand).command)()
                    is AutoSideEffect.DriveCommand -> with(validTransition.sideEffect as AutoSideEffect.DriveCommand) {
                        //this.commands.forEach { opMode.drivetrain.driveToPosition(it)(opMode, it.busyCode) }
                        commands.forEach {
                            println(it)
                        }
                    }
                    AutoSideEffect.Discern -> skystonePosition = opMode.vision.averagedAcquisition(opMode::opModeIsActive)
                }
            }
        }
    }
}

typealias stateMachine = StateMachine.GraphBuilder<State, Event, SideEffect>
typealias stateDef<reified S> = StateMachine.GraphBuilder<State, Event, SideEffect>.StateDefinitionBuilder<S>

inline fun <reified S : Pose> stateMachine.base(state: S, crossinline custom: stateDef<S>.() -> Unit = {}) {
    state<S> {
        on<AutoEvent.GoToPose> {
            val path = this[it.pose]
            val commands = path.map {
                val index = path.indexOf(it)
                println(it)

                if (index >= path.size - 1) DriveToPositionCommand()//rotation = path[index - 1].connections[path[index]]!!.rotation)
                else it.connections.getOrDefault(path[path.indexOf(it) + 1], DriveToPositionCommand())
                //it.connections.getOrDefault(path[path.indexOf(it) + 1], DriveToPositionCommand())
            }.dropLast(1)
            println(commands)
            transitionTo(it.pose, AutoSideEffect.DriveCommand(*commands.toTypedArray()))
        }

        on<AutoEvent.Deposit> {
            transitionTo(this, AutoSideEffect.OpModeCommand {
                TODO("create deposit logic, maybe implement in Arm subsystem")
            })
        }

        custom()
    }
}

inline fun <reified S : State, reified E : AutoEvent> stateDef<S>.eventTransition(newState: Pose, sideEffect: AutoSideEffect) {
    on<E> {
        transitionTo(newState, sideEffect)
    }
}

inline fun <reified S : Pose, reified E : AutoEvent> stateDef<S>.eventTransition(newState: Pose) {
    on<E> {
        transitionTo(newState)
    }
}