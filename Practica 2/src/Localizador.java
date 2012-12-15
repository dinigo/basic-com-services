/*
 * NOMBRE: Daniel Iñigo Baños
 * UO: 194823
 * DNI: 53675885A
 *
 * Clase que se instancia como hebra para buscar en un archivo.
 * Una vez concluida la búsqueda y escritura síncrona en el archivo
 * se llama al callback.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Localizador extends Thread{
	private File archivo;
	private String palabra;
	private Buscador callback;

	public Localizador (File archivo, String palabra, Buscador callback){
		this.archivo = archivo;
		this.palabra = palabra;
		this.callback = callback;
	}

	/**
	 * Busca de forma asincrona (mezclandose con los demas hilos)
	 * y escribe en el fichero de forma sincrona (reteniendolo).
	 */
	public void run(){
		List<String> apariciones = getApariciones();
		synchronized(archivo){
			write(apariciones);
		}
		callback.complete();
		//System.out.println( "END: "+this.getId());
	}

	/**
	 * Busca las apariciones de la palabra especificada y las mete en una lista
	 * junto con los demas datos.
	 * @return apariciones - Lista de las apariciones de la palabra. Contiene el archivo y la linea en cada posicion
	 */
	private ArrayList<String> getApariciones() {
		ArrayList <String> apariciones = new ArrayList<String>();
		try{
			BufferedReader fichBR= new BufferedReader(new FileReader(archivo));
			String linea = "";
			int numLinea = 0;
			while (linea!=null){
				if(linea.contains(palabra)){
					apariciones.add(archivo.getName() + " - " + numLinea + "\n" + linea);
				}
				linea=fichBR.readLine();
				numLinea++;
			}
			fichBR.close();
		}catch (Exception e){};

		return apariciones;
	}
	
	/**
	 * Escribe las diferentes apariciones en un archivo
	 * @param apariciones - Lista de Strings con el contenido a escribir
	 */
	private void write(List<String> apariciones){
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Buscador.NOMBRE_ARCHIVO_RESULTADOS, true)));
			//System.out.println("NUMERO APARICIONES: "+ apariciones.size());
			for(int i=0; i<apariciones.size(); i++){
				//System.out.println("ID: "+this.getId()+" - "+(String)apariciones.get(i));
				out.println((String)apariciones.get(i));
			}
			out.flush();
			out.close();
		} catch (IOException e) {}
	}
}
