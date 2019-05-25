/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package captive;


import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;
import eu.hansolo.enzo.lcd.LcdClock;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.MapProvider;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.Location;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Paint;

/**
 * @author Mojtaba Amini
 */
public class FXMLDocumentController implements Initializable {


    @FXML
    private GridPane mainGrid;
    @FXML
    private GridPane topLeftGrid;
    @FXML
    private GridPane topLeftTopRightGrid;
    @FXML
    private GridPane topLeftBottomRightGrid;
    @FXML
    private GridPane topRightGrid;
    @FXML
    private GridPane topRightTopLeftGrid;
    @FXML
    private GridPane topRightTopRightGrid;
    @FXML
    private GridPane topRightBottomLeftGrid;
    @FXML
    private GridPane topRightBottomRightGrid;
    @FXML
    private GridPane bottomRightGrid;
    @FXML
    private GridPane bottomRightTopLeftGrid;
    @FXML
    private GridPane bottomRightBottomLeftGrid;
    @FXML
    private GridPane bottomRightTopRightGrid;
    @FXML
    private GridPane bottomRightBottomRightGrid;
    double rad2deg = 57.295;

    private Lcd GPSTimeLCD = null;
    private Lcd AltitudeLCD = null;
    private Lcd PacketCountLCD = null;
    private Lcd PressureLCD = null;
    private Lcd TeamIDLCD = null;
    public long zeroTime = 0;
    public long packetTime = 0;
    public Lcd packetTimeLCD = null;
    private Gauge temperature = null;
    private Lcd GPSLongitudeLCD = null;
    private Lcd GPSAltitudeLCD = null;
    private Lcd GPSSatsLCD = null;
    private Lcd CameraDirectionLCD = null;






    private Horizon horizon = null;
    //private AirCompass realCompass = null;
    private Lcd rollLCD = null;
    private Lcd pitchLCD = null;
    private Lcd altLCD = null;
    private Lcd altLCD2 = null;
    private Lcd BladeSpinRateLCD = null;
    private Lcd aofLCD = null;
    private Lcd sideSlipLCD = null;
    private Lcd linAcc1LCD = null;
    private Lcd linAcc2LCD = null;
    private Lcd linAcc3LCD = null;
    private Lcd angVelLCD1 = null;


    private Lcd GPSLatitudeLCD = null;

    private Altimeter altimeter = null;
    private Altimeter altimeter2 = null;
    private Gauge compass = null;
    private Gauge gSpeed = null;
    private Gauge aof = null;
    private Gauge battery = null;
    private Gauge elevator = null;
    private Gauge sideSlip = null;
    // private Gauge aileron=null;
    private Tile mapTile = null;

    Runtime r;
    // XYChart.Series < Number, Number > axSeries;
// XYChart.Series < Number, Number > aySeries;
// XYChart.Series < Number, Number > azSeries;
// XYChart.Series < Number, Number > gxSeries;
// XYChart.Series < Number, Number > gySeries;
// XYChart.Series < Number, Number > gzSeries;
// XYChart.Series < Number, Number > lc1Series;
// XYChart.Series < Number, Number > lc2Series;
// XYChart.Series < Number, Number > lc3Series;
// XYChart.Series < Number, Number > lc4Series;
// @FXML LineChart aSeriesChart;
// @FXML LineChart gSeriesChart;
// @FXML LineChart lcSeriesChart;
    @FXML
    private Button connectBtn;
    @FXML
    private Button disconnectBtn;
    // @FXML private GridPane linAccVBox;
// @FXML private GridPane angVelLCDBox;
// @FXML private GridPane forceLCDBox;
// @FXML private NumberAxis aSeriesX;
// @FXML private NumberAxis gSeriesX;
// @FXML private NumberAxis lcSeriesX;
    boolean updating = false;
    Telemetry tele = null;
    @FXML
    private Button recordCSVBtn;


    boolean exportCSV = false;
    String CSVName = null;
    //@FXML private TextArea logText;
    //Scanner scanner;
    @FXML
    private ChoiceBox comList;
    SerialPort[] comPort = null;
    SerialPort Selected = null;

    @FXML
    void refreshPortsList() {
        comList.getItems().clear();
        comPort = new SerialPort[10];
        comPort = SerialPort.getCommPorts();
        // System.out.print(comPort[0].getDescriptivePortName());
        // comList.hide();

        for (SerialPort item : comPort) {
            comList.getItems().add(item.getDescriptivePortName());
        }
        // comLists.getItems().addAll(comPort);
    }

