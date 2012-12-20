import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServidorBitAlternante {
	static byte Idle=0, WaitAck=1;

	public static void main (String args[]) {
		boolean salir=false;
		String host = "localhost";
		int remotePort=7; //puerto inicial del servidor
		byte state=0;
		int numlin=0;
		if (args.length > 0) { host=args[0]; }
		try {
			InetAddress a = InetAddress.getByName(host);
			DatagramSocket theSocket = new DatagramSocket();
			DatagramPacket dpout= null;
			while (!salir) {
				theSocket.receive(dpout);
				EchoThreadBitAlternante echo = new EchoThreadBitAlternante(state, Idle, WaitAck, dpout);
			} //while
			theSocket.close();
		} catch (UnknownHostException e) {System.err.println(e);
		} catch (IOException e) {} } }


/*
 * Clase que implementa el "echo" concurrente para delegar desde
 * la clase principal.
 */
class EchoThreadBitAlternante extends Thread{
	

	private int state;
	private int Idle;
	private int WaitAck;
	private DatagramPacket dpout;

	public EchoThreadBitAlternante(byte state, byte Idle, byte WaitAck, DatagramPacket dpout) {
		this.state = state;
		this.Idle = Idle;
		this.WaitAck = WaitAck;
		this.dpout = dpout;
	}

	@Override
	public void run() {
		if (state==Idle) {
			System.out.println("introduzca linea (acabar con '.'):");
			String line= lin.readLine();
			if (line.equals(".")) salir=true; //envía al servidor la línea final
			byte[] dataenv={(byte)(numlin%2)};
			line= (new String (dataenv))+line;
			dataenv= line.getBytes();
			dpout= new DatagramPacket(dataenv, dataenv.length, a, remotePort);
			theSocket.send(dpout); // envia un datagrama al servidor y
			state=WaitAck; // cambia de estado para esperar asentimiento
		} else {
			System.out.println("recibido ack NO esperado: "+ datarec[0]+" - retranmision");
			theSocket.send(dpout);
		}
	}



}