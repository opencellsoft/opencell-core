package com.opencellsoft.wildfly.logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Custom log handler implementation for Wildfly server.
 * <p>
 * Manages a pool of Log4j DailyRollingFileAppender instances. Allows to customize a filename based on MDC parameter. MDC parameter name is configurable.
 * </p>
 * <p>
 * 
 * See {@link DailyRollingFileAppender} for detailed description of how log file rolling works and its configuration via <code>datePattern</code> property.
 * </p>
 * Here is a sample logging configuration in Wildfly:
 * 
 * <pre>
 *         &lt;subsystem xmlns="urn:jboss:domain:logging:3.0"&gt;
 *             &lt;custom-handler name="fileByTenant" class="com.opencellsoft.wildfly.logger.MDCDailyRollingFileAppender" module="com.opencellsoft.logger"&gt;                
 *               &lt;formatter&gt;
 *                   &lt;named-formatter name="PATTERN"/&gt;
 *               &lt;/formatter&gt;
 *               &lt;properties&gt;
 *                   &lt;property name="fileName" value="${jboss.server.log.dir}/server{-providerCode}.log"/&gt;
 *                   &lt;property name="datePattern" value="yyyy-MM-dd"/&gt;
 *                   &lt;property name="fileAppend" value="true"/&gt;
 *                   &lt;property name="mdcPropertyName" value="providerCode"/&gt;
 *               &lt;/properties&gt;           
 *           &lt;/custom-handler&gt;
 *           ...
 * 
 * </pre>
 * 
 * 
 * @author Andrius Karpavicius
 */
public class MDCDailyRollingFileAppender extends AppenderSkeleton {

    private Map<String, FileAppender> appendersByMDC = new HashMap<>();

    /**
     * A filename to log to. {} symbols specify an area, which should be used only if MDC value is set. Inside {} mdcPropertyName is replaced with a MDC value.
     */
    private String fileName;

    /**
     * An MDC property name used to customize a file
     */
    private String mdcPropertyName;

    /**
     * The date pattern. By default, the pattern is set to "'.'yyyy-MM-dd" meaning daily rollover.
     */
    private String datePattern = "'.'yyyy-MM-dd";

    /**
     * Controls file truncatation. The default value for this variable is <code>true</code>, meaning that by default a <code>FileAppender</code> will append to an existing file and
     * not truncate it.
     *
     * <p>
     * This option is meaningful only if the FileAppender opens the file.
     */
    private boolean fileAppend = true;

    @Override
    protected void append(LoggingEvent event) {

        String mdcValue = (String) event.getMDC(mdcPropertyName);

        FileAppender appender = appendersByMDC.get(mdcValue);

        if (appender == null) {
            appender = createAppenderForMDC(mdcValue);
        }

        if (appender != null) {
            appender.append(event);
        }
    }

    /**
     * Initialize a default appender for a when no MDC value is present
     */
    @Override
    public void activateOptions() {
        super.activateOptions();

        if (mdcPropertyName == null || mdcPropertyName.isEmpty()) {
            LogLog.error("mdcPropertyName property is not set for appender [" + name + "].");
        }
        if (fileName == null || fileName.isEmpty()) {
            LogLog.error("fileName property is not set for appender [" + name + "].");
        }

        createAppenderForMDC(null);
    }

    public void setFileName(String fileNameByMDC) {
        this.fileName = fileNameByMDC;
    }

    public void setMdcPropertyName(String mdcPropertyName) {
        this.mdcPropertyName = mdcPropertyName;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public void setFileAppend(boolean fileAppend) {
        this.fileAppend = fileAppend;
    }

    /**
     * Create a file appender for a given MDC value.
     * 
     * @param mdcValue MDC property value.MDC property value will be substituted in a filename. If null, an option section, identified by {..} will be ommited
     * @return File appender
     */
    private FileAppender createAppenderForMDC(String mdcValue) {

        String fileNameForAppender = null;
        int from = fileName.indexOf('{');
        int to = fileName.indexOf('}');

        if (mdcValue == null) {
            fileNameForAppender = fileName.substring(0, from) + fileName.substring(to + 1);
        } else {
            fileNameForAppender = fileName.substring(0, from) + fileName.substring(from + 1, to).replaceAll(mdcPropertyName, mdcValue) + fileName.substring(to + 1);
        }

        try {
            DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(getLayout(), fileNameForAppender, datePattern);
            fileAppender.setAppend(fileAppend);
            appendersByMDC.put(mdcValue, fileAppender);
            return fileAppender;

        } catch (IOException e) {
            LogLog.error("Failed to initialize a default file appender");
        }
        return null;
    }

    @Override
    public void close() {

        for (FileAppender appender : appendersByMDC.values()) {
            appender.close();
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    /**
     * Not needed, but complains at startup if method is not present
     * 
     * @param file Filename
     */
    public void setFile(String file) {
    }
}