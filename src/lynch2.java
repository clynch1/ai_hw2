import java.util.LinkedList;
import java.util.Random; 

//Program:	lynch2
//Course:	COSC470
//Description:	HW2: This is a Java program that plays a game called Letters-Squared 
//              in which 1 to 16 scrambled case-sensitive letters with at least 
//              one blank space are positioned in a 4x4 grid and are unscrambled 
//              by a series of moves to form a target string. A move is made by 
//              shifting a letter horizontally or vertically into an open (i.e., 
//              blank) space in the grid. The goal of this game is to find the 
//              minimum number of moves required to unscramble the starting string 
//              in order to form the target string and to show the relevant moves. 
//              For instance, a scrambled start state such as that shown on the 
//              left below might be unscrambled to produce the goal state shown 
//              on the right.
//
//              f c g       a b c d
//              b a v d     e f   g
//              e x t z     h t u v
//              h w u y     w x y z
//
//              Scrambled start states can are generated either by automatically 
//              randomly shuffling the target string a specified number of shuffles 
//              or by manually entering a shuffled state. This program has 
//              options to find a solution using either breadth first search or 
//              best first heuristic search (with or without a depth penalty). 
//              For either mode it shows all moves from start state to goal state 
//              after the path has been determined and it indicates how many moves 
//              were required. It also shows the number of boards in OPEN and CLOSED 
//              at the time when the goal has been found. This program uses the 
//              sum of tiles out of place heruistic.  Format board output as shown 
//              above. The game may be played as many times as desired before exiting. 
//
//Author:	Connor Lynch
//Revised:	3/7/17
//Language:	Java
//IDE:		NetBeans 8.2
//Notes:	
//******************************************************************************
//******************************************************************************
public class lynch2 {
    
    public static KeyboardInputClass    keyboardInput = new KeyboardInputClass();                       //takes keyboard input
    public static LinkedList<Character> finalTableOrderLL = new LinkedList<Character>();                //this is the linked list to the order of the chars in final table
    public static Tile[][]              finalTableArray;                                                //this is the finalTable Array
    public static Tile[][]              startTableArray;                                                //this is the startTable Array
    public static Table                 finalTable;                                                     //this is the finalTable Table 
    public static Table                 startTable;                                                     //this is the startTable Table
    public static boolean               firstTimeInBreathFirst;                                         //this tells if breath first has been called
    public static int                   numberInOpen;                                                   //this is the number of tables in open
    public static int                   numberInClosed;                                                 //this is the number of tables in closed
    
    
    
