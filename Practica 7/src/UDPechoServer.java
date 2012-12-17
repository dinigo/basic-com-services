import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPechoServer {
	public static void main (String args[]) throws Exception {
		byte d[] = new byte[1024];
		DatagramPacket dp = new DatagramPacket(d, d.length);
		DatagramSocket ds = new DatagramSocket(1255);
		System.out.println("SERVIDOR ECHO UDP ");
		System.out.println("    puerto:" + ds.getLocalPort() );
		System.out.println("    host:" + ds.getLocalAddress());
		while (true) {
			try {
				ds.receive(dp);
				System.out.println();
				System.out.println("    from    		: " + dp.getAddress().toString() + " : " + dp.getPort());
				System.out.println("    mensaje bytes	: " + dp.getData());
				System.out.println("    mensaje String	: " + new String(dp.getData()));
				ds.send(dp);
			} catch (Exception e) {}
		}
	}
}