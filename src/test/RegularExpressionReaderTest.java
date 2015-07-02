package test;

import java.io.FileNotFoundException;

import org.junit.Test;

import automaton.Automaton;
import automaton.AutomatonWriter;
import automaton.RegularExpressionReader;

public class RegularExpressionReaderTest {
	
	@Test
	public void test1() throws FileNotFoundException {
		RegularExpressionReader reader = new RegularExpressionReader();
		Automaton automaton = reader.readRegularExpression("./automatas/re2.txt");
		AutomatonWriter writer = new AutomatonWriter();
		writer.makeDot("./automatas/dotre2.txt", automaton);
	}
	
	@Test
	public void test2() throws FileNotFoundException {
		RegularExpressionReader reader = new RegularExpressionReader();
		Automaton automaton = reader.readRegularExpression("./automatas/RE1.txt");
		AutomatonWriter writer = new AutomatonWriter();
		writer.makeDot("./automatas/dotre1.txt", automaton);
	}
	
	@Test
	public void test3() throws FileNotFoundException {
		RegularExpressionReader reader = new RegularExpressionReader();
		Automaton automaton = reader.readRegularExpression("./automatas/re3.txt");
		AutomatonWriter writer = new AutomatonWriter();
		writer.makeDot("./automatas/dotre3.txt", automaton);
	}
	
	@Test
	public void test4() throws FileNotFoundException {
		RegularExpressionReader reader = new RegularExpressionReader();
		Automaton automaton = reader.readRegularExpression("./automatas/re4.txt");
		AutomatonWriter writer = new AutomatonWriter();
		writer.makeDot("./automatas/dotre4.txt", automaton);
	}

}
