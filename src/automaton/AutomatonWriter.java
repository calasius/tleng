package automaton;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AutomatonWriter {
	
	public void writeAutomaton(Automaton automaton, String file) throws IOException {
		
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		
		String statesLines = getStatesLine(automaton);
		writer.println(statesLines);
		
		String sigmaLine = getSigmaLine(automaton);
		writer.println(sigmaLine);
		
		writer.println(automaton.getInitialState().getName());
		
		String finalStatesLine = getFinalStatesLine(automaton);
		writer.println(finalStatesLine);
		
		for (State state : automaton.getTransitions().keySet()) {
			for (Character label : automaton.getTransitions().get(state).keySet()) {
				writer.println(String.format("%s\t%s\t%s",state.getName(),label,automaton.transition(state, label).getName()));
			}
		}
		
		writer.flush();
		
	}

	private String getFinalStatesLine(Automaton automaton) {
		StringBuffer buffer = new StringBuffer();
		for(State state : automaton.getFinalStates()) {
			buffer.append(state.getName()+"\t");
		}
		return buffer.toString().trim();
	}

	private String getSigmaLine(Automaton automaton) {
		StringBuffer buffer = new StringBuffer();
		for(Character character : automaton.getSigma()) {
			buffer.append(character + "\t");
		}
		return buffer.toString().trim();
	}

	private String getStatesLine(Automaton automaton) {
		StringBuffer buffer = new StringBuffer();
		for(State state : automaton.getStates()) {
			buffer.append(state.getName()+"\t");
		}
		return buffer.toString().trim();
	}

}
