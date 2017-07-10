package server;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * this interface Committed to client Handler 
 * 
 * @see handleClient(InputStream inFromUser, OutputStream outToClient) Responsible for communication with the client  
 * @see stop() Allows the administrator to stop communication with the client 
 * */
public interface clientHandler {
	/**handleClient(InputStream inFromUser, OutputStream outToClient) Responsible for communication with the client*/
	public void handleClient(InputStream inFromUser, OutputStream outToClient);
	/**Allows the administrator to stop communication with the client*/
	public void stop();

}
