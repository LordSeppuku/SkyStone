package org.firstinspires.ftc.teamcode.legacy.opmodes.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.legacy.autonomous.StateMachine.Pose.*
import org.firstinspires.ftc.teamcode.meme

@Autonomous(name = meme.justAsTheFoundingFathersIntended, group = "z")
class aaaaaaaaaaa : OurOpMode() {
    override fun run() {
        stateMachine.stateMachine.run {
            while (opModeIsActive()) {
                navigateTo(FoundationWidth)
                navigateTo(StartBeta)
                navigateTo(QuarryBeta)
                navigateTo(StartAlpha)
            }
        }
    }
}