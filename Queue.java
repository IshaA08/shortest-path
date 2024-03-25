public class Queue<T> {

	// Making the queue instance
    private java.util.LinkedList<T> list = new java.util.LinkedList<T>();
    
    // Empty constructor
    public Queue() {
    }
    
    // Empty the queue
    public void clear() {
        list.clear();
    }
    
    // Check if the queue is empty
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    // Get the first element from the queue
    public T firstEl() {
        return list.getFirst();
    }
    
    // Get rid of the first element of the queue
    public T dequeue() {
        return list.removeFirst();
    }
    
    // Add an element to the back
    public void enqueue(T el) {
        list.addLast(el);
    }
    
    // Convert to String
    public String toString() {
        return list.toString();
    }
    
} // End of Queue
