package automaton;

public class Transition {
	
	private final State fromState;
	
	private final State toState;
	
	private final Character simbol;
	
	public Transition(State fromState, State toState, Character simbol) {
		this.fromState = fromState;
		this.toState = toState;
		this.simbol = simbol;
	}

	public State getFromState() {
		return fromState;
	}

	public State getToState() {
		return toState;
	}

	public Character getSimbol() {
		return simbol;
	}
	
}