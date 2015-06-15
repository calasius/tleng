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
	
	public static Automaton complemento(Automaton automaton){
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
		
		int classesSizeBefore = 0;
		int classesSizeActual = stateClasses.size();
		
		while (classesSizeBefore != classesSizeActual) {
			classesSizeBefore = classesSizeActual;
			for (Character label : automaton.getSigma()) {
				Map<Integer,Set<State>> newPartitions = new HashMap<Integer,Set<State>>();
				for (Set<State> stateClass : stateClasses) {
					for (State state : stateClass) {
						Integer stateClassNumber = stateClasses.indexOf(findStateClassOf(automaton.transition(state, label), stateClasses));
						addState(stateClassNumber,state,newPartitions);
					}
				}
				stateClasses.clear();
				stateClasses.addAll(newPartitions.values());
			}
			classesSizeActual = stateClasses.size();
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
				Set<State> desStateClass = findStateClassOf(automaton.transition(memberOfClass, label), stateClasses);
				int destStateClassNumber = stateClasses.indexOf(desStateClass);
				State fromState = new State(String.valueOf(classNumber));
				State toState = new State(String.valueOf(destStateClassNumber));
				insertTransition(fromState, toState, label, minimizedTransitions);
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
	
	public static Automaton union(Automaton aut1, Automaton aut2){
		
		Automaton aut1Comp = complemento(aut1);
		Automaton aut2Comp = complemento(aut2);
		Automaton interComp = intersection(aut1Comp,aut2Comp);
		
		return complemento(interComp);
	}
	
	
	private static boolean llegoAFinal(State st, int n, Automaton aut){
		if(aut.isFinal(st)){
			return true;
		}
		if(n > aut.getStates().length){
			return false;
		}else{
			for(Character c: aut.getSigma()){
				State dst = aut.transition(st, c);
				llegoAFinal(dst, n+1, aut);
			}
			
		}
		return true;
		
	}
	
	public static boolean esVacio(Automaton aut){
		
		for(Character c: aut.getSigma()){
			if(llegoAFinal(aut.transition(aut.getInitialState(),c),2,aut)){
				return false;
			}
		}
			return true;
	}
	
	
	public static boolean areEquivalents(Automaton aut1, Automaton aut2) {
		
		Automaton aut1Comp = complemento(aut1);
		Automaton aut2Comp = complemento(aut2);
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
		
		Map<State, Map<Character, State>> transiciones = new HashMap<State, Map<Character,State>>();
		insertTransition(initialSt, finalSt, a, transiciones);
		return new Automaton(sigma,transiciones, estados, initialSt, estadosFinales);
	}

	public static Automaton concat(Automaton aut1, Automaton aut2){
		
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

		Set<State> alcanzables = alcanzablesPor(null, states, a);
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
				alcanzables = alcanzablesPor(null, temp, a);
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
	
	public Automaton determinizar(Automaton automaton) {
		Set<State> inic = clausuraEstado(automaton.getInitialState(), automaton);
		List<Set<State>> estados = new ArrayList<Set<State>>();
		estados.add(inic);
		Deque<Set<State>> sinMarcar = new LinkedList<Set<State>>();
		sinMarcar.addFirst(inic);
		Map<State, Map<Character, State>> transiciones = new HashMap<State, Map<Character, State>>();

		while (!(sinMarcar.isEmpty())) {
			Set<State> temp = sinMarcar.removeFirst();
			for (Character label : automaton.getSigma()) {
				Set<State> newSt = new HashSet<State>();
				for (State st : temp) {
					// junto los estados alcanzables por cada simbolo
					newSt.addAll(alcanzablesPor(label, st, automaton));
				}

				Set<State> clausuras = new HashSet<State>();

				// newSt tiene los estados a los que puedo llegar desde cada
				// simbolo
				for (State st : newSt) {
					// clausuro por lambda todos los estados
					clausuras.addAll(clausuraEstado(st, automaton));
				}

				// si CLAUSURAS no existía como estado, lo agrego y lo pongo en
				// la cola para clausurarlo
				if (!estados.contains(clausuras)) {
					estados.add(clausuras);
					sinMarcar.addFirst(clausuras);
				}

				// registro las transiciones entre los estados del nuevo DFA (si
				// la clausura no dio vacio)
				if (!(clausuras.isEmpty())) {
					State src = new State(String.valueOf(estados.indexOf(temp)));
					// los nombres de los estados son los indices en la lista
					State dst = new State(String.valueOf(estados
							.indexOf(clausuras)));

					if (transiciones.containsKey(src)) {
						transiciones.get(src).put(label, dst);
					} else {
						Map<Character, State> newTran = new HashMap<Character, State>();
						newTran.put(label, dst);
						transiciones.put(src, newTran);
					}
				}
			}
		}

		Set<State> estadosTotales = new HashSet<State>();
		Set<State> estadosFinales = new HashSet<State>();
		State estadoInicial = new State("0");

		for (Set<State> sst : estados) {
			Set<State> interceccion = interceccion(automaton.getFinalStates(), sst);
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
		
		return (new Automaton(automaton.getSigma(), transiciones,
				estadosTotales.toArray(new State[0]), estadoInicial , estadosFinales));

	}

	private Set<State> clausuraEstado(State state, Automaton automaton) {
		Set<State> states = new HashSet<State>();
		states.add(state);
		return clausuraEstado(states,automaton);
	}
	
	private Set<State> interceccion(Set<State> set1, Set<State> set2) {
		Set<State> interceccion = new HashSet<State>();
		if (set2 == null) {
			return interceccion;
		}
		for (State state : set2) {
			if (set1.contains(state)) {
				interceccion.add(state);
			}
		}
		
		return interceccion;
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
