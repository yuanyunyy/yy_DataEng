import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class yy20150712_DataEng {
	public static void main (String[] args) throws IOException{
		String filename = "input/tweets.txt";
		words_tweeted wtSample = new words_tweeted(filename, 5000, 10, 10);
		median_unique muSample = new median_unique(filename);
		wtSample.generateResult();
		muSample.Calculate();
	}
}
