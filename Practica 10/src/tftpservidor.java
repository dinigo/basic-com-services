/*
 * NOMBRE:	Daniel Iñigo Baños
 * UO: 		194823
 * DNI: 	53675885A
 * 
 * USO:		java tftpservidor host get file
 */


import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;


class tftpservidor extends frame{
	public static void main(String[] args){
		System.out.println("PUERTO: " + ServerPort);

		// Inicializo los Socket y Packet y espero una peticion para lanzar una hebra que lo procese.
		while(true){
			try{ 
				DatagramPacket rec= new DatagramPacket(new byte[516], 516); //paquetes de 516 bytes... 
				DatagramSocket sock = new DatagramSocket(ServerPort);
				sock.receive(rec);
				System.out.println("GET: "+ rec);
				ServidorThread hs = new ServidorThread(rec);
				new Thread(hs).start();
			}catch (Exception e){}
		}
	}
}

class ServidorThread extends frame implements Runnable, cons{

	FileInputStream i;
	LinkedList<DatagramPacket> listrec=new LinkedList<DatagramPacket>();
	int event, state;
	PipedOutputStream o;
	PipedInputStream pi;

	/*
	 * Intenta abrir un InputStream la ruta de mismo nombre que el archivo que se pidió.
	 * Recupero el puerto y dirección del cliente.
	 */
	ServidorThread(DatagramPacket dp) {
		try{
			i = new FileInputStream(file(dp));
			remoteTID = dp.getPort();
			a=dp.getAddress();
		}catch (IOException e){}
	}

	/**
	 * Lee hasta 512 bytes de archivo (o menos), crea una trama nueva y la pone a enviar al socket del cliente.
	 * En caso de que falle el envio se reenviara.
	 */
	int sendDATA () throws IOException {
		int len; byte b[] = new byte[516];
		len = i.read(b, 4, 512);  
		SS(auxsock, DATA(b, (len+4)));  
		this.seqnum++;  
		return len;  
	} 

	public void run() { 
		try {
			// INICIALIZACIÓN
			PipedOutputStream o = new PipedOutputStream(); 
			PipedInputStream pi = new PipedInputStream(o); 
			Timer t = new Timer(o);
			seqnum = 1; 
			auxsock = new DatagramSocket();
			Line li = new Line(o,listrec,auxsock); 
			DatagramPacket auxrec= new DatagramPacket(new byte[516], 516);

			// ENVIO
			System.out.println("Enviando ...");

			int l = sendDATA();  
			t.startTimers();
			state=recibiendo; 
			if(l<512){
				state = espera;
				t.stopTout(); 
				System.out.print("OK\n");
			}

			// TRATAMIENTO DE EVENTOS.
			while(state != espera){  
				event=pi.read(); 
				switch(event){  
				case frame: 
					synchronized(listrec){auxrec=listrec.removeFirst();} 
					if (auxrec.getPort() != remoteTID) 
					{SS(sock, ERROR(0, "wrong TID"));
					System.out.println("La trama no es del cliente"); 
					break;} 
					if (code(auxrec)==ACK){ 
						if ((seqnum(auxrec)) != (seqnum-1)) { 
							t.startTimers(); 
						}
						l = sendDATA();  
						if (state == recibiendo) {t.startTout();}  
						if (l < 512) {state =espera; 
						t.stopTimers();  
						System.out.println("OK\n");  
						} //if
					} //if
					if (code(auxrec)==ERROR) {state = espera;
					t.stopTimers();
					System.out.println(""); //Imprimo un mensaje informativo por pantalla 
					} //if 
					break;
					
				case close: 
					state = espera; 
					break;

				case tout:
					if (state == recibiendo) { l=sendDATA(); t.startTout();}
					break;
				default: break; 
				} //switch
			} //while
		} catch (IOException e) {System.err.println("Fallo");} // Si algo no funciona correctamente imprimo un mensaje informativo indicando que hubo un fallo.
	}//run
} //HebraServidor

