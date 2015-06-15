package automaton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import static automaton.AutomatonOperations.*;

public class ExpressionRegularReader {
	
	public Automaton readRegularExpression(String source) throws FileNotFoundException {
		Scanner scanner = new Scanner(new BufferedReader(new FileReader(source)));
		return readRegularExpression(scanner);
	}
	
	private Automaton readRegularExpression(Scanner scanner) {
		Automaton automaton = null;
		String line = scanner.nextLine();
		if (isAnOperation(line)) {
			OperatorType type = getOperator(line);
			int operandsAmount = getOperandsAmount(line);
			
			Automaton[] operands = new Automaton[operandsAmount];
			for(int i = 0; i < operandsAmount; i++) {
				operands[i] = readRegularExpression(scanner);
			}
			
			switch (type) {
			case CONCAT:
				concat(operands);
				break;
			case STAR:
				estrella(operands[0]);
				break;
			case PLUS:
				break;
			case OPT:
				break;
			case OR:
				break;
			default:
				break;
			}
			
		} else {
			Character label = Character.valueOf(line.charAt(0));
			Set<Character> sigma = new HashSet<Character>();
			sigma.add(label);
			automaton = unCaracter(sigma, label);
		}
		return automaton;
	}

	private int getOperandsAmount(String line) {
		// TODO Auto-generated method stub
		return 0;
	}

	private OperatorType getOperator(String line) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isAnOperation(String line) {
		// TODO Auto-generated method stub
		return false;
	}

}
