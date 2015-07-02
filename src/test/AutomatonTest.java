package test;

import static automaton.AutomatonOperations.*;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import automaton.Automaton;
import automaton.AutomatonOperations;
import automaton.AutomatonReader;
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
		Automaton intersection = AutomatonOperations.intersection(automaton1, automaton2);
		Assert.assertEquals(2, intersection.getStates().length);
		Assert.assertEquals(new State("0"), intersection.transition(intersection.getInitialState(), Character.valueOf('1')));
		Assert.assertEquals(new State("0"), intersection.transition(intersection.getInitialState(), Character.valueOf('2')));
		Assert.assertEquals(new State("1"), intersection.transition(intersection.getInitialState(), Character.valueOf('5')));
	}
	
	@Test
	public void testEmptyIntersection() {
		Set<Character> sigma1 = new HashSet<Character>();
		sigma1.add('a');
		Automaton aut1 = unCaracter(sigma1, 'a');
		
		Set<Character> sigma2 = new HashSet<Character>();
		sigma2.add('b');
		Automaton aut2 = unCaracter(sigma2, 'b');
		
		Automaton intersection = intersection(aut1, aut2);
		
		Assert.assertTrue(esVacio(intersection));
		
	}
	
	@Test
	public void testMinimizar() throws FileNotFoundException {
		AutomatonReader reader = new AutomatonReader();
		Automaton automaton = reader.readAutomaton("./automatas/aut3.txt");
		Automaton minimized = AutomatonOperations.minimizeAutomaton(automaton);
		Assert.assertEquals(2, minimized.getStates().length);
	}
	
	@Test
	public void testUnion() {
		Set<Character> sigma1 = new HashSet<Character>();
		sigma1.add('a');
		Automaton aut1 = unCaracter(sigma1, 'a');
		
		Set<Character> sigma2 = new HashSet<Character>();
		sigma2.add('b');
		Automaton aut2 = unCaracter(sigma2, 'b');
		
		Automaton union = union(aut1, aut2);
		
		Assert.assertEquals(3, union.getStates().length);
		Assert.assertEquals(new State("1"), union.getInitialState());
		Assert.assertEquals(new State("0"), union.getTransitions().get(new State("1")).get('a'));
		Assert.assertEquals(new State("0"), union.getTransitions().get(new State("1")).get('b'));
		Assert.assertEquals(new State("2"), union.getTransitions().get(new State("0")).get('a'));
		Assert.assertEquals(new State("2"), union.getTransitions().get(new State("0")).get('b'));
		Assert.assertTrue(union.getFinalStates().size() == 1 && union.getFinalStates().contains(new State("0")));
	}
	 
	@Test
	public void testConcat() {
		Set<Character> sigma1 = new HashSet<Character>();
		sigma1.add('a');
		Automaton aut1 = unCaracter(sigma1, 'a');
		
		Set<Character> sigma2 = new HashSet<Character>();
		sigma2.add('b');
		Automaton aut2 = unCaracter(sigma2, 'b');
		
		Automaton concat = concat(aut1, aut2);
		Assert.assertNotNull(concat);
	}
	
	
}
