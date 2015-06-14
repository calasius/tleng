package automaton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ExpressionRegularReader {
	
	public Automaton readExpressionRegular(String source) throws FileNotFoundException {
		
		Scanner scanner = new Scanner(new BufferedReader(new FileReader(source)));
		
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			//get
			
		}
		scanner.close();
		return null;
		
	}

}
