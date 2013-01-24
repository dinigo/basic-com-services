interface cons { //Defino la interface cons.
   static final int tout=0, close=1, frame=2; //Defino con un numero los posibles eventos.
   static final int espera=0, recibiendo=1, acabando=2;  //Defino con un numero los posibles estados.
   static final int RRQ=1, WRQ=2, DATA=3, ACK=4,ERROR=5;  //Defino con un numero los codigos de trama.
   static final int ServerPort=69; //Defino el puerto.
} //cons