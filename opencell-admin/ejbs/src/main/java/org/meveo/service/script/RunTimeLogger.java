package org.meveo.service.script;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.meveo.model.shared.DateUtils;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

/**
 * A logger implementation to capture script execution logs locally
 * 
 * @author Andrius Karpavicius
 */
public class RunTimeLogger implements org.slf4j.Logger {

    private String className;

    private String SEP = "  ";
    private String DEBUG = "DEBUG";
    private String INFO = "INFO";
    private String TRACE = "TRACE";
    private String ERROR = "ERROR";
    private String WARN = "WARN";

    StringBuffer logText = new StringBuffer();

    /**
     * Logger instantiation
     * 
     * @param clazz class to log
     */
    public RunTimeLogger(Class<?> clazz) {
        this.className = clazz.getCanonicalName();
    }

    /**
     * Log a message
     * 
     * @param level log's level
     * @param message message
     * @param throwable Exception
     */
    public void log(String level, String message, Throwable throwable) {
        log(level, message, throwable.getMessage());
        StringWriter errors = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errors));
        log(level, message, errors.toString());
    }

    /**
     * Log a message with parameters
     * 
     * @param level log level
     * @param message message
     * @param args arguments.
     */
    public void log(String level, String message, Object... args) {
        StringBuffer sb = new StringBuffer();
        sb.append(DateUtils.formatDateWithPattern(new Date(), "HH:mm:ss,SSS"));
        sb.append(SEP);
        sb.append(level);
        sb.append(SEP);
        sb.append("[" + className + "]");
        sb.append(SEP);
        sb.append(MessageFormatter.arrayFormat(message, args).getMessage());
        sb.append("\n");
        logText.append(sb);
    }

    @Override
    public void debug(String arg0) {
        log(DEBUG, arg0);
    }

    @Override
    public void debug(String arg0, Object arg1) {
        log(DEBUG, arg0, arg1);

    }

    @Override
    public void debug(String arg0, Throwable arg1) {
        log(DEBUG, arg0, arg1);

    }

    @Override
    public void debug(String arg0, Object arg1, Object arg2) {
        log(DEBUG, arg0, arg1, arg2);

    }

    @Override
    public void error(String arg0) {
        log(ERROR, arg0);

    }

    @Override
    public void error(String arg0, Object arg1) {
        log(ERROR, arg0, arg1);

    }

    @Override
    public void error(String arg0, Object... arg1) {
        log(ERROR, arg0, arg1);

    }

    @Override
    public void error(String arg0, Throwable arg1) {
        log(ERROR, arg0, arg1);

    }

    @Override
    public void error(Marker arg0, String arg1) {

    }

    @Override
    public void error(String arg0, Object arg1, Object arg2) {
        log(ERROR, arg0, arg1, arg2);

    }

    @Override
    public void error(Marker arg0, String arg1, Object arg2) {

    }

    @Override
    public void error(Marker arg0, String arg1, Object... arg2) {

    }

    @Override
    public void error(Marker arg0, String arg1, Throwable arg2) {

    }

    @Override
    public void error(Marker arg0, String arg1, Object arg2, Object arg3) {

    }

    @Override
    public String getName() {

        return null;
    }

    @Override
    public void info(String arg0) {
        log(INFO, arg0);

    }

    @Override
    public void info(String arg0, Object arg1) {
        log(INFO, arg0, arg1);

    }

    @Override
    public void info(String arg0, Object... arg1) {
        log(INFO, arg0, arg1);

    }

    @Override
    public void info(String arg0, Throwable arg1) {
        log(INFO, arg0, arg1);

    }

    @Override
    public void info(Marker arg0, String arg1) {

    }

    @Override
    public void info(String arg0, Object arg1, Object arg2) {
        log(INFO, arg0, arg1, arg2);

    }

    @Override
    public void info(Marker arg0, String arg1, Object arg2) {

    }

    @Override
    public void info(Marker arg0, String arg1, Object... arg2) {

    }

    @Override
    public void info(Marker arg0, String arg1, Throwable arg2) {

    }

    @Override
    public void info(Marker arg0, String arg1, Object arg2, Object arg3) {

    }

    @Override
    public boolean isDebugEnabled() {

        return false;
    }

    @Override
    public boolean isDebugEnabled(Marker arg0) {

        return false;
    }

    @Override
    public boolean isErrorEnabled() {

        return false;
    }

    @Override
    public boolean isErrorEnabled(Marker arg0) {

        return false;
    }

    @Override
    public boolean isInfoEnabled() {

        return false;
    }

    @Override
    public boolean isInfoEnabled(Marker arg0) {

        return false;
    }

    @Override
    public boolean isTraceEnabled() {

        return false;
    }

    @Override
    public boolean isTraceEnabled(Marker arg0) {

        return false;
    }

    @Override
    public boolean isWarnEnabled() {

        return false;
    }

    @Override
    public boolean isWarnEnabled(Marker arg0) {

        return false;
    }

    @Override
    public void trace(String arg0) {
        log(TRACE, arg0);

    }

    @Override
    public void trace(String arg0, Object arg1) {
        log(TRACE, arg0, arg1);

    }

    @Override
    public void trace(String arg0, Object... arg1) {
        log(TRACE, arg0, arg1);

    }

    @Override
    public void trace(String arg0, Throwable arg1) {
        log(TRACE, arg0, arg1);

    }

    @Override
    public void trace(Marker arg0, String arg1) {

    }

    @Override
    public void trace(String arg0, Object arg1, Object arg2) {
        log(TRACE, arg0, arg1, arg2);

    }

    @Override
    public void trace(Marker arg0, String arg1, Object arg2) {

    }

    @Override
    public void trace(Marker arg0, String arg1, Object... arg2) {

    }

    @Override
    public void trace(Marker arg0, String arg1, Throwable arg2) {

    }

    @Override
    public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {

    }

    @Override
    public void warn(String arg0) {
        log(WARN, arg0);

    }

    @Override
    public void warn(String arg0, Object arg1) {
        log(WARN, arg0, arg1);

    }

    @Override
    public void warn(String arg0, Object... arg1) {
        log(WARN, arg0, arg1);

    }

    @Override
    public void warn(String arg0, Throwable arg1) {
        log(WARN, arg0, arg1);

    }

    @Override
    public void warn(Marker arg0, String arg1) {

    }

    @Override
    public void warn(String arg0, Object arg1, Object arg2) {
        log(WARN, arg0, arg1, arg2);

    }

    @Override
    public void warn(Marker arg0, String arg1, Object arg2) {

    }

    @Override
    public void warn(Marker arg0, String arg1, Object... arg2) {

    }

    @Override
    public void warn(Marker arg0, String arg1, Throwable arg2) {

    }

    @Override
    public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {

    }

    @Override
    public void debug(String arg0, Object... arg1) {
        log(WARN, arg0, arg1);

    }

    @Override
    public void debug(Marker arg0, String arg1) {

    }

    @Override
    public void debug(Marker arg0, String arg1, Object arg2) {

    }

    @Override
    public void debug(Marker arg0, String arg1, Object... arg2) {

    }

    @Override
    public void debug(Marker arg0, String arg1, Throwable arg2) {

    }

    @Override
    public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {

    }

    /**
     * Return log messages
     * 
     * @return Log messages
     */
    public String getLog() {
        if (logText.length() == 0) {
            return null;
        } else {
            return logText.toString();
        }
    }
}