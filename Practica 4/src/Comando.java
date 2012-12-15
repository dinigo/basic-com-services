/*
 * NOMBRE:	Daniel Iñigo Baños
 * UO: 		194823
 * DNI: 	53675885A
 *
 * Se implementan los comandos con un enum para mejorar la escalabilidad del programa
 * y hacerlo menos sensible a errores por culpa de escribir mal el comando.
 */



import java.util.Arrays;

public enum Comando {
	DIR("DIR"),
	PUT("PUT"),
	GET("GET"),
	OK("OK"),
	ERROR("ERROR"),
	READY("READY"),
	FIN("#FIN#");
	
	private String nombre;
	
    private Comando(final String nombre) {
        this.nombre = nombre;
    }
    
    /**
     * Devuelve el enum deseado o lanza una excepcion
     * @param nombreComando - Cadena de texto con la representacion del comando que se quiere
     * @return Comando
     */
    public static Comando getEnum(String nombreComando){
    	nombreComando = nombreComando.toUpperCase();
    	// si la cadena es "#FIN#" devuelve Comando.FIN.
        if(FIN.toString().equals(nombreComando)){
            return FIN;
        }
        // si la cadena corresponde al valor de alguno de los enum que quedan
        // se devuelve ese enum.
        else if(Arrays.asList(Comando.values()).contains(Comando.valueOf(nombreComando))){
        	return Comando.valueOf(nombreComando);
        }
        // si no se ha encontrado ninguna correspondencia debe
        // estar mal el nombre que se le pasa por parametro.
        throw new IllegalArgumentException("No hay ningun enum definido para esta cadena: "+nombreComando);
    }
    
	@Override
	public String toString(){
		return nombre;
	}
}
