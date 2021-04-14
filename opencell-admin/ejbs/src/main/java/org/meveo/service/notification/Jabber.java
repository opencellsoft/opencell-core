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

package org.meveo.service.notification;

import java.util.Collection;

import javax.inject.Inject;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.slf4j.Logger;

@Deprecated
public class Jabber {
    /**
     * user name.
     */
    public static String username = "abc@mydomain.com";

    /**
     * password.
     */
    public static String password = null;

    /**
     * id.
     */
    public static String id = "";

    /**
     * config connection.
     */
    static ConnectionConfiguration config;

    /**
     * xmpp connection.
     */
    XMPPConnection conn;

    /**
     * chat.
     */
    public static Chat chat;

    /**
     * xmpp connection.
     */
    public static XMPPConnection connection = new XMPPConnection("gmail.com");

    /**
     * free.
     */
    public static boolean free = false;
    private static Logger log;
    static {
        try {
            // establish connection between client and server.
            connection.connect();
            log.info("Connected to " + connection.getHost());
            // call base class function to get login
            connection.login(username, password);


            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            log.info("presence is ............" + presence.toXML());

        } catch (Exception e) {
            log.error("connection failed", e);
        }
    }

    /**
     * default constructor.
     */
    public Jabber() {

    };

    /*
     * Function to send message to specified user
     */
    /**
     * @param message message to be sent
     * @param to destination which receives the message.
     * @throws XMPPException exception
     */
    public void sendMessage(String message, String to) throws XMPPException {
        log.info("Message is ......." + message);
        // Chat chat = connection.getChatManager().createChat(to, new Jabber());

        log.info("Chat obj is ........" + chat);
    }

    /**
     * Function to display user list.
     */
    public void displayBuddyList() {
        Roster roster = connection.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
        Collection<RosterEntry> entries = roster.getEntries();

        log.info("\n\n" + entries.size() + " buddy(ies):");
        for (RosterEntry r : entries) {
            log.info(r.getUser());
            log.info(r.getName());
        }
    }

    /**
     * @param chat instance of Chat
     * @param message message
     */
    public void processMessage(Chat chat, Message message) {
        if (message.getType() == Message.Type.chat) {
            log.info(chat.getParticipant() + " says: " + message.getBody());
        }
    }
}