    //******************************************************************************
    //Method:           main
    //Description:      This method runs the program.  It prompts the user to enter
    //                  a starting string.  It then asks for the if the user would
    //                  like to have the computer shuffle the input a specified number
    //                  of times.  If this is not the case it allows the user to 
    //                  enter the start state.  It then prompts the user to select 
    //                  the search type(breath first, or best first).  Once the search 
    //                  has finished the program prints out the steps it took to 
    //                  unscramnle the puzzle.
    //Parameters:       None
    //Returns:          None 									
    //Throws:           None
    //Calls:            LinkedList<Character, Table>()
    //                  new Table() -           this is the table object
    //                  keyboardInput() class - for user input
    //                  PriorityQueue() -       this declairs the priority queue
    //                  buildTable() -          this takes the user input string and creates the  
    //                                          tile[][]
    //                  scramble() -            this scrambles the final table a specified number of times
    //                  printTable() -          this method print out the passed table
    //                  beathFirstSearch() -    this calls the breath first search
    //                  bestFirstSearch() -     this calls the best first search
    public static void main(String[] args) { 
        System.out.println("Connor Lynch - AI_HW2");
        System.out.println("This program plays letters squared with 16 characters. \n"
                            + "This program will allow you to use duplicate numbers \n"
                            + "or blanks.\n");
        boolean exit = false;        
        while(exit != true){
    //Reset Values
            LinkedList<Character>   finalTableOrderLL = new LinkedList<Character>(); 
            finalTableArray = new Tile[4][4];
            startTableArray = new Tile[4][4];
            finalTable = new Table();
            startTable = new Table();
            firstTimeInBreathFirst = true;
            numberInOpen = 0;
            numberInClosed = 0;
            
            PriorityQueue openPriorityQueue = new PriorityQueue();
    //Get target string 
            String userInputEndState = keyboardInput.getString   ("abcd"
                                                                + "efg "
                                                                + "hijk"
                                                                + "lmno", "Target String:");
            buildTable(userInputEndState, 1);                                   //makes a Tile[][] out of the inputstring
            finalTable.setTable(finalTableArray);                               //make a Table out of the Tile[][]
    //Get start string
            char userInputSramble = keyboardInput.getCharacter(true, 'Y', "YN", 1, "\n" + "Shuffle the board? (Y,N)" +
                    "\nDefault: Y");
            if(userInputSramble == 'N'){
                String userInputStartState = keyboardInput.getString ("abcd"
                                                                    + "efkd"
                                                                    + "ijkl"
                                                                    + "m no", "Start String:");
                buildTable(userInputStartState, 0);                             //make a Tile[][] out of the inputshting 
                startTable.setTable(startTableArray);                           //make a Table out of the Tile[][]
            }//end of if
    //Scramble
            else{
                int userInputNumberOfScrambles = keyboardInput.getInteger(true, 1, 1, 100, "How many moves would you like to scramble?");            
                startTable.setTable(scramble(userInputNumberOfScrambles)); 
            }//end of else
    //Search Method
            int userInputSearchMethod = keyboardInput.getInteger(true, 1, 1, 2, "What search method would you like to use?\n"
                    + "1: Breath First (Default)\n" 
                    + "2: Best First - With Tiles Out Of Place");
    //PrintTables
            System.out.println("Start State");
            printTable(startTable.getTable());
            System.out.println("\nFinal Table");
            printTable(finalTable.getTable());
            System.out.println("");
            
            LinkedList<Table> path = new LinkedList<Table>();                   //this will be the path to solve the problem
            
            boolean bestFirst = false;
            switch(userInputSearchMethod){
                case 1: {                                                       //breath first
                    path = breathFirstSearch(startTable);
                    break;
                }//end of case 1
                case 2: {                                                       //best first
                    path = bestFirst(startTable, openPriorityQueue);
                    bestFirst = true;
                    break;
                }//end of case two                
            }//end of swicth
    //Output Path
            char userInput = keyboardInput.getCharacter(true, 'Y', "YN", 1, "\n" + "Press ENTER to show path.");
            if(userInput == 'Y'){
                int numberOfMoves = path.size() - 1;
                while(path.size() > 0){
                    Table tableToPrint = path.removeLast();
                    printTable(tableToPrint.getTable());
                    if(bestFirst){
                        System.out.println("(Raw score = " + tableToPrint.getRawScore()  + 
                                            " Depth = " + tableToPrint.getDepth() +
                                            " Total score = " + tableToPrint.getScore() + ")");
                    }//end of if
                    System.out.println("");
                }//end of while

                System.out.println(numberOfMoves + " moves out of " + numberInClosed + " moves considered (" + 
                                                + numberInOpen + " nodes left in OPEN)");
                System.out.println("********************************************************");
            }//end of if
    //Load New Table
            userInput = keyboardInput.getCharacter(true, 'Y', "YN", 1, "\n" + "Load new table? (Y,N) \nDefault: Y");
            if(userInput == 'N'){
                exit = true;
            }//end of if
        }//end of while
    }//end of main
    
