package org.firstinspires.ftc.teamcode.lib

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.lib.opmodes.RogueOpMode

object OpModeSingleton : Listener<OpModeSingleton.OpModeState> {
    lateinit var telemetry: Telemetry
        private set

    lateinit var opModeState: OpModeState
        private set

    lateinit var opMode: RogueOpMode
        private set

    fun init(opMode: RogueOpMode) {
        this.opMode = opMode
        this.telemetry = opMode.telemetry
        opModeState = OpModeState.INIT
        opMode.opModeState.add(this)
    }

    enum class OpModeState {
        INIT, START, STOP
    }

    override fun receive(signal: Signal<OpModeState>?, `object`: OpModeState?) {
        opModeState = `object`!!
    }
}