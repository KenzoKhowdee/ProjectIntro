import java.util.Scanner;
import java.util.SplittableRandom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SIR {
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		//input will be number of individuals, infection rate , recover rate, and days(number of time steps)
		System.out.println("The Number of Individuals: ");
		final int individualCount = sc.nextInt(); 
		System.out.println("Rate of Infection: ");
		final double infectionRate = sc.nextDouble();
		System.out.println("Rate of Recovery: ");
		final double recoverRate = sc.nextDouble();
		System.out.println("Number of Days: ");
		final int days = sc.nextInt();
		final int row =(int) Math.sqrt( individualCount);
		//25 1.0 0.0 4 
		
		System.out.println("Base grid file name: ");
		String grid = sc.next()+ ".txt";
		System.out.println("Temporary grid file name: ");
		String temp = sc.next()+ ".txt";
		System.out.println("Data storage file name: ");
		String storage = sc.next() + ".txt";
		File grid_file = new File(grid);
		//for the replacing swap around to change data in a file it must be change and replace the same way as a string
		File temp_file = new File(temp);
		File storage_file = new File(storage);
		createFile(grid_file);
		createFile(temp_file);
		createFile(storage_file);
		System.out.println("Would you like to clear the data in the storage file? y/n");
		String response = sc.next();
		if(response.equals("Y") || response.equals("y") || response.equals("yes") || response.equals("Yes")) {
			clearGrid(storage_file);
			System.out.println("The data storage file has been cleared.");
		}
		else {
			System.out.println("The Data will be appended to the current file");
		}
		
		if(inputValidation(individualCount, infectionRate, recoverRate)) {
		createNewGrid(individualCount, grid_file);
		clearGrid(temp_file);
		printGrid(grid_file, individualCount);
		storeData(storage_file, grid_file, individualCount, 1);
		drawStats(grid_file, individualCount, 1);
		//System.out.println();
		for(int i = 2; i <= days +1;i++ ) {
			//the infection check and replace
			infecting(individualCount, infectionRate, recoverRate, grid_file, temp_file);
			//clearGrid(grid_file);
			//tempToMain(temp_file, grid_file); //moves main to temp for testing
			printGrid(temp_file, individualCount);
			tempToMain(grid_file, temp_file);
			//clearGrid(temp_file);
			drawStats(grid_file, individualCount, i);
			storeData(storage_file, grid_file, individualCount, i);
			if(allRecovered(grid_file, individualCount) ) {
				System.out.println("Plague ended on day " + i + ".");
				break;
			}
		}
		}	
		
	}
	public static boolean allRecovered(File grid_file, int count) throws FileNotFoundException {
		Scanner sc = new Scanner(grid_file);
		String status = "";
		boolean allRecover = false;
		int recoveredCounter = 0;
		int statusCounter = 0;
		for(int i = 0; i < count;i++) {
			status = sc.next();
			if(status.equals("R")){
				recoveredCounter++;
			}
			if(status.equals("S")){
				statusCounter++;
			}
		}
		if(recoveredCounter == count) {
			allRecover = true;
			System.out.println("All of the people have recovered.");
		}
		else if(statusCounter + recoveredCounter == count){
			allRecover = true;
			System.out.println("The plague has been quelled before everyone got infected.");
		}
		return allRecover;
	}
	public static boolean inputValidation(int count, double infectionRate, double recoverRate) {
		boolean valid = false;
		int row =(int) Math.sqrt( count);
		int total= 0;
		if(row * row ==count) {
			total = 1;
		}
		else {
			System.out.println("This is not a perfect square learn what a perfect square is");
		}
		if(infectionRate < 1.0) {
			total +=1;
		}
		else {
			System.out.println("Infection rate must be less than 1.");
		}
		if(recoverRate < 1.0) {
			total +=1;
		}
		else {
			System.out.println("Recover Rate must be less than 1.");
		}
		if(total == 3) {
			System.out.println("Input is valid.");
			valid = true;
		}
		else {
			System.out.println("Input is not valid.");
		}
		return valid;
	}
	public static void storeData(File storage_file, File grid_file, int individualCount, int day) throws IOException{
		Scanner sc = new Scanner(grid_file);
		Scanner scnr = new Scanner(storage_file);
		FileWriter writesFileWriter = new FileWriter(storage_file, true);
		String line= "";
		int recovered = 0;
		int infected = 0; 
		int susceptible = 0;
		String status ;
		int i = 1;
		while(sc.hasNextLine()) {
			status = sc.nextLine();
			if(status.equals("R")){
				recovered++;
			}
			if(status.equals("I")){
				infected++;
			}
			if(status.equals("S")){
				susceptible++;
			}
			i++;
		}
		
		writesFileWriter.write("Day: "+ day + " Susceptible: " + susceptible + " Infected: " + infected +  " Recovered: " + recovered + "\n" );
		
		writesFileWriter.flush();	
	}
	public static void createFile(File name) throws IOException {
		try {
			boolean result = name.createNewFile();
			if(result) {
				System.out.println("file created" + name.getCanonicalPath());
			}
			else {
				System.out.println("File already exist at location: "+name.getCanonicalPath());  

			}
		}
		catch (IOException e)  {
			System.out.println("bad file uh oh");
			e.printStackTrace();
			
		}
	}
	// this method is used to check the infection chance
	public static void infecting(int count, double infectRate, double recRate, File grid_file, File temp_file) throws IOException {
		
		FileWriter writes = new FileWriter(temp_file);
		Scanner sc = new Scanner(grid_file);
		int row = (int) Math.sqrt(count);
		String currentStatus ="";
		String grid = "";
		char temp;
		int i =1;
		int status;
		while(i <= count) {
		
			currentStatus = sc.nextLine();
			if(currentStatus.equals("R")) {
				grid += "R";
			}
			else if(currentStatus.equals("I")) {
				if(recRate > Math.random()){
					grid += "R";	
				}
				else {
					grid += "I";
				}
			}
			else if(currentStatus.equals("S")) {
				if(i == 1) { //top left corner
					grid += infectionCalc(checkDown(grid_file,i,count) + checkRight(grid_file,i), infectRate);
				}
				else if (i == row) { //top right corner
					grid += infectionCalc(checkDown(grid_file,i,count) + checkLeft(grid_file,i) , infectRate);	
				}
				else if(i == count - row + 1) {//bottom left corner
					grid += infectionCalc(checkUp(grid_file,i,count) + checkRight(grid_file,i), infectRate);
				}
				else if(i == count) {//bottom right corner
					grid += infectionCalc(checkUp(grid_file,i,count) + checkLeft(grid_file,i), infectRate);
				}
				else if(i > 1 && i < row) {//top row
					grid += infectionCalc(checkRight(grid_file,i) + checkLeft(grid_file,i) + checkDown(grid_file,i,count)  , infectRate);
				}
				else if(i > count - row && i < count) { // bottom row
					grid += infectionCalc(checkRight(grid_file,i) + checkLeft(grid_file,i) + checkUp(grid_file,i,count)  , infectRate);
				}
				else if(i%row == 1) { // left column 
					grid += infectionCalc(checkDown(grid_file,i,count) + checkUp(grid_file,i,count) + checkRight(grid_file,i), infectRate);
				}
				else if(i%row == 0 ) { // right column
					grid += infectionCalc(checkDown(grid_file,i,count) + checkUp(grid_file,i,count) + checkLeft(grid_file,i), infectRate);
				}
				else{ // the middle ones
					grid += infectionCalc(checkDown(grid_file,i,count) + checkUp(grid_file,i,count) + checkRight(grid_file,i) + checkLeft(grid_file,i), infectRate);
				}			
			}
			else {
				
			}
			
			
			//writeToTemp.write("\n");
			i++;
			grid += "\n";
			//System.out.println(i);
		}
		writes.write(grid);
		//reads.close();
        writes.close();

		//System.out.println("wrote to file");
	}
	//writes the grid with nothing for a fresh start
	public static void clearGrid(File grid) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(grid);
		writer.print("");//writes nothing
		writer.close();	
	}
	//creates the new grid the assigned place and then adds the original infection at the first level
	public static void createNewGrid(int count, File old) throws IOException {
			FileWriter file = new FileWriter(old, true);
			SplittableRandom random = new SplittableRandom();
			clearGrid(old);
			int row = (int) Math.sqrt(count);
			int randomNumber = random.nextInt(1,count +1) ;
			
			// creates a grid using the line number itself as the index much easier using the current scanner
			for(int i = 1; i <= count;i++) {
				if(i == randomNumber ) {
					file.write("I");
					file.write("\n" );
				}
				/*if(i == 7 ) {
					file.write("I");
					file.write("\n" );
				}
				*/
				else {
					file.write("S");
					file.write("\n" );
				}
			}
			file.close();
	}
	//simply prints out the file
	public static void printGrid(File grid_file, int count) throws FileNotFoundException {
		 try (Scanner input = new Scanner(grid_file)) {
			int row = (int) Math.sqrt(count);
			 int i = 1;
			 while (input.hasNextLine())
			 {
			    System.out.print(input.nextLine());
			    if(i == row || i%(row) == 0) {
			    	System.out.println();
			    }
			    i++;
			 }
			// System.out.println("printed");
		}
	}
	//this is the method that sill find the possibility of infection
	public static String infectionCalc(int contacts, double rate) {
		String infected = "S";
		double probability = (contacts * rate);
		//random.nextInt(1, 101) <= scaledProb
		if(probability > Math.random()) {
			infected = "I";
		}
		return infected;
	
	}
	//checks the above by systematically scanning through all of the lines until it reaches it's destination
	public static int checkUp(File grid,  int currentLocation, int count) throws FileNotFoundException {
		
		int infected = 0;
		Scanner sc = new Scanner(grid);
		int row = (int) Math.sqrt(count);//need to find the row so it can move up
		for (int i = 1; i < currentLocation-row; i++){// Discard n-row one above 
			sc.nextLine();
		}
		String status = sc.nextLine();
		if(status.equals("I")) {
			infected = 1;
			//System.out.println("Got you up");
		}
		return infected;
	}
	//checks the bellow by systematically scanning through all of the lines until it reaches it's destination
	public static int checkDown(File grid,  int currentLocation, int count) throws FileNotFoundException {
		int infected = 0;
		Scanner sc = new Scanner(grid);
		int row = (int) Math.sqrt(count);
		
		for (int i = 1; i < currentLocation+row; i++) { // Discard n-1 lines
			sc.nextLine();
		}
		String status = sc.nextLine();
		if(status.equals("I")) {
			infected = 1;
			//System.out.println("Got you down");// to test if it worked or not
		}
		return infected;
	}
	//checks the left by systematically scanning through all of the lines until it reaches it's destination

	public static int checkLeft(File grid,  int currentLocation) throws FileNotFoundException{
		int infected = 0;
		Scanner sc = new Scanner(grid);
		for (int i = 1; i < currentLocation-1; i++) { // Discard n-1 lines
			sc.nextLine();
		}
		String status = sc.nextLine();
		if(status.equals("I")) {
			infected = 1;
			//System.out.println("Got you left");
		}
		return infected;
	}
	//checks the right by systematically scanning through all of the lines until it reaches it's destination
	public static int checkRight(File grid,  int currentLocation) throws FileNotFoundException{
		int infected = 0;
		Scanner sc = new Scanner(grid);
		for (int i = 1; i < currentLocation+1; i++) { // Discard n-1 lines
			sc.nextLine();
		}
		String status = sc.nextLine();
		if(status.equals("I")) {
			infected = 1;
			//System.out.println("Got you right");
		}
		return infected;
	}
	public static void drawStats(File grid_file, int count, int days) throws FileNotFoundException {
		Scanner sc = new Scanner(grid_file);
		int recovered = 0;
		int infected = 0; 
		int susceptible = 0;
		String status ;
		int i = 1;
		while(sc.hasNextLine()) {
			status = sc.nextLine();
			if(status.equals("R")){
				recovered++;
			}
			if(status.equals("I")){
				infected++;
			}
			if(status.equals("S")){
				susceptible++;
			}
			i++;
		}
		System.out.println("Day: " + days);
		System.out.println("Susceptible: " + susceptible);
		System.out.println("Infected: " + infected );
		System.out.println("Recovered: " + recovered);
	}
	public static void tempToMain(File grid_file, File temp_file) throws IOException {
		FileReader reads = new FileReader(temp_file);
		FileWriter writes = new FileWriter(grid_file);
		String str = "";
		int i;
		while((i = reads.read()) != -1){
			  str += (char)i;
			
		}
		writes.write(str);
		reads.close();
        writes.close();
	}
	
	
	
}
