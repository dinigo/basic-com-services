import java.io.*; //Importo las librerias que voy a utlizar.

class Timer implements Runnable, cons { //Defino la clase Timer que hereda de cons, y de Runnable, por lo tanto los objetos de esta clase seran hebras.
	static final int plazo=2, fin=4; //Defino dos variables int y las inicializo.
	int[] timer = {0,0} ;  //Defino un array con contadores de los dos timers.
	PipedOutputStream o;  
	
	Timer (PipedOutputStream ot) { //Defino el constructor de la clase.
		o = ot; //Guardo el PipedOutputStream recibido
		Thread t = new Thread(this); //Creo la hebra.   
		t.start(); //Y la pongo a trabajar.
	} //Timer constructor
	
	synchronized void startTout () { timer[tout] = plazo; } //Defino el metodo StartTout, que inicia el TimeOut.
	
	synchronized void stopTout () { timer[tout] = 0; } //Defno el metodo StopTout, que detiene el TimeOut.
	
	synchronized void startTimers (){ //Defino el metodo StartTimers.
		timer[tout] = plazo; //Arranca los dos timers.
		timer[close] = plazo*fin;
	} //StartTimers
	
	synchronized void stopTimers ()  { //Defino el metodo StopTimers.
		timer[tout] = 0; //Para los dos timers.
		timer[close] = 0;
	} //StopTimers
	
	public void run () { //Defino el metodo run, que sera lo que hara la hebra cuando empieze a trabajar.
		try { //Utilizo un Try/Catch
			while (true) { //Creo un bucle infinito
				Thread.sleep (1000);  //Espero 1 segundo.
				synchronized (this) { //Utilizo un Synchronized para que las hebras vayan de una en una.
					for (int i=0; i < timer.length; i++) {  //Compruebo todos los timers.
						if (timer[i] > 0) //En el caso de que haya tiempo restante en el timer.
							if (--timer[i] == 0) {  //Decremento el timer[i] y comparo.
								o.write((byte)i); o.flush();  //Si es cero envio el timeout.
							}//if
						}//for
					}//synchronized 
				}//while
		} catch (IOException e) {System.out.println("Timer: " + e); //Capturo las interrupciones.
		} catch (InterruptedException e) {System.out.println("Timer"+e); } //Imprimo un mensaje por pantalla en el caso de que salte una excepcion.
	}//run 
}//Timer