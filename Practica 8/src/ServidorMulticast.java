/*
 * NOMBRE:	Daniel Iñigo Baños
 * UO: 		194823
 * DNI: 	53675885A
 *
 * USO:		java ServidorMulticast
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Realizar un programa que retransmita cada 5 segundos la hora
 *  	al grupo "228.6.6.6� de multicast
 * 		utilizando el puerto 7777
 * 		con TTL = 1
 */
public class ServidorMulticast {
	public static void main (String args[]) throws IOException {
		byte[] m;
		InetAddress group = InetAddress.getByName("228.6.6.6");
		MulticastSocket s = new MulticastSocket(7777);
		s.setTimeToLive(1);
		s.joinGroup(group);
		while(true) {
			m = getTime().getBytes();
			System.out.println(getTime());
			DatagramPacket hi= new DatagramPacket(m, m.length, group, 7777);
			s.send(hi);
			Thread t;
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	} 
	
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
