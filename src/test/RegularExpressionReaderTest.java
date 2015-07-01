package test;

import java.io.FileNotFoundException;

import org.junit.Test;

import automaton.Automaton;
import automaton.RegularExpressionReader;

public class RegularExpressionReaderTest {
	
	@Test
	public void test() throws FileNotFoundException {
		RegularExpressionReader reader = new RegularExpressionReader();
		Automaton automaton = reader.readRegularExpression("./automatas/RE1.txt");
		System.out.println(automaton);
	}

}
