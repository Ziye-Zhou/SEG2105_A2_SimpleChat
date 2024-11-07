// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  private String loginID;
  private String host;
  private int port;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   * @param loginID The login ID of the client.
   * @throws IOException If unable to connect to the server.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI, String loginID) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    this.host = host;
    this.port = port;
    
    // Attempt to open the connection to the server
    openConnection();
    
    // Send the #login <LoginID> message to the server immediately after connecting
    sendLoginMessage();
  }

  
  //Instance methods ************************************************
  /**
   * Send the #login <LoginID> message to the server
   */
  private void sendLoginMessage() {
	    try {
	        System.out.println("Sending login message: #login " + this.loginID); // Debugging
	        sendToServer("#login " + this.loginID); // Send directly to the server
	        clientUI.display("Login message sent with login ID: " + this.loginID);
	    } catch (IOException e) {
	        clientUI.display("Error: Unable to send login message to server.");
	        quit(); // Terminate the client if unable to send login message
	    }
	}

  
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message) {
	    try {
	        if (message.startsWith("#login")) {
	            if (isConnected()) {
	                clientUI.display("Error: Already logged in. Please log off first.");
	            } else {
	                sendToServer(message);
	                clientUI.display("Login command sent: " + message);
	            }
	        } else if (message.startsWith("#")) {
	            handleCommand(message);
	        } else {
	            sendToServer(message);
	        }
	    } catch (IOException e) {
	        clientUI.display("Could not send message to server. Terminating client.");
	        quit();
	    }
	}


  
  private void handleCommand (String command) {
	  String[] tokens = command.split("  ");
	  
	  if (command.equals("#quit")) {
		  quit();
	  }
	  else if (command.equals("#logoff")) {
		   try {
		      closeConnection();
		      clientUI.display("Client logged off.");
		    } catch (IOException e) {
		      clientUI.display("Error logging off: " + e.getMessage());
		    }
		  }
	  else if (tokens[0].equals("#sethost")) {
		    if (isConnected()) {
		      clientUI.display("Cannot change host while connected. Please log off first.");
		    } else if (tokens.length == 2) {
		      this.host = tokens[1];
		      clientUI.display("Host set to: " + this.host);
		    } else {
		      clientUI.display("Usage: #sethost <host>");
		    }
	  }
		  else if (tokens[0].equals("#setport")) {
		    if (isConnected()) {
		      clientUI.display("Cannot change port while connected. Please log off first.");
		    } else if (tokens.length == 2) {
		      try {
		        this.port = Integer.parseInt(tokens[1]);
		        clientUI.display("Port set to: " + this.port);
		      } catch (NumberFormatException e) {
		        clientUI.display("Invalid port number.");
		      }
		    } else {
		      clientUI.display("Usage: #setport <port>");
		    }
		  }
		  else if (command.equals("#login")) {
		        if (isConnected()) {
		          clientUI.display("Already connected to the server.");
		        } else {
		          setHost(this.host);
		          setPort(this.port);
		          try {
		            openConnection();
		            clientUI.display("Client logged in.");
		          } catch (IOException e) {
		            clientUI.display("Error logging in: " + e.getMessage());
		          }
		        }
		      }
		  else if (command.equals("#gethost")) {
		    clientUI.display("Current host: " + getHost());
		  }
		  else if (command.equals("#getport")) {
		    clientUI.display("Current port: " + getPort());
		  }
		  else {
		    clientUI.display("Unknown command: " + tokens[0]);
		  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
  		clientUI.display("The server has shut down.");
  		System.exit(0);
	}
  	
  	/**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
  		clientUI.display("Connection closed");
	}
}
//End of ChatClient class
