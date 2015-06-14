package test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import automaton.Automaton;
import automaton.AutomatonWriter;
import automaton.BuilderAutomaton;

public class AutomatonWriterTest {
	
	@Test
	public void testWrite() throws IOException {
		Set<Character> sigma = new HashSet<Character>();
		sigma.add('1');
		sigma.add('2');
		sigma.add('3');
		Automaton aut = BuilderAutomaton.buildStartAutomaton(sigma);
		AutomatonWriter writer = new AutomatonWriter();
		writer.writeAutomaton(aut, "../automatas/aut1.txt");
		
		Set<Character> sigma1 = new HashSet<Character>();
		sigma1.add('1');
		sigma1.add('2');
		sigma1.add('5');
		Automaton aut1 = BuilderAutomaton.buildStartAutomaton(sigma1);
		writer.writeAutomaton(aut1, "../automatas/aut2.txt");
	}

}
