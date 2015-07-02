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

import utils.utils;

public class AutomatonOperations {
	
	private static final char LAMBDA = '_';

	public static Automaton complemento(Automaton automaton, Set<Character> sigma){
		
		automaton.complete(sigma);
		
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
		
		if (!automaton.getFinalStates().isEmpty()) {			
			Set<State> class1 = new HashSet<State>(automaton.getFinalStates());
			Set<State> class2 = new HashSet<State>(Arrays.asList(automaton.getStates()));
			class2.removeAll(class1);
			stateClasses.add(class1);
			stateClasses.add(class2);
		} else {
			stateClasses.add(new HashSet<State>(Arrays.asList(automaton.getStates())));
		}
		
		int classesSizeBefore = 0;
		int classesSizeActual = stateClasses.size();
		
		while (classesSizeBefore != classesSizeActual) {
			classesSizeBefore = classesSizeActual;
			for (Character label : automaton.getSigma()) {
				List<Set<State>> newClasses = new ArrayList<Set<State>>();
				for (Set<State> stateClass : stateClasses) {
					if (stateClass.size() > 1) {
						Map<Integer,Set<State>> newPartitions = new HashMap<Integer,Set<State>>();
						for (State state : stateClass) {
							Integer classNumber = stateClasses.indexOf(findStateClassOf(automaton.transition(state, label), stateClasses));
							addState(classNumber,state,newPartitions);
						}
						newClasses.addAll(newPartitions.values());
					} else {
						newClasses.add(stateClass);
					}
				}
				stateClasses = newClasses;
				classesSizeActual = newClasses.size();
			}
		} 
		
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
				
				State memberOfClass = stateClass.iterator().next();
				if (automaton.transition(memberOfClass, label) != null) {
					Set<State> desStateClass = findStateClassOf(automaton.transition(memberOfClass, label), stateClasses);
					int destStateClassNumber = stateClasses.indexOf(desStateClass);
					State fromState = new State(String.valueOf(classNumber));
					State toState = new State(String.valueOf(destStateClassNumber));
					insertTransition(fromState, toState, label, minimizedTransitions);					
				}
			}
			
		}
		
		return new Automaton(automaton.getSigma(), minimizedTransitions, minimizedStates, minimizedInitialState, minimizedFinalStates);
	}

	private static void addState(Integer stateClassNumber, State state,
			Map<Integer, Set<State>> newPartitions) {
		
		if (newPartitions.containsKey(stateClassNumber)) {
			newPartitions.get(stateClassNumber).add(state);
		} else {
			Set<State> states = new HashSet<State>();
			states.add(state);
			newPartitions.put(stateClassNumber, states);
		}
		
	}

	public static Automaton intersection(Automaton aut1, Automaton aut2) {
		
		//Sigma automata interseccion
		Set<Character> intersectionSigma = utils.<Character> union(aut1.getSigma(),aut2.getSigma());
		
		aut1.complete(intersectionSigma);
		aut2.complete(intersectionSigma);
		
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
				int position = j + (longStates2 * i); 
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
					int position = j + (longStates2 * i); 
					State source = intersectionStates[position];
					State s1 = aut1.getStateNumber(i);
					State s2 = aut2.getStateNumber(j);
					State p1 = aut1.transition(s1, label);
					State p2 = aut2.transition(s2, label);
					int indx1 = aut1States.get(p1.getName());
					int indx2 = aut2States.get(p2.getName());
					int p = indx2 + (longStates2 * indx1); 
					State dest = intersectionStates[p];
					insertTransition(source, dest, label, intersectionTransitions);
				}
			}
		}
		
		Set<State> connectedCOmponent = connectedComponentOf(intersectionInitial, intersectionTransitions, new HashSet<State>());
		finalStates = utils.<State> intersection(connectedCOmponent,finalStates);
		intersectionStates = connectedCOmponent.toArray(new State[0]);
		Automaton intersection = new Automaton(intersectionSigma, intersectionTransitions, intersectionStates, intersectionInitial, finalStates);
		return minimizeAutomaton(intersection);
	}

	private static Set<State> connectedComponentOf(State state,
			Map<State, Map<Character, State>> transitions, Set<State> visited) {
		Set<State> connectedComponent = new HashSet<State>();
		if (visited.contains(state)) {
			return connectedComponent;
		} else {
			connectedComponent.add(state);
			visited.add(state);
			if (transitions.containsKey(state)) {
				for (Character label : transitions.get(state).keySet()) {
					connectedComponent.addAll(connectedComponentOf(transitions.get(state).get(label), transitions, visited));
				}
			}
			return connectedComponent;			
		}
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
			if (stateClass.contains(state)) {
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
	
	public static Automaton union(Automaton ... automatons) {
		Automaton result = automatons[0];
		for (int i = 1; i < automatons.length; i++) {
			result = union(result,automatons[i]);
		}
		return result;
	}
	
	public static Automaton union(Automaton aut1, Automaton aut2){
		Set<Character> sigma = utils.<Character> union(aut1.getSigma(), aut2.getSigma());
		Automaton aut1Comp = complemento(aut1,sigma);
		Automaton aut2Comp = complemento(aut2,sigma);
		Automaton interComp = intersection(aut1Comp,aut2Comp);
		
		return complemento(interComp,sigma);
	}
	
	public static boolean esVacio(Automaton aut){
		return aut.getTransitions().isEmpty() || aut.getFinalStates().isEmpty();
	}
	
	
	public static boolean areEquivalents(Automaton aut1, Automaton aut2) {
		
		Set<Character> sigma = new HashSet<Character>();
		sigma.addAll(aut1.getSigma());
		sigma.retainAll(aut2.getSigma());
		
		Automaton aut1Comp = complemento(aut1,sigma);
		Automaton aut2Comp = complemento(aut2,sigma);
		Automaton aut1_int_aut2C = intersection(aut1,aut2Comp);
		Automaton aut2_int_aut1C = intersection(aut1Comp,aut2);
		Automaton aut_res = union(aut1_int_aut2C,aut2_int_aut1C); 
		
		//creo el automata c = (A Int BComp) U (AComp Int B)
		
		return esVacio(aut_res);
	}
	
	public static Automaton unCaracter(Set<Character> sigma, Character a){
		State initialSt = new State("q0");
		State finalSt = new State("q1");
		State[] estados = new State[] {initialSt,finalSt};
		Set<State> estadosFinales = new HashSet<State>();
		estadosFinales.add(finalSt);
		
		Map<State, Map<Character, State>> transiciones = new HashMap<State, Map<Character,State>>();
		insertTransition(initialSt, finalSt, a, transiciones);
		return new Automaton(sigma,transiciones, estados, initialSt, estadosFinales);
	}
	
	private static State withPrefix(String prefix, State state) {
		return new State(prefix+state.getName());
	}

	public static Automaton concat(Automaton aut1, Automaton aut2){
		
		//Sigma concat
		Set<Character> alfabeto = utils.<Character> union(aut1.getSigma(),aut2.getSigma());
		
		aut1.complete(alfabeto);
		aut2.complete(alfabeto);
		
		//States concat
		State[] concatStates = new State[aut1.getStates().length+aut2.getStates().length];
		for (int i = 0; i < aut1.getStates().length; i++) {
			concatStates[i] = withPrefix("1",aut1.getStates()[i]);
		}
		for(int i = 0; i < aut2.getStates().length; i++) {
			concatStates[i+aut1.getStates().length] = withPrefix("2",aut2.getStates()[i]);
		}
		
		//Initial state concat
		State initialConcat = withPrefix("1",aut1.getInitialState());
		
		//Concat transitions
		Map<State, Map<Character, State>> transiciones = new HashMap<State, Map<Character,State>>();		
		
		// Agrego todas las transiciones del automata 1
		for (State src : aut1.getStates()) {
			for (Character c : aut1.getSigma()) {
				State dst = aut1.transition(src, c);
				insertTransition(withPrefix("1", src), withPrefix("1",dst), c, transiciones);
			}
		}

		// Agrego todas las transiciones del automata 2
		for (State src : aut2.getStates()) {
			for (Character c : aut2.getSigma()) {
				State dst = aut2.transition(src, c);
				insertTransition(withPrefix("2", src), withPrefix("2",dst), c, transiciones);
			}
		}

		//Agrego transiciones lambda de los finales de aut1 al inicial de aut2
		State initial2 = aut2.getInitialState();
		for (State state : aut1.getFinalStates()) {
			insertTransition(withPrefix("1", state), withPrefix("2", initial2), LAMBDA, transiciones);
		}
		
		//Concat final states
		Set<State> finalStates = new HashSet<State>();
		for (State state : aut2.getFinalStates()) {
			finalStates.add(withPrefix("2", state));			
		}
		
		
		Automaton concat = new Automaton(alfabeto,transiciones, concatStates, initialConcat, finalStates);
		return determinizar(concat);
	}
	
	
	public static Automaton concat(Automaton ... automatons) {
		Automaton result = automatons[0];
		for (int i = 1; i < automatons.length; i++) {
			result = concat(result,automatons[i]);
		}
		return result;
	}


	public static Automaton estrella(Automaton aut1){
		
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
	
	private static Set<State> alcanzablesPor(Character label, Set<State> states, Automaton automaton){
		Set<State> alcanzables = new HashSet<State>();
		for (State state : states) {
			Map<Character,State> stateTransitions = automaton.getTransitions().get(state);
			if (stateTransitions != null && stateTransitions.containsKey(label)) {
				alcanzables.add(stateTransitions.get(label));
			}
		}
		return alcanzables;
	}
	
	private static Set<State> alcanzablesPor(Character label, State state,
			Automaton automaton) {
		Set<State> states = new HashSet<State>();
		states.add(state);
		return alcanzablesPor(label, states, automaton);
	}
	
	private static Set<State> clausuraEstado(Set<State> states, Automaton a) {

		Set<State> alcanzables = alcanzablesPor(LAMBDA, states, a);
		Set<State> res = new HashSet<State>();
		Deque<State> q = new LinkedList<State>();
		if (!(alcanzables.isEmpty())) {
			for (State st : alcanzables) {
				q.addFirst(st);
				res.add(st);
			}
			State temp = null;
			while (!(q.isEmpty())) {
				temp = q.removeFirst();
				alcanzables = alcanzablesPor(LAMBDA, temp, a);
				if (!(alcanzables.isEmpty())) {
					for (State st : alcanzables) {
						q.addFirst(st);
						res.add(st);
					}
				}
			}
		}
		res.addAll(states);
		return res;
	}
	
	public static Automaton determinizar(Automaton automaton) {
		Set<State> inic = clausuraEstado(automaton.getInitialState(), automaton);
		List<Set<State>> estados = new ArrayList<Set<State>>();
		estados.add(inic);
		Deque<Set<State>> sinMarcar = new LinkedList<Set<State>>();
		sinMarcar.addFirst(inic);
		Map<State, Map<Character, State>> transiciones = new HashMap<State, Map<Character, State>>();

		while (!(sinMarcar.isEmpty())) {
			Set<State> temp = sinMarcar.removeFirst();
			for (Character label : automaton.getSigma()) {

				Set<State> newSt = alcanzablesPor(label, temp, automaton);
				Set<State> clausura = clausuraEstado(newSt, automaton);
				
				if (!estados.contains(clausura)) {
					estados.add(clausura);
					sinMarcar.addFirst(clausura);
				}

				// registro las transiciones entre los estados del nuevo DFA (si
				// la clausura no dio vacio)
				if (!(clausura.isEmpty())) {
					State src = new State(String.valueOf(estados.indexOf(temp)));
					// los nombres de los estados son los indices en la lista
					State dst = new State(String.valueOf(estados
							.indexOf(clausura)));
					
					insertTransition(src, dst, label, transiciones);
				}
			}
		}

		Set<State> estadosTotales = new HashSet<State>();
		Set<State> estadosFinales = new HashSet<State>();
		State estadoInicial = new State("0");

		for (Set<State> sst : estados) {
			Set<State> interceccion = utils.<State> intersection(automaton.getFinalStates(), sst);
			if (!interceccion.isEmpty()) {
				State estado_dfa = new State(String.valueOf(estados
						.indexOf(sst)));
				estadosFinales.add(estado_dfa);
				estadosTotales.add(estado_dfa);
			} else {
				State estado_dfa = new State(String.valueOf(estados
						.indexOf(sst)));
				estadosTotales.add(estado_dfa);
			}
		}
		
		Automaton deterministic =  (new Automaton(automaton.getSigma(), transiciones,
				estadosTotales.toArray(new State[0]), estadoInicial , estadosFinales));
		
		return minimizeAutomaton(deterministic);

	}

	private static Set<State> clausuraEstado(State state, Automaton automaton) {
		Set<State> states = new HashSet<State>();
		states.add(state);
		return clausuraEstado(states,automaton);
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