    //******************************************************************************
    //Method:           breathFirstSearch
    //Description:      This method uses the breath first search type to search 
    //                  the table and find the goal state.  It uses open and closed
    //                  Linked Lists to store the table values as it searches.  
    //                  Once it finds the goal state, it returns the linked list 
    //                  containing the path.
    //Parameters:       Table currentTable -    this is the current table, it resets
    //                                          this to a new value every time that 
    //                                          it goes through the while 
    //                                          loop.
    //Returns:          LinkedList path -   this is the path the program took to arrive 
    //                                      at the final state.
    //Throws:           None
    //Calls:            LinkedList<Table>()
    //                  tableCompair -  this is a method that compares to table and 
    //                                  returns the number of tiles that are in the correct place
    //                  Table() -       this is the table onject 
    //                  makeMove -      this is a method that generates the children of 
    //                                  a passed table and returns a LinkedList with all the children
    //                  
    public static LinkedList breathFirstSearch(Table currentTable){
        System.out.println("Working . . .");

        LinkedList<Table> open = new LinkedList<Table>();
        LinkedList<Table> closed = new LinkedList<Table>();
        open.add(currentTable);
        while(!open.isEmpty()){
            currentTable = open.removeLast();                                  
            int goalCheck = tableCompair(currentTable.getTable(), finalTable.getTable());
            if(goalCheck == 16){
                System.out.println("Success");
                numberInClosed = closed.size();
                numberInOpen = open.size();              
                
                LinkedList<Table> path = new LinkedList<Table>();            
                boolean pathComplete = false;
                while(!pathComplete){
                    Table newTable = currentTable;
                    path.add(newTable);
                    if(currentTable.getParent() == null){
                        pathComplete = true;
                    }//end of for
                    else{
                        Table parentTable = currentTable.getParent(); 
                        currentTable = parentTable;
                    }//end of else
                }//end of while
                return path;
            }//end of if
            else{
                LinkedList<Tile[][]> childTablesOfCurrentTable = new LinkedList<Tile[][]>();                
                childTablesOfCurrentTable = makeMove(currentTable.getTable());
                closed.addLast(currentTable);                                               
                while(!childTablesOfCurrentTable.isEmpty()){   
                    boolean inOpen = false;
                    boolean inClosed = false;
                    Tile[][] childTable = childTablesOfCurrentTable.remove(childTablesOfCurrentTable.size() - 1);
                    for(int x = 0; x < open.size(); x++){
                        Table tableFromOpen = open.get(x);
                        int numberOfMatches = tableCompair(childTable, tableFromOpen.getTable());
                        if(numberOfMatches == 16){
                            inOpen = true;
                            break;
                        }//end of if
                    }//end of for
                    if(!inOpen){
                        for(int y = 0; y < closed.size(); y++){
                            Table tabelFromClosed = closed.get(y);
                            int numberOfMatches = tableCompair(childTable, tabelFromClosed.getTable());
                            if(numberOfMatches == 16){
                                inClosed = true;
                                break;
                            }//end of if 
                        }//end of for
                        if(inOpen == false && inClosed == false){ 
                            Table nextTable = new Table();
                            nextTable.setTable(childTable);
                            Table parentTable = closed.getLast();               
                            nextTable.setParent(parentTable);
                            open.addFirst(nextTable);                           
                        }//end of if 
                    }//end of if
                }//end of while
            }//end of else
        }//end of while    
        return null;
    }//end of breathFirstSearch
    
