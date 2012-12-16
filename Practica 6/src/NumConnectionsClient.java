import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;


public class NumConnectionsClient {
	public static int DEFAULT_PORT = 18889;
	public static void main(String[] args) {
		// consigue los parametros
		String host= "localhost";
		int port;
		if (args.length>0) host=args[0];
		
		
		try {
			// se abren los canales
			SocketAddress addr		= new InetSocketAddress (host, DEFAULT_PORT);
			SocketChannel client	= SocketChannel.open(addr);
			
			// se abren los buffers 
			ByteBuffer buffernet 	= ByteBuffer.allocate(100); //leer del servidor
			WritableByteChannel out	= Channels.newChannel(System.out);
			client.configureBlocking(false); // socket no bloqueante
			System.out.println("CONECTADO		: " + host + " " + DEFAULT_PORT);
			System.out.println("MODO BLOQUEO		: " + client.isBlocking());
			System.out.println("CONECTADO		: " + client.isConnected());
			System.out.println("REGISTRADO		: " + client.isRegistered());

			while (true) {

				int numBytesLeidos= client.read(buffernet); //lee de servidor en el buffer buffernet
				if (numBytesLeidos>0) {

					System.out.print("    leidos " + numBytesLeidos + " (bytes)");

					buffernet.flip();
					out.write(buffernet);
					buffernet.clear();

				}
			} //while
		} catch (IOException ex){ ex.printStackTrace(); }
	} //main
} //class miEchoClientnb

