
import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

class Line implements Runnable, cons { //Defino la clase Line que hereda de cons, y de Runnable, por lo tanto los objetos de esta clase seran hebras.
	LinkedList <DatagramPacket> listrec; //Defino una lista de DatagramPacket.
	DatagramSocket sock; //Defino un DatagramSocket.
	PipedOutputStream o; //Defino un PipedOutputStream.
	Line (PipedOutputStream ot, LinkedList<DatagramPacket> ll,  DatagramSocket s) { //Defino el constructor de la clase.
		o= ot; //Guardo el PipedOutputStream recibido.
		listrec= ll; //Guardo la lista de DatagramPacket recibidos.
		sock= s;  //Guardo el DatagramSocket.
		Thread l = new Thread(this); //Defino la propia hebra.
		l.start(); //Y la pongo a trabajar.
	} //Constructor
	
	public void run () { //Defino el metodo run, que sera lo que hara la hebra cuando empieze a trabajar.
		while (true) { //Creo un bucle infinito.
			try { //Utilizo un Try/Catch.
				DatagramPacket rec = new DatagramPacket(new byte[516],516); //Creo un DatagramPacket.
				sock.receive(rec);  //Espero a que llegue un datagrama.
				synchronized (listrec){ //Utilizo un Synchronized para que las hebras realizen lo que hay a continuacion de una en una.
				listrec.addLast(rec);  //Anado el datagrama recibido en la lista.
				}//Synchronized
				o.write((byte)frame);
				o.flush(); //Lo vacio.
			} catch (IOException e) {System.out.println("Line: " + e);} //Si salta una excepcion imprimo el error.
		} //while
   } //run
} //Line
