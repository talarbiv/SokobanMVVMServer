package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import ModelPackeg.iModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import view.ServerWindowController;

/**
 * this class VM in architecture MVVM run client in parallel with limit 30
 * 
 * @see ServerController(int port, iModel model) get port listen and the model
 * @see runServer() start the server on the port from the C'tor
 * @see stopServer() close the server but give the client's to finish max wait 30 seconds and the shut down without wait
 * @see killClient(int index) close the client in index (frome list client)
 * */
public class ServerController extends Observable implements Observer{
	
	private int port;
	private List<clientHandler> ListClientHandler;
	private volatile boolean stop; 
	private  ExecutorService executor;
	private static final int THREDNUM=30;
	private iModel model;
	private Integer count=1;
	public List<String[]> data;
	
	public HashMap<Integer, clientHandler> hsClients;
	
	//C'tor
	/**get port listen and the model*/
	public ServerController(int port, iModel model) {
		this.hsClients=new HashMap<>();
		
		this.port = port;
		this.ListClientHandler=new ArrayList<>();
		this.stop = false;
		this.model=model;
		this.executor =Executors.newFixedThreadPool(THREDNUM);
		
		//new
		this.data=new ArrayList<>();
	}
	/**start the server on the port from the C'tor*/
	public void runServer() throws Exception{
			try{
				ServerSocket server = new ServerSocket(this.port);
				System.out.println("server is alive");
				server.setSoTimeout(1000);
				//Waiting to the next client
				while(!stop)
				{
						try{
							//Blocking calls from clients
							Socket aClient = server.accept();
							System.out.println("Client is connected!");
							executor.execute(()->{
								InputStream inFromUser;
								OutputStream outToClient;
								try {
									
									inFromUser = aClient.getInputStream();
									outToClient = aClient.getOutputStream();
									clientHandler ch =new SokobanClientHandlerServer(model);
									this.ListClientHandler.add(ch);
									this.hsClients.put(count, ch);
									data.add(new String[]{count.toString(),"ip: "+aClient.getInetAddress()+" port: "+aClient.getPort()});
									this.count++;
									setChanged();
									notifyObservers();
									ch.handleClient(inFromUser , outToClient);
									ListClientHandler.remove(ch);
									outToClient.close();
									inFromUser.close();
									aClient.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							});
						}
						catch (Exception e) {}		
				}
			server.close();
			
			}catch (Exception e) {}
	}

	
	public void start()
	{
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					runServer();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	/**close the server but give the client's to finish max wait 30 seconds and the shut down without wait*/
	public void stopServer()
	{
		this.executor.shutdown();
		try {
			this.executor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			this.stop = true;
		}
	}
	
	public List<String[]> getData() {
		return data;
	}

	public void setData(List<String[]> data) {
		this.data = data;
	}
	/**close the client in index (frome list client)*/
	public void killClient(int index){
		
		clientHandler c=this.hsClients.get(index);
		c.stop();
		this.ListClientHandler.remove(c);
		for (int i = 0; i < data.size(); i++) {
			if (Integer.parseInt(data.get(i)[0])==index)
				data.remove(i);
		}
		setChanged();
		notifyObservers();
	}

	//WE NEED TO DO
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}


}
