package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@TeleOp(name = "follow cube")
public class Sampling extends LinearOpMode {

    Robot robot;
    //drivetrainDC[0] = RIGHT
    //drivetrainDC[1] = LEFT
    boolean findCube = false;
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    public int cubePlace = 0;// 0 = NOT HERE, 1 = RIGHT (in camera), 2 = LEFT (in camera)
    public boolean noMotor = false;
    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = " ATgDONj/////AAABmW0G/nQirUMiumnzPc6Pl8oJhBOCC2qoUq0BWhir9YWcBFDlhZUfSwATcQArcyyLxIOV21sHaYJeeQEJZfIJ+4spBn3oJ/DfycsbPaNs87+TRpM46/vbUkj1Ok+NtZ/eqMhmMXjFC8dgdCfbCt0aMxoBNzDw4+v28abG+hjUCjVYf86Jq1m7R942XCjw0yhOZqTXWIp3WAZDXY/PdWGQGY/zWae0l6TAZ6Z27t1xYJdkkpLqEsbKM3ZprvtgIs8AsWS9Tri2892OHq2CnCL+1ZHHXKPdxON3fiC1Gd3oihwPhTUReNw0VAg9yeVsVa1UQg7ea9K6WpmVto0FG+T2/LV8uq/3Mp/NHWiNizw2DM4h";
    ;

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;


    @Override

    public void runOpMode() {
        robot = new Robot(hardwareMap);

        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
        waitForStart();
        telemetry.addLine("follow cube 0:");
        telemetry.update();

        if (tfod != null) {
            tfod.activate();
        }

        followCube(0.2);
//        if (getCube() == 0) {
//
//        } else if (getCube() == 1) {
//
//        } else if (getCube() == 2) {
//
//        }
        if (tfod != null) {
            tfod.shutdown();
        }
    }


    private void followCube(double power) {
        telemetry.addLine("follow cube 1:");
        telemetry.update();

        double distanceFromRight = 0;
        double distanceFromLeft = 0;
        double middleCubeX = 0;
        double k = 0.0007; //EDEN
        double[] addToMotors;
        addToMotors = new double[2];
        boolean firstGold = false;
        boolean breakLoop = false;


//        while (opModeIsActive()) {//TODO: ADD CONDITION
//            telemetry.addLine("search cube 1");
//            telemetry.update();
//            if (tfod != null)
//                updatedRecognitions = tfod.getUpdatedRecognitions();
//
//            if (updatedRecognitions != null
//               &&!updatedRecognitions.isEmpty()
//                        && updatedRecognitions.get(0).getLabel().equals(LABEL_GOLD_MINERAL))
//                    break;
//        }
        if (tfod != null)

            do {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();


                if (updatedRecognitions != null
                        && !updatedRecognitions.isEmpty()//was changed
                    //     && updatedRecognitions.get(0) != null
                        ) {
                    if (updatedRecognitions.get(0).getLabel().equals(LABEL_GOLD_MINERAL)) {
                        firstGold = true;
                        middleCubeX = ((updatedRecognitions.get(0).getLeft() + updatedRecognitions.get(0).getRight()) / 2);
                        distanceFromRight = 700 - middleCubeX;
                        distanceFromLeft = middleCubeX;
                        telemetry.addLine("follow cube 4:");

                        addToMotors[0] = k * distanceFromRight;  //RIGHT
                        addToMotors[1] = k * distanceFromLeft; //LEFT
                        telemetry.addData("distance From Right:", addToMotors[0]);
                        telemetry.addData("distance From Left:", addToMotors[1]);
                        telemetry.update();

                        if (!noMotor) {
                            robot.driveTrain[0][1].setPower(addToMotors[0] + power);//RIGHT Front
                            robot.driveTrain[1][1].setPower(addToMotors[0] + power);//Right Back
                            robot.driveTrain[1][0].setPower(addToMotors[1] + power);//Left Back
                            robot.driveTrain[0][0].setPower(addToMotors[1] + power);//LEFT Front

                        }
                    } else {
                        if (firstGold)
                            breakLoop = true;
                        telemetry.addLine("dont see cube 1");
                        telemetry.update();
                    }
                } else {
                    telemetry.addLine("dont see cube 2");
                    telemetry.update();
                    //       break;
                }

//            powerAdd ToMotors[0] = getPowerMotor()[0];//RIGHT
//            powerAddToMotors[1] = getPowerMotor()[1];//LEFT


                //   break;


                //    }
                if (!noMotor) {

                    robot.driveTrain[0][1].setPower(0);//RIGHT Front
                    robot.driveTrain[1][1].setPower(0);//Right Back
                    robot.driveTrain[1][0].setPower(0);//Left Back
                    robot.driveTrain[0][0].setPower(0);//LEFT Front

                }

            }
            while (opModeIsActive() && !breakLoop);
    }

    private int getCube() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first..
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
            }

            if (opModeIsActive()) {//CHANGE TO IF
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                    }
                    if (updatedRecognitions.size() == 1 && updatedRecognitions.get(0).getLabel().equals(LABEL_GOLD_MINERAL)) {

                    }
                    if (updatedRecognitions.size() == 2) {
                        int goldMineralX = -1;
                        int silverMineral1X = -1;
                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                goldMineralX = (int) recognition.getLeft();
                            } else //if (silverMineral1X == -1) {
                                silverMineral1X = (int) recognition.getLeft();

                        }

                        if (updatedRecognitions.size() == 3) {

                        }


                        if (goldMineralX != -1 && silverMineral1X != -1) {
                            if (goldMineralX < silverMineral1X) {
                                telemetry.addData("Gold Mineral Position", "Left");
                                cubePlace = 2;//LEFT in camera
                            } else if (goldMineralX > silverMineral1X) {
                                telemetry.addData("Gold Mineral Position", "Right");
                                cubePlace = 1;//RIGHT in camera
                            }

                        } else {
                            telemetry.addData("Gold Mineral Position", "NOT HERE");
                            cubePlace = 0;//NOT in the camera
                        }
                    }
                    telemetry.update();
                }
            }
        }


        if (tfod != null) {
            tfod.shutdown();
        }
        return (cubePlace);
    }

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

}
