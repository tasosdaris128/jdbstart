package com.tasos.jdbstart.logger;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 *
 * General purpose log class.
 *
 * @author Tasos Daris<tasos.daris@datawise.ai>
 * */
public class Log {

    private static Log instance;

    private volatile boolean debug;

    public enum LogLevel {
        RAW, // For raw printing.
        INFO, // For information logs.
        WARNING, // For warning logs.
        ERROR, // For error logs.
        EXCEPTION, // For exception logs.
        DEBUG // For debug logs.
    }

    private Log() {}

    public static synchronized Log getInstance() {
        if (instance == null) instance = new Log();

        return instance;
    }

    /*
     *
     * Inializes the log instance.
     *
     */
    public static void init(boolean debug) {
        getInstance().setDebug(debug);
    }

    /*
     *
     * Prints raw messages without log tags (just the timestamp
     * on the front).
     *
     */
    public static void print(String format, Object... args) {
        getInstance().log(LogLevel.RAW, format, args);
    }

    /*
     *
     * Prints INFO logs with the corresponding [*] tag.
     *
     */
    public static void i(String format, Object... args) {
        getInstance().log(LogLevel.INFO, format, args);
    }

    /*
     *
     * Prints WARNING logs with the corresponding [!] tag.
     *
     */
    public static void w(String format, Object... args) {
        getInstance().log(LogLevel.WARNING, format, args);
    }

    /*
     *
     * Prints ERROR logs with the corresponding [E] tag.
     *
     */
    public static void e(String format, Object... args) {
        getInstance().log(LogLevel.ERROR, format, args);
    }

    /*
     *
     * Prints EXCEPTION logs with the corresponding [X] tag.
     *
     */
    public static void exc(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        throwable.printStackTrace(printWriter);

        String message = throwable.getMessage();
        String stackTrace = stringWriter.getBuffer().toString();

        getInstance().log(LogLevel.ERROR, message);
        getInstance().log(LogLevel.EXCEPTION, "---- Begin of stack trace ----");
        getInstance().log(LogLevel.EXCEPTION, "\n\n" + stackTrace);
        getInstance().log(LogLevel.EXCEPTION, "---- End of stack trace ----");
    }

    /*
     *
     * Prints DEBUG logs with the corresponding [-] tag.
     *
     */
    public static void d(String format, Object... args) {
        if (getInstance().isDebug()) {
            getInstance().log(LogLevel.DEBUG, format, args);
        }
    }

    /*
     *
     * Internal log method. This method constructs a message
     * according to the level parameter and prints it in the
     * terminal.
     *
     *
     * @param level     The level of logging.
     * @param format    The format of the message.
     * @param args      The arbitrary print argumets.
     *
     */
    private void log(LogLevel level, String format, Object... args) {
        String message;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        message = dateFormatter.format(now);

        switch (level) {
            case INFO:
                message += " [*]: ";
                break;
            case WARNING:
                message += " [!]: ";
                break;
            case ERROR:
                message += " [E]: ";
                break;
            case EXCEPTION:
                message += " [X]: ";
                break;
            case DEBUG:
                message += " [-]: ";
                break;
            case RAW:
            default:
                message += " ";
                break;
        }

        message += format + "\n";

        if ((level == LogLevel.ERROR) || ( level == LogLevel.EXCEPTION)) {
            System.err.printf(message, args);
            return;
        }

        System.out.printf(message, args);
    }

    public static void flex() {
        Log.print("This is a raw message.");
        Log.i("This is an info.");
        Log.w("This is a warning.");
        Log.e("This is an error message.");
        Log.exc(new Exception("This is a stack trace."));
        Log.d("This is a debug message.");
    }

    private void setDebug(boolean debug) {
        this.debug = debug;
    }

    private boolean isDebug() {
        return this.debug;
    }

}
