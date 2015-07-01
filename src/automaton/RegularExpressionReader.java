package automaton;

import static automaton.AutomatonOperations.concat;
import static automaton.AutomatonOperations.estrella;
import static automaton.AutomatonOperations.unCaracter;
import static automaton.AutomatonOperations.union;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.InvalidRegularExpresion;

public class RegularExpressionReader {

	public Automaton readRegularExpression(String source)
			throws FileNotFoundException {
		Scanner scanner = new Scanner(
				new BufferedReader(new FileReader(source)));
		return readRegularExpression(scanner);
	}

	private Automaton readRegularExpression(Scanner scanner) {
		Automaton automaton = null;
		String line = scanner.nextLine();
		if (isAnOperation(line)) {
			OperatorType type = getOperator(line);
			int operandsAmount = getOperandsAmount(line);

			Automaton[] operands = new Automaton[operandsAmount];
			for (int i = 0; i < operandsAmount; i++) {
				operands[i] = readRegularExpression(scanner);
			}

			switch (type) {
			case CONCAT:
				automaton = concat(operands);
				break;
			case STAR:
				automaton = estrella(operands[0]);
				break;
			case PLUS:
				automaton = concat(operands[0], estrella(operands[0]));
				break;
			case OPT:
				break;
			case OR:
				automaton = union(operands);
				break;
			default:
				break;
			}

		} else {
			Character label = Character.valueOf(line.trim().charAt(0));
			Set<Character> sigma = new HashSet<Character>();
			sigma.add(label);
			automaton = unCaracter(sigma, label);
		}
		return automaton;
	}

	private int getOperandsAmount(String line) {
		Pattern regex = Pattern.compile("\\d");
		Matcher regexMatcher = regex.matcher(line);
		if (regexMatcher.find()) {// Finds Matching Pattern in String
			return Integer.valueOf(regexMatcher.group(0));
		} else {
			return 1;
		}
	}

	private OperatorType getOperator(String line){
		Pattern regex = Pattern.compile("\\{(.*?)\\}");
		Matcher regexMatcher = regex.matcher(line);
		if (regexMatcher.find()) {
			return OperatorType.valueOf(regexMatcher.group(1));
		} else {
			throw new InvalidRegularExpresion();
		}
	}

	private boolean isAnOperation(String line) {
		line = line.trim();
		return line.length() > 1;
	}

	public static void main(String[] args) {
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("\\d");
		Matcher regexMatcher = regex.matcher("{CONCAT}3");
		while (regexMatcher.find()) {// Finds Matching Pattern in String
			matchList.add(regexMatcher.group(0));// Fetching Group from String
		}
		for (String str : matchList) {
			System.out.println(str);
		}
	}

}
