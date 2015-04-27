package readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import automaton.Automaton;

public class AutomatonReader {
	
	public Automaton readAutomaton(String source) {
		
		try {
			Scanner s = new Scanner(new BufferedReader(new FileReader(source)));
			String statesLine = s.nextLine();
			String sigmaLine = s.nextLine();
			String initialState = s.nextLine();
			String finalStatesLine = s.nextLine();
			List<String> transitionsLines = new ArrayList<String>();
			while (s.hasNextLine()) {
				transitionsLines.add(s.nextLine());
			}
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

}
