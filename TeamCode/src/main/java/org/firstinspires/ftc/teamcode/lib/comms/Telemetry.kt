package org.firstinspires.ftc.teamcode.lib.comms

import org.firstinspires.ftc.robotcore.external.Telemetry

class OptTelemetry(private val delegate: Telemetry) : Telemetry by delegate