    //******************************************************************************
    //Method:           bestFirstSearch
    //Description:      This method uses the best first search type with the tiles out 
    //                  of place heuristic to search the table and find the goal state.  
    //                  It uses open PriorityQueue and closed Linked Lists to store the 
    //                  table values as it searches. Once it finds the goal state, it 
    //                  returns the linked list containing the path.
    //Parameters:       Table currentTable - this is the current table, it resets
    //                  this to a new value every time that it goes through the while 
    //                  loop.
    //                  PriorityQueue open -    this is the open priority queue
    //Returns:          LinkedList path -       this is the path the program took to arrive 
    //                                          at the final state.
    //Throws:           None
    //Calls:            LinkedList<Table>()
    //                  keyboardInput() -   this gets the user input 
    //                  tableCompair -      this is a method that compares to table and 
    //                                      returns the number of tiles that are in the correct place
    //                  Table() -           this is the table onject 
    //                  makeMove -          this is a method that generates the children of 
    //                                      a passed table and returns a LinkedList with all the children
    //    
    public static LinkedList bestFirst(Table currentTable, PriorityQueue open){
        boolean useDepth = true;
        char userInput = keyboardInput.getCharacter(true, 'Y', "YN", 1, "\n" + "Use Depth Penalty? (Y,N) \nDefault: Y");
            if(userInput == 'N'){
                useDepth = false;
            }//end of if
        System.out.println("Working . . .");
        LinkedList<Table> closed = new LinkedList<Table>(); 
        currentTable.setDepth(0);
        currentTable.setRawScore(16 - tableCompair(currentTable.getTable(), finalTable.getTable()));
        if(!useDepth){
            currentTable.setScore(currentTable.getRawScore()); 
        }//end of if
        else{
            currentTable.setScore(currentTable.getRawScore() + currentTable.getDepth()); 
        }//end of else
        open.piorityEnqueue(currentTable);
        while(!open.isEmpty()){                                                                               
            currentTable = (Table)open.dequeue();
            int goalCheck = tableCompair(currentTable.getTable(), finalTable.getTable());
            if(goalCheck == 16){
                System.out.println("Success");
                numberInClosed = closed.size();
                numberInOpen = open.length;                
                LinkedList<Table> path = new LinkedList<Table>();            
                boolean pathComplete = false;
                while(!pathComplete){
                    Table newTable = currentTable;
                    path.add(newTable);
                    if(currentTable.getParent() == null){
                        pathComplete = true;
                    }//end of for
                    else{
                        Table parentTable = currentTable.getParent(); 
                        currentTable = parentTable;
                    }//end of else
                }//end of while
                return path;
            }//end of if
            else{
                LinkedList<Tile[][]> children = new LinkedList<Tile[][]>();                                                       
                children = makeMove(currentTable.getTable());
                closed.addLast(currentTable);                                  
                while(!children.isEmpty()){                                     //while there are still children 
                    boolean inOpen = false;                                     //false if Table is not in open
                    boolean inClosed = false;                                   //false id table is not in closed
                    Tile[][] currentChild = children.remove(children.size() -1);
                    LinkedList<Table> openCopy = new LinkedList<Table>();
                    while(!open.isEmpty()){                                     //remove everything in open and put it in openCopy                       
                       Table tableFromOpen = (Table)open.dequeue();
                       openCopy.add(tableFromOpen);
                    }//end of while
                    for(int x = 0;x < openCopy.size(); x++){
                        Table tableFromOpen = openCopy.get(x);
                        int openCheck = tableCompair(currentChild, tableFromOpen.getTable());
                        if(openCheck == 16){
                            inOpen = true;
                            break;
                        }//end of if                       
                    }//end of for
                    if(!inOpen){
                        for(int y = 0; y < closed.size(); y ++){
                            Table tableFromClosed = closed.get(y);
                            int closedCheck = tableCompair(currentChild, tableFromClosed.getTable());
                            if(closedCheck == 16){
                                inClosed = true;
                                break;
                            }//end of if
                        }//end of for 
                        if(!inClosed ){
                            Table validChild = new Table();
                            validChild.setTable(currentChild);
                            Table parentTable = closed.getLast();
                            validChild.setParent(parentTable);
                           
                            int currentDepth = parentTable.getDepth() + 1;
                            int tilesOutOfPlace = tableCompair(currentChild, finalTable.getTable());
                            validChild.setRawScore(16 - tilesOutOfPlace);
                           
                            if(!useDepth){
                                validChild.setScore(validChild.getRawScore()); 
                            }//end of if
                            else{
                                validChild.setScore(validChild.getRawScore() + currentDepth); 
                            }//end of else
                            validChild.setDepth(currentDepth);
                            open.piorityEnqueue(validChild);
                        }//end of if
                    }//end of if
                    while(!openCopy.isEmpty()){
                        open.piorityEnqueue(openCopy.removeLast());
                    }//end of while
                }//end of while
            }//end of else 
        }//end of while
        return null;
    }//end of bestFirst
    
