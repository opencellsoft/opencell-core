/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.commons.utils;

import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.db.ColumnMapping;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.filter.MarkerFilter;
import org.apache.logging.log4j.core.net.SocketAddress;
import org.apache.logging.log4j.nosql.appender.cassandra.CassandraAppender;

public class LoggingHelper {

    private static final String APP_MARKER_NAME = "APP";
    private static final String FUNC_MARKER_NAME = "FUNC";

    public static final Marker APP_MARKER = MarkerManager.getMarker(APP_MARKER_NAME);
    public static final Marker FUNC_MARKER = MarkerManager.getMarker(FUNC_MARKER_NAME);

    public static void buildLog() {
        try {
            Level level = Level.INFO;
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);

            ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
            builder.setStatusLevel(level);
            RootLoggerComponentBuilder rootLogger = builder.newRootLogger(level);

            // console logging
            final String stdoutAppenderName = "Stdout";
            AppenderComponentBuilder appenderBuilder = builder.newAppender(stdoutAppenderName, "CONSOLE").addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
            appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", "%d [%t]  %msg%n%throwable"));
            builder.add(appenderBuilder);
            builder.add(rootLogger.add(builder.newAppenderRef(stdoutAppenderName)));

            // building logger
            ((LoggerContext) LogManager.getContext(false)).start(builder.build());

            String cassandraActivation = ParamBean.getInstance().getProperty("meveo.loggingDB.activation", "false");
            boolean isActivated = Boolean.parseBoolean(cassandraActivation);

            if (isActivated) {
                // Cassandra app Appender
                CassandraAppender appCassandraAppender = builCassandraAppender(APP_MARKER_NAME);
                ctx.getConfiguration().addAppender(appCassandraAppender);
                ctx.getRootLogger().addAppender(ctx.getConfiguration().getAppender(appCassandraAppender.getName()));

                // Cassandra func Appender
                CassandraAppender funcCassandraAppender = builCassandraAppender(FUNC_MARKER_NAME);
                ctx.getConfiguration().addAppender(funcCassandraAppender);
                ctx.getRootLogger().addAppender(ctx.getConfiguration().getAppender(funcCassandraAppender.getName()));
            }

            ctx.updateLoggers();

        } catch (Exception e) {
            System.out.println("FAILED TO CONFIGURE LOG4J2 " + e.getMessage());
        }
    }

    private static CassandraAppender builCassandraAppender(String marker) {

        CassandraAppender cassandraAppender = null;

        try {
            // mapping cols
            ColumnMapping logId = ColumnMapping.newBuilder().setName("log_id").setLiteral("now()").build();
            ColumnMapping eventDate = ColumnMapping.newBuilder().setName("event_date").setType(Date.class).build();
            ColumnMapping message = ColumnMapping.newBuilder().setName("message").setPattern("%message").build();
            ColumnMapping typeFlux = ColumnMapping.newBuilder().setName("type_flux").setPattern("%class").build();
            ColumnMapping event = ColumnMapping.newBuilder().setName("event_type").setPattern("%level").build();
            ColumnMapping user = ColumnMapping.newBuilder().setName("user_create").setLiteral("''").build();

            String cassandraHost = ParamBean.getInstance().getProperty("meveo.loggingDB.host", "localhost");
            String cassandraPort = ParamBean.getInstance().getProperty("meveo.loggingDB.port", "9042");
            String cassandraKeyspace = ParamBean.getInstance().getProperty("meveo.loggingDB.keyspace", "cycling");

            SocketAddress socketAddress = SocketAddress.newBuilder().setHost(InetAddress.getByName(cassandraHost)).setPort(Integer.parseInt(cassandraPort)).build();

            String tableName = marker + "_" + "LOGS_TABLE";
            cassandraAppender = CassandraAppender.newBuilder().withName(marker + "CassandraAppender").setKeyspace(cassandraKeyspace).setTable(tableName)
                .setContactPoints(socketAddress).setColumns(logId, typeFlux, event, eventDate, message, user).build();

            Filter filter = MarkerFilter.createFilter(marker, Filter.Result.ACCEPT, Filter.Result.DENY);
            cassandraAppender.addFilter(filter);
            cassandraAppender.start();

        } catch (Exception e) {
            System.out.println("CassandraAppender build fail " + e.getLocalizedMessage());
        }
        return cassandraAppender;
    }

    public static void closeAppenders() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Map<String, Appender> apps = ctx.getRootLogger().getAppenders();
        if (apps != null && apps.values() != null) {
            Iterator<Appender> it = apps.values().iterator();
            while (it.hasNext()) {
                Appender ap = (Appender) it.next();
                if (ap.isStarted()) {
                    ap.stop();
                }
            }
        }
    }
}
