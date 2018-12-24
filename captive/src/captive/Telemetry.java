package captive;

import java.util.HashMap;
import java.util.Map;

public class Telemetry {
  public int checkSumStart=201;
  public int missionTime;
  public long logTime;
  public int roll;
  public int pitch;
  public int heading;
 public float airSpeed;
  public int  elevator;
  public int  aileron;
  public int  alfa;
  public int  betha;
  public int  standAngle;
  public int pressure;
  public int pAlt;
 public float ax;
  public float ay;
 public float az;
 public float gx;
 public float gy;
 public float gz;
  public int lc1;
  public int lc2;
  public int lc3;
  public int lc4;
  public int temprature;
 public int voltage;
  public int GPSSats;
  public double GPSLat;
  public double GPSLong;
  public float GPSAlt;
  public float GPSSpeed;
  public int checkSumEnd=254;
  public String payloadString;

public void initString(String s){
  payloadString =  new String(s.substring(0, s.length()-1));
}
public void parseString(){
    String[] values = payloadString.split(",");
    int i =0;
checkSumStart = Integer.parseInt(values[i++]);
logTime = Integer.parseInt(values[i++]);
missionTime = Integer.parseInt(values[i++]);
roll = Integer.parseInt(values[i++]);
pitch = Integer.parseInt(values[i++]);
heading = Integer.parseInt(values[i++]);
airSpeed = Float.parseFloat(values[i++]);
elevator = Integer.parseInt(values[i++]);
aileron = Integer.parseInt(values[i++]);
alfa = Integer.parseInt(values[i++]);
betha = Integer.parseInt(values[i++]);
standAngle = Integer.parseInt(values[i++]);
pressure = Integer.parseInt(values[i++]);
pAlt = Integer.parseInt(values[i++]);
ax = Float.parseFloat(values[i++]);
ay = Float.parseFloat(values[i++]);
az = Float.parseFloat(values[i++]);
gx = Float.parseFloat(values[i++]);
gy = Float.parseFloat(values[i++]);
gz = Float.parseFloat(values[i++]);
lc1 = Integer.parseInt(values[i++]);
lc2 = Integer.parseInt(values[i++]);
lc3 = Integer.parseInt(values[i++]);
lc4 = Integer.parseInt(values[i++]);
temprature = Integer.parseInt(values[i++]);
voltage = (Integer.parseInt(values[i++]));
GPSSats = Integer.parseInt(values[i++]);
GPSLat = Double.parseDouble(values[i++]);
GPSLong = Double.parseDouble(values[i++]);
GPSAlt = Float.parseFloat(values[i++]);
GPSSpeed = Float.parseFloat(values[i++]);
//checkSumEnd = Integer.parseInt(values[i++]);
}
public boolean checkSumCheck(String temp){
    if (temp.startsWith("201,")&& temp.substring(0, temp.length()-1).endsWith(",254") && findRepeats(temp,',') == 30){
     return true;   
    }else{
        return false;
    }
}
public int findRepeats(String chain , char c){

int cont = 0;
for(int i=0; i<chain.length(); i++) {
   if(chain.charAt(i) == c) {
      cont++;
   } 
}
return cont;
}

}