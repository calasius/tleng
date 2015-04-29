package automaton;
import java.util.HashMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.PrintWriter;
import java.util.Iterator;


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
	
	public Set<Character> getSigma() {
		return this.sigma;
	}
	
	public boolean isFinal(int i) {
		return finalStates.contains(states[i]);
	}
	
	public boolean isFinal(State st) {
		return finalStates.contains(st);
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
	
public boolean reconoce(String str){
	State tran = initialState;
	for (int i = 0, n = str.length(); i < n; i++) {
	    char c = str.charAt(i);
	    Map<Character,State> map = transitions.get(initialState);
	    tran = map.get(c);
	}
	
	return isFinal(tran);
	
}

public Automaton minimize()  { 

	Set<State> estadosFinales = getFinalStates();
	Set<State> estadosNoFinales  = new HashSet<State>(Arrays.asList(getStates()));
	
	estadosNoFinales.removeAll(estadosFinales); 
	//En grupo A estan los estados finales. En el grupo B todos los que no son finales.
	int n = getStates().length;		//n es la cantidad de estados
	
	Set<State>[] ClasesEq = (HashSet<State>[]) new Set[n]; //creo clases de equivalencia, espacio para n posibles. ¿Se inicializan vacios los conjuntos?
	ClasesEq[0] = estadosFinales;
	ClasesEq[1] = estadosNoFinales;
	int ultimoAgregado = 1; //registro ultimo indice
	boolean HayNuevoConjunto = false; 
	
	do{
		for (int j = 0; j < n; j++){ // para cada estado de las clases de equivalencia
			for (Character label : sigma){ //para cada signo del alfabeto
				if (ClasesEq[j].size() >1 ){ //si el estado tiene más de un elemento. 
					Set<State> distinguibles = distinguidos( ClasesEq,j,n,label); //chequeo si se puede distinguir dentro conjunto de estados j de la clase de equivalencia con el elemento i del alfabeto . De ser posible, devuelvo una sub clase de equivalencia. Si no, devuelvo vacío.
					if (! (distinguibles.isEmpty())){
						ClasesEq[j].removeAll(distinguibles);//quito los distinguibles
						ClasesEq[ultimoAgregado+1] = distinguibles;//los agrego como clase de equivalencia
						ultimoAgregado++; //muevo el indice
						HayNuevoConjunto = true; //loopeo de nuevo
					}else{		// si no se puede distinguir la ultima clase de equivalencia se termina el ciclo
						HayNuevoConjunto = false;					
					}								
				}
			}
		}
	}while(HayNuevoConjunto);
	
	//Todos los elementos de ClasesEq que tienen algún elemento son un estado distinto para el autómata mínimo. Las transiciones son las de cada estado que componen cada clase de equivalencia. Los estados finales son las clases que contengan algún estado final. 
	
	// Creo los estados
	int cantEstados = ultimoAgregado - 1;
	State[] estados = new State[cantEstados];
	for (int i = 0; i < cantEstados; i++){
		String name = "q" + Integer.toString(i);
		State est = new State(name);
		estados[i] = est;
	}

	
	// Creo las transiciones y los estados iniciales y finales
	Map<State, Map<Character, State>> transiciones = new HashMap<State, Map<Character,State>>();
	State initialSt;
	Set<State> finalSt = new HashSet<State>();
	
	// Me fijo en las clases de equivalencia si slos estados eran finales o iniciales en el automata original
	
	for (int i = 0; i < cantEstados; i++){
		State src = estados[i];
		int claseStateSrc = perteneceClaseNro (ClasesEq, src,i);
		Iterator <State> it = ClasesEq[i].iterator();
		State st=it.next();
		
		if(isFinal(st)){
			finalSt.add(estados[i]);
		}
		
		if(st == initialState){
			initialSt =  estados[i];
		}
		
		for (Character label : sigma){ //para cada signo del alfabeto
			State dst=transition(st,label);
			//busco la clase de equivalencia a la que pertenece el estado tran
			//para setear la transicion al estado de la clase de equivalencia
			int claseStateDst = perteneceClaseNro (ClasesEq, dst,i);
			Map<Character,State> map = new HashMap<Character,State>();
			map.put(label,estados[claseStateDst]);
			transiciones.put(src,map);
		}
	}

	return new Automaton(sigma,transiciones,estados,initialState,finalStates);
}

int perteneceClaseNro (Set<State>[] ClasesEq, State est,  int j){
	for (int i = 0 ; i <= j ; i++){
		if(ClasesEq[i].contains(est)){
			return i;
		}
	}
	return 0;
}

public  Set<State> distinguidos ( Set<State>[] ClasesEq, int j, int n, Character s){ //devuelve UNA subclase de equivalencia
	Set <State> res = new HashSet<State>();
	//Para cada estado de la clase de equivalencia j, quiero ver si haciendo la transición por el caracter s llego a estados que están en distintas clases de equivalencia.
	//Si es así, entonces puedo distintiguir ese estado y agregarlo al conjunto que devuelvo.
	Iterator <State> it = ClasesEq[j].iterator();
	State st=it.next();
	State tran=transition(st,s);

	//busco la clase de equivalencia a la que pertenece el estado tran para comparar con los demas estados
	int claseState1 = perteneceClaseNro (ClasesEq, tran,j);

	//chequear que haya 2 tran que pertenezcan a 2 clases de equiivalencia distintas.
	//Agregar a res los estados que tenga tran a la misma clase para separarlos de la clase original. 
	
	while(it.hasNext()){
		st=it.next();
		State tran2= transition(st,s);
	
		int claseStateI = perteneceClaseNro (ClasesEq, tran2,j);
		
		if (claseStateI != claseState1){
			res.add(st);
		}
		return res;
	}
	
	return res;
		
}

//puede ser el automata vacio
// con una transicon
// concatenacion de automatas
// estrella

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



}