    @FXML
    void disconnectBtnClick() {
        Selected.removeDataListener();
        Selected.closePort();
        // connectBtn.setVisible(true);
        disconnectBtn.setVisible(false);

    }

    @FXML
    private void recordCSVBtnAction() {
        CSVName = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss'.csv'").format(new Date());
        exportCSV = true;
        recordCSVBtn.setText("Recording...");
        recordCSVBtn.setDisable(true);
        writeToCSV("checksumStart," + "TeamID," + "logTime," + "missionTime," + "roll," + "pitch," + "airSpeed," + "elevator," + "aileron," + "alfa," + "beta," + "standAngle," + "pressure," + "pressureAlt," + "ax," + "ay," + "az," + "p," + "q," + "r," + "lc1," + "lc2," + "lc3," + "lc4," + "temprature," + "voltage," + "GPSSats," + "GPSLat," + "GPSLong," + "GPSAlt," + "GPSSpeed," + "checksumEnd");
    }


    @FXML
    private void zeroTime() throws ConcurrentModificationException {
        zeroTime = System.currentTimeMillis();
//      while(updating); lc1Series.getData().clear();
//      while(updating);  lc2Series.getData().clear();
//     while(updating); lc3Series.getData().clear();
//     while(updating); lc4Series.getData().clear();
//     while(updating); axSeries.getData().clear();
//     while(updating);  aySeries.getData().clear();
//     while(updating);  azSeries.getData().clear();
//     while(updating); gxSeries.getData().clear();
//     while(updating); gySeries.getData().clear();
//     while(updating); gzSeries.getData().clear();
    }

    @FXML
    private void ConnectCom() throws Exception {
        int selectedIndex = comList.getSelectionModel().getSelectedIndex();
        Selected = comPort[selectedIndex];
        Selected.setBaudRate(38400);
        if (!Selected.openPort()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection Failed!Please retry...", ButtonType.CLOSE);
            alert.show();
            return;
        }
        connectBtn.setVisible(false);
        disconnectBtn.setVisible(true);
        StringBuilder temp;
        temp = new StringBuilder();
        zeroTime = System.currentTimeMillis();
        //scanner = new Scanner(Selected.getInputStream());
        Selected.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            @SuppressWarnings("empty-statement")
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[Selected.bytesAvailable()];
                //int numRead = Selected.readBytes(newData, newData.length);
                Selected.readBytes(newData, newData.length);
                try {
                    for (int i = 0; i < newData.length; i++) {
                        //  if(!(Character.isDigit((char) newData[i])|| (char) newData[i] == ',' || (char) newData[i]=='\n') ) continue;

                        temp.append((char) newData[i]);

                        if ((char) newData[i] == '\n') {

                            if (tele.checkSumCheck(temp.toString())) {
                                //logText.appendText(temp.toString());
                                while (updating) ;
                                updating = true;
                                packetTime = System.currentTimeMillis() - zeroTime;
                                StringBuilder pt = new StringBuilder();
                                pt = pt.insert(0, packetTime);
                                pt.append(',');

                                String tempTelem;
                                tempTelem = new String(insert(temp.toString(), pt.toString(), 4));
                                pt.setLength(0);

                                if (exportCSV) {
                                    writeToCSV(tempTelem);
                                }
                                //System.out.println(temp.toString());

                                tele.initString(tempTelem);
                                tele.parseString();

                                try {
                                    updateUI();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                tempTelem = null;
                            }
                            temp.setLength(0);

                            updating = false;
                        }

                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }

        });


    }

    public static String insert(String bag, String marble, int index) {
        String bagBegin = bag.substring(0, index);
        String bagEnd = bag.substring(index);
        return bagBegin + marble + bagEnd;
    }

    private void writeToCSV(String temp) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(new File(System.getProperty("user.home") + "/Desktop/" + CSVName), true /* append = true */));
            //writer = new PrintWriter(new FileOutputStream(new File(CSVName), true /* append = true */ ));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.print(temp);
        writer.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tele = new Telemetry();
        r = Runtime.getRuntime();

