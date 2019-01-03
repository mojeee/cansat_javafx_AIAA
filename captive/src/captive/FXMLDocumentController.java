/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package captive;


import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.MapProvider;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.Location;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

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

    private Horizon horizon = null;
    //private AirCompass realCompass = null;
    private Lcd rollLCD = null;
    private Lcd pitchLCD = null;
    private Lcd altLCD = null;
    private Lcd altLCD2 = null;
    private Lcd elevatorLCD = null;
    private Lcd aofLCD = null;
    private Lcd sideSlipLCD = null;
    private Lcd linAcc1LCD = null;
    private Lcd linAcc2LCD = null;
    private Lcd linAcc3LCD = null;
    private Lcd angVelLCD1 = null;
    private Lcd angVelLCD2 = null;
    private Lcd angVelLCD3 = null;
    private Lcd headingLCD = null;
    private Lcd force1LCD = null;
    private Lcd force2LCD = null;
    private Lcd force3LCD = null;
    private Lcd force4LCD = null;
    private Lcd GPSSpeedLCD = null;
    private Lcd airSpeedLCD = null;
    private Altimeter altimeter = null;
    private Altimeter altimeter2 = null;
    private Gauge compass = null;
    private Gauge gSpeed = null;
    private Gauge airSpeed = null;
    private Gauge GPSSats = null;
    private Gauge aof = null;
    private Gauge battery = null;
    private Gauge elevator = null;
    private Gauge sideSlip = null;
    // private Gauge aileron=null;
    private Gauge temprature = null;
    private Tile mapTile = null;
    public long zeroTime = 0;
    public long packetTime = 0;
    public Lcd packetTimeLCD = null;
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
        writeToCSV("checksumStart," + "logTime," + "missionTime," + "roll," + "pitch," + "heading," + "airSpeed," + "elevator," + "aileron," + "alfa," + "beta," + "standAngle," + "pressure," + "pressureAlt," + "ax," + "ay," + "az," + "p," + "q," + "r," + "lc1," + "lc2," + "lc3," + "lc4," + "temprature," + "voltage," + "GPSSats," + "GPSLat," + "GPSLong," + "GPSAlt," + "GPSSpeed," + "checksumEnd");
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
        horizon = new Horizon();
        //horizon.setAnimated(false);
        topLeftGrid.add(horizon, 0, 0);
        //rollLCD
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
        topLeftTopRightGrid.add(rollLCD, 0, 0);

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
        topLeftTopRightGrid.add(pitchLCD, 0, 1);


        //elevator LCD
        elevatorLCD = LcdBuilder.create()
                .title("Elevator")
                .styleClass(Lcd.STYLE_CLASS_FLAT_BELIZE_HOLE)
                .decimals(0)
                .backgroundVisible(true)
                .value(0)
                .unit("deg")
                .maxMeasuredValueDecimals(0)
                .minValue(-50)
                .maxValue(+50)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
      //  topRightBottomRightGrid.add(elevatorLCD, 0, 1);

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
        sideSlipLCD = LcdBuilder.create()
                .title("Spin rate")
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
        bottomRightBottomRightGrid.add(sideSlipLCD, 0, 1);

        //Altimeter
