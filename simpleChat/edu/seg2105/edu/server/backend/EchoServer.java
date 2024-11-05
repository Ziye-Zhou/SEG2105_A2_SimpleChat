package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;
import edu.seg2105.client.common.ChatIF;

import java.io.IOException;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  private ChatIF serverUI;
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  @Override
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
      String message = "Message received: " + msg + " from " + client.getInfo("username");
      System.out.println(message);
      serverUI.display(message);  // Display message on the server console
      this.sendToAllClients(msg); // Send the message to all clients
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   *This method is called each time a client connects.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
  }
  
  /**
   * This method is called each time a client disconnects.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  System.out.println("Client disconnected: " + client.getInetAddress().getHostAddress());
  }
  
  //Class methods ***************************************************
  public void handleCommand(String command) {
      try {
          if (command.equalsIgnoreCase("#quit")) {
              serverUI.display("Quitting server...");
              System.exit(0);
          } else if (command.equalsIgnoreCase("#stop")) {
              stopListening();
              serverUI.display("Server has stopped listening for new clients.");
          } else if (command.equalsIgnoreCase("#close")) {
              close();
              serverUI.display("Server has stopped listening and disconnected all clients.");
          } else if (command.startsWith("#setport")) {
              if (!isListening()) {
                  String[] tokens = command.split(" ");
                  if (tokens.length == 2) {
                      int port = Integer.parseInt(tokens[1]);
                      setPort(port);
                      serverUI.display("Port set to: " + port);
                  } else {
                      serverUI.display("Invalid command format. Use: #setport <port>");
                  }
              } else {
                  serverUI.display("Cannot set port while server is listening. Stop the server first.");
              }
          } else if (command.equalsIgnoreCase("#start")) {
              if (!isListening()) {
                  listen();
                  serverUI.display("Server is now listening for new clients.");
              } else {
                  serverUI.display("Server is already listening for clients.");
              }
          } else if (command.equalsIgnoreCase("#getport")) {
              serverUI.display("Current port: " + getPort());
          } else {
              serverUI.display("Unknown command: " + command);
          }
      } catch (IOException e) {
          serverUI.display("Error processing command: " + e.getMessage());
      } catch (NumberFormatException e) {
          serverUI.display("Invalid port number. Please enter a valid integer.");
      }
  }
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port, new ChatIF() {
        @Override
        public void display(String message) {
            System.out.println("> " + message);
        }
    });
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