//realCompass = new AirCompass();
//realCompass.setAnimated(false);
        // Horizon Creation
 /*       horizon = new Horizon();
        //horizon.setAnimated(false);
        topRightTopLeftGrid.add(horizon, 0, 0);
     */   //rollLCD
        rollLCD = LcdBuilder.create()
                .title("Roll")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .decimals(0)
                .backgroundVisible(true)
                .value(0)
                .maxMeasuredValueDecimals(0)
                .minValue(-180)
                .unit("deg")
                .maxValue(180)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        bottomRightTopRightGrid.add(rollLCD, 1, 0);

        //pitchLCD
        pitchLCD = LcdBuilder.create()
                .title("Pitch")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .decimals(0)
                .backgroundVisible(true)
                .value(0)
                .unit("deg")
                .maxMeasuredValueDecimals(0)
                .minValue(-180)
                .maxValue(180)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        bottomRightTopRightGrid.add(pitchLCD, 1, 1);


        //pitchLCD
        CameraDirectionLCD = LcdBuilder.create()
                .title("Camera Direction")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .decimals(0)
                .backgroundVisible(true)
                .value(0)
                .unit("deg")
                .maxMeasuredValueDecimals(0)
                .minValue(-180)
                .maxValue(180)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopRightGrid.add(CameraDirectionLCD, 1, 1);


        //elevator LCD
        BladeSpinRateLCD = LcdBuilder.create()
                .title("Blade Spin Rate")
                .styleClass(Lcd.STYLE_CLASS_FLAT_BELIZE_HOLE)
                .decimals(0)
                .backgroundVisible(true)
                .value(0)
                .unit("rad/s")
                .maxMeasuredValueDecimals(0)
                .minValue(-50)
                .maxValue(+50)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        bottomRightTopRightGrid.add(BladeSpinRateLCD, 0, 1);

        //aof LCD
        aofLCD = LcdBuilder.create()
                .title("Angle of Attack")
                .styleClass(Lcd.STYLE_CLASS_FLAT_BELIZE_HOLE)
                .decimals(0)
                .unit("deg")
                .backgroundVisible(true)
                .value(0)
                .maxMeasuredValueDecimals(0)
                .minValue(-20)
                .maxValue(+20)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
       // bottomRightTopRightGrid.add(aofLCD, 0, 1);

        //sideSlip LCD
       /* BladeSpinRateLCD = LcdBuilder.create()
                .title("Blade Spin rate")
                .styleClass(Lcd.STYLE_CLASS_FLAT_BELIZE_HOLE)
                .decimals(0)
                .backgroundVisible(true)
                .value(0)
                .unit("rad/s")
                .maxMeasuredValueDecimals(0)
                .minValue(-90)
                .maxValue(+90)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopRightGrid.add(BladeSpinRateLCD, 0, 3);
*/
        //Altimeter
