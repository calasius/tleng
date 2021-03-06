import static automaton.AutomatonOperations.areEquivalents;
import static automaton.AutomatonOperations.complemento;
import static automaton.AutomatonOperations.intersection;

import java.io.FileNotFoundException;
import java.io.IOException;

import automaton.Automaton;
import automaton.AutomatonReader;
import automaton.AutomatonWriter;
import automaton.RegularExpressionReader;
import exceptions.InvalidAutomatonException;

public class AFD {

	enum Usage {
		None, A_RegexToDFA, B_StringRecognition, C_DOTGeneration, D_DFAIntersection, E_DFAComplement, F_DFAEquivalence
	}

	public static void main(String[] args) {

		try {
			Usage usage = ParseArguments(args);

			switch (usage) {
			case A_RegexToDFA:
				GenerateDFAFromRegex(args[1], args[3]);
				break;

			case B_StringRecognition:
				RecognizeString(args[1], args[2]);
				break;

			case C_DOTGeneration:
				GenerateDOTFromDFA(args[1], args[3]);
				break;

			case D_DFAIntersection:
				ComputeDFAIntersection(args[2], args[4], args[6]);
				break;

			case E_DFAComplement:
				ComputeDFAComplement(args[2], args[4]);
				break;

			case F_DFAEquivalence:
				ComputeDFAEquivalence(args[2], args[4]);
				break;

			default:
				PrintUsage();
				break;
			}
		} catch (Exception ex) {
			System.err.print("[Error] ");
			System.err.println(ex.getMessage());
		}

	}

	private static boolean MatchUsage(String[] args, String[] usage) {
		if (args.length != usage.length)
			return false;
		final String ignore = null;

		for (int i = 0; i < usage.length; ++i) {
			String command = usage[i];
			String arg = args[i];

			if (command != ignore && !arg.equals(command))
				return false;
		}

		return true;
	}

	private static Usage ParseArguments(String[] args) throws Exception {
		if (args.length == 0)
			return Usage.None;
		final String empty = null;
		boolean match = false;

		match = MatchUsage(args, new String[] { "-leng", empty, "-aut", empty });
		if (match)
			return Usage.A_RegexToDFA;

		match = MatchUsage(args, new String[] { "-aut", empty, empty });
		if (match)
			return Usage.B_StringRecognition;

		match = MatchUsage(args, new String[] { "-aut", empty, "-dot", empty });
		if (match)
			return Usage.C_DOTGeneration;

		match = MatchUsage(args, new String[] { "-intersec", "-aut1", empty,
				"-aut2", empty, "-aut", empty });
		if (match)
			return Usage.D_DFAIntersection;

		match = MatchUsage(args, new String[] { "-complemento", "-aut1", empty,
				"-aut", empty });
		if (match)
			return Usage.E_DFAComplement;

		match = MatchUsage(args, new String[] { "-equival", "-aut1", empty,
				"-aut2", empty });
		if (match)
			return Usage.F_DFAEquivalence;

		throw new Exception("Invalid usage.");
	}

	private static void PrintUsage() {
		System.out.println("Usage:");
		System.out.println();
		System.out.println("afd -leng <archivo_regex> -aut <archivo_automata>");
		System.out.println("afd -aut <archivo_automata> <cadena>");
		System.out.println("afd -aut <archivo_automata> -dot <archivo_dot>");
		System.out
				.println("afd -intersec -aut1 <archivo_automata> -aut2 <archivo_automata> -aut <archivo_automata>");
		System.out
				.println("afd -complemento -aut1 <archivo_automata> -aut <archivo_automata>");
		System.out
				.println("afd -equival -aut1 <archivo_automata> -aut2 <archivo_automata>");
	}

	// Ejercicio 3.a
	private static void GenerateDFAFromRegex(String leng, String aut)
			throws NoSuchMethodException, IOException {
		RegularExpressionReader reader = new RegularExpressionReader();
		Automaton automaton = reader.readRegularExpression(leng);
		AutomatonWriter writer = new AutomatonWriter();
		writer.writeAutomaton(automaton, aut);
	}

	// Ejercicio 3.b
	private static void RecognizeString(String aut, String str)
			throws NoSuchMethodException, FileNotFoundException {
		AutomatonReader reader = new AutomatonReader();
		Automaton automaton = reader.readAutomaton(aut);
		automaton.complete(automaton.getSigma());
		boolean recognize = automaton.reconoce(str);
		if (recognize) {
			System.out.println("TRUE");
		} else {
			System.out.println("FALSE");
		}
	}

	// Ejercicio 3.c
	private static void GenerateDOTFromDFA(String aut, String dot)
			throws NoSuchMethodException, InvalidAutomatonException {
		AutomatonReader reader = new AutomatonReader();
		AutomatonWriter writer = new AutomatonWriter();
		try {
			Automaton automaton1 = reader.readAutomaton(aut);
			writer.makeDot(dot, automaton1);
		} catch (FileNotFoundException e) {
			throw new InvalidAutomatonException();
		}
	}

	// Ejercicio 3.d
	private static void ComputeDFAIntersection(String aut1, String aut2,
			String aut) throws NoSuchMethodException, IOException {
		AutomatonReader reader = new AutomatonReader();
		AutomatonWriter writer = new AutomatonWriter();
		Automaton automaton1 = reader.readAutomaton(aut1);
		Automaton automaton2 = reader.readAutomaton(aut2);
		Automaton intersection = intersection(automaton1,
				automaton2);
		writer.writeAutomaton(intersection, aut);
	}

	// Ejercicio 3.e
	private static void ComputeDFAComplement(String aut1, String aut)
			throws NoSuchMethodException {

		AutomatonReader reader = new AutomatonReader();
		AutomatonWriter writer = new AutomatonWriter();
		Automaton automaton1 = null;
		try {
			automaton1 = reader.readAutomaton(aut1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Automaton complement = complemento(automaton1,
				automaton1.getSigma());

		try {
			writer.writeAutomaton(complement, aut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Ejercicio 3.f
	private static void ComputeDFAEquivalence(String aut1, String aut2)
			throws NoSuchMethodException {
		AutomatonReader reader = new AutomatonReader();
		AutomatonWriter writer = new AutomatonWriter();
		Automaton automaton1 = null;
		try {
			automaton1 = reader.readAutomaton(aut1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Automaton automaton2 = null;
		try {
			automaton2 = reader.readAutomaton(aut2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (areEquivalents(automaton1, automaton2)) {
			System.out.println("TRUE");
		} else {
			System.out.println("FALSE");
		}
	}

}
