package rage.pitclient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingTester {
    private final Logger logger = Logger.getLogger(LoggingTester.class
            .getName());
    private FileHandler fh = null;

    public LoggingTester() {
        //just to make our log file nicer :)
        SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
        try {
            fh = new FileHandler(System.getProperty("user.home") + "\\rag\\logs\\"
                + format.format(Calendar.getInstance().getTime()) + ".log");
        } catch (Exception e) {
            e.printStackTrace();
        }

        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
    }

    public void doLogging(String pass) {
        logger.info(pass);
    }
}   