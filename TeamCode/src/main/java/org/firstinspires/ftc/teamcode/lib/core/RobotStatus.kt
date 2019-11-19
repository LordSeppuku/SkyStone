package org.firstinspires.ftc.teamcode.lib.core

import arrow.core.ListK
import org.firstinspires.ftc.teamcode.lib.command.Subsystem

data class RobotStatus(val subsystems: ListK<Subsystem>)