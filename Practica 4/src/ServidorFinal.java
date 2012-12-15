/*
 * USO:		java ServidorFinal
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class ServidorFinal {
	private static final int CONNECTION_PORT = 18788;

	public static void main(String args[]) {
		int port = CONNECTION_PORT;
//		if(args.length > 0) port = Integer.parseInt(args[0]);
		
		ServerSocket serverSocket = null;
		try{
			serverSocket = new ServerSocket(port);
		} catch (IOException e) { System.out.println(e); }

		for(int i=0; i<3; i++){
			new ThreadServidorClase(serverSocket).start();
		}

	} // main
}

class ThreadServidorClase extends Thread{
	private ServerSocket serverSocket;
	private Socket connectionSocket;

	private DataInputStream EntradaBytes;
	private BufferedOutputStream SalidaBytes;
	private BufferedReader EntradaTexto;
	private PrintWriter SalidaTexto;

	public ThreadServidorClase(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		System.out.println("ServidorFinal	ID: "+ this.getId() + "		PORT: "  + serverSocket.getLocalPort());

	}

	@Override
	public void run(){
		String peticion;
		String nombrefichero;
		boolean finalizar = false;
		while(!finalizar) {
			try {
				// esperar peticiones de los clientes
				System.out.println("ServidorFTP_TCP esperando peticiones");
				connectionSocket = serverSocket.accept();
				// obtener los flujos de entrada y salida del socket
				// para la transferencia binaria
				EntradaBytes = new DataInputStream(connectionSocket.getInputStream());
				SalidaBytes= new BufferedOutputStream(
						connectionSocket.getOutputStream());
				// obtener los flujos de entrada y salida del socket
				// para la transferencia de texto
				EntradaTexto = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				SalidaTexto = new PrintWriter(connectionSocket.getOutputStream(), true);
				System.out.println("ServidorFTP_TCP, peticion de "+
						connectionSocket.getInetAddress());
				peticion = EntradaTexto.readLine();
				if (peticion.equals("DIR"))
					ListarFTP();
				else if(peticion.indexOf("PUT") == 0) {
					nombrefichero = peticion.substring(4, peticion.length());
					RecibirFichero(nombrefichero);
				} else if(peticion.indexOf("GET") == 0) {
					nombrefichero = peticion.substring(4, peticion.length());
					EnviarFichero(nombrefichero);
				}
				connectionSocket.close();
			} catch(Exception e) {
				System.out.println("Excepcion: " + e);
			}
			System.out.println("ServidorFTP_TCP, fin peticion");
		}// while(!finalizar)
	}// run

	void ListarFTP() {
		int i;
		File nomdir= new File(".");
		File[] listfich= nomdir.listFiles();
		try {
			// enviar la lista de ficheros
			for(i= 0; i < listfich.length; i++)
				if (listfich[i].isFile())
					SalidaTexto.println(listfich[i].getName());
			SalidaTexto.println("#FIN#");
			SalidaTexto.flush();
		} catch(Exception e) {
			System.out.println("Error en el listado del sitio FTP: "+e);
		}


	} // ListarFTP

	void EnviarFichero(String nombrefichero) {
		File fich= new File(nombrefichero);
		long tamano;
		try {
			if(fich.isFile()) {
				SalidaTexto.println("OK"); // enviar contestacion
				tamano = fich.length(); // obtener el tamaño del fichero
				SalidaTexto.println(tamano); // enviar el tamaño
				SalidaTexto.flush();
				String resp = EntradaTexto.readLine(); // lee READY ... O NO. porque no lo compruebo.
				EnviarBytes (fich, tamano); // enviar el fichero
			} else {
				SalidaTexto.println("ERROR");
			}
		} catch(Exception e) {
			System.out.println("Error en el envio del fichero: " + e);
		}
	} // EnviarFichero

	void EnviarBytes(File fich, long size) {
		try {
			BufferedInputStream fichbis= new BufferedInputStream(
					new FileInputStream(fich));
			/*
		// lee el fichero byte a byte
		int dato;
		System.out.println("leo el fichero byte a byte ");
		for(long i= 0; i< size; i++) {
		dato = fichbis.read();
		SalidaBytes.write(dato);
		}
			 */
			/*
		// lee el fichero por bloques de bytes
		int leidos= 0;
		byte[] buffer= new byte [1024];
		System.out.println("leo el fichero por bloques de bytes ");
		while (leidos!=-1) {
		leidos= fichbis.read (buffer);
		if (leidos!=-1)
		SalidaBytes.write (buffer, 0, leidos);
		}
			 */
			// lee el fichero completo de golpe
			DataInputStream fichdis= new DataInputStream (fichbis);
			System.out.println("leo el fichero completo de golpe ");
			byte[] buffer= new byte [(int) fich.length()];
			fichdis.readFully (buffer);
			SalidaBytes.write (buffer);
			SalidaBytes.flush();
			fichbis.close();
		} catch(Exception e) {
			System.out.println("Error en el envio del fichero binario: "+e);
		}
	} // EnviarBytes

	void RecibirFichero(String nombrefichero) {
		try {
			// leer el tamaño del fichero
			long tamano = Integer.parseInt(EntradaTexto.readLine());
			SalidaTexto.println("READY"); // enviar comando READY al cliente
			SalidaTexto.flush();
			RecibirBytes(nombrefichero, tamano);
		} catch(Exception e) {
			System.out.println("Error en la recepción del fichero: " + e);
		}
	} // RecibirFichero
	void RecibirBytes(String nomfich, long size){
		int dato;
		nomfich="copiaenservidor_"+nomfich;
		System.out.println("cambio nombre fichero a: "+nomfich);
		try {
			BufferedOutputStream fichbos= new BufferedOutputStream(
					new FileOutputStream(nomfich));
			for(long i= 0; i<size; i++) {
				dato = EntradaBytes.readByte();
				fichbos.write(dato);
			}
			fichbos.close();
		} catch(Exception e) {
			System.out.println("Error en la recepcion del fichero binario: " + e);
		}
	} // RecibirBytes
}







