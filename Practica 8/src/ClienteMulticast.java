/*
 * NOMBRE:	Daniel Iñigo Baños
 * UO: 		194823
 * DNI: 	53675885A
 *
 * USO:		java ClienteMulticast
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ClienteMulticast {
	static MulticastSocket s;
	
	public static void main (String args[]) throws IOException {
		byte[] recibido;
		InetAddress group = InetAddress.getByName("228.6.6.6");
		s = new MulticastSocket(7777);
		s.joinGroup(group);
		while(true) {
			recibeString();
		}
	} 
	
	/**
	 * Recibe una cadena de texto.
	 */
	private static void recibeString() {
		byte[] buffer = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		try {
			s.receive(dp);

			System.out.println(" recibido :" + new String(dp.getData()));
		} catch (IOException ex) { 	System.err.println(ex);} 
	}
}