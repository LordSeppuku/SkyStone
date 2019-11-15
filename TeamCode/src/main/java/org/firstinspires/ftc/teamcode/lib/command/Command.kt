package org.firstinspires.ftc.teamcode.lib.command

interface Command {

    fun registerHardwareDevices()

    fun init()



}

enum class COMMAND_TYPE {
    PERIODIC, IMPULSE
}

enum class COMMAND_STATUS {
    INACTIVE, INITIALIZED, PROCESSING, FINISHED
}