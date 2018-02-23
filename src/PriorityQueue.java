//************************************************************************
//************************************************************************
public class PriorityQueue implements QueueInterface, java.io.Serializable {
	private Node firstNode;
	private Node lastNode;
        int length;
        //*********************************************************************
        public boolean piorityEnqueue(Comparable newEntry) {
		Node newNode = new Node(newEntry);
		Node nodeBefore = getNodeBefore(newEntry);

		if (isEmpty() || (nodeBefore == null)) {
			newNode.setNextNode(firstNode);
			firstNode = newNode;
		}
		else {
			Node nodeAfter = nodeBefore.getNextNode();
			newNode.setNextNode(nodeAfter);
			nodeBefore.setNextNode(newNode);
		}
                length ++;
		return true;	
        }
        //*********************************************************************
	private Node getNodeBefore(Comparable anEntry) {
		Node currentNode = firstNode;
		Node nodeBefore = null;

		while ((currentNode != null) && (anEntry.compareTo(currentNode.getData()) > 0)) {
			nodeBefore = currentNode;
			currentNode = currentNode.getNextNode();
		}
		return nodeBefore;
	}
	//********************************************************************
	public PriorityQueue() {
		firstNode = null;
		lastNode = null;
	}
	//********************************************************************
	public void enqueue(Object newEntry) {
		Node newNode = new Node(newEntry, null);
		if (isEmpty())
			firstNode = newNode;
		else
                    lastNode.setNextNode(newNode);
                    lastNode = newNode;
                    length ++;
	}
	//********************************************************************
	public Object dequeue() {
		Object front = null;
		if (!isEmpty()) {
			front = firstNode.getData();
			firstNode = firstNode.getNextNode();
			if (firstNode == null)
				lastNode = null;
		}
                length --;
		return front;
	}
	//********************************************************************
	public Object getFront() {
		Object front = null;
		if (!isEmpty())
			front = firstNode.getData();
		return front;
	}
        
	//********************************************************************
	public boolean isEmpty() {
		return firstNode == null;
	}
	//********************************************************************
	public void clear() {
		firstNode = null;
		lastNode = null;
                length = 0;
	}
	//********************************************************************
	//********************************************************************
	private class Node {
		private Object data;
		private Node next;

		private Node(Object dataPortion) {
			data = dataPortion;
			next = null;	
		}
		
		private Node(Object dataPortion, Node nextNode) {
			data = dataPortion;
			next = nextNode;	
		}
		
		private Object getData() {
			return data;
		}
		
		private void setData(Object newData) {
			data = newData;
		}
		
		private Node getNextNode() {
			return next;
		}
		
		private void setNextNode(Node nextNode) {
			next = nextNode;
		}
	}
	//********************************************************************
	//********************************************************************
}
//************************************************************************
//************************************************************************