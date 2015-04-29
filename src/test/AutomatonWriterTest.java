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
		writer.writeAutomaton(aut, "/home/claudio/ejercicios/TP1-Tlen/src/test/aut.txt");
	}

}
