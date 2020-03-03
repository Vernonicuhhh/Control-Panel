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

//code reviewed by Seymour for ultra compile speed

class ControlPanel 
 {  
private final portMap map = new portMap();  
Color currentColor;
Color colorSensed;
Color lastColor;
private final I2C.Port i2cPort = I2C.Port.kOnboard;
public final ColorSensorV3 m_ColorSensor = new ColorSensorV3(i2cPort);
ColorMatch m_colorMatcher = new ColorMatch();
private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
public ArrayList <Color> sequence = new ArrayList<>();
//public CANSparkMax mechanism = new CANSparkMax(map.wheelTurner, MotorType.kBrushless);
int i = 0;
int times=4;
public Color goalColor;
boolean start = true;
  public ControlPanel()
  {
      
  }
public void init()
{
  m_colorMatcher.addColorMatch(kBlueTarget);
  m_colorMatcher.addColorMatch(kGreenTarget);
  m_colorMatcher.addColorMatch(kRedTarget);
  m_colorMatcher.addColorMatch(kYellowTarget);
  sequence.add(kGreenTarget);
  sequence.add(kBlueTarget);
  sequence.add(kYellowTarget);
  sequence.add(kRedTarget);
  sequence.add(kGreenTarget);
  sequence.add(kBlueTarget);
  sequence.add(kRedTarget);
  sequence.add(kYellowTarget);
}
   public void getCurrentColor()
   {
     Color detected = m_ColorSensor.getColor();
    ColorMatchResult match = m_colorMatcher.matchClosestColor(detected);
    
    lastColor=currentColor;
    currentColor = match.color;
   }
public void setGoalColor(Color color)
{
    goalColor = color;
}

 


public int findCell(Color color)
{
  int index = 6;
  for(int i = 0; i<sequence.size(); i++)
  {
    if(color==sequence.get(i))
    {
      index=i;
    }
    

  }
  if(index>3)
  {
    index%=4;
  }
  return index;

}

public void colorControl()
{
  //mechanism.set(.1);
  String finished;
  if (currentColor==goalColor&& lastColor==null)
  {
    //mechanism.set(0)
    finished = "yes";
  }
 else if(currentColor==goalColor)//&&currentColor==sequence.get(findCell(lastColor)+1))
  {
    //mechanism.set(0)
    finished = "yes";
  }
  else
  {
    finished = "no";
  }
  SmartDashboard.putString("finshed", finished);
}
 public void turnWheel(LED colorLight)
{
  if(start)
  {
  getCurrentColor();
  start = false;
  }  
  if(i<times*2) 
  {
    getCurrentColor();
  // mechanism.set(.1);
      if(currentColor==goalColor&& currentColor==sequence.get(findCell(lastColor)+1))
      {   
            i++;       
            SmartDashboard.putNumber("Times Sensed", (double)i);
            SmartDashboard.putNumber("Full rotations", (double)i/2);
      }
  }
  //else
 // mechanism.set(0);
}

public void ledSense(LED colorLight)
{
  
  if(currentColor == kRedTarget)
  {
    colorLight.set(.61);
  }
  if(currentColor == kBlueTarget)
  {
    colorLight.set(.87);}
  if(currentColor == kGreenTarget)
  {colorLight.set(.77);}
  if(currentColor == kYellowTarget)
  {colorLight.set(.69);}

}

public void printColor()
  { 
     String colorString;
    if (currentColor == kBlueTarget) 
    {
      colorString = "Blue";
    }
     else if (currentColor == kRedTarget) 
     {
   
      colorString = "Red";
    }
     else if (currentColor == kGreenTarget) 
     {
      colorString = "Green";
     }
     else if (currentColor == kYellowTarget) 
     {
      colorString = "yellow";
    }
    else 
    {
      colorString = "unkown";
    }


      if(lastColor==currentColor|| lastColor == null)
      {
        SmartDashboard.putString("discard?", "no");
        SmartDashboard.putString("Color sensed", colorString);
      }

      if(lastColor!=currentColor)
      {
        int lastC = findCell(lastColor);
        if(currentColor!=sequence.get(lastC+1))
        {
          SmartDashboard.putString("discard?", "yes");
        }
        else
        {
          SmartDashboard.putString("discard?", "no");
          SmartDashboard.putString("Color Sensed", colorString);
        }
 
      }
     
    }
}   