package captive;


public class Telemetry {
    public int checkSumStart=201;
    public int TeamID;
    public int missionTime;
    public float Altitude;
    public int PacketCount;
    public float Pressure;
    public float temperature;
    public float voltage;
    public long GPSTime;
    public double GPSLatitude;
    public double GPSLongitude;
    public float GPSAltitude;
    public int pitch;
    public int roll;
    public int BladeSpinRate;
    public int GPSSats;
    public int CameraDirection;
    public int checkSumEnd=254;
    public String payloadString;

public void initString(String s){
  payloadString =  new String(s.substring(0, s.length()-1));
}
public void parseString(){
    String[] values = payloadString.split(",");
    int i =1;
        checkSumStart = Integer.parseInt(values[i++]);
        TeamID = Integer.parseInt(values[i++]);
        missionTime = Integer.parseInt(values[i++]);
        PacketCount = Integer.parseInt(values[i++]);
        Altitude = Float.parseFloat(values[i++]);
        Pressure = Float.parseFloat(values[i++]);
        temperature = Float.parseFloat(values[i++]);
        voltage = Float.parseFloat(values[i++]);
        GPSTime = Long.parseLong(values[i++]);
        GPSLatitude=Double.parseDouble(values[i++]);
        GPSLongitude=Double.parseDouble(values[i++]);
        GPSAltitude=Float.parseFloat(values[i++]);
        pitch = Integer.parseInt(values[i++]);
        roll = Integer.parseInt(values[i++]);
        BladeSpinRate= Integer.parseInt(values[i++]);
        GPSSats = Integer.parseInt(values[i++]);
        CameraDirection = Integer.parseInt(values[i++]);
        checkSumEnd = Integer.parseInt(values[i++]);


}
public boolean checkSumCheck(String temp){
    if (temp.startsWith("201,")&& temp.substring(0, temp.length()-1).endsWith(",254") && findRepeats(temp,',') == 17){
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
