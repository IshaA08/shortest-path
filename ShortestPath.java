import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Random;

import java.io.*;														// For file I/O
import java.util.*;														// Scanner, FileReader, etc.

public class ShortestPath {

	/*
	 * Constructor that does nothing when called.
	 */
	 
	public ShortestPath () {
		// Do nothing
	}
	
	private static HashMap<Integer, ArrayList<Integer>> mazeMap = null;
	private static MazeVisualizer drawApp = null;
	private static int vertexNumber = 0;


	/*
	 * Will read from the maze file, store the maze information in a HashMap.
	 * Will then initialize the MazeVisualizer applet.
	 * Then use the path-finder driver method to use BFS to find the shortest path.
	 * Display the path and end program.
	 * 
	 * @param String args[2] contains two Strings: the maze text file name and the corresponding query file name.
	 */
	public static void main (String args[]) {
		
		// Making class instance
		ShortestPath driver = new ShortestPath();
		
		// Setting up the maze visualizer applet; using the main-method code provided in MazeVisualizer.java
		JFrame frame = new JFrame("MazeVisualizer");
		
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

		// Read information from maze file and store in appropriate data structure (HashMap)
		driver.readMazeFile(args[0]);		

		// Pass the query file name to the path-find driver
		driver.pathFindDriver(args[1]);

		// Visualizing the paths
        frame.getContentPane().add("Center", drawApp);
        drawApp.init();
        frame.pack();
        frame.setBackground(Color.WHITE);
        frame.setSize(new Dimension(512,512));
        frame.setVisible(true);

	} // End of main


