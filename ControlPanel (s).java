package frc.robot;

import java.util.ArrayList;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.DriverStation;


class ControlPanel{
    /*Fields*/
    private final portMap map = new portMap();
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    public final ColorSensorV3 m_ColorSensor = new ColorSensorV3(i2cPort);
    public CANSparkMax mechanism = new CANSparkMax(map.wheelTurner, MotorType.kBrushless);

    public Color goalColor;
    Color currentColor;
    Color colorSensed;
    Color lastColor;

    ColorMatch m_colorMatcher = new ColorMatch();
    private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

    public ArrayList <Color> sequence = new ArrayList<>();


    //Variables
    int i = 0, times=4;
    boolean start = true;


    /*Constructor*/
    public ControlPanel(){}


    /*Methods*/

    //Initialization
    public void init(){
        //asigning colors
        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kGreenTarget);
        m_colorMatcher.addColorMatch(kRedTarget);
        m_colorMatcher.addColorMatch(kYellowTarget);
        //inserting colors into arrlist
        sequence.add(kGreenTarget);
        sequence.add(kBlueTarget);
        sequence.add(kYellowTarget);
        sequence.add(kRedTarget);
        sequence.add(kGreenTarget);
        sequence.add(kBlueTarget);
        sequence.add(kRedTarget);
        sequence.add(kYellowTarget);
    }


    /*Positional Control*/
    //Color Receiver
    public void receiveColor(){
        String gameData;
        gameData = DriverStation.getInstance().getGameSpecificMessage();
        if(gameData.length() > 0){
            switch (gameData.charAt(0)){
                case 'B' :
                    setGoalColor(kBlueTarget);
                    break;
                case 'G' :
                    setGoalColor(kGreenTarget);
                    break;
                case 'R' :
                    setGoalColor(kRedTarget);
                    break;
                case 'Y' :
                    setGoalColor(kYellowTarget);
                    break;
                default :
                    //This is corrupt data
                    System.out.println("Corrupt data");
                    break;
            }
        }
        else{
            //Code for no data received yet
        }
    }

    //Color Idetifiers
    public void setGoalColor(Color color){goalColor = color;}

    //Color Recognizer
    public void getCurrentColor(){
        //detecing colors
        Color detected = m_ColorSensor.getColor();
        ColorMatchResult match = m_colorMatcher.matchClosestColor(detected);
        //simultaneously updating colors
        lastColor=currentColor;
        currentColor = match.color;
    }

    //Color-Sensor Changer
    public void ledSense(LED colorLight){
        if(currentColor == kRedTarget){
            colorLight.set(.61);
        }
        if(currentColor == kBlueTarget){
            colorLight.set(.87);
        }
        if(currentColor == kGreenTarget){
            colorLight.set(.77);
        }
        if(currentColor == kYellowTarget){
            colorLight.set(.69);
        }
    }

    //Color Checker
    public void colorControl(){
        String finished;
        //mechanism.set(.1);
        if (currentColor == goalColor && lastColor == null){
            //mechanism.set(0)
            finished = "yes";
        }
        else if(currentColor == goalColor){ //&&currentColor==sequence.get(findCell(lastColor)+1))
            //mechanism.set(0)
            finished = "yes";
        }
        else{
            finished = "no";
        }
        SmartDashboard.putString("finshed", finished);
    }

    //Sequence Indexer
    public int findCell(Color color){
        int index = 6;
        for(int i = 0; i < sequence.size(); i++)
        {
            if(color == sequence.get(i))
            {
                index = i;
            }
        }
        if(index > 3){
            index %= 4;
        }
        return index;
    }

    //SmartDashboard Ouput
    public void printColor(){
        String colorString;
        if (currentColor == kBlueTarget){ colorString = "Blue";}
        else if (currentColor == kRedTarget){ colorString = "Red"; }
        else if (currentColor == kGreenTarget){ colorString = "Green"; }
        else if (currentColor == kYellowTarget){ colorString = "yellow"; }
        else{ colorString = "unkown"; }

        if(lastColor == currentColor|| lastColor == null){
            SmartDashboard.putString("discard?", "no");
            SmartDashboard.putString("Color sensed", colorString);
        }
        if(lastColor != currentColor){
            int lastC = findCell(lastColor);
            if(currentColor!=sequence.get(lastC+1)){
                SmartDashboard.putString("discard?", "yes");
        }
        else{
          SmartDashboard.putString("discard?", "no");
          SmartDashboard.putString("Color Sensed", colorString);
        }
    }


    /*Rotational Control*/
    //Wheel Turner
    public void turnWheel(LED colorLight){
        if(start){
            getCurrentColor();
            start = false;
        }
        if(i < times*2) {
            getCurrentColor();
            //mechanism.set(.1)
        }
        if(currentColor == goalColor && currentColor == sequence.get(findCell(lastColor)+1)){
            i++;
            SmartDashboard.putNumber("Times Sensed", (double)i);
            SmartDashboard.putNumber("Full rotations", (double)i/2);
        }
        //else -> mechanism.set(0);
    }

    //Wheel Stopper
    public void stopWheel(){
        mechanism.set(0);
    }

    //Manual Operation
    public void manualControl(){
        if(){//controller input
            mechanism.set(0.1);
            System.out.println("spin");
        }
        else if(){//controller input
            mechanism.set(0);
            System.out.println("stop");
        }
    }
}


/*Robot.java
 if(operator.getRawButton(OI.LB)){
      colorSpin.run_encoder();
      System.out.println("Start rotation");
  }
  if(operator.getRawButton(OI.RB)){
      System.out.println("Start color find colorwheel");
      colorSpin.find_color();
  }*/