import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.Scanner;


import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//TODO make values colors red if out of bounds, make a textarea for warningchecker.get(0);

class TSNR{

    public boolean tempFlag = false;
    boolean speedFlag = false;
    boolean slippageFlag = false;
    boolean impactFlag = false;

    public int speed;
    public int wheelRPM;
    public int distance;
    public int impact;
    public int temperature;
    public int objectSpeed;
    public final double circumference;
    public boolean objectDetected;
    public boolean objectMoving;
    public boolean is_gate;
    public boolean gate_Open;
    Scanner reader = null;

    public TSNR(){
        speed = 0;
        wheelRPM = 0;
        distance = 0;
        impact = 0;
        temperature = 0;
        objectSpeed = 0;
        circumference = 33.333;
        objectDetected = false;
        objectMoving = false;
        is_gate = false;
        gate_Open = false;
        File input = new File("src/tests.txt");

        try {
            reader = new Scanner(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    /** HTR = new IoT();
     IoT.User peyrovian = IoT.User("Peyrovian", "CCRP", "Technician");
     IoT.User conductor = new IoT.User("conductor", "password", "Regular");
     IoT.User[] users = new IoT.User[]{peyrovian, conductor};
     String permissions = HTR.login("Peyrovian", "CCRP", users);
     **/

    private void parser(String input){
        String[] tokens = input.split(" ");
        speed = Integer.parseInt(tokens[0]);
        wheelRPM = Integer.parseInt(tokens[1]);
        distance = Integer.parseInt(tokens[2]);
        impact = Integer.parseInt(tokens[6]);
        temperature = Integer.parseInt(tokens[7]);
        objectSpeed = Integer.parseInt(tokens[8]);

        objectDetected = tokens[3].equals("1");

        objectMoving = tokens[4].equals("1");

        is_gate = tokens[5].equals("1");

        gate_Open = tokens[9].equals("1");
    }

    public void readLine(){

        String data = reader.nextLine();
        parser(data);

    }

    public int get_speed(){
        return speed;
    }

    public int get_RPM(){
        return wheelRPM;
    }

    public int get_distance(){
        return distance;
    }

    public int get_impact(){
        return impact;
    }

    public int get_temperature(){
        return temperature;
    }

    public boolean get_Objdetected(){
        return objectDetected;
    }

    public boolean get_isMoving(){
        return objectMoving;
    }

    public boolean get_isGate(){
        return is_gate;
    }

    public int get_objectSpeed(){
        return objectSpeed;
    }

    public boolean get_gateOpen(){
        return gate_Open;
    }

    public String getMovingMessage(){
        if(get_isMoving()){
            return "Object Moving at " + get_objectSpeed() + "km/h ";
        }else{
            return "Object Not Moving ";
        }
    }


}



class IoT{
    public long wheelCircumference = (long) 33.3333;
    public int speed;
    public int wheelRPM;
    public int distance;
    public int objectSpeed;
    public int impact;
    public int temperature;
    public boolean objectDetected;
    public boolean isMoving;
    public boolean isGate;
    public boolean gateOpen;
    public final double circumference = 33.333;




    public IoT(TSNR router){
        speed = router.get_speed();
        wheelRPM = router.get_RPM();
        distance = router.distance;
        impact = router.get_impact();
        temperature = router.get_temperature();
        objectSpeed = router.get_objectSpeed();
        objectDetected = router.get_Objdetected();
        isMoving = router.get_isMoving();
        isGate = router.get_isGate();
        gateOpen = router.get_gateOpen();
    }

    public void set_speed(int router_speed){
        speed = router_speed;
    }

    public void set_wheelRPM(int router_RPM){
        wheelRPM = router_RPM;
    }

    public void set_distance(int router_distance){
        distance = router_distance;
    }

    public void set_objectSpeed(int router_objSpeed){
        objectSpeed = router_objSpeed;
    }

    public void set_impact(int router_impact){
        impact = router_impact;
    }

    public void set_temperature(int router_temperature){
        temperature = router_temperature;
    }

    public void set_objDetected(boolean router_objDetected){
        objectDetected = router_objDetected;
    }

    public void set_isMoving(boolean router_isMoving){
        isMoving = router_isMoving;
    }

    public void set_isGate(boolean router_isGate){
        isGate = router_isGate;
    }

    public void set_gateOpen(boolean router_gateOpen){
        gateOpen = router_gateOpen;
    }





    public ArrayList<String> warningChecker(int object_Speed, boolean object_on_track, boolean object_moving){
        ArrayList<String> output = new ArrayList<>();


        //if gate crossing is detected
        if(isGate){
            if(distance > 1600){
                output.add("Gate Crossing Detected: Blow Horn for 15 seconds");
            }
            if(distance > 800 && !gateOpen){
                output.add("Gate Crossing Detected: Decrease Speed");
            }
            else{
                if(distance < 100){
                    output.add("Blow Horn for 5 seconds");
                }
                output.add("Gate Crossing Detected: Brake");
            }
        }
        else{

            //If standing object is on the track, and is not a gate crossing
            if (objectDetected && !isMoving){
                if (distance > 800){
                    output.add("Standing Object on Track: Decrease Speed");
                }
                else{
                    output.add("Standing Object on Track: Brake");
                }
            }
        }

        //If moving object is on the track
        if ((objectDetected && isMoving) && (objectSpeed < speed)){
            if (objectSpeed < 0 && distance > 800){
                output.add("Moving Object on Track moving toward Train: Decrease Speed");
            }
            if (objectSpeed > 0 && distance < 800){
                output.add("Moving Object on Track moving away from Train: Brake");
            }
            if (objectSpeed < 0 && distance < 800){
                output.add("Moving Object on Track moving toward Train: Brake");
            }
            if (objectSpeed > 0 && distance > 800){
                output.add("Moving Object on Track moving away from Train: Decrease Speed");
            }
        }

        if (impact >= 100){
            output.add("Impact Detected");
        }

        if (temperature <= 20 || temperature >= 100){
            output.add("Temperature Warning");
        }


        return output;
    }


    //Log to store interactions between the Operator and IoT
    static class Log{

        public Log(){
        }
        //Makes a string including date and time for event with warning message that can be stored in the log.
        public String storeable_log(String instruction){
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return formatter.format(date)+": "+instruction;
        }

        public void writeToLog(File file, String logged)  {
            Writer output = null;
            try {
                output = new BufferedWriter(new FileWriter(file, true));  //clears file every time
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                output.append(logged+ "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
    public class User{
        private String userID;
        private String password;

        private String access_level;

        public User(String user, String pwd, String level){
            this.userID = user;
            this.password = pwd;
            this.access_level = level;
        }

        public String get_access_level(){
            return access_level;
        }

        public String get_User_ID(){
            return userID;
        }

        public String get_password(){
            return password;
        }
    }


    public String login(String UserID, String password, User[] users){
        for(int i = 0; i < users.length; i++){
            if (UserID == users[i].get_User_ID()){
                if(password == users[i].get_password()){
                    return users[i].get_access_level();
                }
                else{
                    return "Password Incorrect: Try Again";
                }
            }
        }

        return "User ID not found";
    }

    final static String LOOKANDFEEL = "Metal";
    final static String THEME = "DefaultMetal";


    private static void initLookAndFeel() {
        String lookAndFeel = null;

        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
                //  an alternative way to set the Metal L&F is to replace the
                // previous line with:
                // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";

            } else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } else if (LOOKANDFEEL.equals("GTK")) {
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "
                        + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {


                UIManager.setLookAndFeel(lookAndFeel);

                // If L&F = "Metal", set the theme

                if (LOOKANDFEEL.equals("Metal")) {
                    if (THEME.equals("DefaultMetal"))
                        MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                    else if (THEME.equals("Ocean"))
                        MetalLookAndFeel.setCurrentTheme(new OceanTheme());

                    UIManager.setLookAndFeel(new MetalLookAndFeel());
                }


            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {

        //create Queue that stores logs
        TSNR router = new TSNR();
        IoT HTR = new IoT(router);
        Log log = new Log();
        File logFile = new File("src/log.txt");
        Runnable update = new Runnable(){
            public void run(){
                router.readLine();
                HTR.set_speed(router.get_speed());
                HTR.set_wheelRPM(router.get_RPM());
                HTR.set_distance(router.get_distance());
                HTR.set_objectSpeed(router.get_objectSpeed());
                HTR.set_impact(router.get_impact());
                HTR.set_temperature(router.get_temperature());
                HTR.set_objDetected(router.get_Objdetected());
                HTR.set_isGate(router.get_isGate());
                HTR.set_isMoving(router.get_isMoving());
            }
        };

        router.readLine();
        HTR.set_speed(router.get_speed());
        HTR.set_wheelRPM(router.get_RPM());
        HTR.set_distance(router.get_distance());
        HTR.set_objectSpeed(router.get_objectSpeed());
        HTR.set_impact(router.get_impact());
        HTR.set_temperature(router.get_temperature());
        HTR.set_objDetected(router.get_Objdetected());
        HTR.set_isGate(router.get_isGate());
        HTR.set_isMoving(router.get_isMoving());

        ArrayList<String> warnings = new ArrayList<String>();
        warnings = HTR.warningChecker(router.get_speed(), router.get_Objdetected(), router.get_isMoving());



        initLookAndFeel();

        JFrame parent = new JFrame("Train Monitor");

        parent.setSize(500, 700);
        parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel gate = new JLabel("Gate Status: ");
        gate.setBounds(0, 100, 120, 20);

        JLabel gateStatus = new JLabel();
        gateStatus.setBounds(140, 100, 350, 20);

        JLabel speedText = new JLabel("Speed: ");
        speedText.setBounds(0, 50, 120, 40);

        JLabel speed = new JLabel();
        speed.setBounds(140, 50, 120, 40);


        JLabel wheelRPM = new JLabel();
        wheelRPM.setBounds(140, 150, 120, 20);

        JLabel wheelRPM_ = new JLabel("Wheel RPM: ");
        wheelRPM_.setBounds(0, 150, 120, 20);


        JLabel impact = new JLabel();
        impact.setBounds(140, 200, 150, 40);

        JLabel impact_ = new JLabel("Impact Sensor Status: ");
        impact_.setBounds(0, 200, 200, 40);

        JLabel object = new JLabel();
        object.setBounds(140, 250, 300, 40);

        JLabel object_ = new JLabel("Object Sensor: ");
        object_.setBounds(0, 250, 120, 40);

        JLabel temperature = new JLabel();
        temperature.setBounds(140, 300, 150, 40);

        JLabel temperature_ = new JLabel("Temperature: ");
        temperature_.setBounds(0, 300, 120, 40);


        JLabel notification = new JLabel("Current Warning: ");
        notification.setBounds(0, 350, 120, 40);

        JLabel notification_ = new JLabel();
        notification_.setBounds(140, 350, 350, 40);

        JButton button = new JButton("Enter");
        button.setBounds(200, 550, 200, 100);

        JPasswordField password = new JPasswordField();
        password.setName("password");
        password.setBounds(200, 525, 200, 25);

        /**JLabel admin = new JLabel("admin password");
        admin.setBounds(350, 625, 100, 25);
**/
        JTextField adminUser = new JTextField();
        adminUser.setName("username");
        adminUser.setBounds(200, 500, 150, 25);

        JPasswordField adminPanel = new JPasswordField();
        adminPanel.setBounds(200, 300, 150, 20);

        JLabel placeholder = new JLabel();


// add all labels and field to the GUI
        parent.add(speed);
        parent.add(speedText);
        parent.add(gate);
        parent.add(gateStatus);
        parent.add(wheelRPM_);
        parent.add(wheelRPM);
        parent.add(impact);
        parent.add(object);
        parent.add(impact_);
        parent.add(object_);
        parent.add(temperature);
        parent.add(temperature_);
        parent.add(button);
        parent.add(password);
        parent.add(notification);
        parent.add(notification_);
        //parent.add(admin);
        //parent.add(adminUser);
        //parent.add(adminPanel);
        parent.add(placeholder);


        parent.add(placeholder);
        parent.setVisible(true);



        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (password.getText().equals("CCRP")) {
                    try {

                        JFrame admin = new JFrame("Admin: Logs");
                        JPanel panel = new JPanel();
                        JScrollPane scrollInfo = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

                        // Delete admin frame upon closing window
                        admin.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                                admin.dispose();
                            }
                        });

                        JTextArea logs = new JTextArea();
                        FileReader reader = new FileReader("src/log.txt");
                        logs.read(reader, "LOG");

                        logs.add(new JTextArea());
                        panel.add(logs);

                        admin.add(scrollInfo);
                        admin.setSize(650, 650);
                        admin.setVisible(true);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }else{
                    password.setText("");

                }
            }
        });


        //Update all values in the GUI every second
        Timer SimpleTimer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                speed.setText(String.valueOf(router.get_speed()) + " km/h");
                temperature.setText((String.valueOf(router.get_temperature()) + " degrees Fahrenheit"));
                if(HTR.warningChecker(router.get_speed(), router.get_Objdetected(), router.get_isMoving()).size() > 0){
                    notification_.setText(HTR.warningChecker(router.get_speed(), router.get_Objdetected(), router.get_isMoving()).get(0));
                }else{
                    notification_.setText("No current warnings.");
                }
                impact.setText(String.valueOf(router.get_impact()) + " PSI");
                if ((Math.abs(router.get_speed()) - router.get_RPM() * router.circumference) > 3) {
                    wheelRPM.setText(String.valueOf(router.get_RPM()));
                    wheelRPM.setForeground(Color.RED);
                    wheelRPM_.setForeground(Color.RED);
                } else {
                    wheelRPM.setText(String.valueOf(router.get_RPM()));
                    wheelRPM.setForeground(Color.BLACK);
                    wheelRPM_.setForeground(Color.BLACK);
                }

                if (router.get_isGate()) {
                    if(router.get_distance() < 1600 && router.get_distance() > 200) {
                        gateStatus.setText("Gate detected " + router.get_distance() + " meters away. Blow Horn for 15 Seconds");
                        gateStatus.setForeground(Color.RED);
                        log.writeToLog(logFile, log.storeable_log("Gate detected " + router.get_distance() + " meters away. Blow Horn for 15 Seconds"));
                    }else if(router.get_distance() > 200){
                        gateStatus.setText("Gate detected " + router.get_distance() + " meters away. Blow Horn for 5 Seconds");
                        gateStatus.setForeground(Color.RED);
                        log.writeToLog(logFile, log.storeable_log("Gate detected " + router.get_distance() + " meters away. Blow Horn for 15 Seconds"));

                    }
                    else{
                        gateStatus.setForeground(new Color(255, 140, 0));
                        gateStatus.setText("Gate detected " + router.get_distance() + " meter away.");

                    }

                } else {
                    gateStatus.setText("Gate Not Detected!");
                    gateStatus.setForeground(Color.BLACK);
                }

                if (router.get_temperature() >= 100 || router.get_temperature() <= 20) {
                    temperature.setForeground(Color.RED);
                    if (!router.tempFlag) {
                        log.writeToLog(logFile, log.storeable_log("Temperature Not Within Range! " + router.get_temperature() + " degrees"));
                        router.tempFlag = true;
                    }
                } else {
                    router.tempFlag = false;
                    temperature.setForeground(Color.BLACK);
                }

                if (router.get_Objdetected()) {

                    object.setText("Object Detected " + router.get_distance() +" " + router.getMovingMessage());
                    object.setForeground(Color.RED);
                    log.writeToLog(logFile, log.storeable_log("Object Detected " + router.get_distance() + " " + router.getMovingMessage()));
                } else {
                    object.setForeground(Color.BLACK);
                    object.setText("No Object Detected!");
                }

                if(router.get_impact() >= 100){
                    impact.setForeground(Color.RED);
                    impact.setText(router.get_impact() + " PSI Impact Detected!");
                    log.writeToLog(logFile, log.storeable_log("Impacted Detected! PSI is " + router.get_impact()));
                }else{
                    impact.setForeground(Color.BLACK);
                    impact.setText(String.valueOf(router.get_impact()) + " PSI");
                }
            }
        });
        SimpleTimer.start();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(update, 1, 1, TimeUnit.SECONDS);

    }
}