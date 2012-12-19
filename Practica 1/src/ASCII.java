import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



public class ASCII {




	/**
	 * Toma una imagen y devuelve su representacion grafica a partir de caracteres ASCII
	 * @param imageFileName
	 * @return
	 */
	public static String getImageText(String imageFileName) {
		BufferedImage image; 
		double gValue;

		String textImage = "";
		int deltaWidth = 0;
		int deltaHeight = 0;
		try {
			image = ImageIO.read(new File(imageFileName)); 
			deltaWidth = image.getWidth()/100;
			deltaHeight =  (int) (2 * (float)deltaWidth * (float)image.getHeight()/(float)image.getWidth()); // Calcula el aspect ratio y lo compensa ligeramente (porque el sitio que ocupan los caracteres es mas rectangular que cuadrado)

			for (int y = 0; y <image.getHeight(); y+=deltaHeight) {
				for (int x = 0; x < image.getWidth(); x+=deltaWidth){
					Color pixelColor = new Color(image.getRGB(x,y)); // color con del pixel
					gValue = (((pixelColor.getRed()*0.2989)+(pixelColor.getBlue()*0.5870)+(pixelColor.getGreen()*0.1140))); // equivalente del color en escala de grises (0,255)
					textImage+=returnStrPos(gValue);// caracter para ese valor de gris
				}
				textImage+="\n";
			}
		} 
		catch (IOException e) {}
		return textImage;
	}

	/*
	 * Asigna un caracter en funcion del nivel de gris. Cuando mas oscuro mas gris habrá en el recuadro
	 * por lo que el caracter debera ocupar mas espacio (las lineas llenan mas superficie);
	 */
	public static String returnStrPos(double g){
		String str = " ";

		if (g >= 230)str = " ";
		else if (g >= 200)str = ".";
		else if (g >= 180)str = ",";
		else if (g>= 160)str = ":";
		else if (g >= 130)str = "*";
		else if (g >= 100)str = "o";
		else if (g >= 70)str = "8";
		else if (g >= 50)str = "#";
		else str = "@";

		return str; 
	}

	/*
	 * Version de lo anterior en negativo
	 */
	public static String returnStrNeg(double g){
		String str = " ";

		if (g >= 230)str = "@";
		else if (g >= 200)str = "#";
		else if (g >= 180)str = "8";
		else if (g>= 160)str = "&";
		else if (g >= 130)str = "o";
		else if (g >= 100)str = ":";
		else if (g >= 70)str = "*";
		else if (g >= 50)str = ".";
		else str = " ";

		return str;
	}

	public void print(String str)// print helper mthod to print the results onto the result.txt file.
	{
		System.out.print(str);
	}



}