/*        altimeter = new Altimeter();
        topLeftBottomRightGrid.add(altimeter, 0, 0);
*/        //altLCD
        AltitudeLCD = LcdBuilder.create()
                .title("Altitude")
                .styleClass(Lcd.STYLE_CLASS_YELLOW_BLACK)
                .decimals(0)
                .unit("m")
                .backgroundVisible(true)
                .value(0)
                .maxMeasuredValueDecimals(0)
                .maxValue(10000)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopRightGrid.add(AltitudeLCD, 0, 2);

        //Altimeter
   /*     altimeter2 = new Altimeter();
        topLeftBottomRightGrid.add(altimeter2, 1, 0);
  */      //altLCD2
        altLCD2 = LcdBuilder.create()
                .title("GPS Altitude")
                .styleClass(Lcd.STYLE_CLASS_BLACK_YELLOW)
                .decimals(0)
                .unit("m")
                .backgroundVisible(true)
                .value(0)
                .maxMeasuredValueDecimals(0)
                .maxValue(10000)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
      //  topLeftBottomRightGrid.add(altLCD2, 0, 2);



        //headingLCD
        TeamIDLCD = LcdBuilder.create()
                .title("Team ID")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .decimals(0)
                .backgroundVisible(true)
                .value(0)
                .maxMeasuredValueDecimals(0)
                .maxValue(2000)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopRightGrid.add(TeamIDLCD, 0, 0);

        //Ground Speed
  /*      gSpeed = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.MODERN)
                // Related to Title Text
                .title("Ground Speed") // Set the text for the title
                .unit("m/s") // Set the text for the unit
                .minValue(0.0) // Set the start value of the scale
                .decimals(1)
                .maxValue(80) // Set the end value of the scale
                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                .build();
        topRightTopLeftGrid.add(gSpeed, 1, 0);
*/
        //AirSpeed
  /*      airSpeed = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.MODERN)
                // Related to Title Text
                .title("Air Speed") // Set the text for the title
                // Related to Sub Title Text
                .unit("m/s") // Set the text for the unit
                .minValue(0.0) // Set the start value of the scale
                .decimals(1)
                .maxValue(80) // Set the end value of the scale
                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                .build();
        topRightTopLeftGrid.add(airSpeed, 0, 0);
*/

        //GPSSats

        //map
        TileBuilder tileBuilder = TileBuilder.create();
        tileBuilder.skinType(SkinType.MAP);
        tileBuilder.title("Live Location");
        tileBuilder.currentLocation(new Location(35.701840, 51.352514, "Home"));
        tileBuilder.mapProvider(MapProvider.STREET);
        tileBuilder.animated(false);
        tileBuilder.oldValueVisible(true);
        tileBuilder.smoothing(true);
        mapTile = tileBuilder.build();
        mainGrid.add(mapTile, 0, 1);



        //battery
        battery = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.BATTERY)
                .barBackgroundColor(Color.rgb(20,20,20))
                .valueColor(Color.WHITE)
                // Related to Title Text
                .minValue(0) // Set the start value of the scale
                .maxValue(100) // Set the end value of the scale
                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                .build();
        topRightTopRightGrid.add(battery, 1, 2);

        //temp

        temperature = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.SLIM)
                // Related to Title Text
                .title("Temperature") // Set the text for the title
                .foregroundBaseColor(Color.BLACK)
                .barBackgroundColor(Color.BLACK)
                // Related to Sub Title Text
                .unit("C") // Set the text for the unit
                .minValue(-20) // Set the start value of the scale
                .maxValue(50) // Set the end value of the scale
                .value(40)
                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                .build();
        topRightTopRightGrid.add(temperature, 1, 3);

        //linear Acc LCD
        linAcc1LCD = LcdBuilder.create()
                .title("a_X")
                .styleClass(Lcd.STYLE_CLASS_YELLOW_BLACK)
                .decimals(3)
                .unit("g")
                .backgroundVisible(true)
                .value(0.0)
                .maxMeasuredValueDecimals(3)
                .minValue(-20)
                .maxValue(20)
                .foregroundShadowVisible(false)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
       // topRightBottomLeftGrid.add(linAcc1LCD, 0, 0);
        //linear Acc LCD
        linAcc2LCD = LcdBuilder.create()
                .title("a_Y")
                .styleClass(Lcd.STYLE_CLASS_YELLOW_BLACK)
                .decimals(3)
                .backgroundVisible(true)
                .value(0.0)
                .unit("g")
                .maxMeasuredValueDecimals(3)
                .minValue(-20)
                .maxValue(20)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
      //  topRightBottomLeftGrid.add(linAcc2LCD, 0, 1);
        linAcc3LCD = LcdBuilder.create()
                .title("a_Z")
                .styleClass(Lcd.STYLE_CLASS_YELLOW_BLACK)
                .decimals(3)
                .unit("g")
                .backgroundVisible(true)
                .value(0.0)
                .maxMeasuredValueDecimals(3)
                .minValue(-20)
                .maxValue(20)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
       // topRightBottomLeftGrid.add(linAcc3LCD, 0, 2);

        //gyro lcd
        angVelLCD1 = LcdBuilder.create()
                .title("p")
                .unit("Deg/s")
                .styleClass(Lcd.STYLE_CLASS_YELLOW_BLACK)
                .decimals(3)
                .backgroundVisible(true)
                .value(0)
                .maxMeasuredValueDecimals(3)
                .minValue(-200)
                .maxValue(200)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
      //  topRightBottomLeftGrid.add(angVelLCD1, 1, 0);

        PacketCountLCD = LcdBuilder.create()
                .title("Packet Count")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .backgroundVisible(true)
                .value(00)
                .maxValue(1000)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopRightGrid.add(PacketCountLCD, 1, 0);

        PressureLCD = LcdBuilder.create()
                .title("Pressure")
                .styleClass(Lcd.STYLE_CLASS_YELLOW_BLACK)
                .decimals(1)
                .unit("Pa")
                .backgroundVisible(true)
                .value(00)
                .maxMeasuredValueDecimals(3)
                .minValue(-200)
                .maxValue(200)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopRightGrid.add(PressureLCD, 0, 3);


        //force LCD
        GPSAltitudeLCD = LcdBuilder.create()
                .title("GPS Altitude")
                .unit("m")
                .styleClass(Lcd.STYLE_CLASS_FLAT_GREEN_SEA)
                .decimals(0)
                .backgroundVisible(true)
                .maxMeasuredValueDecimals(0)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .maxValue(100000)
                .minValue(-100000)
                .build();
        bottomRightTopLeftGrid.add(GPSAltitudeLCD, 0, 0);

        GPSSatsLCD = LcdBuilder.create()
                .title("Gps Sats")
                .styleClass(Lcd.STYLE_CLASS_FLAT_GREEN_SEA)
                .decimals(0)
                .backgroundVisible(true)
                .maxMeasuredValueDecimals(0)

                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .maxValue(100000)

                .minValue(-100000)

                .build();
        bottomRightTopLeftGrid.add(GPSSatsLCD, 1, 0);

        packetTimeLCD = LcdBuilder.create()
                .title("Mission Time")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .decimals(1)
                .unit("s")
                .backgroundVisible(true)
                .maxMeasuredValueDecimals(3)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .maxValue(10000)
                .minValue(0)
                .build();
        topRightTopRightGrid.add(packetTimeLCD, 0, 1);


        GPSLongitudeLCD = LcdBuilder.create()
                .title("GPS LONGITUDE")
                .styleClass(Lcd.STYLE_CLASS_FLAT_GREEN_SEA)
                .decimals(0)

                .backgroundVisible(true)
                .maxMeasuredValueDecimals(0)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .maxValue(100000)

                .minValue(-100000)

                .build();
        bottomRightTopLeftGrid.add(GPSLongitudeLCD, 0, 1);

        GPSLatitudeLCD = LcdBuilder.create()
                .title("GPS LATITUDE")
                .styleClass(Lcd.STYLE_CLASS_FLAT_GREEN_SEA)
                .decimals(0)

                .backgroundVisible(true)
                .maxMeasuredValueDecimals(0)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .maxValue(100000)

                .minValue(-100000)

                .build();
        bottomRightTopLeftGrid.add(GPSLatitudeLCD, 1, 1);




        GPSTimeLCD = LcdBuilder.create()
                .title("GPS Time (UTC)")
                .styleClass(LcdClock.STYLE_CLASS_FLAT_GREEN_SEA)
                .backgroundVisible(true)
                .value(0)
                .maxMeasuredValueDecimals(3)
                .minValue(0)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
