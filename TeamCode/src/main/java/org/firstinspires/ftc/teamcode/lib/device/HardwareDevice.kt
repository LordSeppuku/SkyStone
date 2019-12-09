package org.firstinspires.ftc.teamcode.lib.device

import arrow.core.*
import arrow.fx.IO
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.lib.command.Command

interface HardwareDevice<T> {

    suspend fun initialize(hardwareMap: HardwareMap, block: HardwareDevice<T>.() -> Unit): Either<Failure, Success<Some<HardwareDevice<T>>>>

    suspend fun getDevice(): IO<T>

    suspend fun processCommand(command: Some<Command>): Either<Failure, Success<Int>>

    suspend fun uninitialize(block: HardwareDevice<T>.() -> Unit): Either<Failure, Success<None>>

}