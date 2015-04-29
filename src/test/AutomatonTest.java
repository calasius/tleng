package test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import automaton.Automaton;
import automaton.BuilderAutomaton;
import automaton.State;

public class AutomatonTest{

	@Test
	public void testIntersection() {
		Set<Character> sigma1 = new HashSet<Character>();
		sigma1.add('1');
		sigma1.add('2');
		sigma1.add('3');
		Set<Character> sigma2 = new HashSet<Character>();
		sigma2.add('1');
		sigma2.add('2');
		sigma2.add('5');
		Automaton automaton1 = BuilderAutomaton.buildStartAutomaton(sigma1);
		Automaton automaton2 = BuilderAutomaton.buildStartAutomaton(sigma2);
		Automaton intersection = Automaton.intersection(automaton1, automaton2);
		Assert.assertEquals(1, intersection.getStates().length);
		Assert.assertEquals(intersection.getInitialState(), intersection.transition(intersection.getInitialState(), Character.valueOf('1')));
		Assert.assertEquals(intersection.getInitialState(), intersection.transition(intersection.getInitialState(), Character.valueOf('2')));
		Assert.assertNull(intersection.transition(intersection.getInitialState(), Character.valueOf('5')));
	}
}
