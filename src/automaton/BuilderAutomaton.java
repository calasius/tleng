package automaton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuilderAutomaton {
	
	public static Automaton buildStartAutomaton(Set<Character> sigma) {
		State[] states = new State[1];
		State initialState = new State("q0");
		states[0] = initialState;
		Set<State> finalStates = new HashSet<State>();
		finalStates.add(initialState);
		
		Map<State, Map<Character, State>> transitions = new HashMap<State, Map<Character,State>>();
		
		for (Character label : sigma) {
			insertTransition(initialState, initialState, label, transitions);
		}
		
		return new Automaton(sigma, transitions, states, initialState, finalStates);
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

}