    //******************************************************************************
    //Method:           scramble
    //Description:      This method takes a passed in int containing the the number
    //                  of shuffle moves.  It the randomly generates moves.  Once 
    //                  it has finished scrambling it return the scrambled table.
    //Parameters:       int numberOfMoves -     this is the desired number of moves
    //Returns:          Tile[][] -              this is a tile type array containing the tile 
    //                                          objects that make up the table.
    //Throws:           None
    //Calls:            LinkedList<Tile[][]>()
    //                  deepCopy -      this takes a passed table and then returs a deep 
    //                                  copy of the passed table
    //                  makeMove -      this is a method that generates the children of 
    //                                  a passed table and returns a LinkedList with all the children
    //                  Random() -      this generates a random number
    //                  tableCompair -  this is a method that compares to table and 
    //                                  returns the number of tiles that are in the correct place
    public static Tile[][] scramble(int numberOfMoves){
        LinkedList<Tile[][]> previousScrambleMoves = new LinkedList<Tile[][]>();         
        Tile[][] tableScramble = deepCopy(finalTable.getTable());
        previousScrambleMoves.add(tableScramble);                                                           //adds start state to previousTables
        int actualNumberOfScrambles = 0;
        for(int x = 0; x < numberOfMoves; x++){
            LinkedList<Tile[][]> childTablesOfCurrentTable = new LinkedList<Tile[][]>();    
            childTablesOfCurrentTable = makeMove(tableScramble);
            Random rand = new Random(); 
            int randomTable = rand.nextInt(childTablesOfCurrentTable.size());                               //this generates a number within the bounds of the list size  
            int numberOfRandomTableTries = 0;               
            boolean inPreviousScrambleMoves = true;
            while(inPreviousScrambleMoves == true){                                                         //while the table has already been used look for a new one            
                Tile[][] newTableScramble = childTablesOfCurrentTable.get(randomTable);
                int counter = 0;
                for(int y = 0; y < previousScrambleMoves.size(); y++){
                    Tile[][] tableFromPreviousScramble = previousScrambleMoves.get(y);
                    int numberOfMatches = tableCompair(newTableScramble, tableFromPreviousScramble);
                    if(numberOfMatches != 16){                                                              //the tables do not match 
                        counter ++;
                    }//end of if
                }//end of for
                if(counter == previousScrambleMoves.size()){                                                //the tables do not match 
                    inPreviousScrambleMoves = false;
                    tableScramble = deepCopy(newTableScramble);
                    previousScrambleMoves.add(tableScramble);
                    break;
                }//end of if
                numberOfRandomTableTries ++;
                if(numberOfRandomTableTries == childTablesOfCurrentTable.size()){
                   return tableScramble;                                        
                }//end of if
                else if(randomTable < (childTablesOfCurrentTable.size() -1)){
                    randomTable ++;
                }//end of if 
                else if(randomTable == (childTablesOfCurrentTable.size() - 1)){                                                                               
                    randomTable = 0;
                }//end of if                
            }//end of while  
            actualNumberOfScrambles ++;
        }//end of for       
        System.out.println("Actual Number of Scrambles: " + actualNumberOfScrambles);
        System.out.println("");
        return tableScramble;       
    }//end of scramble
    
    //******************************************************************************
    //Method:           makeMove
    //Description:      This method takes a passed in table and generats the valid
    //                  child of the table. 
    //Parameters:       Tile[][] currentTable -     this is the parent table 
    //Returns:          LinkedList -                this contains all of the valid children
    //Throws:           None
    //Calls:            LinkedList<Tile[][], Character, Integer>()
    //                  tableSearch -   this searches a table and returns the x,y 
    //                                  location.
    //                  moves -         this takes the location of the blank tile and returns
    //                                  a liked list of valid directions.
    public static LinkedList makeMove(Tile[][] currentTable){
        LinkedList<Tile[][]> newTables = new LinkedList<Tile[][]>();
        LinkedList<Character> validMoves = new LinkedList<Character>();
        LinkedList<Integer> locationOfChar = new LinkedList<Integer>();
        locationOfChar = tableSearch(currentTable, ' ');
        while(!locationOfChar.isEmpty()){
                int xValue = locationOfChar.removeFirst();
                int yValue = locationOfChar.removeFirst();
            validMoves = moves(currentTable, xValue, yValue);
            for(int x = 0; x < validMoves.size(); x++){
                char currentMove = validMoves.get(x);
                switch(currentMove){
                    case 'd': {
                        Tile[][] copyOfCurrentTable = deepCopy(currentTable);
                        Tile tile1 = (Tile)copyOfCurrentTable[xValue][yValue];                             //this is the blank tile             
                        Tile tile2 = (Tile)copyOfCurrentTable[xValue -1 ][yValue];                         //this is the tile above the blank tile
                        copyOfCurrentTable[xValue][yValue] = tile2;
                        copyOfCurrentTable[xValue -1][yValue] = tile1;
                        newTables.add(copyOfCurrentTable);
                        break;
                    }//end of case down
                    case 'u': {
                        Tile[][] copyOfCurrentTable = deepCopy(currentTable);
                        Tile tile1 = (Tile)copyOfCurrentTable[xValue][yValue];                             //this is the blank tile             
                        Tile tile2 = (Tile)copyOfCurrentTable[xValue + 1][yValue];                         //this is the tile above the blank tile
                        copyOfCurrentTable[xValue][yValue] = tile2;
                        copyOfCurrentTable[xValue + 1][yValue] = tile1;
                        newTables.add(copyOfCurrentTable); 
                        break;
                    }//end of case up
                    case 'r': {
                        Tile[][] copyOfCurrentTable = deepCopy(currentTable);
                        Tile tile1 = (Tile)copyOfCurrentTable[xValue][yValue];                             //this is the blank tile             
                        Tile tile2 = (Tile)copyOfCurrentTable[xValue][yValue - 1];                         //this is the tile above the blank tile
                        copyOfCurrentTable[xValue][yValue] = tile2;
                        copyOfCurrentTable[xValue][yValue - 1] = tile1;
                        newTables.add(copyOfCurrentTable);
                        break;
                    }//end of case left
                    case 'l': {
                        Tile[][] copyOfCurrentTable = deepCopy(currentTable);
                        Tile tile1 = (Tile)copyOfCurrentTable[xValue][yValue];                             //this is the blank tile             
                        Tile tile2 = (Tile)copyOfCurrentTable[xValue][yValue + 1];                         //this is the tile above the blank tile
                        copyOfCurrentTable[xValue][yValue] = tile2;
                        copyOfCurrentTable[xValue][yValue + 1] = tile1;
                        newTables.add(copyOfCurrentTable);
                        break;
                    }//end of case right                
                }//end of switch
            }//end of for
        }//end of while
        return newTables;
    }//end of makeMove
    
