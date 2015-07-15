package automaton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class AutomatonReader {

	public Automaton readAutomaton(String source) throws FileNotFoundException {
		Scanner scanner = new Scanner(new BufferedReader(new FileReader(source)));
		String statesLine = scanner.nextLine();
		String sigmaLine = scanner.nextLine();
		String initialStateLine = scanner.nextLine();
		String finalStatesLine = scanner.nextLine();
		
		List<String> transitionsLines = new ArrayList<String>();
		while (scanner.hasNextLine()) {
			transitionsLines.add(scanner.nextLine());
		}

		State[] states = createStates(statesLine);
		Set<Character> sigma = createSigma(sigmaLine);
		State initialState = getInitialState(initialStateLine);
		Set<State> finalStates = getFinalStates(finalStatesLine);
		Map<State, Map<Character, State>> transitions = getTransitions(transitionsLines);

		scanner.close();
		return new Automaton(sigma, transitions, states, initialState,
				finalStates);
	}

	private Map<State, Map<Character, State>> getTransitions(
			List<String> transitionsLines) {
		Map<State, Map<Character, State>> transitions = new HashMap<State, Map<Character, State>>();

		for (String transitionLine : transitionsLines) {
			String[] names = transitionLine.split("\t");
			insertTransition(new State(names[0]), new State(names[2]),
					Character.valueOf(names[1].charAt(0)), transitions);
		}
		return transitions;
	}

	private Set<State> getFinalStates(String finalStatesLine) {
		String[] names = finalStatesLine.split("\t");
		Set<State> finalStates = new HashSet<State>();
		for (int i = 0; i < names.length; i++) {
			finalStates.add(new State(names[i]));
		}
		return finalStates;
	}

	private State getInitialState(String initialStateLine) {
		return new State(initialStateLine.trim());
	}

	private Set<Character> createSigma(String sigmaLine) {
		String[] characters = sigmaLine.split("\t");
		Set<Character> sigma = new HashSet<Character>();
		for (int i = 0; i < characters.length; i++) {
			sigma.add(characters[i].charAt(0));
		}
		return sigma;
	}

	private State[] createStates(String statesLine) {
		String[] names = statesLine.split("\t");
		State[] states = new State[names.length];
		for (int i = 0; i < names.length; i++) {
			states[i] = new State(names[i]);
		}
		return states;
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

}
