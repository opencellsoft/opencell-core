/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.admin;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.slf4j.Logger;

/**
 * Class to manage other applications : Medina, Vertina, Oudaya, Bayad.
 */
@Named
@ConversationScoped
public class Management implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected Logger log;

    /** paramBeanFactory */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    /**
     * Application name for daemon to know what application info to send back.
     */
    private static String application;

    /**
     * Output stream for network communication.
     */
    private transient ObjectOutputStream oos = null;

    /**
     * Input stream for network communication.
     */
    private transient SecureObjectInputStream ois = null;

    /**
     * Client socket. (A socket is an endpoint for communication between two machines. )
     */
    private transient Socket socket = null;

    /**
     * Is connection established flag.
     */
    private boolean connectionEstablished;

    /**
     * Connects to socket server.
     */
    public void connect() {

        ParamBean paramBean = paramBeanFactory.getInstance();
        String connectionUrl = paramBean.getProperty("connectionUrl", "127.0.0.1");
        int connectionPort = Integer.parseInt(paramBean.getProperty("connectionPort", "3000"));

        connectionEstablished = false;
        // open a socket connection
        try {
            socket = new Socket(connectionUrl, connectionPort);
            connectionEstablished = true;
            // open I/O streams for objects
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new SecureObjectInputStream(socket.getInputStream());

        } catch (UnknownHostException e) {
            log.error("Unknown Host Exception", e);
        } catch (IOException e) {
            log.error("IOException", e);
        }
    }

    /**
     * Checks if connection is available.
     * 
     * @return True if connection is available
     */
    public boolean checkConnection() {
        connect();
        try {
            if (connectionEstablished)
                oos.writeObject("check");
        } catch (IOException e) {
            log.error("IOException", e);
        } finally {
            close();
        }
        return connectionEstablished;

    }

    /**
     * Closes connection.
     * 
     */
    public void close() {
        try {
            if (oos != null)
                oos.close();
            if (ois != null) {
                ois.close();
            }
        } catch (IOException e) {
            log.error("IOException", e);

        }

    }

    /**
     * Checks application status.
     * 
     * @return true if enabled.
     */
    public boolean isEnabled() {
        try {
            connect();
            oos.writeObject("status");
            oos.writeObject(application);
            String text = (String) ois.readObject();
            close();
            if (text.equals("true")) {
                return true;
            } else
                return false;
        } catch (IOException e) {
            log.error("IOException", e);

            return false;
        } catch (ClassNotFoundException e1) {
            log.error("ClassNotFoundException", e1);
            return false;
        }
    }

    /**
     * Converts Logging Events list to string list.
     * 
     * @param tempLogList Logging Events List.
     */
    /*
     * public void convertLogs(List<LoggingEvent> tempLogList) {
     * 
     * for (Object o : tempLogList) { if (o instanceof LoggingEvent) { final LoggingEvent logEvent = (LoggingEvent) o; logs.add("[" + logEvent.getLevel() + "] " +
     * logEvent.getRenderedMessage()); } } }
     */

    /**
     * Get logs from application (what it does at the time).
     */
    /*
     * @SuppressWarnings("unchecked") public List<String> getLogs() { connect(); try { oos.writeObject("log"); logs.clear(); List<LoggingEvent> tempLogList = (List<LoggingEvent>)
     * ois.readObject(); convertLogs(tempLogList); close(); } catch (IOException e) { log.error(e.getMessage()); } catch (ClassNotFoundException e) { log.error(e.getMessage()); }
     * return logs; }
     */

    /**
     * Sends application kill signal to socket server.
     */
    public void kill() {
        try {
            connect();
            oos.writeObject("kill");
            oos.writeObject(application);
            close();
        } catch (IOException e) {
            log.error("IOException", e);

        }

    }

    /**
     * Sends restart signal to socket server
     * 
     */
    public void restart() {
        kill();
        init();
    }

    /**
     * Sends "Turn On" signal to socket server.
     */
    public void init() {
        try {
            connect();
            oos.writeObject("init");
            oos.writeObject(application);
            String text = (String) ois.readObject();
            log.info(text);
            close();

        } catch (Exception e) {
            log.error("IOException", e);
        }
    }

    public String getApplication() {
        return application;
    }

    @SuppressWarnings("static-access")
    public void setApplication(String application) {
        this.application = application;
    }

}
