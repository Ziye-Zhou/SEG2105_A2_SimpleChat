package edu.seg2105.edu.server.backend;

import edu.seg2105.client.common.ChatIF;
import java.io.IOException;
import java.util.Scanner;


/**
 * This class constructs the UI for a chat server. It implements the
 * ChatIF interface in order to activate the display() method.
 *
 * @author Ziye Zhou
 */
public class ServerConsole implements ChatIF {
    // Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    // Instance variables **********************************************

    /**
     * The instance of the server that this console interacts with.
     */
    EchoServer server;

    /**
     * Scanner to read from the console.
     */
    Scanner fromConsole;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the ServerConsole UI.
     *
     * @param port The port to connect on.
     */
    public ServerConsole(int port)  {
        
        try {
            server = new EchoServer(port,this);  // Properly initializing the EchoServer instance
            server.listen(); // Start listening for connections
        } catch (IOException exception) {
            System.out.println("Error: Can't set up server!"
                    + " Terminating.");
            System.exit(1);
        }
        // Create scanner object to read from the console
        fromConsole = new Scanner(System.in);  // Properly initializing the Scanner instance
    }

    // Instance methods ************************************************

    /**
     * This method waits for input from the console. Once it is
     * received, it sends it to all clients connected to the server
     * or processes server-specific commands.
     */
    public void accept() {
        try {
            String message;

            while (true) {
                message = fromConsole.nextLine();

                if (message.startsWith("#")) {
                    server.handleCommand(message);
                } else if (message != null && !message.trim().isEmpty()) {
                    // Prefix message with "SERVER MSG>"
                    String serverMessage = "SERVER MSG> " + message;

                    // Send message to all clients
                    server.sendToAllClients(serverMessage);

                    // Display message in server console
                    display(serverMessage);
                }
            }
        } catch (Exception ex) {
            System.out.println("Unexpected error while reading from console!");
        }
    }
  
    /**
     * This method overrides the method in the ChatIF interface.
     * It displays a message onto the server console.
     *
     * @param message The string to be displayed.
     */
    @Override
    public void display(String message) {
        System.out.println("> " + message);
    }

    // Class methods ***************************************************

    /**
     * The main method to start the server and its console interface.
     *
     * @param args Command line arguments specifying the port number.
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT; // The port number to listen on

        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]); // Get port from command line arguments
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Using default port " + DEFAULT_PORT);
        }

        ServerConsole console = new ServerConsole(port);
        console.accept(); // Accept console data from server operator
    }
}
