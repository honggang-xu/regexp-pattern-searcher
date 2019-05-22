import java.lang.IllegalArgumentException;

/*
Grammar Rules:
E -> T
E -> TE
T -> F
T -> F*
T -> F?
T -> F|T
F -> V
F -> .
F -> [L] or ^[L]
F -> (E)
L -> V
*/

public class REcompiler
{
	//string to store regular expression
	public String regexp;
	//finite state machine
	public FSM machine;
	//pointer points to the index position of the regexp to be processed
	public int pointer;
	//state points to the next state to be built
	public int state;

	//constructor
	public REcompiler(String phrase)
	{
		regexp = phrase;
		machine = new FSM();
		pointer = 0;
		state = 1;
	}

	//check whether a character is in the vocabulary
	public boolean isVocab(char c)
	{
		return (".*?|()[]^\\".indexOf(c) == -1);
	}

	public static void main(String [] args)
	{
		if (args.length != 1)
		{
			System.err.println("Usage: java REcompiler \"regexp\"");
			return;
		}

		try
		{
			REcompiler compiler = new REcompiler(args[0]);
			compiler.parse();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//build an expression
	public int expression() throws IllegalArgumentException
	{
		//integer for the initial state
		int r;
		//build a term
		r = term();
		//if the processing of regular expression is not finished
		if (pointer < regexp.length())
		{
			if (isVocab(regexp.charAt(pointer)) || "([^\\.".indexOf(regexp.charAt(pointer)) >= 0)
				expression();
		}	
		return r;
	}

	//build a term
	public int term() throws IllegalArgumentException
	{
		//t1 is term1, t2 is term2, f is the final state
		int r, t1, t2, f;
		//set the final state to the last state processed
		f = state - 1;
		//build a facotor
		r = t1 = factor();
		if (pointer < regexp.length())
		{
			//if looking at a *
			if (regexp.charAt(pointer) == '*')
			{
				//add a branching state to the machine
				machine.addState("BR", state + 1, t1);
				//advance pointer
				pointer++;
				//set initial state to be this branching state
				r = state;
				//advance state
				state++;
			}
			//if looking at a ?
			else if (regexp.charAt(pointer) == '?')
			{
				//if there is a next character in the regular expression
				if (pointer + 1 < regexp.length())
				{
					//if it follows a branching state, there is no need to create a branching state
					if (regexp.charAt(pointer + 1) != '|')
					{
						machine.addState("BR", state + 1, t1);
						r = state;
						state++;
					}
				}
				else
				{
					machine.addState("BR", state + 1, t1);
					r = state;
					state++;
				}

				//create a branching state points to the next state after consume this symbol
				machine.addState("BR", state + 1, state + 1);
				state++;
				//set the next states of the symbol before ? to the branching state points to next state 
				machine.setState(r - 1, state - 1, state - 1);
				pointer++;
			}
			if (pointer < regexp.length())
			{
				//if looking at a |
				if (regexp.charAt(pointer) == '|')
				{
					//if the two states of the last state is the same, set one to the state that are going to be built
					if (machine.getState(f).next1 == machine.getState(f).next2)
						machine.getState(f).next2 = state;
					machine.getState(f).next1 = state;
					//set the final state to the last state processed
					f = state - 1;
					pointer++;
					r = state;
					state++;
					//add a branching state
					machine.addState("BR", t1, t1);
					//build a term
					t2 = term();
					//set the branching
					machine.setState(r, t1, t2);
					
					//if the two states of the last state is the same, set one to the state that are going to be built
					if (machine.getState(f).next1 == machine.getState(f).next2)
						machine.getState(f).next2 = state;
					machine.getState(f).next1 = state;
				}
			}
		}
		return r;
	}

	//build a factor
	public int factor() throws IllegalArgumentException
	{
		int r = 0;
		if (pointer < regexp.length())
		{
			//if looking at a vocabulary character
			if (isVocab(regexp.charAt(pointer)))
			{
				//add a state for this character
				machine.addState(Character.toString(regexp.charAt(pointer)), state + 1, state + 1);
				pointer++;
				r = state;
				state++;
			}
			else
			{
				//if looking at escape character
				if (regexp.charAt(pointer) == '\\' && (pointer + 1) < regexp.length())
				{
					//add a state for the character after the escape character
					machine.addState(Character.toString(regexp.charAt(pointer + 1)), state + 1, state + 1);
					pointer += 2;
					r = state;
					state++;
				}
				//if looking at (
				else if (regexp.charAt(pointer) == '(')
				{
					pointer++;
					//build an expression
					r = expression();
					//if there is a matching )
					if (regexp.charAt(pointer) == ')')
					{
						//System.out.println("looking at )");
						pointer++;
					}
					else
					{
						error();
					}
				}
				//if looking at [
				else if (regexp.charAt(pointer) == '[')
				{
					pointer++;
					String match = "[";

					//if immediately followed by a ], treat it as a literal
					if (regexp.charAt(pointer) == ']')
					{
						match += regexp.charAt(pointer);
						pointer++;
					}
					//build the list of literals while ] is not found
					while (regexp.charAt(pointer) != ']')
					{
						//if there is no matching ] found after processing the regular expression
						if (pointer >= regexp.length())
						{
							error();
						}
						match += regexp.charAt(pointer);
						pointer++;
					}
					match += "]";
					//add a state for this list
					machine.addState(match, state + 1, state + 1);
					pointer++;
					r = state;
					state++;
				}
				//if looking at ^
				else if (regexp.charAt(pointer) == '^')
				{
					if (regexp.charAt(pointer + 1) == '[')
					{

						pointer += 2;
						String match = "^[";
						//if immediately followed by a ], treat it as a literal
						if (regexp.charAt(pointer) == ']')
						{
							match += regexp.charAt(pointer);
							pointer++;
						}
						//build the list of literals while ] is not found
						while (regexp.charAt(pointer) != ']')
						{
							//if there is no matching ] found after processing the regular expression
							if (pointer >= regexp.length())
							{
								error();
							}
							match += regexp.charAt(pointer);
							pointer++;
						}
						match += "]";
						//add a state for this list
						machine.addState(match, state + 1, state + 1);
						pointer++;
						r = state;
						state++;
					}
					else
					{
						error();
					}
				}
				//if looking at .
				else if (regexp.charAt(pointer) == '.')
				{
					//add a state for wildcard
					machine.addState(".", state + 1, state + 1);
					pointer++;
					r = state;
					state++;
				}
				else
				{
					error();
				}
			}
		}
		else
		{
			error();
		}
		return r;
	}

	//method for parsing a regular expression
	public void parse() throws IllegalArgumentException
	{
		//the first state is a branching state that points to the start state of the machine
		machine.addState("BR", 1, 1);
		//integer for initial state
		int initial;
		//build an expression
		initial = expression();
		//set the initial state
		machine.setState(0, initial, initial);
		//if the regular expression has not been fully processed, show error
		if (pointer != regexp.length())
			error();
		System.out.print(machine.print());
	}

	//method for error handling
	public void error() throws IllegalArgumentException
	{
		String e = "Error occurred during parsing, please check your regular expression.";
		throw new IllegalArgumentException(e);
		
	}
}