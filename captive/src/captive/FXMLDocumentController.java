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

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.MapProvider;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.Location;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

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


/**
 * @author Mojtaba Amini
 */
public class FXMLDocumentController implements Initializable {


    @FXML
    private GridPane mainGrid;
    @FXML
    private GridPane topLeftGrid;
    @FXML
    private GridPane topRightGrid;
    @FXML
    private GridPane topRightTopRightGrid;
    @FXML
    private GridPane bottomRightTopLeftGrid;
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
    private Lcd rollLCD = null;
    private Lcd pitchLCD = null;
    private Lcd BladeSpinRateLCD = null;
    private Lcd GPSLatitudeLCD = null;
    private Gauge battery = null;


    private Tile mapTile = null;

    Runtime r;

    @FXML
    private Button connectBtn;
    @FXML
    private Button disconnectBtn;

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

    }

    @FXML
    private void ConnectCom() throws Exception {
        int selectedIndex = comList.getSelectionModel().getSelectedIndex();
        Selected = comPort[selectedIndex];
        Selected.setBaudRate(2400);
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
                .unit("RPM")
                .maxMeasuredValueDecimals(0)
                .minValue(0)
                .maxValue(+500000)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        bottomRightTopRightGrid.add(BladeSpinRateLCD, 0, 1);

        AltitudeLCD = LcdBuilder.create()
                .title("Altitude")
                .styleClass(Lcd.STYLE_CLASS_YELLOW_BLACK)
                .decimals(1)
                .unit("m")
                .backgroundVisible(true)
                .value(0)
                .maxMeasuredValueDecimals(0)
                .maxValue(100000)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopRightGrid.add(AltitudeLCD, 0, 2);


        TeamIDLCD = LcdBuilder.create()
                .title("Team ID")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .decimals(0)
                .backgroundVisible(true)
                .value(0)
                .maxMeasuredValueDecimals(0)
                .maxValue(20000)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopRightGrid.add(TeamIDLCD, 0, 0);


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

        PacketCountLCD = LcdBuilder.create()
                .title("Packet Count")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .backgroundVisible(true)
                .value(00)
                .maxValue(1000000)
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
                .minValue(0)
                .maxValue(10000000)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        topRightTopRightGrid.add(PressureLCD, 0, 3);

        GPSAltitudeLCD = LcdBuilder.create()
                .title("GPS Altitude")
                .unit("m")
                .styleClass(Lcd.STYLE_CLASS_FLAT_GREEN_SEA)
                .decimals(1)
                .backgroundVisible(true)
                .maxMeasuredValueDecimals(0)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .maxValue(10000000)
                .minValue(0)
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
                .minValue(0)
                .build();
        bottomRightTopLeftGrid.add(GPSSatsLCD, 1, 0);

        packetTimeLCD = LcdBuilder.create()
                .title("Mission Time")
                .styleClass(Lcd.STYLE_CLASS_GREEN_DARKGREEN)
                .decimals(0)
                .unit("s")
                .backgroundVisible(true)
                .maxMeasuredValueDecimals(3)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .maxValue(1000000000)
                .minValue(0)
                .build();
        topRightTopRightGrid.add(packetTimeLCD, 0, 1);


        GPSLongitudeLCD = LcdBuilder.create()
                .title("GPS LONGITUDE")
                .styleClass(Lcd.STYLE_CLASS_FLAT_GREEN_SEA)
                .decimals(6)
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
                .decimals(6)
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
                .build();
        bottomRightTopRightGrid.add(GPSTimeLCD, 0,0);

// Altitude Plot************************************************************************************
        final NumberAxis xAxis_missionTime = new NumberAxis(); // we are gonna plot against time
        final NumberAxis yAxis_Altitude = new NumberAxis();
        xAxis_missionTime.setLabel("Time(s)");
        xAxis_missionTime.setAnimated(false); // axis animations are removed
        yAxis_Altitude.setLabel("Altitude (m)");
        yAxis_Altitude.setAnimated(false); // axis animations are removed

        //creating the line chart with two axis created above
        final LineChart<Number, Number> lineChart_Altitude = new LineChart<>(xAxis_missionTime, yAxis_Altitude);
        lineChart_Altitude.setTitle("Altitude Realtime Plot");
        lineChart_Altitude.setAnimated(false); // disable animations
        // show the stage
        //defining a series to display data
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Series");
        lineChart_Altitude.setLegendVisible(false);
        lineChart_Altitude.setCreateSymbols(false);
        // add series to chart
        lineChart_Altitude.getData().add(series);
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
            double y =  tele.Altitude;
            double x =  tele.missionTime;

            // Update the chart
            Platform.runLater(() -> {
                // get current time
               // Date now = new Date();
                // put random number with current time
                series.getData().add(new XYChart.Data<>(x, y));
            });
        }, 0, 100, TimeUnit.MILLISECONDS);
        //  root.getChildren().addAll();

        topRightGrid.add(lineChart_Altitude, 0 , 0);

