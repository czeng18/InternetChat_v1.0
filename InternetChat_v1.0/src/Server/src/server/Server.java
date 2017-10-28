package server;

/**
 * Opens a server port for Clients to connect to for chats
 * @author Caroline Zeng
 * @version 2.0.0
 */

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    ArrayList<Handler> handlers = new ArrayList<Handler>();
    static ArrayList<String> names = new ArrayList<String>();
    int portNumber;
    ServerSocket serverSocket;
    boolean open = true;
    JTextArea messageArea;

    /**
     * Default constructor for server
     */
    public Server() throws IOException
    {
        portNumber   = 4000;
        serverSocket = new ServerSocket(portNumber);
    }

    /**
     * Constructor for server
     * Set portNumber to default port
     * @param messageArea   Component for Handlers to send information to
     */
    public Server(JTextArea messageArea) throws IOException
    {
        portNumber       = 4000;
        this.messageArea = messageArea;
        serverSocket     = new ServerSocket(portNumber);
    }

    /**
     * Constructor for server
     * Give server a Component to put information in and a port
     * @param portNumber    Port the server is connected to
     * @param messageArea   Component for Handlers to send information to
     */
    public Server(int portNumber, JTextArea messageArea) throws IOException
    {
        this.portNumber  = portNumber;
        this.messageArea = messageArea;
        serverSocket     = new ServerSocket(portNumber);
    }

    /**
     * Send out message to all currently connected Clients
     * @param m Message sent
     */
    public synchronized void sendMessage(String m)
    {
        for (Handler h : handlers)
        {
            h.out.println(m);
        }
        messageArea.append(m + "\n");
    }

    /**
     * Checks whether a name already exists in the list of names of Clients already connected
     * @param name  name to check
     * @return      true if not already in list
     *              false if already in list
     */
    public static synchronized boolean addName(String name)
    {
        for (String n : names)
        {
            if (n.equals(name)) return false;
        }
        return true;
    }

    /**
     * Sends out to currently connected Clients that another client has left
     * @param h the Handler associated to the client
     */
    public void clientLeft(Handler h)
    {
        sendMessage(h.getName() + " has left");
        // Trash cleanup
        handlers.remove(h);
        names.remove(h.getName());
    }

    /**
     * Run the server
     * Receive clients and relegate handling clients to handler threads
     */
    public void run()
    {
        try
        {
            while (open)
            {
                Socket clientSocket = serverSocket.accept();
                Handler handler     = new Handler(clientSocket, this);
                handler.start();
                handlers.add(handler);
            }
        } catch (IOException e)
        {
            interrupt();
        }
    }

    /**
     * Close the server without throwing error to end the program
     */
    public void close()
    {
        try {
            serverSocket.close();
        } catch (IOException e)
        {
            System.out.println(e.getStackTrace());
        }
        interrupt();
    }
}
