package server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
/**
 * This class is responsible for communicating with web service in our format Embedded in class
 * 
 * @see getQuery(char[][] Board) get a sokoban board and ask web service(in our format Embedded in class) if we have a solution if have a solution return a solution in our format if not return null 
 * @see postQuery(char[][] Board, String solusion) get a sokoban board and solution and send to web server (in our format Embedded in class)
 * */
public class WebManager {
	/** get a sokoban board and ask web service if we have a solution if have a solution return a solution in our format if not return null*/
	public static String getQuery(char[][] Board) {
		String compBoard = comprese(Board);
		Client client = ClientBuilder.newClient();
		WebTarget webTarget2 = client
				.target("http://localhost:8080/RESTSokobanService/SokobanServices/get/" + compBoard);
		Invocation.Builder invocationBuilder2 = webTarget2.request();
		Response response = invocationBuilder2.get();
		String msg = response.readEntity(String.class);
		if (response.getMediaType() == null)
			return null;
		else
			return msg;

	}
	/** get a sokoban board and solution and send to web server */
	public static void postQuery(char[][] Board, String solusion) {
		String boardAndSol = comprese(Board) + "=" + solusion;

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target("http://localhost:8080/RESTSokobanService/SokobanServices/add");
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.post(Entity.entity(boardAndSol, MediaType.TEXT_PLAIN));

	}

	private static String comprese(char[][] Board) {
		String compBoard = "";
		for (char[] cs : Board) {
			int count = 0;
			char ch = cs[0];
			for (char c : cs) {
				if (c == ch) {
					count++;
				} else {
					if (ch == '#')
						ch = 'w';
					compBoard += ch;
					compBoard += count;
					count = 1;
					ch = c;

				}
			}
			if (ch == '#')
				ch = 'w';
			compBoard += ch;
			compBoard += count;
			compBoard += "n";
		}

		return compBoard;
	}
}
