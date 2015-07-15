package test;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import automaton.Automaton;
import automaton.AutomatonReader;
import automaton.State;

public class AutomatonReaderTest {
	
	@Test
	public void test_read_one_node_automaton() throws FileNotFoundException {
		AutomatonReader reader = new AutomatonReader();
		Automaton automaton = reader.readAutomaton("./automatas/oneNodeAutomaton.txt");
		Assert.assertEquals(1, automaton.getStates().length);
		Assert.assertEquals("q0", automaton.getInitialState().getName());
		Assert.assertEquals(1, automaton.getFinalStates().size());
		Assert.assertEquals("q0", automaton.getFinalStates().iterator().next().getName());
		Assert.assertEquals(new State("q0"), automaton.transition(new State("q0"), Character.valueOf('a')));
		Assert.assertEquals(new State("q0"), automaton.transition(new State("q0"), Character.valueOf('b')));
		Assert.assertEquals(new State("q0"), automaton.transition(new State("q0"), Character.valueOf('c')));
	}

}
