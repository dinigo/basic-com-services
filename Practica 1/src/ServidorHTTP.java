/*
 * NOMBRE:	Daniel Iñigo Baños
 * UO: 		194823
 * DNI: 	53675885A
 *
 * Clase que lee una cabecera y en funcion del MIME-TYPE del archivo solucitad
 * se compone una respuesta que contiene el archivo, si es de texto, o un "200 OK" si
 * existe, o un codigo de error en caso de que no exista.
 * Si la solicitud no pide ningun archivo el archivo que se buscará por defecto
 * será el "index.html".
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;


public class ServidorHTTP {
	public static void main (String[] args){
		
		String cabecera= "";
		String contenido = "";

		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));    	// Abierto un buffer al archivo con las instrucciones
			String primeraLinea = reader.readLine();                                    // Carga la primera linea del archivo
			reader.close();																// Cierra el buffer (no lo uso mas)
			if(getComando(primeraLinea).equalsIgnoreCase("GET")){						// Si el comando que contiene la primera linea es GET
				String archivoSolicitado = getArchivo(primeraLinea);					// se coge el nombre del archivo de la primera linea
				if(archivoSolicitado == null){
					cabecera = getCabecera("404 File not Found",archivoSolicitado);
				}else{
					cabecera = getCabecera("200 OK",archivoSolicitado);

					// Aqui se tratan los diferentes MIME-TYPES
					if(getMimeType(archivoSolicitado).startsWith("text")){				//		y si es un archivo de texto
						contenido = printArchivo(archivoSolicitado);								//		se ense�a por pantalla
					}
				}
			} else System.out.println("SOLO IMPLEMENTADO EL COMANDO \"GET\"");								// Si no se pide un comando conocido no se hace nada
		}catch (Exception e){
			System.out.println("Error: "+e);
		}
		

		// Muestra la peticion
		System.out.println("____________________________");
		System.out.println(printArchivo(args[0]));
		System.out.println("____________________________");
		// Mestra la cabecera de la respeusta y el contenido en caso de que se pidiera un archivo de texto
		System.out.println(cabecera);
		System.out.println();
		System.out.println(contenido);

	}

	/**
	 * Comprueba el mimeType de un archivo y lo devuelve
	 * @param codigoRespuesta - String con el numero y el significado del código de respuesta
	 * @param nombreFichero - Nombre del archivo solicitado
	 */
	static String getCabecera (String codigoRespuesta, String nombreFichero){
		String cabecera = "";

		// Protocolo
		cabecera += ("HTTP/1.0 "+codigoRespuesta+"\n");

		// Fecha
		Date now=new Date();
		cabecera += ("Date:"+now+"\n");
		cabecera += ("Server: servidor");

		// Mime
		
		if(nombreFichero != null){
			String mimeType = getMimeType(nombreFichero);
			cabecera += ("\n"+"Content-Type: "+ mimeType);

		}
		
		return cabecera;
	}

	/**
	 * Devuelve el comando HTTP contenido en la primera linea de la peticion
	 */
	private static String getComando(String linea){
		return linea.split(" ")[0];
	}

	/**
	 * Devuelve el nombre del archivo solicitado o al archivo por defecto si no se solicita ninguno
	 * Si el archivo solicitado no existe en el directorio se devuelve un null
	 * @param linea - primera linea del archivo donde se encuentran las instructiones
	 * @return nombre - nombre del archivo si existe, null si no existe o index si no se especifica ninguno
	 */
	private static String getArchivo(String linea){
		// por alguna razon al crear un objeto File no reconoce los nombres de archivo que empiezan por "/". 
		// Así "/texto.txt" no funciona, pero "texto.txt" sí.
		// Soluciono esto cogiendo la string a continuacion del primer caracter
		String nombeArchivo =  (linea.split(" ")[1].equals("/"))? "index.html" : linea.split(" ")[1].substring(1);
		if((new File(nombeArchivo)).isFile()){
			return nombeArchivo;
		} else return null;
	}

	/**
	 * Devuelve el mimetype del archivo en funcion de su extension en formato String
	 * @param nombreArchivo - nombre del archivo o ruta que contenga su nombre
	 * @ret String
	 */
	private static String getMimeType(String nombreFichero){
		String mimeType;
		if(nombreFichero.endsWith(".html")||nombreFichero.endsWith(".htm")){
			mimeType = "text/html";
		}else if(nombreFichero.endsWith(".jpg")||nombreFichero.endsWith(".jpeg")){
			mimeType = "image/jpeg";
		}else mimeType = "text/plain";
		return mimeType;
	}

	/**
	 * Imprime un archivo de texto por pantalla linea a linea
	 */
	static String printArchivo (String archivo){
		String contenidoArchivo = "";
		try{
			FileReader fich= new FileReader(archivo);
			BufferedReader fichBR= new BufferedReader(fich);
			String linea=fichBR.readLine();
			while (linea!=null){
				contenidoArchivo += linea + "\n";
				linea=fichBR.readLine();
			}
			fichBR.close();
		}catch (Exception e){};
		return contenidoArchivo;
	}
}
