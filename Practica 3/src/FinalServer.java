/*
 * NOMBRE: Daniel Iñigo Baños
 * UO: 194823
 * DNI: 53675885A
 *
 * Servidor de "echo" multihilo a partir del codigo de ejemplo
 * proporcionado en la practica 3. Los hilos se gestionan desde
 * una piscina de hilos con el "ejecutor" de JAVA
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/*
 *Clase test para probar el ServerSocket
 */
public class FinalServer {
	private static final int NUM_THREADS = 3;
	public static final int CONNECTION_PORT = 17777;


	public static void main(String args[]){
		// usa el puerto por defecto CONNECTION_PORT o uno que se le pase por parametrod
		int port = CONNECTION_PORT;
		if(args.length > 0) port = Integer.parseInt(args[0]);
		
		// crea tantos hilos como estan especificados en la variable gloval NUM_THREADS
		try {
			ServerSocket serverSocket = new ServerSocket (port);
			for(int i=0 ; i<NUM_THREADS; i++){
				ServerThread1 serverThread = new ServerThread1(serverSocket);
				serverThread.start();
				System.out.println("Servidor n:" + (i+1) + "    ID:"+serverThread.getId());
			}
		} catch (IOException e) {  e.printStackTrace(); 	}
	}
}   

/*
 *Clase que ejecuta un servidor sobre un sobre un ServerSocket que se
 *le pasa por parametro.
 */
class ServerThread extends Thread{
	private final ServerSocket serverSocket;

	public ServerThread(ServerSocket s){
		this.serverSocket = s;
		System.out.println("SERVIDOR A LA ESCUCHA POR EL PUERTO: "+ serverSocket.getLocalPort());
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket c = serverSocket.accept();

				// inicializa los buffers de entrada por el socket conectado
				BufferedReader in = new BufferedReader(new InputStreamReader( c.getInputStream()));
				PrintWriter out = new PrintWriter(c.getOutputStream(), true);

				// realiza la funcion de "echo", es decir reenvia el mensaje que recibe
				echo(in, out);
			}
		} catch (IOException e) { System.err.println(e);
		} finally{
			// siempre se intenta cerrar el ServerSocket. Aunque haya una excepcion.
			try {
				serverSocket.close();
			} catch(IOException e) {System.out.println("Error: no se pudo cerrar el socket");}
		}
	}

	/**
	 * Realiza las funciones de E/S a partir de los buffers
	 */
	private void echo(BufferedReader in, PrintWriter out){
		System.out.println("ID:"+ this.getId()+"		CONEXION ACEPTADA");
		String line = "";
		try {
			while((line=in.readLine()) != null){
				System.out.println("ID:"+ this.getId()+"		MENSAJE: " 	+ line);
				out.println(line);
			}
			System.out.println("ID:"+ this.getId()+"		CONEXION TERMINADA");

		} catch(IOException e) {System.out.println("Error: no se pudo leer desde el cliente");}
	}
}
