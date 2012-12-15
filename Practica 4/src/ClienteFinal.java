/*
 * NOMBRE:	Daniel Iñigo Baños
 * UO: 		194823
 * DNI: 	53675885A
 * 
 * USO: 	java ClienteFinal <host_name | host_IP> <Comando> <file_name>
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
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Se conecta al host que se le pasa por parámetro, si no se conecta a localhost. Siempre por el puerto numero 7 (para echo).
 * Inicializa Readers y Writers
 */
public class ClienteFinal {
	//	private static final String CONNETCION_HOST = "localhost";
	private static final int CONNECTION_PORT = 18788;

	// Entrada/Salida
	//	private Socket socket;
	private static DataInputStream entradaBytes;
	private static BufferedOutputStream salidaBytes;
	private static BufferedReader entradaTexto;
	private static PrintWriter salidaTexto;

	public static void main (String args[]) {
		int port = CONNECTION_PORT;
		
//		// Obtencion de los parametros
//		String serverHost	= args[0];
//		int port			= Integer.parseInt(args[1]);
//		Comando comando 	= Comando.getEnum(args[2]);
//		String argumento	= (args.length>3)? args[3] : null;
		
		// Obtencion de los parametros como quiere el profe
		String serverHost	= args[0];
		Comando comando 	= Comando.getEnum(args[1]);
		String argumento	= (args.length>2)? args[2] : null;
		// inicializo el socket para poder cerrarlo si falla el try{}
		Socket clientSocket = null;
		try {
			clientSocket = new Socket(serverHost, port);

			// Inicializa los buffers de entrada/salida
			configuraIO(clientSocket);		
			// Ejecucion de la operacion
			ejecutaComando(clientSocket,comando,argumento);
		} catch (UnknownHostException e) { System.out.println("Error: Host desconocido: "+serverHost);
		} catch (IOException e) {System.out.println("Error: fallo en la comunicacion");
		} finally {
			if(clientSocket!=null){
				try{
					clientSocket.close();
				}catch (Exception e){ System.out.println("Error: no se pudo cerrar el socket");}
			}
		}
	}

	private static void configuraIO(Socket socket) {
		try {
			// obtener los flujos de entrada y salida del socket
			// para la transferencia binaria
			entradaBytes = new DataInputStream(socket.getInputStream());
			salidaBytes= new BufferedOutputStream(socket.getOutputStream());
			// obtener los flujos de entrada y salida del socket
			// para la transferencia de texto
			entradaTexto = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			salidaTexto = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {e.printStackTrace();}
	}

	private static void ejecutaComando(Socket clientSocket, Comando comando,
			String argumento) {
		switch(comando){
		case PUT:
			enviarFichero(argumento);
			break;
		case GET:
			recibirFichero(argumento);
			break;
		case DIR:
			dir(clientSocket);
			break;
		default:
			break;

		}

	}

	/**
	 * Envia el comando "DIR" y recibe una lista con los archivos
	 * en el directori ode trabajo. La lista termina con un comando
	 * "#FIN#".
	 */
	private static void dir(Socket clientSocket) {
		try {
			salidaTexto.println(Comando.DIR.toString());
			String linea = "";
			while(!(linea=entradaTexto.readLine()).equals(Comando.FIN.toString())){
				System.out.println("    " + linea);
			}
		} catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * Recibe el tamaño del archivo, lo crea y pide los bytes del archivo
	 */
	static void recibirFichero(String nombrefichero) {
		try {
			salidaTexto.println(Comando.GET.toString() + " " + nombrefichero);
			Comando cmd = Comando.getEnum(entradaTexto.readLine());
			System.out.println( );
			// si el fichero no existe se envia un mensaje de error y se termina la funcion
			if(cmd.equals(Comando.ERROR)){
				System.out.println("El archivo "+ nombrefichero + " no existe en el servidor");
				return;
			} else if(cmd.equals(Comando.OK)){
				long tamano = Integer.parseInt(entradaTexto.readLine());
				salidaTexto.println(Comando.READY.toString()); // enviar comando READY al cliente
				salidaTexto.flush();			
				recibirBytes(nombrefichero, tamano);
			}
		} catch(Exception e) {
			System.out.println("Error en el comando get: " + e);
		}

	}

	/**
	 * Rellena un archivo con bytes hasta que completa el tamaño que se
	 * indica por parametro
	 */
	static void recibirBytes(String nomfich, long size){
		int dato;	// variable auxiliar para 

		// como se van a ejecutar en el mismo directorio de trabajo esto ayuda a distinguir
		// los archivos de uno y de otro
		nomfich="copiaencliente_"+nomfich;
		System.out.println("cambio nombre fichero a: "+nomfich);

		try {
			// se reciben bytes hasta que se completa el archivo
			BufferedOutputStream fichbos= new BufferedOutputStream(
					new FileOutputStream(nomfich));
			for(long i= 0; i<size; i++) {
				dato = entradaBytes.readByte();
				fichbos.write(dato);
			}
			fichbos.close();
		} catch(IOException e) {
			System.out.println("Error en la recepcion del fichero binario: " + e);
		}
	}

	/**
	 * Envia el tamaño de un fichero y espera a que el servidor este listo para 
	 * mandarlo.
	 */
	static void enviarFichero(String nombrefichero) {
		File fich= new File(nombrefichero);
		long tamano;
		try {
			if(fich.isFile()) {
				salidaTexto.println(Comando.PUT.toString() + " " + nombrefichero); 	
				tamano = fich.length(); 		// obtener el tamaño del fichero
				salidaTexto.println(tamano); 	// enviar el tamaño
				salidaTexto.flush();
				String resp = entradaTexto.readLine(); // lee el comando
				if(Comando.getEnum(resp).equals(Comando.READY)) {
					enviarBytes (fich, tamano); 	// enviar el fichero
				}
			} else {
				salidaTexto.println(Comando.ERROR.toString());
			}
		} catch(Exception e) {
			System.out.println("Error en el envio del fichero: " + e);
		}
	} 

	/**
	 * Envia un fichero byte a byte hasta que completa el tamaño de bytes del archivo.
	 */
	static void enviarBytes(File fich, long size) {
		try {
			BufferedInputStream fichbis= new BufferedInputStream(
					new FileInputStream(fich));
			// lee el fichero completo de golpe
			DataInputStream fichdis= new DataInputStream (fichbis);
			System.out.println("leo el fichero completo de golpe ");
			byte[] buffer= new byte [(int) fich.length()];
			fichdis.readFully (buffer);
			salidaBytes.write (buffer);
			salidaBytes.flush();
			fichbis.close();
		} catch(Exception e) {
			System.out.println("Error en el envio del fichero binario: "+e);
		}
	}
}