//   .maxSize(100, 60)
                .build();
        bottomRightTopRightGrid.add(GPSTimeLCD, 0,0);

// first plot************************************************************************************
        final NumberAxis xAxis_missionTime = new NumberAxis(); // we are gonna plot against time
        final NumberAxis yAxis_Pressure = new NumberAxis();
        xAxis_missionTime.setLabel("Time(s)");
        xAxis_missionTime.setAnimated(false); // axis animations are removed
        yAxis_Pressure.setLabel("Speed ( m/s )");
        yAxis_Pressure.setAnimated(false); // axis animations are removed

        //creating the line chart with two axis created above
        final LineChart<Number, Number> lineChart_Pressure = new LineChart<>(xAxis_missionTime, yAxis_Pressure);
        lineChart_Pressure.setTitle("Payload Speed Realtime Plot");
        lineChart_Pressure.setAnimated(false); // disable animations
        // show the stage
        //defining a series to display data
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Series");
        lineChart_Pressure.setLegendVisible(false);
        lineChart_Pressure.setCreateSymbols(false);
        // add series to chart
        lineChart_Pressure.getData().add(series);
        // setup scene
//            Scene scene = new Scene(lineChart, 800, 600);
        // this is used to display time in HH:mm:ss format
       // final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");

        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            double y =  Math.random();
            double x =  Math.random();

            // Update the chart
            Platform.runLater(() -> {
                // get current time
               // Date now = new Date();
                // put random number with current time
                series.getData().add(new XYChart.Data<>(x, y));
            });
        }, 0, 100, TimeUnit.MILLISECONDS);
        //  root.getChildren().addAll();

        topRightGrid.add(lineChart_Pressure, 0 , 0);

