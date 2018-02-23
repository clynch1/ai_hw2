public interface QueueInterface {        
	public void enqueue(Object newEntry);
	public Object dequeue();
	public Object getFront();
	public boolean isEmpty();
	public void clear();
}