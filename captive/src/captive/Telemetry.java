package captive;

import java.util.HashMap;
import java.util.Map;

public class Telemetry {
    public int checkSumStart=201;
    public int TeamID;
    public int missionTime;
    public float Altitude;
    public int PacketCount;
    public int Pressure;
    public int temperature;
    public int voltage;
    public int GPSTime;
    public int GPSLatitude;
    public int GPSLongitude;
    public float GPSAltitude;
    public int pitch;
    public int roll;
    public int BladeSpinRate;





  public long logTime;



  public int  elevator;
  public int  aileron;
  public int  alfa;
  public int  betha;
  public int  standAngle;
  public int pAlt;
 public float ax;
  public float ay;
 public float az;
 public float gx;
  public int lc1;
  public int lc2;
  public int lc3;
  public int lc4;

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
        TeamID = Integer.parseInt(values[i++]);
        missionTime = Integer.parseInt(values[i++]);
        PacketCount = Integer.parseInt(values[i++]);
        Altitude = Float.parseFloat(values[i++]);
        Pressure = Integer.parseInt(values[i++]);
        temperature = Integer.parseInt(values[i++]);
        voltage = Integer.parseInt(values[i++]);
        GPSTime = Integer.parseInt(values[i++]);
        GPSLatitude=Integer.parseInt(values[i++]);
        GPSLongitude=Integer.parseInt(values[i++]);
        GPSAltitude=Float.parseFloat(values[i++]);
        pitch = Integer.parseInt(values[i++]);
        roll = Integer.parseInt(values[i++]);
        BladeSpinRate= Integer.parseInt(values[i++]);





logTime = Integer.parseInt(values[i++]);


elevator = Integer.parseInt(values[i++]);
aileron = Integer.parseInt(values[i++]);
alfa = Integer.parseInt(values[i++]);
betha = Integer.parseInt(values[i++]);
standAngle = Integer.parseInt(values[i++]);
pAlt = Integer.parseInt(values[i++]);
ax = Float.parseFloat(values[i++]);
ay = Float.parseFloat(values[i++]);
az = Float.parseFloat(values[i++]);
gx = Float.parseFloat(values[i++]);

lc1 = Integer.parseInt(values[i++]);
lc2 = Integer.parseInt(values[i++]);
lc3 = Integer.parseInt(values[i++]);
lc4 = Integer.parseInt(values[i++]);
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
