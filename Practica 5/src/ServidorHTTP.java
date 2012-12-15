/*
 * USO: java ServidorHTTP <directorio_base> <puerto> <nombre_archivo_index>
 */




import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;


public class ServidorHTTP {
	private static final int NUM_THREADS = 10;
	private File baseDirectory;
	private String indexFileName= "index.html";
	private ServerSocket server;


	public ServidorHTTP(File basedir, int port, String indexfich) {
		try {
			// Asignar los valores del constructor
			baseDirectory= basedir;
			if (!baseDirectory.isDirectory()) {
				throw new IOException(baseDirectory + " no es un directorio");
			}
			indexFileName = indexfich;
			server = new ServerSocket(port);

			// Instanciar las hebras que escuchan
			for (int i = 0; i < NUM_THREADS; i++) {
				Thread t = new Thread( new RequestHTTP (baseDirectory, indexFileName));
				t.start();
			}
			// Servir
			while (true) {
				try {
					Socket request = server.accept();
					RequestHTTP.processRequest(request);
				} catch (IOException ex) {}
			}
		} catch (IOException ex) {
			System.out.println("jhttp1 Excepcion: " + ex);
		}
	}


	public static void main(String[] args) {
		System.out.println("PATH PROGRAMA: "+ new File(".").getAbsolutePath());
		System.out.println("PATH BASE:     "+args[0]);
		File baseDir;
		String nomfich= "index.html";
		int port;
		try {
			baseDir = new File(args[0]);
		} catch (Exception ex) {
			System.out.println("Uso: java jhttp1 directorio_base [port indexfile]");
			return;
		}
		try {
			port = Integer.parseInt(args[1]);
			if (port < 0 || port > 65535) port = 80;
		} catch (Exception ex) { port = 80;}
		if (args.length==3) nomfich=args[2];
		try {
			ServidorHTTP webserver = new ServidorHTTP(baseDir,port,nomfich);
		} catch (Exception ex) {
			System.out.println("jhttp1 server no arranca por: ");
			System.out.println(ex);
		}
	}
}