/*        altimeter = new Altimeter();
        topLeftBottomRightGrid.add(altimeter, 0, 0);
*/        //altLCD
        altLCD = LcdBuilder.create()
                .title("Barometric Altitude")
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
        topLeftBottomRightGrid.add(altLCD, 0, 1);

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


        //compass
        compass = GaugeBuilder.create()
                .minValue(0)
                .maxValue(359)
                .startAngle(180)
                .angleRange(360)
                //  .autoScale(false)
                //.titleColor(Color.BLUE)
                .customTickLabelsEnabled(true)
                .customTickLabels("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
                .customTickLabelFontSize(35)
                .minorTickMarksVisible(true)
                .mediumTickMarksVisible(true)
                .majorTickMarksVisible(false)
                //.valueVisible(true)
                .needleType(Gauge.NeedleType.AVIONIC)
                .needleShape(Gauge.NeedleShape.ROUND)
                .knobType(Gauge.KnobType.METAL)
                .knobColor(Gauge.DARK_COLOR)
                .borderPaint(Gauge.DARK_COLOR)
                .animated(false)
                .animationDuration(500)
                .needleBehavior(Gauge.NeedleBehavior.OPTIMIZED)
                //      .foregroundBaseColor(Color.WHITE)

                .build();
        topLeftGrid.add(compass, 1, 0);
        //topLeftGrid.add(realCompass, 0,1);
        //headingLCD
        headingLCD = LcdBuilder.create()
                .title("Heading")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .decimals(0)
                .backgroundVisible(true)
                .value(0)
                .unit("deg")
                .maxMeasuredValueDecimals(0)
                .minValue(-360)
                .maxValue(360)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topLeftBottomRightGrid.add(headingLCD, 0, 0);

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
        GPSSats = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.SIMPLE)
                .sections(new Section(0, 3.1, "1", Color.web("#11632f")),
                        new Section(3.2, 5.1, "2", Color.web("#36843d")),
                        new Section(5.2, 6.1, "3", Color.web("#67a328")),
                        new Section(6.2, 8.1, "4", Color.web("#80b940")),
                        new Section(8.2, 12.1, "5", Color.web("#95c262")),
                        new Section(12.2, 20, "6", Color.web("#badf8d")))
                .title("GPS Stats")
                .threshold(50)
                .animated(false)
                .maxValue(20)
                .borderPaint(Color.GRAY)
                .thresholdColor(Color.BLACK)
                .valueColor(Color.WHITE)
                .foregroundBaseColor(Color.BLACK)
                .titleColor(Color.WHITE)
                .build();
        topRightBottomLeftGrid.add(GPSSats, 0, 0);
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


        //alfa
        aof = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.HORIZONTAL)
                // Related to Title Text
                .title("Angle of Attack") // Set the text for the title
                .titleColor(Color.BLUE)
                .needleType(Gauge.NeedleType.AVIONIC)
                // Related to Sub Title Text
                .unit("°") // Set the text for the unit
                .minValue(-20) // Set the start value of the scale
                .maxValue(+20) // Set the end value of the scale
                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                //    .foregroundBaseColor(Color.WHITE)

                .build();

       // bottomRightTopRightGrid.add(aof, 0, 0);

        //betha
        sideSlip = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.HORIZONTAL)
                // Related to Title Text
                .knobColor(Color.RED)
                .titleColor(Color.BLUE)
                .customTickLabelFontSize(12)
                .title("spin rate") // Set the text for the title
                .needleType(Gauge.NeedleType.AVIONIC)
                // Related to Sub Title Text
                .minSize(50,50)
                .maxSize(400,100)
                .unit("°") // Set the text for the unit
                .minValue(0) // Set the start value of the scale
                .maxValue(100000) // Set the end value of the scale
                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                //    .foregroundBaseColor(Color.WHITE)

                .build();

        bottomRightBottomRightGrid.add(sideSlip, 0, 0);


        elevator = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.HORIZONTAL)
                // Related to Title Text
                .knobColor(Color.RED)
                .titleColor(Color.BLUE)
                .customTickLabelFontSize(12)
                .title("Elevator") // Set the text for the title
                .needleType(Gauge.NeedleType.AVIONIC)
                // Related to Sub Title Text
                .minSize(50,50)
                .maxSize(400,100)
                .unit("°") // Set the text for the unit
                .minValue(-15) // Set the start value of the scale
                .maxValue(+15) // Set the end value of the scale
                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                //    .foregroundBaseColor(Color.WHITE)

                .build();

       // topRightBottomRightGrid.add(elevator, 0, 0);
        /* //elevator
        elevator = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.VERTICAL)
                // Related to Title Text
                .title("Elevator Angle") // Set the text for the title
                .needleType(Gauge.NeedleType.AVIONIC)
                // Related to Sub Title Text
                .unit("°") // Set the text for the unit

                // .foregroundBaseColor(Color.WHITE)
                .minValue(-15) // Set the start value of the scale
                .maxValue(+15) // Set the end value of the scale
                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                .build();
        elevator.setValue(5);
        topRightBottomRightGrid.add(elevator, 1, 0);
       */ //aileron
