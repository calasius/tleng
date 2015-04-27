package automaton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.PrintWriter;
import java.io.File;


public class Automaton {
	
	private final Set<Character> sigma;
	
	private final Map<State, Map<Character, State>> transitions;
	
	private final State[] states;
	
	private final State initialState;
	
	private final Set<State> finalStates;
	
	
	public Automaton(Set<Character> sigma, Map<State, Map<Character, State>> transitions, State[] states, 
			State initialState, Set<State> finalStates) {
		
		this.sigma = sigma;
		this.transitions = transitions;
		this.states = states;
		this.initialState = initialState;
		this.finalStates = finalStates;

	}
	
	public State getStateNumber(int i) {
		return this.states[i];
	}
	
	public boolean isFinal(int i) {
		return finalStates.contains(states[i]);
	}
	
	public State transition(State q, Character label) {
		return transitions.get(q).get(label);
	}
	
	public static Automaton intersection(Automaton aut1, Automaton aut2) {
		
		//Sigma automata interseccion
		Set<Character> intersectionSigma = new HashSet<Character>(aut1.sigma); 
		intersectionSigma.retainAll(aut2.sigma);
		
		//Estado inicial automata interseccion
		String inicial1 = aut1.getInitialState().getName();
		String inicial2 = aut2.getInitialState().getName();  
		String inicialPar = inicial1+inicial2;
		State intersectionInitial = new State(inicialPar);

		//Estados de automata interseccion
		int longStates1 = aut1.getStates().length;
		int longStates2 = aut2.getStates().length;
		State[] intersectionStates = new State[(longStates1 * longStates2) + 1];
		Set<State> finalStates = new HashSet<State>();
		Map<String, Integer> aut1States = new HashMap<String, Integer>();
		Map<String, Integer> aut2States = new HashMap<String, Integer>();
		
		for (int i = 0; i < aut1.getStates().length; i++){
			for(int j=0; j< aut2.getStates().length; j++){
				String nombre1 = aut1.getStates()[i].getName();
				aut1States.put(nombre1, i);
				String nombre2 = aut2.getStates()[j].getName();
				aut1States.put(nombre2, j);
				State estado = new State(nombre1 + nombre2);
				int position = i + (longStates2 * j); 
				intersectionStates[position] = estado;	
				if (aut1.isFinal(i) && aut2.isFinal(j)) {
					finalStates.add(estado);
				}
			}
		}
		
		//Transiciones del automata interseccion
		Map<State, Map<Character, State>> intersectionTransitions = new HashMap<State, Map<Character,State>>();
		
		for (int i = 0; i < aut1.getStates().length; i++){
			for(int j=0; j< aut2.getStates().length; j++){
				for (Character label : intersectionSigma) {
					int position = i + (longStates2 * j); 
					State source = intersectionStates[position];
					State s1 = aut1.getStateNumber(i);
					State s2 = aut2.getStateNumber(j);
					State p1 = aut1.transition(s1, label);
					State p2 = aut2.transition(s2, label);
					if (p1 != null && p2 != null) {
						//Si los dos estan definidos entonces genero la tansicion
						int indx1 = aut1States.get(p1.getName());
						int indx2 = aut2States.get(p2.getName());
						int p = indx1 + (longStates2 * indx2); 
						State dest = intersectionStates[p];
						insertTransition(source, dest, label, intersectionTransitions);
					} else {
						//Si alguno no esta definido entonces no hay transicion
					}
				}
			}
		}
			
		return new Automaton(intersectionSigma, intersectionTransitions, intersectionStates, intersectionInitial, finalStates);
	}

	private static void insertTransition(State source, State dest, Character label,
			Map<State, Map<Character, State>> unionTransitions) {
		
		if (unionTransitions.containsKey(source)) {
			unionTransitions.get(source).put(label, dest);
		} else {
			Map<Character, State> labelTransitions = new HashMap<Character,State>();
			labelTransitions.put(label, dest);
			unionTransitions.put(source, labelTransitions);
		}
		
	}

	public State[] getStates() {
		return states;
	}

	public State getInitialState() {
		return initialState;
	}

	public Set<State> getFinalStates() {
		return finalStates;
	}

	public void makeDot( String filename) {
		
		try {
		    
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println("strict digraph {\n rankdir=LR;\n");
			writer.println("node [shape = none, label = \", width = 0, height = 0]; qd;");
			writer.println("node [label=\"\\N\", width = 0.5, height = 0.5];");
			
			
			// Imprimo los estados finales con doble circulo
			writer.println("node [shape = doublecircle]; ");
			
			for (State s : this.finalStates) {
				writer.println(s.getName() + ";\n");	// Imprimo cada nombre de los estados finales
			}
			
			writer.println("node [shape = circle];");
			
			// Imprimo las transiciones con sus respectivos labels

			
			for (State src : this.transitions.keySet())
			{
				for(Character symbol :  transitions.get(src).keySet()) {
					writer.println(src + " -> " + this.transitions.get(src).get(symbol) + "[label=\"" + symbol + "\"]" );	
				}
			}
			
			// Cierro el archivo
			writer.println("}");
			writer.close();
			
		    } catch (Exception e) {
	        e.printStackTrace(); 
	    }
	}
	
}
