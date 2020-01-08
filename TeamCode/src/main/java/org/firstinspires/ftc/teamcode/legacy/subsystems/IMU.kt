package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.navigation.Orientation

class IMU(private val hwMap: HardwareMap) {

    var isInit = false
        private set

    val init: () -> Boolean = {
        if (!isInit) {
            isInit = imu.initialize(parameters)
        }
        isInit
    }

    private val imu: BNO055IMU
        get() = hwMap.get(BNO055IMU::class.java, "imu")

    private val parameters: BNO055IMU.Parameters = BNO055IMU.Parameters().apply {
        angleUnit = BNO055IMU.AngleUnit.RADIANS
        accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        loggingEnabled = true
        loggingTag = "IMU"
        accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()
    }

    val angles: () -> Orientation = {
        init()
        imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS)/*.apply {
            firstAngle = (((2 * PI) - firstAngle) % 2 * PI).toFloat()
            secondAngle = (((2 * PI) - secondAngle) % 2 * PI).toFloat()
            thirdAngle = (((2 * PI) - thirdAngle) % 2 * PI).toFloat()
        }
        */
    }

}