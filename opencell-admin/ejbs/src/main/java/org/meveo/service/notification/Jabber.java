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

public class Jabber {
    /**
     * user name.
     */
    public static String username = "abc@mydomain.com";

    /**
     * password.
     */
    public static String password = "password";

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
    @Inject

    private static Logger log;
    static {
        try {
            // establish connection between client and server.
            connection.connect();
            System.out.println("Connected to " + connection.getHost());
            // call base class function to get login
            connection.login(username, password);

            System.out.println(connection.isAuthenticated());

            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            System.out.println("presence is ............" + presence.toXML());

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
        System.out.println("Message is ......." + message);
        // Chat chat = connection.getChatManager().createChat(to, new Jabber());

        System.out.println("Chat obj is ........" + chat);
    }

    /**
     * Function to display user list.
     */
    public void displayBuddyList() {
        Roster roster = connection.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
        Collection<RosterEntry> entries = roster.getEntries();

        System.out.println("\n\n" + entries.size() + " buddy(ies):");
        for (RosterEntry r : entries) {
            System.out.println(r.getUser());
            System.out.println(r.getName());
        }
    }

    /**
     * @param chat instance of Chat
     * @param message message
     */
    public void processMessage(Chat chat, Message message) {
        if (message.getType() == Message.Type.chat) {
            System.out.println(chat.getParticipant() + " says: " + message.getBody());
        }
    }
}