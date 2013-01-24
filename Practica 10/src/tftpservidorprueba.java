import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;



class tftpservidorprueba extends frame implements Runnable {
   FileInputStream i;
   tftpservidorprueba() {
	   Thread ts = new Thread(this); 
	   ts.start();
   }
   
int sendDATA () throws IOException {
int len; byte b[] = new byte[516];
len = i.read(b, 4, 512);
SS(auxsock, DATA(b, (len+4)));
this.seqnum++;
return len;
   }  // env�a tramas de datos del fichero

public static void main(String[] args){};

   public void run() { 
try {
		boolean salir=false;
	   	seqnum = 1;  // inicializar n�mero de secuencia
	   	rec= new DatagramPacket(new byte[516], 516);
	   	sock = new DatagramSocket(ServerPort);  // socket para RRQ
	   	auxsock = new DatagramSocket();  // socket para resto de com.
	   	sock.receive(rec);  // espera primer RRQ
	   	i = new FileInputStream(file(rec)); 
	   	remoteTID=rec.getPort(); 
	   	a=rec.getAddress();
	   	DatagramPacket auxrec= new DatagramPacket(new byte[516], 516);
	   	int l = sendDATA();  // env�a datos
	   	while(!salir){
	   			if(l < 512){
	   			salir=false;
	   			}
	   			else{
	   				auxsock.receive(auxrec);
	   				System.out.println("Seqnum es " + (seqnum-1));
	   				if (seqnum(auxrec)==(seqnum-1)){
	   					l = sendDATA();
	   				}
	   				else{System.out.println("ACK Incorrecto");}
	   			}
	   	}
} catch (IOException e) {System.err.println("Fallo");
   }
}
}
   