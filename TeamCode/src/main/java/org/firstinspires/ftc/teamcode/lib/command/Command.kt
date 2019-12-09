package org.firstinspires.ftc.teamcode.lib.command

interface Command {

    val type: Type

    fun registerHardwareDevices()

    fun getStatus(): Status

    fun init(): Status

    fun run(): Status

    fun stop(): Status

    enum class Type {
        PERIODIC, IMPULSE
    }

    enum class Status {
        INACTIVE, INITIALIZED, PROCESSING, FINISHED
    }

}