//  aileron = GaugeBuilder
//   .create()
//   .skinType(Gauge.SkinType.VERTICAL)
//   // Related to Title Text  
//   .title("Aileron Angle") // Set the text for the title  
//   .needleType(Gauge.NeedleType.AVIONIC)
//   // Related to Sub Title Text  
//   //  .foregroundBaseColor(Color.WHITE)
//
//  .unit("°") // Set the text for the unit  
//   .minValue(-15) // Set the start value of the scale  
//   .maxValue(+15) // Set the end value of the scale  
//   .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)  
//   .build();
//  bottomRightTopRightGrid.add(aileron, 1, 0);

        //battery
        battery = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.BATTERY)
                .barBackgroundColor(Color.GRAY)
                // Related to Title Text
                .minValue(0) // Set the start value of the scale
                .maxValue(100) // Set the end value of the scale
                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                .build();
        topRightTopRightGrid.add(battery, 0, 0);

        //temp
        temprature = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.SLIM)
                // Related to Title Text
                .title("Temperature") // Set the text for the title
                // Related to Sub Title Text
                .unit("C") // Set the text for the unit
                .minValue(-20) // Set the start value of the scale
                .maxValue(50) // Set the end value of the scale
                .value(40)

                .animationDuration(500) // Speed of the needle in milliseconds (10 - 10000 ms)
                .build();
        topRightBottomRightGrid.add(temprature, 0, 0);

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

        angVelLCD2 = LcdBuilder.create()
                .title("q")
                .unit("deg/s")
                .styleClass(Lcd.STYLE_CLASS_YELLOW_BLACK)
                .decimals(3)
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
     ///   topRightBottomLeftGrid.add(angVelLCD2, 1, 1);

        angVelLCD3 = LcdBuilder.create()
                .title("Pressure")
                .styleClass(Lcd.STYLE_CLASS_YELLOW_BLACK)
                .decimals(3)
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
        bottomRightTopRightGrid.add(angVelLCD3, 0, 1);


        //force LCD
        force1LCD = LcdBuilder.create()
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
        bottomRightTopLeftGrid.add(force1LCD, 0, 0);

        force2LCD = LcdBuilder.create()
                .title("Gps Stats")
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
        bottomRightTopLeftGrid.add(force2LCD, 1, 0);

        packetTimeLCD = LcdBuilder.create()
                .title("Mission Time")
                .styleClass(Lcd.STYLE_CLASS_FLAT_MIDNIGHT_BLUE)
                .decimals(3)
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
        bottomRightTopRightGrid.add(packetTimeLCD, 0, 0);


        force3LCD = LcdBuilder.create()
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
        bottomRightTopLeftGrid.add(force3LCD, 0, 1);

        force4LCD = LcdBuilder.create()
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
        bottomRightTopLeftGrid.add(force4LCD, 1, 1);

        airSpeedLCD = LcdBuilder.create()
                .title("Airspeed")
                .styleClass(Lcd.STYLE_CLASS_FLAT_POMEGRANATE)
                .decimals(3)
                .backgroundVisible(true)
                .value(0.0)
                .unit("m/s")
                .maxMeasuredValueDecimals(3)
                .minValue(0)
                .maxValue(99)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopLeftGrid.add(airSpeedLCD, 0, 1);


        GPSSpeedLCD = LcdBuilder.create()
                .title("Ground Speed")
                .styleClass(Lcd.STYLE_CLASS_FLAT_POMEGRANATE)
                .decimals(3)
                .backgroundVisible(true)
                .value(0.0)
                .unit("m/s")
                .maxMeasuredValueDecimals(3)
                .minValue(0)
                .maxValue(99)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
//   .maxSize(100, 60)
                .build();
        topRightTopLeftGrid.add(GPSSpeedLCD, 0,0);


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
        pitchLCD.setValue(tele.pitch);
        horizon.setPitch(Double.parseDouble(String.valueOf(tele.pitch)));
        horizon.setRoll(Double.parseDouble(String.valueOf(tele.roll)));
        rollLCD.setValue(tele.roll);
        compass.setValue(tele.heading);
        altimeter.setValue(tele.pAlt);
        altLCD.setValue(tele.pAlt);
        altLCD2.setValue(tele.GPSAlt);
        altimeter2.setValue(tele.GPSAlt);
        gSpeed.setValue(tele.GPSSpeed);
        headingLCD.setValue(tele.heading);
        GPSSpeedLCD.setValue(tele.GPSSpeed);
        airSpeedLCD.setValue(tele.airSpeed);
        GPSSats.setValue(tele.GPSSats);
        airSpeed.setValue(tele.airSpeed);
        aof.setValue(tele.alfa);
        aofLCD.setValue(tele.alfa);
        sideSlipLCD.setValue(tele.betha);
        sideSlip.setValue(tele.betha);
        elevator.setValue(tele.elevator);
        elevatorLCD.setValue(tele.elevator);
// aileron.setValue(tele.aileron);
        battery.setValue(tele.voltage);
        mapTile.setCurrentLocation(new Location(tele.GPSLat, tele.GPSLong));
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
        linAcc1LCD.setValue(tele.ax / 9.8);
        linAcc2LCD.setValue(tele.ay / 9.8);
        linAcc3LCD.setValue(tele.az / 9.8);
        angVelLCD1.setValue(tele.gx * rad2deg);
        angVelLCD2.setValue(tele.gy * rad2deg);
        angVelLCD3.setValue(tele.gz * rad2deg);

        force1LCD.setValue(tele.GPSAlt);
        force2LCD.setValue(tele.GPSSats);
        force3LCD.setValue(tele.GPSLong);
        force4LCD.setValue(tele.GPSLat);
//
//
        packetTimeLCD.setValue(tele.logTime / 1000.0);
        temprature.setValue(tele.temprature);
//if (tele.logTime % 5000 ==0) r.gc();
//realCompass.setBearing(tele.heading);

    }

}