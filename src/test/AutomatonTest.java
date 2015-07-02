package test;

import static automaton.AutomatonOperations.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

import automaton.Automaton;
import automaton.AutomatonOperations;
import automaton.AutomatonReader;
import automaton.AutomatonWriter;
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
		Assert.assertEquals(1, minimized.getFinalStates().size());
		AutomatonWriter writer = new AutomatonWriter();
		try {
			writer.writeAutomaton(minimized, "./automatas/aut7.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testMinimizarComplejo() throws FileNotFoundException {
		AutomatonReader reader = new AutomatonReader();
		Automaton automaton = reader.readAutomaton("./automatas/autToMin.txt");
		Automaton minimized = AutomatonOperations.minimizeAutomaton(automaton);
		//Assert.assertEquals(9, automaton.getTransitions().size());
		//String dotMin = null;
		//Assert.assertEquals(3, minimized.getStates().length);
		//Assert.assertEquals(1, minimized.getFinalStates().size());
		AutomatonWriter writer = new AutomatonWriter();
		try {
			System.out.println("hay " + automaton.getTransitions().size() + " transiciones");

			for (State src : automaton.getTransitions().keySet()) {
				for (Character symbol : automaton.getTransitions().get(src).keySet()) {
					System.out.println(src.getName() + " -> "
							+ automaton.getTransitions().get(src).get(symbol).getName()
							+ "[label=\"" + symbol + "\"]");
				}
			}
			writer.writeAutomaton(minimized, "./automatas/autMinRes.txt");
			writer.makeDot("./automatas/dotToMin.dot", automaton);
			writer.makeDot("./automatas/dotMin.dot", minimized);
			
			System.out.println("EL RESULTADO");
			for (State src : minimized.getTransitions().keySet()) {
				for (State destino : minimized.getStates()){
					Vector<Character> chars = new Vector<Character>();
					for (Character symbol : minimized.getTransitions().get(src).keySet()) {
						
						
						if (minimized.getTransitions().get(src).get(symbol).equals(destino) ){
							chars.add(symbol);
							
						}
						
					}	
					
					String str = new String();
					for(Character c : chars){
						if(c != chars.lastElement()){
						str = str +(c.toString() + ",");
						}else{
							str = str + (c.toString());
						}
						}
					
					if (!str.isEmpty()){
					System.out.println(src.getName() + " -> "
							+ destino.getName()
							+ "[label=\"" + str + "\"]");
					}
				}
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void testEstrella() throws FileNotFoundException {
		Set<Character> sigma = new HashSet<Character>();
		sigma.add('a');
		Automaton automaton = unCaracter(sigma, 'a');
		Automaton star = estrella(automaton);
		AutomatonWriter writer = new AutomatonWriter();
		writer.makeDot("./automatas/dotToStar.txt", automaton);
		writer.makeDot("./automatas/dotStar.txt", star);
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
	
	@Test
	public void test1() throws FileNotFoundException {
		AutomatonWriter writer = new AutomatonWriter();
		AutomatonReader reader = new AutomatonReader();
		Automaton aut1 = reader.readAutomaton("./pruebas/automatas/casa_o_casado.aut");
		Automaton aut2 = reader.readAutomaton("./pruebas/automatas/casa.aut");
		Automaton inter = intersection(aut1, aut2);
		writer.makeDot("./dots/test1/casa_o_casado.dot", aut1);
		writer.makeDot("./dots/test1/casa.dot", aut2);
		writer.makeDot("./dots/test1/inter.dot", inter);
		Assert.assertTrue(inter.reconoce("casa"));
	}
	
	@Test
	public void test2() throws FileNotFoundException {
		AutomatonWriter writer = new AutomatonWriter();
		AutomatonReader reader = new AutomatonReader();
		Automaton aut1 = reader.readAutomaton("./pruebas/automatas/un_simbolo.aut");
		aut1.complete(aut1.getSigma());
		writer.makeDot("./dots/test2/un_simbolo.dot", aut1);
		Assert.assertTrue(!aut1.reconoce("aa"));
		Assert.assertTrue(!aut1.reconoce("abc"));
	}
	
	
}
