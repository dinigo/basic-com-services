import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;


public class NumConnectionsServer {

	public static int DEFAULT_PORT = 18889;
	public static void main(String[] args) {
		// -----------------
		// INICIALIZACIÓN
		// -----------------
		int port;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception ex) { port = DEFAULT_PORT; }
		Byte numconnect=new Byte((byte)0);
		ServerSocketChannel serverChannel;
		Selector selector;
		try {
			serverChannel = ServerSocketChannel.open();
			ServerSocket ss = serverChannel.socket();
			InetSocketAddress address = new InetSocketAddress(port);
			ss.bind(address);
			serverChannel.configureBlocking(false);
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}			
		// -----------------
		// METODO "RUN"
		// -----------------
		while (true) {

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) { e.printStackTrace(); 	}
			try {
				selector.select();
			} catch (IOException ex) {
				ex.printStackTrace();
				break;
			}

			Set readyKeys = selector.selectedKeys();
			Iterator iterator = readyKeys.iterator();
			// recorre todas las keys que tiene y mira que operación tiene que hacer con cada una
			int k = 1;
			while (iterator.hasNext()) {

				SelectionKey key = (SelectionKey) iterator.next();
				iterator.remove();
				try {
					/*
					 * Si es la clave de que se acepta un cliente nuevo 
					 * se incrementa el numero de conexiones. Se acepta 
					 * la conexión y se le registra a ese cliente una clave
					 * de lectura o escritura.
					 * 
					 * Despues se le asigna al cliente un buffer de bytes.
					 */
					if (key.isAcceptable()) {
						ServerSocketChannel server= (ServerSocketChannel) key.channel();
						SocketChannel client = server.accept();
						numconnect++;
						System.out.println("Accepted connection "+numconnect);
						client.configureBlocking(false);
						SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);
						ByteBuffer buffer = ByteBuffer.allocate(100);
						clientKey.attach(buffer);
					}

					/*
					 * Si la clave es de escritura: 
					 */
					if (key.isWritable()) {
						String respuesta = numconnect + " - " + getTime();
						
						// solo para log
						if(k==1)System.out.println("respuesta :" +respuesta);
						System.out.println("    key:" + k ); k++;
						
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer output = (ByteBuffer) key.attachment();
						output.put(respuesta.getBytes());
						output.put("\r\n".getBytes());
						output.flip();
						client.write(output);
						output.compact();	
					}
				}
				catch (IOException ex) {
					key.cancel();
					System.out.println("desactivo conexion, quedan "+--numconnect);
					try {
						key.channel().close();
					} catch (IOException cex) { System.out.println("Exception: "+ cex);}
				}
			} // while iterator
		} // while true
	} // main

	/**
	 * Devuelve una cadena de texto con la hora, minutos y segundos
	 * en la que se invoca el método.
	 * @return hora:minutos:segundos
	 */
	private static String getTime(){
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cal.getTime());
	}
}
// class miEchoServernb