// roll and pitch plot************************************************************************************
       final NumberAxis xAxis_missionTime2 = new NumberAxis(); // we are gonna plot against time
        final NumberAxis yAxis_Angle = new NumberAxis();
        xAxis_missionTime2.setLabel("Time(s)");
        xAxis_missionTime2.setAnimated(false); // axis animations are removed
        yAxis_Angle.setLabel("(deg/s)");
        yAxis_Angle.setAnimated(false); // axis animations are removed
        //creating the line chart with two axis created above
        final LineChart<Number, Number> lineChart_Angle = new LineChart<>(xAxis_missionTime2, yAxis_Angle);
        lineChart_Angle.setTitle("Roll & Pitch Realtime Plot");
        lineChart_Angle.setAnimated(false); // disable animations
        // show the stage
        //defining a series to display data
        XYChart.Series<Number, Number> series_roll = new XYChart.Series<>();
        series_roll.setName("Roll");
        XYChart.Series<Number, Number> series_pitch = new XYChart.Series<>();
        series_pitch.setName("Pitch");
        lineChart_Angle.setLegendVisible(true);
        lineChart_Angle.setCreateSymbols(false);
        // add series to chart
        lineChart_Angle.getData().add(series_roll);
        lineChart_Angle.getData().add(series_pitch);
        // setup scene
//            Scene scene = new Scene(lineChart, 800, 600);
        // this is used to display time in HH:mm:ss format
        // final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");

        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService_Angle;
        scheduledExecutorService_Angle = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService_Angle.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            double roll =  tele.roll;
            double pitch =  tele.pitch;
            double x =  tele.missionTime;

            // Update the chart
            Platform.runLater(() -> {
                // get current time
                // Date now = new Date();
                // put random number with current time
                series_roll.getData().add(new XYChart.Data<>(x, roll));
                series_pitch.getData().add(new XYChart.Data<>(x, pitch));
            });
        }, 0, 100, TimeUnit.MILLISECONDS);
        //  root.getChildren().addAll();

        topLeftGrid.add(lineChart_Angle, 1 , 0);

        // Third plot************************************************************************************
        final NumberAxis xAxis_missionTime3 = new NumberAxis(); // we are gonna plot against time
        final NumberAxis yAxis_BladeSpinRate = new NumberAxis();
        xAxis_missionTime3.setLabel("Time(s)");
        xAxis_missionTime3.setAnimated(false); // axis animations are removed
        yAxis_BladeSpinRate.setLabel("Blade Spin Rate (RPM)");
        yAxis_BladeSpinRate.setAnimated(false); // axis animations are removed

        //creating the line chart with two axis created above
        final LineChart<Number, Number> lineChart_BladeSpinRate = new LineChart<>(xAxis_missionTime3, yAxis_BladeSpinRate);
        lineChart_BladeSpinRate.setTitle("Blade Spin Rate Realtime Plot");
        lineChart_BladeSpinRate.setAnimated(false); // disable animations
        // show the stage
        //defining a series to display data
        XYChart.Series<Number, Number> series_BladeSpinRate = new XYChart.Series<>();
        series.setName("Data Series");
        lineChart_BladeSpinRate.setLegendVisible(false);
        lineChart_BladeSpinRate.setCreateSymbols(false);
        // add series to chart
        lineChart_BladeSpinRate.getData().add(series_BladeSpinRate);
        // setup scene


        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService_BladeSpinRate;
        scheduledExecutorService_BladeSpinRate = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService_BladeSpinRate.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            double y = tele.BladeSpinRate;
            double x =  tele.missionTime;

            // Update the chart
            Platform.runLater(() -> {
                // get current time
                // Date now = new Date();
                // put random number with current time
                series_BladeSpinRate.getData().add(new XYChart.Data<>(x, y));
            });
        }, 0, 100, TimeUnit.MILLISECONDS);

        topLeftGrid.add(lineChart_BladeSpinRate, 0 , 0);

        Image image = new Image("captive/icon.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(130);
        imageView.setFitWidth(130);
        bottomRightBottomRightGrid.add(imageView, 0 , 0 );

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
        AltitudeLCD.setValue(tele.Altitude);

    }

}