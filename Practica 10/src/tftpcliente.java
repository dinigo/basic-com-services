/*
 * NOMBRE:	Daniel Iñigo Baños
 * UO: 		194823
 * DNI: 	53675885A
 * 
 * USO:		java tftpservidor host get file
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

class tftpcliente extends frame implements Runnable, cons {
   String file, host;  
   FileOutputStream o;  
   PipedInputStream i; 
   Timer t;  
   LinkedList<DatagramPacket> listrec;  
   int event, state = espera;  
	
   tftpcliente (String f, String h, PipedInputStream inp, Timer ti, LinkedList<DatagramPacket> ll ,DatagramSocket s) { //Constructor por defecto de la clase.
		file = f; 
		host = h;
		i = inp;  
		t = ti;  
		listrec= ll; 
		sock = s; 
		try {
			o = new FileOutputStream("copiaencliente_" + file); 
			a = InetAddress.getByName(host); 
			Thread tc = new Thread(this); 
			tc.start(); 
		} catch (UnknownHostException e) {System.out.println("tftpcliente excep.: "+e); 
		} catch (IOException e) {System.out.println("tftpcliente excepcion: " + e);} 
	}

   public void run () {
	   try { 
		   remoteTID = ServerPort; 
		   SS(sock, (sent=RRQ(file, "netascii")));	// Envio la RRQ
		   t.startTimers(); 						// Se ponen los timer
		   state = recibiendo;
		   
		   // Espera a que ocurra un evento.
		   while (state != espera) {
			   event = i.read();
			   switch (event) {
			   
			   case frame:
				   // Si se recibe una trama
				   synchronized (listrec) {rec=listrec.removeFirst();}	// Elimino la peticion procesada
				   if (firstDATAframe()){remoteTID=rec.getPort();} 		// Si es la primera trama guardo el timer.
				   // Si la trama no tiene el puerto adecuado es que no es del servidor.
				   if (rec.getPort() != remoteTID){
					   SS(sock, ERROR(0, "wrong TID"));
					   break;}
				   // Si es una trama de datos y coincide el numero de secuencia escribo en fichero
				   // Si no es el num de secuencia pongo a contar los timer.
				   // Envia el asentimiento.
				   if (code(rec)==DATA){ 
					   if ((seqnum(rec)) != seqnum) { 
					   seqnum=seqnum(rec);
					   t.startTimers(); 
					   o.write(dat(rec));}
					   SS(sock, (sent=ACK())); 
					   if (state == recibiendo) {t.startTout();} // Enciendo el Timer de TimeOut.
					   if (rec.getLength() < 516) {state =acabando; t.stopTout();} // Si no coincide con el tamaño del array es que se han acabado los datos (Estado --> acabado).
				   }
				   if (code(rec)==ERROR) {state = espera; t.stopTimers();}  
				   break;
				     
			   case close:
				   // Si se cierra la conexion se pone el estado de espera
				   state = espera; 
				   break;
				   
			   case tout:
				   // Si se llega al TimeOut se reenvia la trama y se reinicia el contador.
				   if (state == recibiendo) { SS(sock, sent); t.startTout();} 
				   break;
			   default: break;
			   }
		   }
		   o.close();
	   	} catch (IOException e){};
		}
	}