	/*
	 * readMazeFile will read the command-line given maze text file name and store the values in a HashMap.
	 * 
	 * @param String mazeFileName contains the E/V information of the maze
	 */
	private void readMazeFile (String mazeFileName) {
		
		HashMap<Integer, ArrayList<Integer>> mazeHashMap = new HashMap<Integer, ArrayList<Integer>>();
		String currentLine = null;
		String lineArray[] = null;
		int key;
		int store;
		int numOfPoints;
		
		// Opening and reading from the file
		try {
			// File I/O
			Scanner mazeScanner = new Scanner(new FileReader(mazeFileName));
			
			// Get the first line (the number of vertices in the graph)
			numOfPoints = Integer.parseInt(mazeScanner.nextLine().trim());
			vertexNumber = numOfPoints*numOfPoints;						// Updating global variable
			
			// Initialize the applet
			drawApp = new MazeVisualizer(numOfPoints);
	
			currentLine = mazeScanner.nextLine();
			lineArray = currentLine.trim().split("\\s+");
			
		
			key = Integer.parseInt(lineArray[0]);
			store = Integer.parseInt(lineArray[1]);
			
			// Run the loop while we still have input
			while (currentLine != null) {
				
				// Updating the applet
				drawApp.addEdge(key, store);
				
				// Add a vertex if it is new to the map (if the map does not already contain the vertex)
				if (!mazeHashMap.containsKey(key)) {
					
					mazeHashMap.put(key, new ArrayList<Integer>());
					mazeHashMap.get(key).add(store);

				}
				// Else that means that the map already contains the key/vertex, so add a new edge
				else {
					
					mazeHashMap.get(key).add(store);
					
				}
				
				// Moving to the next line now and updating currentLine, key and store variables
				
				if (mazeScanner.hasNextLine()) {
					currentLine = mazeScanner.nextLine();
				}
				else {
					currentLine = null;
				}
				if (currentLine != null) {								// To prevent null-pointer exception
					lineArray = currentLine.trim().split("\\s+");
					key = Integer.parseInt(lineArray[0]);
					store = Integer.parseInt(lineArray[1]);
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("Unable to read from maze file.");
		}

		this.mazeMap = mazeHashMap;		
		
	} // End of readMazeFile
	
	

	/*
	 * pathFindDriver deals with finding the shortest path between two given vertexes.
	 * Will first open the query file and store all the source-values in the sources int array, and the
	 * corresponding target-values are stored in the targets array (each source value has the same index as its target value).
	 * Will also update the visual applet along the way.
	 *  
	 * @param String fileName is the query file name given through the command line and passed in by the main-method
	 * 
	 */
	private void pathFindDriver(String fileName) {

		// Will store sources and targets in different arrays
		ArrayList<Integer> sources = new ArrayList<Integer>();
		ArrayList<Integer> targets = new ArrayList<Integer>();
		String currentLine = null;
		String currentLineBreaker[] = null;
		
		try {
			// File I/O
			Scanner queryScanner = new Scanner(new FileReader(fileName));
			
			// Run the loop while we still have lines to read from the file
			currentLine = queryScanner.nextLine();
			currentLineBreaker = currentLine.trim().split("\\s+");
						
			while (queryScanner.hasNextLine()) {
			
			sources.add(Integer.parseInt(currentLineBreaker[0]));			
			targets.add(Integer.parseInt(currentLineBreaker[1]));
			
			currentLine = queryScanner.nextLine();
			currentLineBreaker = currentLine.trim().split("\\s+");

			}
		}
		
		catch (FileNotFoundException e) {
			System.out.println("got to error.");
		}
			
		// Finding the shortest path for each source/target pair
		for (int i = 0; i < sources.size(); i++) {
			
			LinkedList<Integer> path = new LinkedList<Integer>();
			
			// Do Breadth-First Search of graph
			path = bfsPathFind(sources.get(i), targets.get(i));
			
			// Display the path via Maze Display
			if (path.getFirst() != -1) {
				drawApp.addPath(path);
			}
			
		}
		

	} // End of pathFind Driver
	

	/*
	 * bfsPathFind uses breadth-first searching to find the shortest path between a given source and target.
	 * 
	 * @param int source is the vertex we start at
	 * @param int target is the vertex we want to end at 
	 * 
	 * @return LinkedList<Integer> returnPath that contains the shortest path from Vertex[source] to Vertex[target]
	 */
	private LinkedList<Integer> bfsPathFind (int source, int target) {
		
		LinkedList<Integer> returnPath = new LinkedList<Integer>();		// Contains the shortest path
		Queue<Integer> queue = new Queue<Integer>();					// Will be used for BFS
		Stack<Integer> stack = new Stack<Integer>();					// Will be used for shortest path finding
		boolean[] visitArray = new boolean[vertexNumber];					// Indicates whether a vertex has been visited or not
		
		int currentVertex = source;
		int targetAdjuster;
		int loopControl = 0;
		ArrayList<Integer> vertexNeighbours = new ArrayList<Integer>();
		
		if (source == target) {
			returnPath.add(currentVertex);
			return returnPath;
		}
		
		// Enqueue/push source for queue/stack initialization
		queue.enqueue(currentVertex);
		stack.push(currentVertex);
		
		// Mark the current vertex as visited; decrement by one to adjust to array index
		visitArray[currentVertex] = true;
		
		// Using BFS to fill the queue and stack; we are not finding the shortest path right now
		while (!queue.isEmpty()) {

			currentVertex = queue.dequeue();
			vertexNeighbours = mazeMap.get(currentVertex);
			
			// Visiting each neighbour
			for (int i = 0; i < vertexNeighbours.size(); i++) {

				// Check if we have reached the target; if we have then leave the loop
				if (vertexNeighbours.get(i) == target && !visitArray[vertexNeighbours.get(i)]) {

					// Do nothing and adjust loop variables to exit
					queue.clear();
					break;
					
				}
				
				else if (visitArray[vertexNeighbours.get(i)] == false && vertexNeighbours.get(i) < visitArray.length) {
					
					queue.enqueue(vertexNeighbours.get(i));
					visitArray[vertexNeighbours.get(i)] = true;
					stack.push(vertexNeighbours.get(i));
					
					// Check if we have reached the target again
					if (vertexNeighbours.get(i) == target && !visitArray[vertexNeighbours.get(i)]) {
						
						// Do nothing and adjust loop variables to exit
						queue.clear();
						break;
					
					}
					
				}
				
				else {													// Avoiding IndexOutOfBounds Exception
					
					//queue.enqueue(vertexNeighbours.get(i));
					visitArray[vertexNeighbours.get(i)] = true;
					stack.push(vertexNeighbours.get(i));
					
					// Check if we have reached the target again
					if (vertexNeighbours.get(i) == target && !visitArray[vertexNeighbours.get(i)]) {
						
						// Do nothing and adjust loop variables to exit
						queue.clear();
						break;
					
					}
					
				}
				
			}
			
		}
		
		// Now using the stack to find the shortest path
		currentVertex = 0;													// Reinitialize currentVertex
		targetAdjuster = target;											// Starting from the top and...
		returnPath.add(targetAdjuster);										// Will be going backwards using the stack
		
		while(!stack.isEmpty()) {
			currentVertex = stack.pop();
			
			if (areTheyAdjacent(currentVertex, targetAdjuster)) {
				// If the vertex we are at is adjacent to the target, then add it to the return variable
				returnPath.addFirst(currentVertex);
				targetAdjuster = currentVertex;
				
				// Check if we have reached the source value; clear the stack if we have
				if (currentVertex == source || targetAdjuster == source) {
					stack.clear();
				}
				
			}
			
		}
		
		// Check if a path has actually been found
		if (returnPath.get(0) != source) {
			returnPath.clear();												// Clear returnPath and store -1 in to indicate error
			returnPath.add(-1);
		}
		
		// returnPath now contains the shortest path from source to target, if there is one
		return returnPath;
		
	} // End of bfsPathFind



	/*
	 * areTheyAdjacent will determine if two given vertices are direct neighbours.
	 * 
	 * @param vertexOne
	 * @param vertexTwo
	 * 
	 * @return boolean result that is true if the two are neighbours, and false if they are not
	 */
	 
	 private boolean areTheyAdjacent (int vertexOne, int vertexTwo) {
		 
		 boolean result = false;
		 ArrayList<Integer> vertexTwoEdgeList = mazeMap.get(vertexTwo);
		 
		 // Iterate through each element of the second vertex's edge list to check if vertexOne is present
		 for (int i = 0; i < vertexTwoEdgeList.size(); i++) {
			 
			 if (vertexOne == vertexTwoEdgeList.get(i)) {
				 return true;
			 }
			 
		 }
		 
		 return result;
		 
	 } // End of areTheyAdjacent

} // End of ShortestPath
