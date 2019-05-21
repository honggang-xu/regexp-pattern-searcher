import java.util.ArrayList;

public class FSM
{
	//array to store states
	public ArrayList<State> states;

	//constructor
	public FSM()
	{
		states = new ArrayList<State>();
	}

	//return the state object based on parameter
	public State getState(int index)
	{
		return states.get(index);
	}

	//set the next1 and next1 of the state object based on parameters
	public void setState(int index, int next1, int next2)
	{
		State s = states.get(index);
		s.next1 = next1;
		s.next2 = next2;
	}

	//initialize a state object and add it to the array
	public void addState(String s, int next1, int next2)
	{
		State newState = new State(s, next1, next2);
		states.add(newState);
	}

	//return the size of the array
	public int size()
	{
		return states.size();
	}

	//print all the states in the array
	public String print()
	{
		String result = new String();
		for (int i = 0; i < states.size(); i++)
		{
			result += (i + " " + states.get(i).print() + "\n");
		}
		return result;
	}

	//private class representing the state
	public class State
	{
		//string in this state
		public String symbol;
		//possible next two state
		public int next1;
		public int next2;

		//constructor
		public State(String s, int x, int y)
		{
			symbol = s;
			next1 = x;
			next2 = y;
		}

		//print this state
		public String print()
		{
			return (symbol + " " + next1 + " " + next2); 
		}
	}
}