    //******************************************************************************
    //Method:           moves
    //Description:      This method takes a passed table and x and y cordinates 
    //                  and generates a linked list of valid directions.
    //Parameters:       Tile[][] -      this is the table we are looking for moves in 
    //                  xValue -        x location for the blank tile
    //                  yValue -        y location for the blank tile
    //Returns:          LinkedList -    this list contains the directions that are 
    //                                  valid for the current Table.
    //Throws:           None
    //Calls:            LinkedList<Character>()
    public static LinkedList moves(Tile[][] currentTable, int xValue, int yValue){
        LinkedList<Character> validMoves = new LinkedList<Character>();
            if(xValue != 0){
                validMoves.add('d');                                            //moving down is a valid move
            }//end of if 
            if(xValue != 3){
                validMoves.add('u');                                            //moving up is a valid move
            }//end of if
            if(yValue != 0){
                validMoves.add('r');                                            //moving left if a valid move
            }//end of if
            if(yValue != 3){
                validMoves.add('l');                                            //moving right is a valid move
            }//end of if    
        return validMoves;
    }//end of moves
    
    //******************************************************************************
    //Method:           tableSearch
    //Description:      This method searches for a char in the passed table.
    //Parameters:       Tile[][] tableToSearch -    this is the table to be searched
    //                  char charToFind -           this is the char to find
    //Returns:          LinkedList -    this list contains the directions that are 
    //                                  valid for the current Table.
    //Throws:           None
    //Calls:            LinkedList<Character>()
    public static LinkedList tableSearch(Tile[][] tableToSearch, char charToFind){
        LinkedList<Integer> locationOfChar = new LinkedList<Integer>();
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 4; y++){
                Tile currentTile = (Tile)tableToSearch[x][y];                
                if(currentTile.getTileChar() == charToFind){
                    locationOfChar.add(x);
                    locationOfChar.add(y);
                }//end of if
            }//end of for
        }//end of for 
        return locationOfChar;
    }//end of table search
    
    //******************************************************************************
    //Method:           tableCompair
    //Description:      This method compares two passed tables
    //Parameters:       Tile[][] table1-    this is the first table 
    //                  Tile[][] table2 -   this is the second table 
    //Returns:          int -   this is the number of tiles in the correct place
    //Throws:           None
    //Calls:            Nothing   
    public static int tableCompair(Tile[][] table1, Tile[][] table2){
        int numberOfMatches = 0;
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 4; y++){
                Tile tileFromTable1 = (Tile)table1[x][y];
                Tile tileFromTable2 = (Tile)table2[x][y];
                if(tileFromTable1.tileChar == tileFromTable2.tileChar){
                    numberOfMatches ++;
                }//end of if
            }//end of for
        }//end of for 
        return numberOfMatches;
    }//end of table compair
    
    //******************************************************************************
    //Method:           deepCopy
    //Description:      This method creates a deep copy of the passed table
    //Parameters:       Tile[][] tableToBeCoppied 
    //Returns:          Tile[][] -  this is the coppied table
    //Throws:           None
    //Calls:            new Tile() - this is the table object  
    public static Tile[][] deepCopy(Tile[][] tableToBeCoppied){
        Tile[][] newTable;
        newTable = new Tile [4][4];
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 4; y++){
                Tile tileFromTableToBeCoppied = tableToBeCoppied[x][y];               
                Tile currentTile = new Tile();
                currentTile.setTileChar(tileFromTableToBeCoppied.getTileChar());
                currentTile.setScore(tileFromTableToBeCoppied.getScore());
                newTable[x][y] = currentTile;
            }//end of for
        }//end of for 
        return newTable;
    }//end of table compair
    
    //******************************************************************************
    //Method:           buildTable
    //Description:      This method takes the string of the users input and parses 
    //                  it and creates a tile[][] out of it.
    //Parameters:       String userInput - this is the users input string 
    //                  int tableID - this is the type of table being built.
    //Returns:          Nothing
    //Throws:           None
    //Calls:            Nothing  
    public static void buildTable(String userInput, int tableId){       
        char[] charArray = userInput.toCharArray();       
        switch(tableId){
            case 1: {
                int counter = 1;
                for(int x = 0; x < 4; x++){
                    for(int y = 0; y < 4; y++){       
                        Tile currentTile = new Tile();
                        currentTile.setTileChar(charArray[counter - 1]);
                        currentTile.setScore(0);
                        finalTableArray[x][y] = currentTile;
                        finalTableOrderLL.add(charArray[counter - 1]);          //this adds the char of the tile to a linked list so that I can search it to set the start board
                        counter ++;
                    }//end of for
                }//end of for   
                break;
            }//end of case 1
            case 0: {
                int x = 0;
                int y = 0;
                for(int i = 0; i < charArray.length; i++){
                    if(y == 4){
                        x++;
                        y = 0;
                    }//end of if                    
                    char currentValue = charArray[i];
                    int locationOfChar = 0  ;                       
                    for(int z = 0; z < finalTableOrderLL.size(); z++){
                        if(finalTableOrderLL.get(z) != null){
                            char toBeCompaired = finalTableOrderLL.get(z);
                            if(toBeCompaired == currentValue){   
                                locationOfChar = z;
                                break;
                            }//end of if
                        }//end of if 
                    }//end of for                                                         
                    finalTableOrderLL.set(locationOfChar, null);     
                        Tile currentTile = new Tile();
                        currentTile.setTileChar(currentValue);
                        currentTile.setScore(0);
                        startTableArray[x][y] = currentTile;
                    y++;
                }//end of for
                break;
            }//end of case 0
        }//end switch    
    }//end of build table
    
    //******************************************************************************
    //Method:           printTable
    //Description:      This method prints out the passed table
    //Parameters:       Tile[][] tableToPrint 
    //Returns:          Nothing
    //Throws:           None
    //Calls:            Nothing
    public static void printTable(Tile[][] tableToPrint){       
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 4; y++){
                Tile currentTile = (Tile)tableToPrint[x][y];    
                if(currentTile.tileChar == ' '){
                    System.out.print("-" + " ");
                }//end of if 
                else{
                    System.out.print(currentTile.tileChar + " "); 
                }//end of else 
            }//end of for
            System.out.println("");
        }//end of for 
    }//end of printTable
}//end of lynch2

