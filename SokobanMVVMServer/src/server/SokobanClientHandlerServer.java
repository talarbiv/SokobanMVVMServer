package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import ModelPackeg.iModel;
import commons.Level2D;

/**
 * this class Contracting with one client and Responsible for all requests from
 * him
 * 
 * @see solve(Level2D level) return the sulosion in string
 * @see add(Object o) add to DB
 * @see update(Object o) update in DB
 * @see selectScore(String query) return result list<object>
 */
public class SokobanClientHandlerServer extends Observable implements clientHandler {

	private boolean stopRead;
	private HashMap<String, Runnable> commands;
	private BufferedReader clientInput;
	private ObjectOutputStream serverOutput;
	private iModel model;

	public SokobanClientHandlerServer(iModel model) {
		this.model = model;
		this.stopRead = false;
		this.commands = new HashMap<>();
		this.init();
	}

	private void init() {
		this.commands.put("solve", () -> readBoard());
		this.commands.put("db", () -> readQuery());
		this.commands.put("exit", () -> stop());
	}

	/**
	 * get a input from client and output to client and open thread to talk this
	 * class answering requests to solve level ,High Score DB,Close talk: <br>
	 * <br>
	 * format:
	 * <li>solve level: you need to send solve and then send board level line
	 * after line and finally send 'end' return solution in one line like
	 * "u,d,l,r"</li>
	 * <li>High Score DB:send 'bd' and than
	 * <ul>
	 * <li>add high score:send 'add' user/userLevel/levels and params</li>
	 * </ul>
	 * </li> <br>
	 */
	@Override
	public void handleClient(InputStream inClient, OutputStream outClient) {

		try {
			this.clientInput = new BufferedReader(new InputStreamReader(inClient));
			this.serverOutput = new ObjectOutputStream(outClient);

			// open a new thread
			Thread fromClient = createThreadToRead(clientInput, "exit");

			fromClient.join();
			this.clientInput.close();
			this.serverOutput.close();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// read from client and notify
	private void readInputs(BufferedReader in, String exit) {
		String line;

		try {
			while (!this.stopRead) {

				line = in.readLine();
				this.commands.get(line).run();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// create thread to read from client
	private Thread createThreadToRead(BufferedReader in, String exitStr) {

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				readInputs(in, exitStr);
			}
		});
		t.start();
		return t;
	}

	public void stop() {
		try {
			this.serverOutput.writeObject("bye");
			this.serverOutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.stopRead = true;
	}

	private void readBoard() {
		String line;
		ArrayList<char[]> board = new ArrayList<>();
		boolean flag = false;

		try {
			while (!flag) {

				line = clientInput.readLine();

				if (line.equals("end")) {
					flag = true;

					char[][] newBoard = new char[board.size()][];
					newBoard = board.toArray(newBoard);
					String solve;
					String result = WebManager.getQuery(newBoard);

					// have a solution in Web DB
					if (result != null) {
						solve = result;
					}
					// don't have a solution in Web DB
					else {
						Level2D level = new Level2D(newBoard);
						solve = model.solve(level);
						// sucsses solve need to post to DB
						if (solve != null)
							WebManager.postQuery(level.getBoared(), solve);
					}

					if (solve == null) {
						this.serverOutput.writeObject("no solution");
						this.serverOutput.flush();

					} else {
						this.serverOutput.writeObject(solve);
						this.serverOutput.flush();
					}
				} else {
					board.add(line.toCharArray());
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readQuery() {
		try {
			String line = clientInput.readLine();
			String[] TypeQAndQ = line.split(" ", 2);
			String TypeQ = TypeQAndQ[0];
			TypeQ = TypeQ.toLowerCase();
			String Query = TypeQAndQ[1];
			switch (TypeQ) {
			case "add":
				try {
					this.model.add(Query);

					this.serverOutput.writeObject("sucsses");
					this.serverOutput.flush();
				} catch (Exception e) {
					// send to client "DisplayMassege The name already exists"
					this.serverOutput.writeObject("DisplayMassege The name already exists");
					this.serverOutput.flush();
				} finally {
					break;
				}
			case "update":
				try {
					this.model.update(Query);
				} catch (Exception e) {
					// send to client "DisplayMassege Wrong Input"
					this.serverOutput.writeObject("DisplayMassege Wrong Input");
					this.serverOutput.flush();
				} finally {
					break;
				}

			case "select":
				List<Object> HiScoreList = this.model.selectScore(Query);
				// print to scean high score like a list string
				for (Object obj : HiScoreList) {
					this.serverOutput.flush();
				}

				this.serverOutput.writeObject(HiScoreList);
				this.serverOutput.flush();

			default:
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}