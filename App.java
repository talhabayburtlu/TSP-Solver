import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class App {
    private static ArrayList<City> CITIES = new ArrayList<City>(); // Holds cities in general.
    private static ArrayList<Integer> VISITED_INDEXES = new ArrayList<Integer>(); // Acts as tour that has minimum distance among all other processed tours.
    private static FileWriter writer; // "log.txt" writer.
    
    public static void main(String[] args) throws Exception {
    	writer = new FileWriter(new File("log.txt"));

    	createCities(); // Creating cities from "input.txt".
        findMinOfNearestNeighbor(); // Finding minimum costed tour for all nearest neighbor algorithms.
        
        findMinOfTwoOpt(); // Applying 2-opt algorithm.
        findMinOfThreeopt(); // Applying 3-opt algorithm.
        
        printPath(); // Printing path to "output.txt" file.
        System.out.println("Minimum distance and path is printed at output.txt.");
        System.out.println("Log of all operations at log.txt.");
        
        writer.close();
    }

    public static void createCities() throws FileNotFoundException { // Creates cities from "input.txt" file.
        Scanner inFile = new Scanner(new File("input.txt"));
		while (inFile.hasNextLine()) {
            String[] info = inFile.nextLine().trim().split(" ");
            
            info = Arrays.stream(info).filter(str -> !str.equals("")).toArray(String[]::new); // Filtering spaces between city informations.
            
            CITIES.add(new City(Integer.parseInt(info[0]), Integer.parseInt(info[1]) , Integer.parseInt(info[2]))); // Creating and adding cities.
        }
		inFile.close();
    }
    
    public static ArrayList<Integer> nearestNeighbor(int start) { // Applies nearest neighbor algorithm.
    	ArrayList<Integer> visitedIndexes = new ArrayList<Integer>();
    	City currCity = CITIES.get(start); // Taking initial city as current city.
    	visitedIndexes.add(start);
    	
    	int size = CITIES.size();
    	while (visitedIndexes.size() != size) { // Loop iterates until new tour has equal size of initial tour.
    		int minDistance = Integer.MAX_VALUE, minCity = -1;
        	for (int j = 0; j < size ; j++) { // For current city, finding minimum distance costed neighbor.
        		if (visitedIndexes.contains(j))
        			continue;
        		
        		int distance = currCity.distance(CITIES.get(j));
        		if (distance < minDistance) { // Checking current city and j'th city has minimum distance or not.
        			minDistance = distance;
        			minCity = j;
        		}
        	}

        	currCity = CITIES.get(minCity);
        	visitedIndexes.add(minCity);
    	}
    	return visitedIndexes;
    }
    
    public static void findMinOfNearestNeighbor() throws IOException { // Finds minimum costed tour for all nearest neighbor algorithms.
    	int size = CITIES.size(), minDistance = Integer.MAX_VALUE;
    	int iterationTimes = 1; // Iteration count of nearest neighbor algorithm becomes 1 if size of cities is higher than 3000.
    	
    	if (size < 1250) // Iteration count of nearest neighbor algorithm becomes size when size < 1250.
    		iterationTimes = size;
    	else if (size < 3000) // Iteration count of nearest neighbor algorithm becomes a number between 
    		iterationTimes = 75 + Math.round(( 275  / (size - 1000))); // 75 and 275 based on inverse proportion between 1000-3000.
    	
    	int startIndexes[] = new int[iterationTimes];
    	
    	// Upper quartile and lower quartile is the operation field of all neighbor algorithms.
    	// They are determined as if size is between 1250 and 3000 the value is statistical quartiles, else the value is size.
    	int upperQuartile = iterationTimes != size && iterationTimes != 1 ? (3 * size) / 4 : size ; 
    	int lowerQuartile = iterationTimes != size && iterationTimes != 1 ? (size / 4) : 0;
    	
    	// Determining which cities becomes start point of nearest neighbor algortihm based on operation field.
    	for (int i = 0 ; i < startIndexes.length ; i++) 
    		startIndexes[i] =  lowerQuartile + (int)((((upperQuartile * 1.0) / (startIndexes.length + 1)) * ( i + 1 )));
    	
    	System.out.println("There will be " + startIndexes.length + " different computations for nearest neighbor algorithm. Starting at " + LocalDateTime.now());
    	writer.write("There will be " + startIndexes.length + " different computations for nearest neighbor algorithm. Starting at " + LocalDateTime.now() +"\n");
    	
    	ArrayList<Integer> minVisitedIndexes = new ArrayList<Integer>();
    	
    	for (int i = 0; i < startIndexes.length ; i++) {
    		System.out.println("Running " + (i+1) + ". times nearest neighbor algorithm with starting point: " + startIndexes[i] + ".");
    		writer.write("Running " + (i+1) + ". times nearest neighbor algorithm with starting point: " + startIndexes[i] + ".\n");
    		
    		ArrayList<Integer> visitedIndexes = nearestNeighbor(startIndexes[i]);
    		int distance = computeDistance(visitedIndexes);
    		
    		if (distance < minDistance) { // Comparing distances for finding minimum costed tour find with nearest neighbor algorithm.
    			minDistance = distance;
    			minVisitedIndexes = visitedIndexes;
    		}

    		System.out.println((i+1) + ". nearest neighbor algortihm come to the end. This iteration distance -> " + distance + ". Min distance up to this iteration -> " + minDistance + ".");
    		writer.write((i+1) + ". nearest neighbor algortihm come to the end. This iteration distance -> " + distance + ". Min distance up to this iteration -> " + minDistance + ".\n");
    	}
    	System.out.println("End of finding minimum of all nearest neighbor algorithms. Ending at " + LocalDateTime.now());
    	writer.write("End of finding minimum of all nearest neighbor algorithms.Ending at + " + LocalDateTime.now() + ".\n");
    	VISITED_INDEXES = minVisitedIndexes;
    }
    
    public static int twoOpt(int iteration) throws IOException { // Applies 2-opt algorithm.
    	System.out.println("Starting of " + (iteration+1) +". two-opt algorithm. Starting at " + LocalDateTime.now());
    	writer.write("Starting of " + (iteration+1) +". two-opt algorithm. Starting at " + LocalDateTime.now() + ".\n");
    	int size = CITIES.size(), minDistance = Integer.MAX_VALUE;
    	ArrayList<Integer> minIndexes = new ArrayList<Integer>();
    	for (int i = 0; i < size - 1 ; i++) {
    		for (int j = i+1 ; j < size ; j++) {
    			ArrayList<Integer> indexes = new ArrayList<Integer>(new ArrayList<Integer>(VISITED_INDEXES.subList(0, i))); // Copying cities from 0 to i.
    			
    			ArrayList<Integer> reversed = new ArrayList<Integer>(new ArrayList<Integer>(VISITED_INDEXES.subList(i, j+1))); // Copying cities from i to j+1.
    			Collections.reverse(reversed); // Reversing cities from i to j+1
    			
    			indexes.addAll(reversed); // Adding reversed cities to copied initial cities.
    			indexes.addAll(new ArrayList<Integer>(VISITED_INDEXES.subList(j+1 , size))); // Adding remained cities to initial and reversed cities.
    			
    			int distance = computeDistance(indexes); // Computing distances for created tour.
    			if (distance < minDistance) { // Comparing distances for finding minimum costed tour.
    				System.out.println("Two-opt i="+ i + " j=" + j + " and min distance up to now ="  + distance + ".");
					writer.write("Two-opt i="+ i + " j=" + j + " and min distance up to now ="  + distance + ".\n");
    				minDistance = distance;
    				minIndexes = indexes;
    			}
    		}
    	}

    	System.out.println("End of " + (iteration+1) + ". two-opt algorithm.Minimum distance found is " + minDistance + ". Ending at " + LocalDateTime.now());
    	writer.write("End of "+ (iteration+1) + ". two-opt algorithm.Minimum distance found is " + minDistance + ". Ending at " + LocalDateTime.now() + ".\n");
    	VISITED_INDEXES = minIndexes; // Copying minimum costed tour to global visited indexes.
    	return minDistance;
    }
    
    public static void findMinOfTwoOpt() throws IOException {
    	int size = CITIES.size();
    	int iteration = 1; 

    	if (size > 10000)
    		iteration = 0;
    	
    	if (iteration == 0) {
    		System.out.println("Two-opt skipped because of size of input.");
        	writer.write("Two-opt skipped because of size of input.\n");
    	} else {
    		int i = 1;
        	int minOldDistance = -1;
        	int minNewDistance = 1;
        	while (minOldDistance != minNewDistance) {
        		minOldDistance = minNewDistance;
        		minNewDistance = twoOpt(i);
        		i++;
        	}
   
        	System.out.println("All Two-opt algorithms are finished. Minimum distance up to now " + minNewDistance );
        	writer.write("All Two-opt algorithms are finished. Minimum distance up to now " + minNewDistance + ".\n");
    	}
    }
    
    public static void findMinOfThreeopt() throws IOException {
    	int size = CITIES.size();
    	int iteration = 1; 
    	
    	if (size < 500)
    		iteration = 4;
    	else if (size > 30000)
    		iteration = 0;
    	
    	
    	System.out.println("There will be " + iteration + " times three-opt algorithm.");
    	writer.write("There will be " + iteration + " times three-opt algorithm.\n");
    	
    	if (iteration != 0) {
    		for (int i = 0; i < iteration; i++)
        		threeOpt(i);
    	} else {
    		System.out.println("Three-opt skipped because of size of input.");
        	writer.write("Three-opt skipped because of size of input.\n");
    	}	
    }
    
    public static void threeOpt(int iteration) throws IOException { // Applies 3-opt algorithm.
    	int size = CITIES.size();
    	ArrayList <Integer> minIndexes = new ArrayList<Integer>();
    	int minDistance = Integer.MAX_VALUE;
    	
    	// Deciding increment value of i,j and k depending on size. For size < 400 the increment value is 1.
    	// For size <= 400 , the increment value is ceiling of size/250.
    	int inc = size > 400 ? (int) Math.ceil(size / 250): 1; 
    	
    	System.out.println("Start of "+ (iteration+1) +". three-opt algorithm. Starting at " + LocalDateTime.now() + ".");
    	writer.write("Start of "+ (iteration+1) +". three-opt algorithm. Starting at " + LocalDateTime.now() + ".\n");
    	
    	// i,j and k initializing for every loop and those values increments as depending on increment and random values for time optimization.
    	for (int i = 0; i < size - 5 ; i += ((int)Math.ceil(inc / 2)) + 1 + ((int) (Math.random() * (inc / 2)))) {
    		for (int j = i + 2; j < size - 3 ; j += ((int)Math.ceil(inc / 2)) + 1 + ((int)(Math.random() * (inc / 2)))) { 
    			for (int k = j + 2 ; k < size - 1 ; k += ((int)Math.ceil(inc / 2)) + 1 + ((int)(Math.random() * (inc / 2)))) {
    				// Reversed combinations of sub tours are initializing.
    				String reverseCombinations[] = {"" , "A" , "B" , "C" , "AB" , "AC" , "BC" , "ABC"}; 
    				for (int l = 0; l < 8 ; l++) { // Loop iterates for every combination of sub tours.
    					
    					ArrayList<Integer> A = new ArrayList<Integer>(VISITED_INDEXES.subList(i+1, j+1)); // A sub tour determined from i+1 to j.
    					if (reverseCombinations[l].contains("A")) // Reversing A sub tour if it is in combination.
    						Collections.reverse(A);
    					
    					ArrayList<Integer> B = new ArrayList<Integer>(VISITED_INDEXES.subList(j+1, k+1)); // B sub tour determined from j+1 to k.
    					if (reverseCombinations[l].contains("B")) // Reversing A sub tour if it is in combination.
    						Collections.reverse(B);
    					
    					ArrayList<Integer> C = new ArrayList<Integer>(VISITED_INDEXES.subList(k+1, size)); // C sub tour determined from k+1 to i.
    					C.addAll(new ArrayList<Integer>(VISITED_INDEXES.subList(0, i+1)));
    					if (reverseCombinations[l].contains("C")) // Reversing C sub tour if it is in combination.
    						Collections.reverse(C);
    					
    					// Creating a new tour with combining sub tours.
    					ArrayList<Integer> indexes = new ArrayList<Integer>();
    					indexes.addAll(A);
    					indexes.addAll(B);
    					indexes.addAll(C); 					
    					
    					int distance = computeDistance(indexes); // Computing distance of new tour.
    					
    					if (distance < minDistance) { // Comparing distances for finding minimum costed new tour.
    						System.out.println("Three-opt i="+ i + " j=" + j + " k=" + k + " and min distance up to now ="  + distance + ".");
    						writer.write("Three-opt i="+ i + " j=" + j + " k=" + k + " and min distance up to now ="  + distance + ".\n");
    						minDistance = distance;
    						minIndexes = indexes;
    					}
    				}
    				
    			}
    		}
    	}
    	
    	System.out.println("End of " + (iteration+1) + "three-opt algorithm. Ending at " + LocalDateTime.now() + ".");
    	writer.write("End of " + (iteration+1) + ". three-opt algorithm. Ending at " + LocalDateTime.now() + ".\n");
    	
    	VISITED_INDEXES = minIndexes; // Copying minimum costed tour to global visited indexes.
    }
    
    public static int computeDistance(ArrayList<Integer> indexes) { // Computes distance of given tour.
    	int distance = 0;
    	for (int i = 0; i < indexes.size() - 1 ; i++) {
    		distance += CITIES.get(indexes.get(i)).distance(CITIES.get(indexes.get(i+1)));
    	}
    	
    	return distance + CITIES.get(indexes.get(indexes.size() - 1)).distance(CITIES.get(indexes.get(0))); 
    }
    
    public static void printPath() throws IOException { // Prints path to "output.txt" file.
    	FileWriter outWriter = new FileWriter(new File("output.txt"));
    	int distance = computeDistance(VISITED_INDEXES);
         
        outWriter.write(distance + "\n");
         
        for (int i = 0; i < VISITED_INDEXES.size() ; i++)
        	outWriter.write(CITIES.get(VISITED_INDEXES.get(i)).getId()+"\n");
        outWriter.close();
    }
}
