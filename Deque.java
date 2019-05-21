public class Deque
{
	//public int size;
	//top of the dequeue
	public Node first;
	//end of the deque
	public Node last;

	//insert node into the front of the queue
	public void insertFront(int number)
	{
		//create a node based on the argument
		Node newNode = new Node(number, null, first);
		//set the previous node of the first node to be the new node if first node is not null
		if (first != null)
			first.previous = newNode;
		//set the last node to be the new node if last node is null
		if (last == null)
			last = newNode;
		//set the first node to be the new node
		first = newNode;

	}

	//insert node into the rear of the queue
	public void insertRear(int number)
	{
		//create a node based on the argument
		Node newNode = new Node(number, last, null);
		//set the next node of the last node to be the new node if last node is not null
		if (last != null)
			last.next = newNode;
		//set the first node to be the new node if first node is null
		if (first == null)
			first = newNode;
		//set the last node to be the new node
		last = newNode;
	}

	//delete the front of the queue
	public void deleteFront()
	{
		//set first node to be its next node
		first = first.next;
		if (first == null)
			last = null;
		else
			first.previous = null;
	}

	//delete the rear of the queue
	public void deleteRear()
	{
		//set last node to be its previous node
		last = last.previous;
		if (last == null)
			first = null;
		else
			last.next = null;
	}

	//return the front of the queue
	public int getFront()
	{
		return first.state;
	}

	//return the rear of the queue
	public int getRear()
	{
		return last.state;
	}

	//whether the queue is empty
	public boolean isEmpty()
	{
		return first == null;
	}

	//node in the queue
	private class Node
	{
		//integer value in this node
		public int state;
		//the previous node of this node
		public Node previous;
		//the next node of this node
		public Node next;

		//constructor
		public Node(int number, Node p, Node n)
		{
			state = number;
			previous = p;
			next = n;
		}
	}
}