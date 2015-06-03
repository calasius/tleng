//ALGORITMO SEGUIDO: http://web.cecs.pdx.edu/~harry/compilers/slides/LexicalPart3.pdf


Set<State> alcanzablesPor(Character c, Set s, Automaton a){
	return (a.getTransitions()).get(s).get(c); //devuelvo los estados alcanzables por una transicion c desde s.
}

Set<State> clausuraEstado(Set s, Automaton a){

	 State inicial=a.getInitialState();
	 Set<State> alcanzables=alcanzablesPor(null,s,a);
	 Set<State> res = new HashSet<State>();
	 Queue <State> q = new Queue<State>() ;
	 if ( !(alcanzables.isEmpty()) ){
		for (State st : alcanzables){
			q.enqueue(st); 
			res.add(st);
		}
	State temp=new State();	 
	while( !(q.isEmpty()) ){
		temp=q.dequeue();
		alcanzables=alcanzablesPor(null,temp,a);
	    if ( !(alcanzables.isEmpty()) ){
			for (State st : alcanzables){
				q.enqueue(st); 
				res.add(st);
			}
		}
	}
	}
		return res.addAll(s); //al resultado agrego los elementos del conjunto de partida	
}


Automaton clausuraL(Automaton a){
	Set<State> inic= clausuraEstado(a.getInitialState(),a);
	List< Set<State> > estados = new ArrayList< Set<State> >();
	estados.add(inic); //estados[0] tiene al estado inicial del nuevo automata
	Queue< Set<State> > sinMarcar = new Queue< Set<State> >();
	sinMarcar.enqueue(inic);
	Map<State, Map<Character,State>> transiciones=new HashMap<State, Map<Character,State>>();
	
	while(!(sinMarcar.isEmpty())){
		Set<State> temp = sinMarcar.dequeue(); //saco un conjunto de estados 
		for(Character c: a.getSigma()){ //miro las transiciones por cada simbolo del alfabeto
			
			Set<State> newSt =new Set<State>();
			for (State st: temp){
				newSt.addAll(alcanzablesPor(c,st,a)); //junto los estados alcanzables por cada simbolo
			}
			Set<State> clausuras = new Set<State>(); 
			for (State st: newSt){ //newSt tiene los estados a los que puedo llegar desde cada simbolo
				clausuras.addAll(clausuraEstado(st,a)); //clausuro por lambda todos los estados 
			}
			if(! (estados.contains(clausuras) ){ //si CLAUSURAS no existía como estado, lo agrego y lo pongo en la cola para clausurarlo
				estados.add(clausuras);
				sinMarcar.enqueue(clausuras);				
			}
												
			if (!(clausuras.isEmpty())){//registro las transiciones entre los estados del nuevo DFA (si la clausura no dio vacio)
				State src = new State((estados.indexOf(temp)).toString());
				State dst = new State((estados.indexOf(clausuras)).toString()); //los nombres de los estados son los indices en la lista
				
				if (transiciones.containsKey(src)) {
					transiciones.get(src).put(c, dst);
				} else {				
				Map<Character, State> newTran = new HashMap<Character,State>();
				newTran.put(c,dst);
				transiciones.put(src,newTran);
				}
			}
		}
			
	}
	//En ESTADOS estan todos los estados del automata determinizado como conjunto de estados del NFA. En TRANSICIONES están todas las transiciones entre esos estados empleando su posicion en la lista "estados". 
	//Queda definir que estados son finales y transformar los conjuntos de estados en estados para el nuevo DFA usando su posicion en la lista "estados" para que coincida con TRANSICIONES.
	Set<State> estadosFinales = new Set<State>();
	Set<State> estadosTotales = new Set<State>();
	
	
	//para saber si un estado del DFA va a ser final, tengo que ver si se formó usando algún estado final
	
	for (Set<State> sst: estados){ 
		boolean hayFinal? = false;
		for(State st : sst){ 
			if (a.getFinalStates().contains(st)){
				State estado_dfa =new State((estados.indexOf(sst)).toString());
				estadosFinales.add(estado_dfa );
				estadosTotales.add(estado_dfa);
				hayFinal?=true;
				break;
			}
		}
		if(!hayFinal?){
			State estado_dfa =new State((estados.indexOf(sst)).toString());
			estadosTotales.add(estado_dfa);
		}
	}
	return (new Automaton(a.getSigma(), transiciones,estadosTotales, estadosTotales[0], estadosFinales));
			
}