// second plot************************************************************************************
       final NumberAxis xAxis_missionTime2 = new NumberAxis(); // we are gonna plot against time
        final NumberAxis yAxis_Temperature = new NumberAxis();
        xAxis_missionTime2.setLabel("Time(s)");
        xAxis_missionTime2.setAnimated(false); // axis animations are removed
        yAxis_Temperature.setLabel("Speed ( m/s )");
        yAxis_Temperature.setAnimated(false); // axis animations are removed

        //creating the line chart with two axis created above
        final LineChart<Number, Number> lineChart_Temperature = new LineChart<>(xAxis_missionTime2, yAxis_Temperature);
        lineChart_Temperature.setTitle("Payload Speed Realtime Plot");
        lineChart_Temperature.setAnimated(false); // disable animations
        // show the stage
        //defining a series to display data
        XYChart.Series<Number, Number> series_Temperature = new XYChart.Series<>();
        series.setName("Data Series");
        lineChart_Temperature.setLegendVisible(false);
        lineChart_Temperature.setCreateSymbols(false);
        // add series to chart
        lineChart_Temperature.getData().add(series_Temperature);
        // setup scene
//            Scene scene = new Scene(lineChart, 800, 600);
        // this is used to display time in HH:mm:ss format
        // final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");

        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService_Temperature;
        scheduledExecutorService_Temperature = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService_Temperature.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            double y =  Math.random();
            double x =  Math.random();

            // Update the chart
            Platform.runLater(() -> {
                // get current time
                // Date now = new Date();
                // put random number with current time
                series_Temperature.getData().add(new XYChart.Data<>(x, y));
            });
        }, 0, 10, TimeUnit.SECONDS);
        //  root.getChildren().addAll();

        topLeftGrid.add(lineChart_Temperature, 1 , 0);

        // Third plot************************************************************************************
        final NumberAxis xAxis_missionTime3 = new NumberAxis(); // we are gonna plot against time
        final NumberAxis yAxis_Altitude = new NumberAxis();
        xAxis_missionTime3.setLabel("Time(s)");
        xAxis_missionTime3.setAnimated(false); // axis animations are removed
        yAxis_Altitude.setLabel("Speed ( m/s )");
        yAxis_Altitude.setAnimated(false); // axis animations are removed

        //creating the line chart with two axis created above
        final LineChart<Number, Number> lineChart_Altitude = new LineChart<>(xAxis_missionTime3, yAxis_Altitude);
        lineChart_Altitude.setTitle("Payload Speed Realtime Plot");
        lineChart_Altitude.setAnimated(false); // disable animations
        // show the stage
        //defining a series to display data
        XYChart.Series<Number, Number> series_Altitude = new XYChart.Series<>();
        series.setName("Data Series");
        lineChart_Altitude.setLegendVisible(false);
        lineChart_Altitude.setCreateSymbols(false);
        // add series to chart
        lineChart_Altitude.getData().add(series_Altitude);
        // setup scene
