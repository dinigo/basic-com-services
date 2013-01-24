import java.net.*; //Importo las librerias que voy a utilizar.
import java.io.*;

class frame implements cons {  

	InetAddress a;   
	int seqnum = 0;  
	int remoteTID;   
	DatagramSocket sock;  
	DatagramSocket auxsock;  
	DatagramPacket sent, rec;  

	/**
	 * Devuelve true si es la primera trama de datos.
	 */
	boolean firstDATAframe () { 
		return((code(rec)==DATA)&&(remoteTID==ServerPort));  
	} 

	String ch(int b) {  
		byte[] bb = {(byte) b}; return new String(bb); 
	} 

	int code(DatagramPacket d) { 
		return ((((d.getData())[0])<< 8)|((d.getData())[1])); 
	} 

	/**
	 * Devuelve el numero de secuencia
	 */
	int seqnum(DatagramPacket d) { 
		int a= d.getData()[2];  
		int b= d.getData()[3]; 
		a= a<0?a+256:a;
		b= b<0?b+256:b;
		return a*256+b;
	} //seqnum

	/**
	 * Devuelve los datos de un datagrama como array de bytes.
	 */
	byte[] dat(DatagramPacket d) { 
		byte[] b = new byte[(d.getLength() - 4)];  
		System.arraycopy(d.getData(), 4, b, 0, (d.getLength() - 4));
		return b;
	}  

	/*
	 * Obtiene el nombre de archvio solicitado en el datagrma.
	 */
	String file(DatagramPacket d) {  
		int i; byte[] b = d.getData(); 
		for (i = 2; b[i] != 0; i++){;};   
		return new String(b, 2, (i-2));  
	}  

	/**
	 * Obtiene el modo del datagrama.
	 */
	String mode(DatagramPacket d) { 
		int del, i; 
		byte[] b = d.getData(); 
		for (i = 2; b[i]!=0; i++){;};  
		del=i; 
		for (i = del+1; b[i]!=0; i++){;}; 
		return new String(b, (del+1), (i-(del+1)));  
	} 

	/**
	 * Obtiene la representación en String de un DatagramPacket que contiene un ERROR.
	 */
	String errormsg (DatagramPacket d) { 
		int i; 
		byte[] b = d.getData();  //Saco los datos del DatagramPacket y los guardo en un byte.
		for (i = 4; b[i]!=0; i++){}; //Me recorro el byte entero.
		return new String(b, 4, (i-4)); //Y Devuelvo como String el mensaje de error de una trama de ERROR.
	}//errormsg    

	/**
	 * Construyo un DatagramPacket con una trama RRQ
	 */
	DatagramPacket RRQ(String file, String type) throws IOException { 
		byte[] b =(ch(0)+ch(RRQ)+file +ch(0)+type+ch(0)).getBytes();
		return new DatagramPacket(b, b.length, a, remoteTID);
	}

	/**
	 * Construyo un DatagramPacket con una trama WRQ a partir de 
	 * un nombre de archivo y el tipo de archivo.
	 */
	DatagramPacket WRQ(String file, String type) throws IOException {
		byte[] b =(ch(0)+ch(WRQ)+file+ch(0)+type+ch(0)).getBytes(); 
		return new DatagramPacket(b, b.length, a, remoteTID);
	}

	/**
	 * Devuelve un datagrama con una trama ERROR con el codigo de error.
	 */
	DatagramPacket ERROR(int code, String msg) throws IOException{ 
		byte[] b = (ch(0) + ch(ERROR) + ch(0) + ch(code) + msg + ch(0)).getBytes(); 
		return new DatagramPacket(b, b.length, a, remoteTID); 
	}

	/**
	 * Construye una trama de datos a partir de un array de bytes y la longitud
	 * que se debe enviar
	 */
	DatagramPacket DATA (byte[] b, int l) throws IOException {
		b[0] = 0; 
		b[1] = DATA;
		b[2] = (byte) (seqnum/256);
		b[3] = (byte) (seqnum%256);
		return new DatagramPacket(b, l, a, remoteTID); 
	}

	/**
	 * Devuelve un datagrama con una trama ACK
	 */
	DatagramPacket ACK() throws IOException { 
		byte[] b = {0, ACK, (byte)(seqnum/256), (byte)(seqnum%256)}; 
		return new DatagramPacket(b, b.length, a, remoteTID); //Y lo retorno.
	}

	/**
	 * Envia el DatagramPacket y lo guarda en sent para la retransmision.
	 */
	void SS (DatagramSocket s, DatagramPacket p) throws IOException{
		s.send(sent = p); 
		printframe(p);
	}

	/**
	 * Imprime por pantalla la información contenida en un datagrama.
	 */
	void printframe (DatagramPacket dp) {
		byte[] b = dp.getData(); 
		if (code(dp) == RRQ) {
			System.out.println("RRQ" + " file=" + file(dp) + " mode=" + 
					mode(dp) + "   remotePort:" + dp.getPort()+" len:" +dp.getLength() 
					+ " Addr:" + dp.getAddress());};  
					if (code(dp) == WRQ) {  
						System.out.println("WRQ" + " file=" + file(dp) + " mode=" + 
								mode(dp) + "   remotePort:" + dp.getPort()+" len:" +dp.getLength() 
								+ " Addr:" + dp.getAddress());}; 
								if (code(dp) == DATA) {  
									System.out.println("DATA" + " seqnum=" + seqnum(dp));};  
									if (code(dp) == ACK) {  
										System.out.println("ACK" + " seqnum=" + seqnum(dp));};  
										if (code(dp) == ERROR) {  
											System.out.println("ERROR" + " error code=" + errormsg(dp));};  
	}

	/**
	 * Devuelve la representación en String del código del evento
	 */
	public String evento (int ev){
		switch (ev){ 
		case frame: return "frame";
		case close: return "close";
		case tout: return "time-out";
		default: return "evento no identificado"; 
		}
	}

	/**
	 * Devuelve la representación en String del código del estado
	 */
	public String estado (int st){
		switch (st){ 
		case espera: return "espera";
		case recibiendo: return "recibiendo";
		case acabando: return "acabando";
		default: return "estado no identificado";
		} 
	} 
}