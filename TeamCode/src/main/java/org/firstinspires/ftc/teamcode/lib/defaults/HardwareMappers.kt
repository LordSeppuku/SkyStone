package org.firstinspires.ftc.teamcode.lib.defaults

import com.badlogic.ashley.core.ComponentMapper
import org.firstinspires.ftc.teamcode.lib.components.DcMotorComponent
import org.firstinspires.ftc.teamcode.lib.components.EncoderComponent

object HardwareMappers {
    val dcMotor = ComponentMapper.getFor(DcMotorComponent::class.java)
    val encoder = ComponentMapper.getFor(EncoderComponent::class.java)
}