//            Scene scene = new Scene(lineChart, 800, 600);
        // this is used to display time in HH:mm:ss format
        // final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");

        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService_Altitude;
        scheduledExecutorService_Altitude = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService_Altitude.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            double y =  Math.random();
            double x =  Math.random();

            // Update the chart
            Platform.runLater(() -> {
                // get current time
                // Date now = new Date();
                // put random number with current time
                series_Altitude.getData().add(new XYChart.Data<>(x, y));
            });
        }, 0, 10, TimeUnit.SECONDS);
        //  root.getChildren().addAll();

        topLeftGrid.add(lineChart_Altitude, 0 , 0);

        Image image = new Image("captive/icon.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(130);
        imageView.setFitWidth(130);
       // imageView.setX(500);
       // imageView.setY(500);
        bottomRightBottomRightGrid.add(imageView, 0 , 0 );






        //charts

//  axSeries = new XYChart.Series < Number, Number > ();
//  aySeries = new XYChart.Series < Number, Number > ();
//  azSeries = new XYChart.Series < Number, Number > ();
//  axSeries.setName("a_x(m/s^2)");
//  aySeries.setName("a_y(m/s^2)");
//  azSeries.setName("a_z(m/s^2)");
//  aSeriesChart.getData().add(axSeries);
//  aSeriesChart.getData().add(aySeries);
//  aSeriesChart.getData().add(azSeries);
//  aSeriesChart.setCreateSymbols(false);
//
//  aSeriesChart.getStyleClass().add("thick-chart");
//
//
//
//  gxSeries = new XYChart.Series < Number, Number > ();
//  gySeries = new XYChart.Series < Number, Number > ();
//  gzSeries = new XYChart.Series < Number, Number > ();
//  gxSeries.setName("p(Rad/s)");
//  gySeries.setName("q(Rad/s)");
//  gzSeries.setName("r(Rad/s)");
//  gSeriesChart.getData().add(gxSeries);
//  gSeriesChart.getData().add(gySeries);
//  gSeriesChart.getData().add(gzSeries);
//  gSeriesChart.setCreateSymbols(false);
//  gSeriesChart.getStyleClass().add("thick-chart");
//
//
//  lc1Series = new XYChart.Series < Number, Number > ();
//  lc2Series = new XYChart.Series < Number, Number > ();
//  lc3Series = new XYChart.Series < Number, Number > ();
//  lc4Series = new XYChart.Series < Number, Number > ();
//
//  lc1Series.setName("Force 1(g)");
//  lc2Series.setName("Force 2(g)");
//  lc3Series.setName("Force 3(g)");
//  lc4Series.setName("Force 4(g)");
//  lcSeriesChart.getData().add(lc1Series);
//  lcSeriesChart.getData().add(lc2Series);
//  lcSeriesChart.getData().add(lc3Series);
//  lcSeriesChart.getData().add(lc4Series);
//  lcSeriesChart.setCreateSymbols(false);
//  lcSeriesChart.getStyleClass().add("thick-chart");
//
//  //lcSeriesChart.setLegendVisible(true);
//  aSeriesX.setForceZeroInRange(false);
//  gSeriesX.setForceZeroInRange(false);
//  lcSeriesX.setForceZeroInRange(false);
//  //   aSeriesX.setAutoRanging(true);
//  //  gSeriesX.setAutoRanging(true);
//  // lcSeriesX.setAutoRanging(true);
    }

    public void updateUI() throws Exception {

        packetTimeLCD.setValue(tele.missionTime);
        PacketCountLCD.setValue(tele.PacketCount);
        TeamIDLCD.setValue(tele.TeamID);
        PressureLCD.setValue(tele.Pressure);
        temperature.setValue(tele.temperature);
        battery.setValue(tele.voltage);
        GPSTimeLCD.setValue(tele.GPSTime);
        GPSLatitudeLCD.setValue(tele.GPSLatitude);
        GPSLongitudeLCD.setValue(tele.GPSLongitude);
        GPSAltitudeLCD.setValue(tele.GPSAltitude);
        GPSSatsLCD.setValue(tele.GPSSats);
        pitchLCD.setValue(tele.pitch);
        rollLCD.setValue(tele.roll);
        BladeSpinRateLCD.setValue(tele.BladeSpinRate);
        mapTile.setCurrentLocation(new Location(tele.GPSLatitude, tele.GPSLongitude));
        CameraDirectionLCD.setValue(tele.CameraDirection);








/*
        horizon.setPitch(Double.parseDouble(String.valueOf(tele.pitch)));
        horizon.setRoll(Double.parseDouble(String.valueOf(tele.roll)));

//        altimeter.setValue(tele.pAlt);
//        altLCD.setValue(tele.pAlt);
        altLCD2.setValue(tele.GPSAlt);
        altimeter2.setValue(tele.GPSAlt);
        gSpeed.setValue(tele.GPSSpeed);
//*/        //aof.setValue(tele.alfa);
//        aofLCD.setValue(tele.alfa);
//        sideSlipLCD.setValue(tele.betha);
  //      sideSlip.setValue(tele.betha);
//        elevator.setValue(tele.elevator);
// aileron.setValue(tele.aileron);
//
////  //charts
////  axSeries.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.ax));
////  aySeries.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.ay));
////  azSeries.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.az));
////  gxSeries.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.gx));
////  gySeries.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.gy));
////  gzSeries.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.gz));
////  
////  lc1Series.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.lc1));
////  lc2Series.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.lc2));
////  lc3Series.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.lc3));
////  lc4Series.getData().add(new XYChart.Data < Number, Number > (tele.logTime / 1000.0, tele.lc4));
//  
 /*       linAcc1LCD.setValue(tele.ax / 9.8);
        linAcc2LCD.setValue(tele.ay / 9.8);
        linAcc3LCD.setValue(tele.az / 9.8);
        angVelLCD1.setValue(tele.gx * rad2deg);
*/


//
//

//if (tele.logTime % 5000 ==0) r.gc();
//realCompass.setBearing(tele.heading);

    }

}