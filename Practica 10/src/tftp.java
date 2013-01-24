/*
 * NOMBRE:	Daniel Iñigo Baños
 * UO: 		194823
 * DNI: 	53675885A
 * 
 * USO:		java tftpservidor host get file
 */
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

public class tftp {
	public static void main (String args[]) throws IOException { 
		if (args.length!= 3){
			throw(new RuntimeException ("ERROR DE SINTAXYS")); 
		} 
		else 
			if (args[1].equals("get")){
				PipedOutputStream o = new PipedOutputStream(); 
				PipedInputStream i = new PipedInputStream(o); 
				LinkedList<DatagramPacket> listdp=new LinkedList<DatagramPacket>();
				DatagramSocket sock = new DatagramSocket(); 
				Timer ti = new Timer(o); 
				Line l = new Line(o, listdp, sock); 
				tftpcliente tf = new tftpcliente(args[2], args[0], i, ti, listdp, sock); 

			} else {System.out.println("ERROR DE COMANDO: "+args[0] + " "  +args[1]+ " "+args[2]);}
	} 
} 