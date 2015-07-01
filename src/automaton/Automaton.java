package automaton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import exceptions.NoTransitionException;

/**
 * @author calasius
 *
 */
public class Automaton {

	private final Set<Character> sigma;

	private final Map<State, Map<Character, State>> transitions;

	private final State[] states;

	private final State initialState;

	private final Set<State> finalStates;

	public Automaton(Set<Character> sigma,
			Map<State, Map<Character, State>> transitions, State[] states,
			State initialState, Set<State> finalStates) {

		this.sigma = sigma;
		this.transitions = transitions;
		this.states = states;
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.complete(getSigma());
	}

	private boolean isComplete(Set<Character> sigma) {
		Set<Character> newSigma = new HashSet<Character>();
		newSigma.addAll(sigma);
		newSigma.retainAll(this.getSigma());
		for (Character label : newSigma) {
			for (State state : this.getStates()) {
				if (transition(state, label) == null) {
					return false;
				}
			}
		}
		return true;
	}

	public void complete(Set<Character> sigma) {
		if (!isComplete(sigma)) {
			State[] newStates = Arrays.copyOf(this.getStates(),
					this.getStates().length + 1);
			State nullState = new State("NULL");
			newStates[this.getStates().length] = nullState;

			Set<Character> newSigma = new HashSet<Character>();
			newSigma.addAll(sigma);
			newSigma.retainAll(this.getSigma());

			for (State state : newStates) {
				for (Character label : newSigma) {
					if (transition(state, label) == null) {
						insertTransition(state, nullState, label,
								getTransitions());
					}
				}
			}
		}
	}

	public State getStateNumber(int i) {
		return this.states[i];
	}

	public Map<State, Map<Character, State>> getTransitions() {
		return transitions;
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
		if (transitions.isEmpty() || !transitions.containsKey(q)) {
			return null;
		}
		return transitions.get(q).get(label);
	}

	private static void insertTransition(State source, State dest,
			Character label, Map<State, Map<Character, State>> unionTransitions) {

		if (unionTransitions.containsKey(source)) {
			unionTransitions.get(source).put(label, dest);
		} else {
			Map<Character, State> labelTransitions = new HashMap<Character, State>();
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

	public boolean reconoce(String str) {
		State tran = initialState;
		for (int i = 0, n = str.length(); i < n; i++) {
			char c = str.charAt(i);
			Map<Character, State> map = transitions.get(initialState);
			tran = map.get(c);
		}

		return isFinal(tran);

	}

	public boolean acepta(String s, Automaton aut1) {
		State ini = aut1.getInitialState();
		State tran = ini;
		boolean aceptado = true;
		for (int i = 0, n = s.length(); i < n; i++) {
			char c = s.charAt(i);
			tran = aut1.transition(ini, c);
			if (tran == null) {// si no hay transición
				aceptado = false;
				break;
			}
		}
		if (aceptado && ((aut1.getFinalStates()).contains(tran))) {
			// si tuvo todas la transiciones y llega a un estadofinal
			return aceptado;
		} else {
			return false;
		}
	}
}
