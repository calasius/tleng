package automaton;
import java.util.List;

import exceptions.InvalidAutomatonException;


public class Automaton {
	
	private final List<Character> sigma;
	
	private final List<Transition> transitions;
	
	private final List<State> states;
	
	private final State initialState;
	
	private final List<State> finalStates;
	
	
	public Automaton(List<Character> sigma, List<Transition> transitions, List<State> states, 
			State initialState, List<State> finalStates) throws InvalidAutomatonException {
		
		this.sigma = sigma;
		this.transitions = transitions;
		this.states = states;
		this.initialState = initialState;
		this.finalStates = finalStates;

	}


	public List<Character> getSigma() {
		return sigma;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public List<State> getStates() {
		return states;
	}

	public State getInitialState() {
		return initialState;
	}

	public List<State> getFinalStates() {
		return finalStates;
	}


}
