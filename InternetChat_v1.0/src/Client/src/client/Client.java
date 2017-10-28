package client;
/**
 * Opens connection with a server that is already open and creates a UI for user to interact with
 * Chats with other users on this port
 * Connects to default port
 * @author Caroline Zeng
 * @version 2.0.0
 */

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {
    // Holds host name and port number to connect to
    String[] args;
    // Connection to host
    Socket socket;
    // Input and output streams
    PrintWriter out;
    BufferedReader in;
    JTextArea messageArea;
    JTextField textField;
    String name, serverAddress;
    int portNumber;
    boolean open = true;

    /**
     * Default constructor for client
     * Creates placeholder values for the server address and port number
     */
    public Client()
    {
        // Not sure exactly what this does
        this.setDaemon(false);
        serverAddress = "";
        portNumber    = -1;
    }

    /**
     * Constructor for client
     * Allows host to be determined
     * @param args  host information
     */
    public Client(String[] args)
    {
        this.setDaemon(false);
        this.args     = args;
        serverAddress = args[0];
        portNumber    = Integer.parseInt(args[1]);
    }

    /**
     * Establish connection to server, and send and receive information from host
     */
    public void run()
    {
        try
        {
            if (serverAddress.equals("") || portNumber == -1)
            {
                getServerAddress();
                getPortNumber();
            }
            socket = new Socket(serverAddress, portNumber);
            out    = new PrintWriter(socket.getOutputStream(), true);
            in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            getUserName();
            receive();
            out.println("END");
        } catch (UnknownHostException e)
        {
            // Can't connect to host
            JOptionPane.showMessageDialog(null, "Don't know about host " + serverAddress);
        } catch (IOException e)
        {
            // Socket is closed
            JOptionPane.showMessageDialog(null, "Couldn't get I/O for the connection to host " + serverAddress);
        }
    }

    /**
     * Receive information from host
     */
    public void receive()
    {
        try
        {
            String inputLine;
            // Keep receiving information
            while (open)
            {
                inputLine = in.readLine();
                // Differentiates information received from name selection process
                if (!inputLine.equals("NO") && !inputLine.equals(null)) messageArea.append(inputLine + "\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Prompt for and save the desired screen name.
     */
    public void getUserName()
    {
        String input;
        try
        {
            name = JOptionPane.showInputDialog(
                    null,
                    "Choose a screen name:",
                    "Screen name selection",
                    JOptionPane.PLAIN_MESSAGE);
            if (name == null) System.exit(0);
            out.println(name);
            while ((input = in.readLine()).equals("NO"))
            {
                name = JOptionPane.showInputDialog(
                        null,
                        "Choose a screen name:",
                        "Screen name selection",
                        JOptionPane.PLAIN_MESSAGE);
                if (name == null) System.exit(0);
                out.println(name);
            }
        } catch (IOException e) {}
        this.setName(name);
        textField.setEditable(true);
    }

    /**
     * Prompt for address of server to connect to
     */
    public void getServerAddress()
    {
        textField.setEditable(false);
        serverAddress = JOptionPane.showInputDialog(
                null,
                "Enter IP Address of the server:",
                "Welcome to the Chatter",
                JOptionPane.QUESTION_MESSAGE);
        if (serverAddress == null) System.exit(0);
    }

    public void getPortNumber()
    {
        String input = JOptionPane.showInputDialog(
                null,
                "Enter Port Number of the server:",
                "Welcome to the Chatter",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null) System.exit(0);
        boolean isInt = true;
        try {
            portNumber = Integer.parseInt(input);
        } catch (Exception e)
        {
            isInt = false;
            while (!isInt)
            {
                try {
                    input = JOptionPane.showInputDialog(
                            null,
                            "Enter Port Number of the server:",
                            "Welcome to the Chatter",
                            JOptionPane.QUESTION_MESSAGE);
                    if (input == null) System.exit(0);
                    portNumber = Integer.parseInt(input);
                    isInt      = true;
                } catch (Exception e1)
                {
                    isInt = false;
                }
            }
        }
    }

    public static void main(String[] args)
    {
        Client client      = new Client();
        ClientUI w         = new ClientUI(client);
        client.messageArea = w.messageArea;
        client.textField   = w.textField;
        client.start();
    }
}