//Class:	Tile
//Course:	COSC470
//Description:	The purpose of this class is to create a tile object.  This object 
//              is the individual piece that is inside of the table.  It has two 
//              properties:
//              Char tileChar - this is the char assosiated with every tile
//              int score - this would be used in a distance out of place
//              application.
//******************************************************************************
class Tile {
    char tileChar;
    int score;
//******************************************************************************
//Method:           setTileChar
//Description:      This method sets the tileChar value.
//Parameters:       char passedChar
//Returns:          Nothing
//Throws:           None
//Calls:            Nothing
public void setTileChar(char passedChar){    
    this.tileChar = passedChar;              
}//end of setTileChar
//******************************************************************************
//Method:           getTileChar
//Description:      This method returns tileChar.
//Parameters:       None 
//Returns:          char - this is the char value for the tile.
//Throws:           None
//Calls:            Nothing
public char getTileChar(){
    return tileChar;
}//end of getTileChar
//******************************************************************************
//Method:           setScore
//Description:      This method sets the score for the tile.
//Parameters:       int passedScore
//Returns:          Nothing
//Throws:           None
//Calls:            Nothing
public void setScore(int passedScore){
    this.score = passedScore;
}//end of setScore
//******************************************************************************
//Method:           getScore
//Description:      This method returns the tiles score.
//Parameters:       None 
//Returns:          int - this is the score of the tile.
//Throws:           None
//Calls:            Nothing
public int getScore(){
    return score;
}//end of getScore
}//end of Tile

