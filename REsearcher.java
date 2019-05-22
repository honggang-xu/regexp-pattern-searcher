import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class REsearcher
{
	public static void main (String [] args)
	{
		if (args.length != 1)
		{
			System.err.println("Usage: java REsearcher <filename>");
			return;
		}
		try
		{
			BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			String line;
			//create a finite state machine
			FSM machine = new FSM();
			while ((line = stdReader.readLine()) != null)
			{
				String[] inputs = line.split(" ");
				String state = inputs[1];
				int next1 = Integer.parseInt(inputs[2]);
				int next2 = Integer.parseInt(inputs[3]);
				machine.addState(state, next1, next2);
			}

			//while there is input in the file
			while ((line = reader.readLine()) != null)
			{
				//process one line
				if (process(machine, line))
					System.out.println(line);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//method to search matching patterns in the line
	public static boolean process(FSM machine, String line)
	{
		for (int i = 0; i < line.length(); i++)
		{
			//the substring to be processed
			String subString = line.substring(i, line.length());
			//index into the substring
			int index = 0;
			//create a double ended queue
			Deque deque = new Deque();
			//-1 representing SCAN
			deque.insertFront(-1);
			//insert starting state to the rear of the queue
			deque.insertRear(machine.getState(0).next1);
			//if there are possilble next states
			while (deque.getRear() != -1)
			{
				//if final state can be reached, return true
				if (deque.getRear() == machine.size())
					return true;
				//if it is a branching state at the rear of the queue
				if (machine.getState(deque.getRear()).symbol.equals("BR"))
				{
					//get the two next states from the state at the rear of the queue
					int next1 = machine.getState(deque.getRear()).next1;
					int next2 = machine.getState(deque.getRear()).next2;
					//pop rear of the queue
					deque.deleteRear();
					//only add one state to the front of the queue if both next states are the same
					if (next1 == next2)
					{
						deque.insertFront(next1);
					}
					//else add both next states
					else
					{
						deque.insertFront(next1);
						deque.insertFront(next2);
					}	
				}
				//if the rear of the queue is not a branching state, pop and add it to the front of the queue
				else
				{
					//System.out.println("looking at potential states: " + machine.getState(deque.getRear()).symbol);
					int stateAtRear = deque.getRear();
					deque.deleteRear();
					deque.insertFront(stateAtRear);
				}
		
				//while there are possible current states
				while (deque.getFront() != -1)
				{
					//if final state can be reached, return true
					if (deque.getFront() == machine.size())
						return true;

					//if it is a branching state at the front of the queue
					if (machine.getState(deque.getFront()).symbol.equals("BR"))
					{
						//get the two next states from the state at the front of the queue
						int next1 = machine.getState(deque.getFront()).next1;
						int next2 = machine.getState(deque.getFront()).next2;
						//pop front of the queue
						deque.deleteFront();
						//only add one state to the front of the queue if both next states are the same
						if (next1 == next2)
						{
							deque.insertFront(next1);
						}
						//else add both next states
						else
						{
							deque.insertFront(next1);
							deque.insertFront(next2);
						}	
					}
					//if final state can be reached, return true
					if (deque.getFront() == machine.size())
						return true;
					//if there are characters to be processed in the substring
					if (index < subString.length())
					{
						//get the string from the state at the front of the queue
						String symbol = machine.getState(deque.getFront()).symbol;
						//try matching the current character in the substring
						//get the character in the substring from index position
						char c = subString.charAt(index);
						//if the state is ^[], representing mathching a character not in list of literals
						if (symbol.charAt(0) == '^' && symbol.charAt(symbol.length() - 1) == ']')
						{
							//get the list of literals
							String literals = symbol.substring(2, symbol.length() - 1);
							//if the character is not in the list
							if (literals.indexOf(c) == -1)
							{
								//then it is a match
								//advance index
								index++;
								//get the two next states from the state at the front of the queue
								int next1 = machine.getState(deque.getFront()).next1;
								int next2 = machine.getState(deque.getFront()).next2;
								//pop front of the queue
								deque.deleteFront();
								//only add one state to the rear of the queue as possible next state if both next states are the same
								if (next1 == next2)
								{
									deque.insertRear(next1);
								}
								//else add both next states
								else
								{
									deque.insertRear(next1);
									deque.insertRear(next2);
								}	
							}
							else
							{
								//not a match, pop the state at the front of the queue
								deque.deleteFront();
							}
							
						}
						//if the state is [], representing mathching a character in list of literals
						else if (symbol.charAt(0) == '[' && symbol.charAt(symbol.length() - 1) == ']')
						{
							String literals = symbol.substring(1, symbol.length() - 1);
							if (literals.indexOf(c) >= 0)
							{
								index++;
								int next1 = machine.getState(deque.getFront()).next1;
								int next2 = machine.getState(deque.getFront()).next2;
								deque.deleteFront();
								if (next1 == next2)
								{
									deque.insertRear(next1);
								}
								else
								{
									deque.insertRear(next1);
									deque.insertRear(next2);
								}
							}
							else
							{
								//not a match, pop the state at the front of the queue
								deque.deleteFront();
							}
							
						}
						//match any literal
						else if (symbol.equals("."))
						{
							index++;
							int next1 = machine.getState(deque.getFront()).next1;
							int next2 = machine.getState(deque.getFront()).next2;
							deque.deleteFront();
							if (next1 == next2)
							{
								deque.insertRear(next1);
							}
							else
							{
								deque.insertRear(next1);
								deque.insertRear(next2);
							}
						}
						//match itself
						else
						{
							if (symbol.indexOf(c) >= 0)
							{
								index++;
								int next1 = machine.getState(deque.getFront()).next1;
								int next2 = machine.getState(deque.getFront()).next2;
								deque.deleteFront();
								if (next1 == next2)
								{
									deque.insertRear(next1);
								}
								else
								{
									deque.insertRear(next1);
									deque.insertRear(next2);
								}
							}
							else
							{
								//not a match, pop the state at the front of the queue
								deque.deleteFront();
							}
							
						}
					}
					//else finish processing this substring
					else
					{
						break;
					}
				}
			}
		}	
		return false;
	}
}
