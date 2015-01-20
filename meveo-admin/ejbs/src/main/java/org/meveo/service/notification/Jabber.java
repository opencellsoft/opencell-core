package org.meveo.service.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SASLAuthentication;

public class Jabber {

public static String username="abc@mydomain.com";
public static String password="password";
public static String id="";
static ConnectionConfiguration config;
XMPPConnection conn;
public static Chat chat;
public static XMPPConnection connection = new XMPPConnection("gmail.com");
public static boolean free=false;
static {

	  try {
	        //establish connection between client and server.
	        connection.connect();
	        System.out.println("Connected to " + connection.getHost());
	        //call base class function to get login
	        connection.login(username,password);

	        System.out.println(connection.isAuthenticated());

	        Presence presence = new Presence(Presence.Type.available);
	        connection.sendPacket(presence);
	        System.out.println("presence is ............" + presence.toXML());

	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
}


public Jabber(){

};

/*
 * Function to send message to specified user
 */
public void sendMessage(String message, String to) throws XMPPException
{
    System.out.println("Message is ......."+message);
    //Chat chat = connection.getChatManager().createChat(to, new Jabber());

            System.out.println("Chat obj is ........"+ chat);
}
/**
 * Function to display user list
 */
public void displayBuddyList()
{
Roster roster = connection.getRoster();
roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
Collection<RosterEntry> entries = roster.getEntries();

System.out.println("\n\n" + entries.size() + " buddy(ies):");
for(RosterEntry r:entries)
{
System.out.println(r.getUser());
System.out.println(r.getName());
}
}

public void processMessage(Chat chat, Message message)
{
if(message.getType() == Message.Type.chat)
System.out.println(chat.getParticipant() + " says: " + message.getBody());
}
}