//Class:	Table
//Course:	COSC470
//Description:	The purpose of this class is to create a table object.  This object
//              has several properties:
//              Tile[][] table - this is an array of tile objects that represent 
//              the actual table
//              Table parent - this is the parent table
//              Comparable score - this is the score of the table
//              int depth - this is the depth of the table 
//              int rawScore - this is the raw score of the table(i.e. with no depth
//              penalty.
//******************************************************************************
class Table implements Comparable<Table>{
    Tile[][] table = new Tile[4][4];
    Table parent;
    Comparable score;
    int depth;
    int rawScore;
//******************************************************************************
//Method:           setTable
//Description:      This method sets the table contents
//Parameters:       Tile[][] passedTable
//Returns:          Nothing
//Throws:           None
//Calls:            Nothing
public void setTable(Tile[][] passedTable){
    Tile[][] newTable;
    newTable = new Tile [4][4];                                               
    for(int x = 0; x < 4; x++){
        for(int y = 0; y < 4; y++){
            Tile tileFromTableToBeCoppied = passedTable[x][y];               
            Tile currentTile = new Tile();
            currentTile.setTileChar(tileFromTableToBeCoppied.getTileChar());
            currentTile.setScore(tileFromTableToBeCoppied.getScore());
            newTable[x][y] = currentTile;
        }//end of for
    }//end of for 
    this.table = newTable;
}//end of setTable
//******************************************************************************
//Method:           getTable
//Description:      This method returns the table.
//Parameters:       None
//Returns:          Tile[][] - this is the table.
//Throws:           None
//Calls:            Nothing
public Tile[][] getTable(){
    return table;
}//end of getTable
//******************************************************************************
//Method:           setParent
//Description:      This method sets the parent table.
//Parameters:       Table passedTable - this is the parent table
//Returns:          Nothing
//Throws:           None
//Calls:            Nothing
public void setParent(Table passedTable){    
   this.parent = passedTable;           
}//end of setParent
//******************************************************************************
//Method:           getParent
//Description:      This method returns the parent table.
//Parameters:       None 
//Returns:          Table - this is the parent table 
//Throws:           None
//Calls:            Nothing
public Table getParent(){
    return parent;
}//end of getParent
//******************************************************************************
//Method:           setScore
//Description:      This method sets the score for the table.
//Parameters:       int passedScore
//Returns:          Nothing
//Throws:           None
//Calls:            Nothing
public void setScore(int passedScore){
    this.score = passedScore;                                                   
}//end of setScore
//******************************************************************************
//Method:           getScore
//Description:      This method returns the table's score.
//Parameters:       None 
//Returns:          Comparable - this is the score of the table's.
//Throws:           None
//Calls:            Nothing
public Comparable getScore(){
    return score;
}//end of getScore
//******************************************************************************
//Method:           setDepth
//Description:      This method sets the depth of the table.
//Parameters:       int passedDepth
//Returns:          Nothing
//Throws:           None
//Calls:            Nothing 
public void setDepth(int passedDepth){
    this.depth = passedDepth;
}//end of setDepth
//******************************************************************************
//Method:           getDepth
//Description:      This method returns the table depth
//Parameters:       None 
//Returns:          int - this is the depth
//Throws:           None
//Calls:            Nothing
public int getDepth(){
    return depth;
}//end of getDepth
//******************************************************************************
//Method:           setRawScore
//Description:      This method sets the raw score.
//Parameters:       int passedRawScore
//Returns:          Nothing
//Throws:           None
//Calls:            Nothing
public void setRawScore(int passedRawScore){
    this.rawScore = passedRawScore;
}//end of setDepth
//******************************************************************************
//Method:           getRawScore
//Description:      This method returns the table raw score.
//Parameters:       None 
//Returns:          int - this is the raw score of the table.
//Throws:           None
//Calls:            Nothing
public int getRawScore(){
    return rawScore;
}//end of getRawScor
@Override
//******************************************************************************
//Method:           compareTo
//Description:      This is a compare to method that will override the normal one.
//                  This method will compare on the score. 
//Parameters:       Table x - This is the table to be compared that is to be compared.
//Returns:          
//Throws:           None
//Calls:            Nothing
public int compareTo(Table x){
    return this.score.compareTo(x.score); 
}//end of compareTo
}//end of Table
