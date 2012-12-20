import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;





public class FinalClient {
	public static void main (String args[]) {

		String host = "localhost", theLine;
		//if (args.length > 0) { host=args[0]; }
		//System.out.println(""+ host);

		int port = FinalServer.CONNECTION_PORT;
		if(args.length > 0) port = Integer.parseInt(args[0]);
		try {
			Socket s = new Socket(host,port);
			System.out.println("CLIENTE CONECTADO : "+ s.getInetAddress().toString() + ":" + s.getPort());

			LineNumberReader netIn = new LineNumberReader( new InputStreamReader(s.getInputStream()));
			PrintWriter netOut = new PrintWriter(s.getOutputStream(), true);
			LineNumberReader sysIn = new LineNumberReader( new InputStreamReader(System.in));
			
			while (true) {
				System.out.print(">>");
				theLine = sysIn.readLine();
				System.out.print("<<");
				if (theLine.equals(".")){
					break;
				}
				netOut.println(theLine);
				System.out.println(netIn.readLine());
			}
		} catch (UnknownHostException e) { System.out.println(e);
		} catch (IOException e) {System.out.println(e);}
		System.out.println("fin");

	}
}
