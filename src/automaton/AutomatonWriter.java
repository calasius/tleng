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
		writer.close();
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
	
	public void makeDot(String filename, Automaton automaton) {

		try {

			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println("strict digraph {\n rankdir=LR;\n");
			writer.println("node [shape = none, label = \"\", width = 0, height = 0]; qd;");
			writer.println("node [label=\"\\N\", width = 0.5, height = 0.5];");

			// Imprimo los estados finales con doble circulo
			writer.println("node [shape = doublecircle]; ");

			for (State s : automaton.getFinalStates()) {
				writer.println(s.getName() + ";\n"); // Imprimo cada nombre de
														// los estados finales
			}

			writer.println("node [shape = circle];");

			// Imprimo las transiciones con sus respectivos labels
			writer.println("qd -> " + automaton.getInitialState().getName());
			for (State src : automaton.getTransitions().keySet()) {
				for (Character symbol : automaton.getTransitions().get(src).keySet()) {
					writer.println(src.getName() + " -> "
							+ automaton.getTransitions().get(src).get(symbol).getName()
							+ "[label=\"" + symbol + "\"]");
				}
			}

			// Cierro el archivo
			writer.println("}");
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
