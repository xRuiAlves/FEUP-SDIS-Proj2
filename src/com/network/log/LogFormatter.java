package com.network.log;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private SimpleDateFormat dateFormat;
    private String nodeId;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public LogFormatter(String peerId) {
        this.nodeId = peerId;
        dateFormat = new SimpleDateFormat("hh:mm:ss");
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateFormat.format(new Date(record.getMillis())));
        stringBuilder.append(" - [").append(this.nodeId).append("]");
        stringBuilder.append(" - ");

        stringBuilder.append(this.getColorByLevel(record.getLevel()));
        stringBuilder.append("[").append(record.getLevel().getName()).append("]");
        stringBuilder.append(ANSI_RESET).append(" - ");

        Object[] parameters = record.getParameters();
        if (parameters != null) {
            stringBuilder.append("[");
            for (int i = 0; i < parameters.length; i++) {
                stringBuilder.append(parameters[i].toString());
                if (i < parameters.length - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("] - ");
        }

        stringBuilder.append(record.getMessage()).append("\n");

        return stringBuilder.toString();
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getColorByLevel(Level level) {
        if (Level.INFO.equals(level)) {
            return ANSI_BLUE;
        } else if (Level.WARNING.equals(level)) {
            return ANSI_YELLOW;
        } else if (Level.SEVERE.equals(level)) {
            return ANSI_RED;
        }
        return ANSI_RESET;
    }
}
