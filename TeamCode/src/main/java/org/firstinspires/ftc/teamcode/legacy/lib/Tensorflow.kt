package org.firstinspires.ftc.teamcode.legacy.lib

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector

class Tensorflow(hwMap: HardwareMap) {

    private val VUFORIA_KEY = "AasLr6H/////AAAAGU9DSfvoF0zouI6A9has0UxpiBZHzGEw/OR8AEFeSJD8+QTnDRCBbCSN29VRmfAZbDbHXW+MBuFeWsZrK/CC1PrrUlOpl3eOaZGLMBhkDVYLTsbvc7zB7D/ua57nlxRTntMdcPidV7m0/cxv7v3saWfW+fx4l0l9qWl1gK9fuU86ENuxel7FAk7oVJK9ijCwqiG+tX1nNXkhvWiawm/l+obUFijlvqIWLftXhiWLIIqU5nB4zBr+FeRpAwwx1pnclhy1faC/iwVwLZKJyqk3XixTKcN7uHsWB8mppDB1rpScVTy4S/fKuE9XFtn49pZW+3+YW2RHwuryZTfkwmsMhgXEgDDfNPf41VR580uTAJ/1"
    private val TFOD_MODEL_ASSET = "Skystone.tflite"
    val LABEL_FIRST_ELEMENT = "Stone"
    val LABEL_SECOND_ELEMENT = "Skystone"

    private val tfod: TFObjectDetector
    var isActive = false
        private set

    init {
        if (!ClassFactory.getInstance().canCreateTFObjectDetector()) {
            throw UnknownError("YOOO WHATI THE ACTUAL FUCK")
        }

        tfod = ClassFactory.getInstance().createTFObjectDetector(
                TFObjectDetector.Parameters(hwMap.appContext.resources.getIdentifier(
                        "tfodMonitorViewId", "id", hwMap.appContext.packageName)).apply {
                    minimumConfidence = 0.70
                },
                ClassFactory.getInstance().createVuforia(VuforiaLocalizer.Parameters().apply {
                    vuforiaLicenseKey = VUFORIA_KEY
                    cameraName = hwMap.get(WebcamName::class.java, "Webcam 1")
                })
        ).also {
            it.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT)
        }
    }

    fun acquireRecognitions(): MutableList<Recognition>? {
        start()
        return tfod.recognitions
    }

    fun start() {
        if (!isActive) {
            tfod.activate()
            isActive = true
        }
    }

    fun shitdown() {
        if (isActive) {
            tfod.shutdown()
            isActive = false
        }
    }

}