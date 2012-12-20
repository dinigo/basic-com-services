/*
 * NOMBRE: 	Daniel Iñigo Baños
 * UO: 		194823
 * DNI:		53675885A
 *
 * Clase que busca una palabra en una serie de archivos y escribe los
 * resultados por pantalla y en otro archivo "resultados.txt".
 * El callback de los hilos que lanza se ha hech seguro para que no
 * se impriman los reulstados antes de que acaben todas las hebras.
 * 
 * USO: 	java Buscador HOLA
 * 			java Buscador ./ HOLA
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Buscador {
	public static final String NOMBRE_ARCHIVO_RESULTADOS = "resultados.TXT";
	private int numHebras;
	private boolean todasHebrasFuncionando;
	private final String palabra;
	private final File directorio;
	
	/** Constructor completo */
	public Buscador(String palabra, String directorio){
		this.numHebras = 0;
		this.todasHebrasFuncionando = false;
		this.palabra = palabra;
		this.directorio = new File(directorio);
		String [] archivos = this.directorio.list();
		System.out.println("DIRECTORIO SELECCIONADO: "+ this.directorio.getAbsolutePath());
		System.out.println("ARCHIVOS:");
		for(int i=0; i<archivos.length; i++) {
			System.out.println("    "+ archivos[i]);
		}
	}
	
	/** 
	 * Constructor en caso de que no se disponga de
	 * una carpeta especifica para la busqueda
	 */
	public Buscador(String palabra){
		this(palabra, ".");
	}

	public static void main(String args[]) {
		System.out.println("DIRECTORIO DE TRABAJO: " + new File(".").getAbsolutePath());
		Buscador mBuscador = null;
		if(args.length == 1){
			mBuscador = new Buscador(args[0]);
		}
		else if(args.length == 2){
			mBuscador = new Buscador(args[1], args[0]);
		}
		mBuscador.buscar();
	}

	/**
	 * Inicia tantos hilos como archivos de texto hay en el directorio
	 * (solo los archivos que terminan en txt)
	 * @param palabra 
	 */
	public void buscar(){
		// GESTIONA EL ARCHIVO RESULTADOS.TXT
		File resultados = new File(NOMBRE_ARCHIVO_RESULTADOS);
		if(resultados.exists()) resultados.delete();
		try {
			resultados.createNewFile();
		} catch (IOException e) {e.printStackTrace();}
		
		// LANZA HEBRAS PARA LOS ARCHIVOS VALIDOS
		File [] listado= this.directorio.listFiles();
		for (int i=0; i<listado.length; i++){
			if(listado[i].getName().endsWith("txt")){
				new Localizador(listado[i], this.palabra, this).start();
				this.numHebras++;
			}
		}
		this.todasHebrasFuncionando = true;
	}
	
	/**
	 * Cuando se invoca decrementa el contador de hebras en funcionamiento.
	 * Cuando el contador llega a cero se muestra el resultado de la búsqueda
	 * 
	 * IMPORTANTE!: Este metodo usa un flag "todasHebrasFuncionando" para que
	 * aunque la primera hebra (por ejemplo) nada mas se la invoca (numHebras 0 -> 1, numHebras 1 -> 0)
	 * no se imprima el archivo.
	 */
	public void complete() {
		this.numHebras--;
		if(this.numHebras==0 && this.todasHebrasFuncionando){
			printResultados();
		}
		
	} 
	
	/**
	 * Lee el archivo resultados y lo imprime por pantalla
	 * linea a linea
	 */
	private void printResultados() {
		try{
			BufferedReader fichBR= new BufferedReader(new FileReader(NOMBRE_ARCHIVO_RESULTADOS));
			String linea = "";
			while ((linea=fichBR.readLine())!=null){
				System.out.println(linea);
			}
			fichBR.close();
		}catch (Exception e){};

	}
} 
