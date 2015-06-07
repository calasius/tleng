package automaton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AutomatonOperations {
	
	public Automaton complemento(Automaton automaton){
		Set<State> finales = new HashSet<State>(); 
		
		for (State state : automaton.getStates()) {
			finales.add(state);
		}
		
		finales.removeAll(automaton.getFinalStates());
		
		Automaton complementAutomaton = new Automaton(automaton.getSigma(), automaton.getTransitions(), automaton.getStates(), automaton.getInitialState(), finales);
		
		return minimizeAutomaton(complementAutomaton);
	}
	
	public static Automaton minimizeAutomaton(Automaton automaton) {
		
		List<Set<State>> stateClasses = new ArrayList<Set<State>>();
		
		Set<State> class1 = new HashSet<State>(automaton.getFinalStates());
		Set<State> class2 = new HashSet<State>(Arrays.asList(automaton.getStates()));
		class2.removeAll(class1);
		stateClasses.add(class1);
		stateClasses.add(class2);
		
		int classesSize;
		
		do {
			classesSize = stateClasses.size();
			for (Character label : automaton.getSigma()) {
				List<Set<State>> newStateClasses = new ArrayList<Set<State>>();
				for (Set<State> stateClass : stateClasses) {
					Set<State> newStateClass = new HashSet<State>();
					for (State state : stateClass) {
						//if state class has only one state it can't be partitioned
						if (!stateClass.contains(automaton.transition(state, label)) && stateClass.size() > 1) {
							newStateClass.add(state);
							stateClass.remove(state);
						}
					}
					if (!newStateClass.isEmpty()) {
						newStateClasses.add(newStateClass);
					}
				}
				if (!newStateClasses.isEmpty()) {
					stateClasses.addAll(newStateClasses);				
				}
			}			
		} while (classesSize < stateClasses.size());
		
		Map<State, Map<Character,State>> minimizedTransitions = new HashMap<State, Map<Character,State>>();
		State[] minimizedStates = new State[stateClasses.size()];
		Set<State> minimizedFinalStates = new HashSet<State>();
		State minimizedInitialState = null;
		
		for (Character label : automaton.getSigma()) {
			for (Set<State> stateClass : stateClasses) {
				int classNumber = stateClasses.indexOf(stateClass);
				State minimizedState = new State(String.valueOf(classNumber));
				minimizedStates[classNumber] = minimizedState;
				if (isFinalStateClass(stateClass, automaton)) {
					minimizedFinalStates.add(minimizedState);
				}
				if (isInitialStateClass(stateClass, automaton)) {
					minimizedInitialState = minimizedState;
				}
				for (State state : stateClass) {
					Set<State> destStateClass = null;
					if (!stateClass.contains(automaton.transition(state, label))) {
						destStateClass = findStateClassOf(state, stateClasses);
					} else {
						destStateClass = stateClass;
					}
					classNumber = stateClasses.indexOf(destStateClass);
					State destState = new State(String.valueOf(classNumber));
					insertTransition(state, destState, label, minimizedTransitions);
				}
			}
			
		}
		
		return new Automaton(automaton.getSigma(), minimizedTransitions, minimizedStates, minimizedInitialState, minimizedFinalStates);
	}
	
	public static Automaton intersection(Automaton aut1, Automaton aut2) {
		
		//Sigma automata interseccion
		Set<Character> intersectionSigma = new HashSet<Character>(aut1.getSigma()); 
		intersectionSigma.retainAll(aut2.getSigma());
		
		//Estado inicial automata interseccion
		String inicial1 = aut1.getInitialState().getName();
		String inicial2 = aut2.getInitialState().getName();  
		String inicialPar = inicial1+inicial2;
		State intersectionInitial = new State(inicialPar);

		//Estados de automata interseccion
		int longStates1 = aut1.getStates().length;
		int longStates2 = aut2.getStates().length;
		State[] intersectionStates = new State[(longStates1 * longStates2)];
		Set<State> finalStates = new HashSet<State>();
		Map<String, Integer> aut1States = new HashMap<String, Integer>();
		Map<String, Integer> aut2States = new HashMap<String, Integer>();
		
		for (int i = 0; i < aut1.getStates().length; i++){
			for(int j=0; j< aut2.getStates().length; j++){
				String nombre1 = aut1.getStates()[i].getName();
				aut1States.put(nombre1, i);
				String nombre2 = aut2.getStates()[j].getName();
				aut2States.put(nombre2, j);
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

	private static boolean isInitialStateClass(Set<State> stateClass,
			Automaton automaton) {
		return stateClass.contains(automaton.getInitialState());
	}

	private static boolean isFinalStateClass(Set<State> stateClass,
			Automaton automaton) {
		return automaton.getFinalStates().contains(stateClass.iterator().next());
	}

	private static Set<State> findStateClassOf(State state, List<Set<State>> stateClasses) {
		for (Set<State> stateClass : stateClasses) {
			if (stateClass.contains(null)) {
				return stateClass;
			}
		}
		return null;
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
	
	public static boolean areEquivalents(Automaton aut1, Automaton aut2) {

		Set<Pair<State, State>> relation = new HashSet<Pair<State, State>>();

		Deque<Pair<State, State>> todo = new LinkedList<Pair<State, State>>();
		todo.add(new Pair<State, State>(aut1.getInitialState(), aut2
				.getInitialState()));

		while (!todo.isEmpty()) {
			Pair<State, State> pair = todo.removeFirst();
			if (relation.contains(pair)) {
				continue;
			}
			if (aut1.isFinal(pair.getX()) != aut2.isFinal(pair.getY())) {
				return false;
			}

		}

		return false;
	}
	
	public Automaton unCaracter(Set<Character> sigma, Character a){
		State initialSt = new State("q0");
		State finalSt = new State("q1");
		State[] estados = new State[] {initialSt,finalSt};
		Set<State> estadosFinales = new HashSet<State>();
		
		Map<State, Map<Character, State>> transiciones = new HashMap<State, Map<Character,State>>();
		insertTransition(initialSt, finalSt, a, transiciones);
		return new Automaton(sigma,transiciones, estados, initialSt, estadosFinales);
	}

	public Automaton concat(Automaton aut1, Automaton aut2){
		
		Set<Character> alfabeto = new HashSet<Character>(aut1.getSigma());
		alfabeto.addAll(aut2.getSigma());
		
		State initialSt = new State("q0");
		State finalSt = new State("q1");
		State[] estados = new State[aut1.getStates().length + aut2.getStates().length + 2];
		estados[0] = initialSt;
		for (int i = 0 ; i < aut1.getStates().length; i++) {
			estados[i+1] = aut1.getStates()[i];
		}
		
		for (int i = 0 ; i < aut2.getStates().length; i++) {
			estados[aut1.getStates().length+i+2] = aut2.getStates()[i];
		}
		
		estados[aut1.getStates().length + aut2.getStates().length + 1] = finalSt;
		
		Set<State> estadosFinales = new HashSet<State>();
		estadosFinales.add(finalSt);
		
		Map<State, Map<Character, State>> transiciones = new HashMap<State, Map<Character,State>>();
		
		//Agrego las transiciones del nuevo estado inicial al estado inicial de cada automata
		insertTransition(initialSt, aut1.getInitialState(), null, transiciones);
		insertTransition(initialSt, aut2.getInitialState(), null, transiciones);
		
		// Agrego todas las transiciones del automata 1
		for (State src : aut1.getStates()) {
			for (Character c : aut1.getSigma()) {
				State dst = aut1.transition(src, c);
				insertTransition(src, dst, c, transiciones);
			}
		}

		// Agrego todas las transiciones del automata 2
		for (State src : aut2.getStates()) {
			for (Character c : aut1.getSigma()) {
				State dst = aut1.transition(src, c);
				insertTransition(src, dst, c, transiciones);
			}
		}
		

		//Agrego las transiciones desde cada estado final al nuevo estado final
		for(Iterator<State> iterator = aut1.getFinalStates().iterator(); iterator.hasNext();) {
			State src = (State) iterator.next();
			insertTransition(src, finalSt, null, transiciones);	
		}
		

		//Agrego las transiciones desde cada estado final al nuevo estado final
		for (Iterator<State> iterator = aut2.getFinalStates().iterator(); iterator.hasNext();) {
			State src = (State) iterator.next();
			insertTransition(src, finalSt, null, transiciones);
		}
		
		
		return new Automaton(alfabeto,transiciones, estados, initialSt, estadosFinales);
	}


	public Automaton estrella(Automaton aut1){
		
		Set<Character> alfabeto = new HashSet<Character>(aut1.getSigma());
		
		State initialSt = new State("q0");
		State finalSt = new State("q1");
		State[] estados = new State[aut1.getStates().length +  2];
		estados[0] = initialSt;
		for (int i = 0 ; i < aut1.getStates().length; i++) {
			estados[i+1] = aut1.getStates()[i];
		}

		
		estados[aut1.getStates().length + 1] = finalSt;

		Set<State> estadosFinales = new HashSet<State>();
		estadosFinales.add(finalSt);

		
		Map<State, Map<Character, State>> transiciones = new HashMap<State, Map<Character,State>>();

		//Agrego la transicion lambda del nuevo estado inicial al nuevo estado final
		insertTransition(initialSt, finalSt, null, transiciones);

		
		//Agrego las transiciones del nuevo estado inicial al estado inicial de cada automata
		insertTransition(initialSt, aut1.getInitialState(), null, transiciones);
		
		// Agrego todas las transiciones del automata 1
		for (State src : aut1.getStates()) {
			for (Character c : aut1.getSigma()) {
				State dst = aut1.transition(src, c);
				insertTransition(src, dst, c, transiciones);
			}
		}

		//Agrego las transiciones desde cada estado final al nuevo estado final
			for(Iterator<State> iterator = aut1.getFinalStates().iterator(); iterator.hasNext();) {
				State src = (State) iterator.next();
				insertTransition(src, finalSt, null, transiciones);	
			}
		

			//Agrego las transiciones desde cada estado final al estado inicial original del automata
				for(Iterator<State> iterator = aut1.getFinalStates().iterator(); iterator.hasNext();) {
					State src = (State) iterator.next();
					insertTransition(src, aut1.getInitialState(), null, transiciones);	
				}
				
			
		return new Automaton(alfabeto,transiciones, estados, initialSt, estadosFinales);
	}
	
	public static Automaton makeDeterministics(Automaton automaton) {
		return null;
	}


}


class Pair<X,Y> {

	private final X x;
	private final Y y;
	
	public Pair(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}
}
