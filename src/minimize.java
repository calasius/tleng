public static Automaton minimize(Automaton aut1, Character[] sigma, int E) { //recibimos un automata y un alfabeto (el del autómata) con su longitud E

	Set<State> EstadosFinales= aut1.getFinalStates();
	Set<State> EstadosNoFinales= aut1.getStates();
	groupB.removeAll(EstadosFinales); 
	//En grupo A estan los estados finales. En el grupo B todos los que no son finales.
	n=aut1.getStates().length;
	
	Set<State> [] ClasesEq=new State[n]; //creo clases de equivalencia, espacio para n posibles. ¿Se inicializan vacios los conjuntos?
	ClasesEq[0]= (EstadosFinales);
	ClasesEq[1]= (EstadosNoFinales);
	int ultimoAgregado=1; //registro ultimo indice
	bool HayNuevoConjunto =false; 
	
	do{
		for (int j = 0; j < n; j++){ // para cada estado de las clases de equivalencia
			for (int i = 0; i < E; i++){ //para cada signo del alfabeto
				if (ClasesEq[j].size() >1 ){ //si el estado tiene más de un elemento. 
					Set<State> distinguibles = distinguidos(Automaton aut1, ClasesEq,j,n,sigma[i]); //chequeo si se puede distinguir dentro conjunto de estados j de la clase de equivalencia con el elemento i del alfabeto . De ser posible, devuelvo una sub clase de equivalencia. Si no, devuelvo vacío.
					if (! (distinguibles.isEmpty())){
						ClasesEq[j].removeAll(distinguibles);//quito los distinguibles
						ClasesEq[ultimoAgregado+1]=distinguibles;//los agrego como clase de equivalencia
						ultimoAgregado++; //muevo el indice
						HayNuevoConjunto =true; //loopeo de nuevo
					} 								
				}
			}
		}
	}while(HayNuevoConjunto);
	
	//Todos los elementos de ClasesEq que tienen algún elemento son un estado distinto para el autómata mínimo. Las transiciones son las de cada estado que componen cada clase de equivalencia. Los estados finales son las clases que contengan algún estado final. 
}

public static Set<State> distinguidos (Automaton aut1, Set<State>[] ClasesEq, int j, int n, Character s){ //devuelve UNA subclase de equivalencia
	Set <State> res = new HashSet<State>();
	//Para cada estado de la clase de equivalencia j, quiero ver si haciendo la transición por el caracter s llego a estados que están en distintas clases de equivalencia. Si es así, entonces puedo distintiguir ese estado y agregarlo al conjunto que devuelvo.
	Iterator <Set <State> > it = ClasesEq[j].iterator();
	while(it.hasNext()){
		State st=it.next();
		State tran=transition(aut1,st,s);
		//chequear que haya 2 tran que pertenezcan a 2 clases de equiivalencia distintas. Agregar a res los estados que tenga tran a la misma clase para separarlos de la clase original. 
	}
	}
