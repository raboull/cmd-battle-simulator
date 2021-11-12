import java.util.Scanner;

/*
  Class summary: World class creates a world represented by an array and displays user specified entities.
  
  Class details: 
  --Creates a world array of 10x10
  --The position of a character in the input text file will correspond to the position of an array of 
    "Entity" in the simulated world.
  --Contains simulated world in the form of a 2D array of references to Entity objects.
  --Each 'O' in the starting text file becomes an orcish fighter.
  --Each 'E' in the starting text file becomes an elven warrior.
  --Each Orc of Elf are instances of the Entity class with different values for attributes such as:
    hit points, appearance and damage.
  --Empty spaces will become null elements in the world array.
  --Start method runs endlessly until an end game condition occurs with a prompt to hit enter at end of each turn.
  --The user can toggle debugging mode along with appropriate message as the debugging state changes.
    The option to toggle will occur at the end of each turn. The user can type in 'd' of 'D' and enter to
    toggle debug mode after which the simulation proceeds to the next turn. Typing anything else (along)
    with enter will simply advance to the next turn.
  --Displays the array with bounding lines around each element: above, below, left and right.
  --Orcs move diagonally downward (to the right and down a row). Debugging messages show the previous 
    and the distination before and after the move for each entity.
  --Elves move diagonally upward (to the left and up a row). Debugging messages show the previous
    and the destination before and after the move for each entity.
  --Boundary checking prevents orcs and elves from moving past the bounds of the array.
  --Elves and orcs only move onto empty squares.
  --Entities only move if they are not adjacent to an enemy. Debugging messages show the (row/column)
    of the entity that is constrained from moving and the (row/column) of the adjacent enemy opponent.
  --Entities can attack members (entities only attack once per turn) of the opposing side. Debugging
    messages show information about the entity doing the attacking: row, column and its appearance. 
    Also the debugging messages show information about the entity being attacked: row, column,
    appearance, its original hit points and the hit points afterits been attacked. Entities stop moving
    when they are adjacent to an enemy because they are not in range to attack each other. An entity will
    attack one adjacent enemy.
  --Unconscious entities (with zero or fewer hit points) removed at the end of the turn. Unconscious
    entities don't get to attack back.
  --Can determine when the orcs have won (there are no elves remaining but there's at least one orc),
    game ends with appropriate status message.
  --Can determine when the elves have won (there are no orcs remaining but there's at least one elf), 
    game ends with appropriate status message.
  --Can determine when there is draw (no entities left), game ends with appropriate status message.
  --Can determine when there is a ceasefire (no attacks have occurred for 10 successive turns), game 
    ends with appropriate status message. The debugging message display (and clearly label) the 
    number of turns that have passed since an attack has occurred.

  Limitations:
  --The world size is limited to a 10x10 matrix.
  --Valid input file characters consist only of 'O', 'E', or spaces.
  --Movement is consistent for each enemy without any variations.
  --Attacking an opposing entity focuses on one entity at a time.
  --Either a move or an attack can be performed per turn.
 
  Version: March 11, 2021
  --Updated the entire class to work with all entities in one array
    instead of two separate arrays, one for orcs and one for elves.

  Version: March 7, 2021  
  --Added the bounding lines when elements are printed.
  --Added an orcs and elves counter functions.
  --Added move orcs functionality
  --Added move elves functionality
  --Added the orcs attack functionality
  --Added the elves attack functionality
  --Added the cease fire counter
  --Added a funtction that removes unconscious Entities
  --Added a function that checks end game conditions and reports the winner
  --Added the draw condition check.
 
  Version: March 6, 2021
  --added the start method and commented the steps that need to be performed in that method
*/

public class World{
    public static final int SIZE = 10;
    public static final int ORCS_WIN = 0;
    public static final int ELVES_WIN = 1;
    public static final int DRAW = 2;
    public static final int CEASE_FIRE = 3;
    public static final int NOT_OVER = 4;

    private Entity [][] aWorld;//a 2D array that represents the world grid and the entities within that world
    private boolean keep_playing; //create a boolean variable that determines if the game should continue
    private int numOfOrcs;//instantiate a variable that holds the number of Orcs in the world
    private int numOfElves;//instantiate a variable that holds the number of Elves in the world
    private int numOfEntities;//instantiate a variable that holds the number of Entities in the world
    private Entity entities [];//declare a reference variable that will reference an array of Entities
    private Entity futureStateEntities [];//declare a reference variable that will reference an array of Entities
    private boolean entitiesAttackedThisTurn;//boolean flag to indicate if an attack took place this turn
    private int ceaseFireCounter;//variable that counts how many turns took place without any attacks in a row

    // Post-condition: the position of the characters in the
    // starting input file will determine the position of the
    // objects in the array. The type of character in the file
    // determines the appearance of the Entity(O or E) or if the
    // element is empty (spaces become null elements)
    public World(){
        //initialize World class object attributes
        keep_playing = true;
        numOfEntities = 0;
        entitiesAttackedThisTurn = false;
        ceaseFireCounter = 0;
        aWorld = new Entity[SIZE][SIZE];//create the world array
        //initialize each element of the world array to null
        for (int r = 0; r < SIZE; r++){
            for (int c = 0; c < SIZE; c++){
                aWorld[r][c] = null;
            }
        }
        aWorld = FileInitialization.read();//read entity location from a file and place them into the world array
    }

    // Displays array elements on at a time one row per line
    // Each element is bound above, below, left and right by 
    // bounding lines
    public void displayOrig(){
        int r = -1;
        int c = -1;
        int b = -1;
        for (r = 0; r < SIZE; r++){
            for (c = 0; c < SIZE; c++){
                if(aWorld[r][c] == null)
                    System.out.print(" ");
                else
                    System.out.print(aWorld[r][c].getAppearance()); 
            }
            System.out.println();
        }
        for (b = 0; b < SIZE; b++)
            System.out.print("-");
        System.out.println();
    }

    // Displays array elements one at a time one row per line
    // Each element is bound above, below, left and right by 
    // bounding lines
    public void display(){
        for (int r = 0; r <=SIZE*2; r++){//iterate through each row element to display
            for (int c = 0; c <=SIZE*2; c++){//iterate through each column element to display
                if (r%2 == 0){//check if current row number is even
                    printTopBottomBorder(c); //print top or bottom cell border
                }
                else if (r%2 != 0){//check if current row number is odd
                    printSideBordersAndContent(r,c);//print side borders and cell content
                }
            }
        }
    }

    //This method prints the top and bottom borders of the world cells
    private void printTopBottomBorder(int currentCol){
        if(currentCol==SIZE*2){//check if we are currently on the last column
            System.out.printf(" \n");
        }
        else if (currentCol%2 == 0){//check if current column is even
            System.out.printf(" ");
        }
        else if (currentCol%2 != 0){//check if current column is odd
            System.out.printf("-");
        }        
    }    

    //This method prints the side borders and the contents of each world cell
    private void printSideBordersAndContent(int currentRow, int currentCol){
        if(currentCol == SIZE*2){//check if we are currently on the last column
            System.out.printf("|\n");
        }
        else if (currentCol%2 == 0){//check if current column is even
            System.out.printf("|");
        }
        else if (currentCol%2 != 0){//check if current column is odd
            if (aWorld[currentRow/2][currentCol/2]!= null)//print the Entity symbol if the cell is not null
                System.out.print(aWorld[currentRow/2][currentCol/2].getAppearance());
            else
                System.out.printf(" ");//print an empty space if the cell is null
        }
    }

    //This method counts the number of entities in the world array
    private void countEntities(){
        //reset entity counters
        numOfEntities = 0;
        numOfElves = 0;
        numOfOrcs = 0;
        //iterate through the world and count how many times the cell object is an 'E' or an 'O'
        for (int r = 0; r < SIZE; r++){
            for (int c = 0; c < SIZE; c++){
                if(aWorld[r][c] == null){
                    //do noting
                }
                if(aWorld[r][c] != null && (aWorld[r][c].getAppearance() == 'E' || aWorld[r][c].getAppearance() == 'O'))
                    numOfEntities++;//increment the entities counter
                if (aWorld[r][c] != null && aWorld[r][c].getAppearance() == 'E')
                    numOfElves++;//increment the elves counter
                if (aWorld[r][c] != null && aWorld[r][c].getAppearance() == 'O')
                    numOfOrcs++;//increment the elves counter   
            }
        }
    }
  
    //This method will sort the entities array by simply looking for an entitiy at each location of the world in incremental order
    private void sortEntities(){
        Entity sortedEntities[]= new Entity[entities.length];//create a new entities array that will be filled in with sorted entities
        int sortedIndex = 0;
        for (int r=0; r<SIZE; r++){
            for (int c=0; c<SIZE; c++ ){
                if(aWorld[r][c] != null){
                    Location currentLocation = new Location(r,c);
                    sortedEntities[sortedIndex] = entityAt(currentLocation);
                    sortedIndex++;
                }   
            }     
        }  
        entities = sortedEntities;//repoint the entities reference oject to the sortedEntities array
        sortedEntities = null;//dereference the sortedEntities array reference object
    }

    //This method initialized the entities along with their attributes
    private void initializeEntities(){
        countEntities();//count entities present in the world array
        entities = new Entity [numOfEntities];//create an array of Entities
        int indexCounter = 0;//initialize a counter that will track what Entity is being updated
        //itterate through the world array to find each entity
        for (int r = 0; r < SIZE; r++){
            for (int c = 0; c < SIZE; c++){
                if(aWorld[r][c] == null){
                    //do noting
                }
                else if(aWorld[r][c].getAppearance() == 'E')
                {
                    //initialize the current elve Entity found
                    entities[indexCounter] = new Entity(Entity.ELF, Entity.ELF_HP, Entity.ELF_DAMAGE, r, c);
                    indexCounter++;//update the entities array index
                }
                else if(aWorld[r][c].getAppearance() == 'O')
                {
                    //initialize the current orc Entity found
                    entities[indexCounter] = new Entity(Entity.ORC, Entity.ORC_HP, Entity.ORC_DAMAGE, r, c);
                    indexCounter++;//update the entities array index
                }
            }
        }
    }

    //this method prompts the user to hit enter to contine the simulation and provides an option to toggle debug mode
    private void promptUser(){        
        Scanner in = new Scanner(System.in);
        //ask for user to hit the enter key or enter debug mode
        System.out.print("Press enter to continue, or input D and then hit enter to toggle debug mode and continue: ");
        String entry = in.nextLine();//store the line entered by the user
        char firstChar = ' ';//initialize a variable that will store the first char if it is present
        if (entry.length()>0){//check if anything was typed by the user into the input line
            firstChar = entry.charAt(0);//store the first char of the input
        }
        //toggle debug mode if user entered 'D' or 'd'
        if (firstChar == 'D' || firstChar == 'd'){ 
            if(GameStatus.debugModeOn == false)
                GameStatus.debugModeOn = true;
            else if(GameStatus.debugModeOn == true)
                GameStatus.debugModeOn = false;
            System.out.println("Debug value is now set to " + GameStatus.debugModeOn +".");
        }
    }

    //this method prints a debug message if the mode is enabled when an entity moves
    private void movingEntityDebugMessage(Entity entity, Location newLocation){
        //print debug message if debug mode is enabled by the user
        if (GameStatus.debugModeOn == true){
            
            int entityRow = entity.getLocationRow();
            int entityCol = entity.getLocationCol();
            int movedEntityRow = newLocation.getRow();
            int movedEntityCol = newLocation.getColumn();
            
            System.out.println("World.moveEntity()");
            System.out.println("        "+entityRow+" "+entityCol);
            System.out.println("        Source (r/c):=("+entityRow+"/"+entityCol+")");
            System.out.println("        Destination (r/c):=("+movedEntityRow+"/"+movedEntityCol+")");
            System.out.println("        <<< World.moveEntity() >>>");
            System.out.println("        Entity at location (r/c):=("+entityRow+"/"+entityCol+")");
            System.out.println("        will move to location (r/c):=("+movedEntityRow+"/"+movedEntityCol+")");
        }
    }

    //this method prints a debug message if the mode is emabled when at entity is picking a spot for its next move
    private void checkingMoveDebugMessage(int entityIndex){
        if (GameStatus.debugModeOn == true){//print debug message if debug mode is enabled by the user
            System.out.println("World.moveEntities()");
            System.out.println("    Checking move for (r/c):=("+entities[entityIndex].getLocationRow()+"/"+entities[entityIndex].getLocationCol()+")");
            if (isEnemyAdjacent(entities[entityIndex]) == true){//if an adjacent enemy is preventing movement print the location of that enemy
                Location adjacentEntity = locationOfFirstAdjacentEnemy(entities[entityIndex]);
                System.out.println("    Movement for (r/c):=("+entities[entityIndex].getLocationRow()+"/"+entities[entityIndex].getLocationCol()+
                                   ") is prevented by an adjacent opponent at (r/c): "+adjacentEntity.getRow()+"/"+adjacentEntity.getColumn());
            }
        }
    }

    //This method will move the entities according to preset rules for each entity type
    private void moveEntities(){
        
        entitiesAttackedThisTurn = false;//reset the attack indicator flag
        sortEntities();//sort entities before movement takes place       
        futureStateEntities = new Entity [entities.length];//create an array of Entities
        deepCopyArray(entities, futureStateEntities);//deep copy the entities array
        
        for (int i = 0; i<entities.length; i++){//iterate through each entity
            
            if (entities[i] != null){//make sure the current entity is not null
                Location potentialNewLocation = new Location();//create a Location object to hold a potential new location
                potentialNewLocation = computePotentialNewLocation(i); //set a potential new location according to the rules of how elves and orcs move
                checkingMoveDebugMessage(i);//print a debug message about the potential move of entity at i
                boolean okToMove = false;//this flag will be used to see if any move conditions are not met
                
                if (isInsideWorld(potentialNewLocation) == true && isSomeoneThere(potentialNewLocation) == false && isEnemyAdjacent(entities[i]) == false)
                        okToMove = true;
                if (okToMove == true)//assign new location if all move conditions were met
                    executeMove(i, potentialNewLocation);
                if (isEnemyAdjacent(entities[i]) == true){//attack an adjacent enemy if one is present    
                    executeAttack(i);
                    removeUnconscious();
                }
            }
        }
        deepCopyArray(futureStateEntities, entities);
        futureStateEntities = null;
        wipeTheWorld();
        placeEntitiesIntoWorld();
    }

    //this method removes unconscious entities from the entities array
    private void removeUnconscious(){
        for (int i=0; i<entities.length;i++){//remove unconscious entities from the entities array
            if (entities[i] != null){
                if (entities[i].getHitPoints()<0)
                    entities[i] = null;
            }
        }

        for (int i=0; i<entities.length;i++){//remove unconscious entities from the futureStateEntities array
            if (futureStateEntities[i] != null){
                if (futureStateEntities[i].getHitPoints()<0)
                    futureStateEntities[i] = null;
            }
        }

        //also remove unconscious entities from the world array
        for (int r = 0; r < SIZE; r++){
            for (int c = 0; c < SIZE; c++){
                if(aWorld[r][c] != null){
                    if( aWorld[r][c].getHitPoints() <0)
                        aWorld[r][c]  = null;
                }
            }
        }
    }
    
    //this method returns a location object to represent a moved location of entity at entityIndex
    private Location computePotentialNewLocation(int entityIndex){
        Location newLocation = new Location();//create a Location object to hold the new location 
        if (entities[entityIndex].getAppearance() == 'E')//compute new location according to the rules of how elves move
            newLocation.setRowAndColumn(entities[entityIndex].getLocationRow()-1, entities[entityIndex].getLocationCol()-1);
        else if (entities[entityIndex].getAppearance() == 'O')//compute new location according to the rules of how orcs move
            newLocation.setRowAndColumn(entities[entityIndex].getLocationRow()+1, entities[entityIndex].getLocationCol()+1);

        return newLocation;
    }
    
    //this method performs a deep copy of Entities array
    private void deepCopyArray(Entity []fromArray, Entity []toArray){
        if (fromArray.length == toArray.length){
            for (int i=0; i<fromArray.length; i++){
                if (fromArray[i] != null){
                    toArray[i] = new Entity(fromArray[i].getAppearance(), fromArray[i].getHitPoints(), 
                                            fromArray[i].getDamage(), fromArray[i].getLocationRow(), 
                                            fromArray[i].getLocationCol());
                }
            }
        }
    }

    //this method updates the location of entity at entityIndex
    private void executeMove(int entityIndex, Location newLocation){
        movingEntityDebugMessage(futureStateEntities[entityIndex], newLocation);//print debug message if debug mode is enabled
        futureStateEntities[entityIndex].setLocation(newLocation);//update the location that is stored by the current entity

    }

    //this method decreases the HP of an adjacent enemy of the entity stored at entityIndex of the entities array
    private void executeAttack(int entityIndex){
        
        attackFirstAdjacentEnemy(entities[entityIndex]);
        entitiesAttackedThisTurn = true;//indicate that at attack took place this turn

        //decrement HP of the same entity in the futureStateEntities array
        int firstAdjacentEnemyIndex = -1;
        if (isEnemyAdjacent(entities[entityIndex]) == true){//check if the adjacent enemy is still alive after the attack
            firstAdjacentEnemyIndex = firstAdjacentEnemyIndex(entities[entityIndex]);//find an index that matches the location attribute of current entity
            if(firstAdjacentEnemyIndex != -1){//make sure that a conscious adjacent enemy is present    
                if (entities[firstAdjacentEnemyIndex] != null){//deep copy the updated state of the attacked entity into the same entity in the future state array 
                    futureStateEntities[firstAdjacentEnemyIndex] = new Entity(entities[firstAdjacentEnemyIndex].getAppearance(), 
                                                                            entities[firstAdjacentEnemyIndex].getHitPoints(), 
                                                                            entities[firstAdjacentEnemyIndex].getDamage(), 
                                                                            entities[firstAdjacentEnemyIndex].getLocationRow(), 
                                                                            entities[firstAdjacentEnemyIndex].getLocationCol());
                }
                else if (entities[firstAdjacentEnemyIndex] == null){//if the entity is no longer present in the entities array, then remove it from the futureStateEntities array as well
                    futureStateEntities[firstAdjacentEnemyIndex] = null;
                }
            }
        }
    }

    //this method returns a boolean answer to the question: is there an enemy adjacent to inputEntity?
    private boolean isEnemyAdjacent(Entity inputEntity){
        boolean isEnemyAdjacent = false; //flag to tell us if any enemies are adjacent to inputEntity
        if (isEnemyAbove(inputEntity)   == true ||  isEnemyAboveRight(inputEntity)  == true ||
            isEnemyRight(inputEntity)   == true ||  isEnemyBelowRight(inputEntity)  == true ||
            isEnemyBelow(inputEntity)   == true ||  isEnemyBelowLeft(inputEntity)   == true ||
            isEnemyLeft(inputEntity)    == true ||  isEnemyAboveLeft(inputEntity)   == true){
            isEnemyAdjacent = true;
        }      
        return isEnemyAdjacent;
    }

    //this method returns a boolean answer to the question: is there an enemy above-left of the inputEntity?
    private boolean isEnemyAboveLeft(Entity inputEntity){
        //this boolean flag answers whether there is an enemy above-left of the inputEntity
        boolean isEnemyAboveLeft = false;
        //store the input entity's type
        char inputEntityType = inputEntity.getAppearance();
        //store the coordinates of the location above-left of the inputEntity
        Location aboveLeft = new Location(inputEntity.getLocationRow()-1,inputEntity.getLocationCol()-1);
        //check if the location above-left of the inputEntity is inside the world
        if (isInsideWorld(aboveLeft) == true){
            //check if the location above-left of the inputEntity contains an entity of a different type
            if (aWorld[aboveLeft.getRow()][aboveLeft.getColumn()] != null && 
                aWorld[aboveLeft.getRow()][aboveLeft.getColumn()].getAppearance() != inputEntityType){
                isEnemyAboveLeft = true;
            }
        }
        return isEnemyAboveLeft;
    }
    
    //this method returns a boolean answer to the question: is there an enemy left of the inputEntity?
    private boolean isEnemyLeft(Entity inputEntity)
    {
        //this boolean flag answers whether there is an enemy left of the inputEntity
        boolean isEnemyLeft = false;
        //store the input entity's type
        char inputEntityType = inputEntity.getAppearance();
        //store the coordinates of the location left of the inputEntity
        Location left = new Location(inputEntity.getLocationRow(),inputEntity.getLocationCol()-1);
        //check if the location left of the inputEntity is inside the world
        if (isInsideWorld(left) == true){
            //check if the location left of the inputEntity contains an entity of a different type
            if (aWorld[left.getRow()][left.getColumn()] != null && 
                aWorld[left.getRow()][left.getColumn()].getAppearance() != inputEntityType){
                isEnemyLeft = true;
            }
        }
        return isEnemyLeft;
    }
    
    //this method returns a boolean answer to the question: is there an enemy below-left of the inputEntity?
    private boolean isEnemyBelowLeft(Entity inputEntity){
        //this boolean flag answers whether there is an enemy below-left of the inputEntity
        boolean isEnemyBelowLeft = false;
        //store the input entity's type
        char inputEntityType = inputEntity.getAppearance();
        //store the coordinates of the location below-left of the inputEntity
        Location belowLeft = new Location(inputEntity.getLocationRow()+1,inputEntity.getLocationCol()-1);
        //check if the location below-left of the inputEntity is inside the world
        if (isInsideWorld(belowLeft) == true){
            //check if the location below-left of the inputEntity contains an entity of a different type
            if (aWorld[belowLeft.getRow()][belowLeft.getColumn()] != null && 
                aWorld[belowLeft.getRow()][belowLeft.getColumn()].getAppearance() != inputEntityType){
                isEnemyBelowLeft = true;
            }
        }
        return isEnemyBelowLeft;
    }
    
    //this method returns a boolean answer to the question: is there an enemy below the inputEntity?
    private boolean isEnemyBelow(Entity inputEntity){
        //this boolean flag answers whether there is an enemy below the inputEntity
        boolean isEnemyBelow = false;
        //store the input entity's type
        char inputEntityType = inputEntity.getAppearance();
        //store the coordinates of the location below the inputEntity
        Location below = new Location(inputEntity.getLocationRow()+1,inputEntity.getLocationCol());
        //check if the location below the inputEntity is inside the world
        if (isInsideWorld(below) == true){
            //check if the location below the inputEntity contains an entity of a different type
            if (aWorld[below.getRow()][below.getColumn()] != null && 
                aWorld[below.getRow()][below.getColumn()].getAppearance() != inputEntityType){
                isEnemyBelow = true;
            }
        }
        return isEnemyBelow;
    }
    
    //this method returns a boolean answer to the question: is there an enemy below-right of the inputEntity?
    private boolean isEnemyBelowRight(Entity inputEntity){
        //this boolean flag answers whether there is an enemy below-right of the inputEntity
        boolean isEnemyBelowRight = false;
        //store the input entity's type
        char inputEntityType = inputEntity.getAppearance();
        //store the coordinates of the location below-right of the inputEntity
        Location belowRight = new Location(inputEntity.getLocationRow()+1,inputEntity.getLocationCol()+1);
        //check if the location below-right of the inputEntity is inside the world
        if (isInsideWorld(belowRight) == true){
            //check if the location below-right of the inputEntity contains an entity of a different type
            if (aWorld[belowRight.getRow()][belowRight.getColumn()] != null && 
                aWorld[belowRight.getRow()][belowRight.getColumn()].getAppearance() != inputEntityType){
                isEnemyBelowRight = true;
            }
        }
        return isEnemyBelowRight;
    }
    
    //this method returns a boolean answer to the question: is there an enemy right of the inputEntity?
    private boolean isEnemyRight(Entity inputEntity){
        //this boolean flag answers whether there is an enemy to the right of the inputEntity
        boolean isEnemyRight = false;
        //store the input entity's type
        char inputEntityType = inputEntity.getAppearance();
        //store the coordinates of the location to the right of the inputEntity
        Location right = new Location(inputEntity.getLocationRow(),inputEntity.getLocationCol()+1);
        //check if the location to the right of the inputEntity is inside the world
        if (isInsideWorld(right) == true){
            //check if the location to the right of the inputEntity contains an entity of a different type
            if (aWorld[right.getRow()][right.getColumn()] != null && 
                aWorld[right.getRow()][right.getColumn()].getAppearance() != inputEntityType){
                isEnemyRight = true;
            }
        }
        return isEnemyRight;
    }
    
    //this method returns a boolean answer to the question: is there an enemy above-right the inputEntity?
    private boolean isEnemyAboveRight(Entity inputEntity){
        //this boolean flag answers whether there is an enemy above-right the inputEntity
        boolean isEnemyAboveRight = false;
        //store the input entity's type
        char inputEntityType = inputEntity.getAppearance();
        //store the coordinates of the location that is above-right of the inputEntity
        Location aboveRight = new Location(inputEntity.getLocationRow()-1,inputEntity.getLocationCol()+1);
        //check if the location above-right of the inputEntity is inside the world
        if (isInsideWorld(aboveRight) == true){
            //check if the location above-right of the inputEntity contains an entity of a different type
            if (aWorld[aboveRight.getRow()][aboveRight.getColumn()] != null && 
                aWorld[aboveRight.getRow()][aboveRight.getColumn()].getAppearance() != inputEntityType){
                isEnemyAboveRight = true;
            }
        }
        return isEnemyAboveRight;
    }
    
    //this method returns a boolean answer to the question: is there an enemy above the inputEntity?
    private boolean isEnemyAbove(Entity inputEntity){
        //this boolean flag answers whether ther is an enemy above the inputEntity
        boolean isEnemyAbove = false;
        //store the input entity's type
        char inputEntityType = inputEntity.getAppearance();
        //store the coordinates of the location above the inputEntity
        Location above = new Location(inputEntity.getLocationRow()-1,inputEntity.getLocationCol());
        //check if the location above the inputEntity is inside the world
        if (isInsideWorld(above) == true){
            //check if the location direcly above the inputEntity contains an entity of a different type
            if (aWorld[above.getRow()][above.getColumn()] != null && 
                aWorld[above.getRow()][above.getColumn()].getAppearance() != inputEntityType){
                isEnemyAbove = true;
            }
        }
        return isEnemyAbove;
    }
    
    //check if the passed row and col location already has an Entity present at that location
    private boolean isSomeoneThere(Location inputLocation){
        boolean isSomeoneThere = true;
        if (aWorld[inputLocation.getRow()][inputLocation.getColumn()] == null ){
            isSomeoneThere = false;
        }
        return isSomeoneThere;
    }

    //this method answers the question: is the inputLocation inside the world?
    private boolean isInsideWorld(Location inputLocation){   
        boolean isInsideWorld = false;
        if (inputLocation.getRow()>=0 && inputLocation.getRow()<SIZE 
            && inputLocation.getColumn()>=0 && inputLocation.getColumn()<SIZE){
            isInsideWorld = true;
        }
        return isInsideWorld;
    }    
   
    //this method will dereference current entities from the world array
    private void wipeTheWorld(){
        for (int r = 0; r < SIZE; r++){//itterate through the world array
            for (int c = 0; c < SIZE; c++)
            aWorld[r][c] = null;//set element at r,c to null
        }
    }

    //this method will place entities into the world according to the location they are storing
    private void placeEntitiesIntoWorld(){
        for (int i = 0; i<entities.length; i++){//iterate through each entity
            if (entities[i] != null){            
                int drawAtRow = entities[i].getLocationRow();//get row of current entity location
                int drawAtCol = entities[i].getLocationCol();//get col of current entity location
                if (entities[i].getHitPoints()>0)//deep copy the current entity into the new location if they are conscious
                    aWorld[drawAtRow][drawAtCol] = new Entity(entities[i].getAppearance(), entities[i].getHitPoints(), entities[i].getDamage(), drawAtRow, drawAtCol);
            }
        }
    }

    //this method returns the location of the first adjacent enemy as a Location object
    private Location locationOfFirstAdjacentEnemy(Entity attacker){
        //initialize the location object of the first adjacent enemy
        Location firstAdjacentEnemyLocation = null;
        //find location of first adjacent enemy
        if(isEnemyAboveLeft(attacker) == true)
        firstAdjacentEnemyLocation = attacker.getLocationAboveLeft();
        else if (isEnemyAbove(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationAbove();
        else if(isEnemyAboveRight(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationAboveRight();
        else if(isEnemyRight(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationRight();
        else if(isEnemyBelowRight(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationBelowRight();
        else if(isEnemyBelow(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationBelow();
        else if(isEnemyBelowLeft(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationBelowLeft();
        else if(isEnemyLeft(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationLeft();

        return firstAdjacentEnemyLocation;     
    }

    //this method returns a reference to the first adjacent enemy to the input attacker
    private int firstAdjacentEnemyIndex(Entity attacker){
        
        //initialize the location object of the first adjacent enemy
        Location firstAdjacentEnemyLocation = null;
        
        //find location of first adjacent enemy
        if(isEnemyAboveLeft(attacker) == true)
        firstAdjacentEnemyLocation = attacker.getLocationAboveLeft();
        else if (isEnemyAbove(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationAbove();
        else if(isEnemyAboveRight(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationAboveRight();
        else if(isEnemyRight(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationRight();
        else if(isEnemyBelowRight(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationBelowRight();
        else if(isEnemyBelow(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationBelow();
        else if(isEnemyBelowLeft(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationBelowLeft();
        else if(isEnemyLeft(attacker) == true)
            firstAdjacentEnemyLocation = attacker.getLocationLeft();

        return entityAtIndex(firstAdjacentEnemyLocation);     
    }

    //this method attacks the first adjacent enemy found around the q square perimeter of the attacker Entity
    private void attackFirstAdjacentEnemy(Entity attacker){
        
        Entity firstAdjacentEnemy = null;//this reference points to the firstAdjacentEnemy encountered 
        
        //find location of first adjacent enemy
        if(isEnemyAboveLeft(attacker) == true)
            firstAdjacentEnemy = entityAt(attacker.getLocationAboveLeft());        
        else if (isEnemyAbove(attacker) == true)
            firstAdjacentEnemy = entityAt(attacker.getLocationAbove());
        else if(isEnemyAboveRight(attacker) == true)
            firstAdjacentEnemy = entityAt(attacker.getLocationAboveRight());
        else if(isEnemyRight(attacker) == true)
            firstAdjacentEnemy = entityAt(attacker.getLocationRight());
        else if(isEnemyBelowRight(attacker) == true)
            firstAdjacentEnemy = entityAt(attacker.getLocationBelowRight());
        else if(isEnemyBelow(attacker) == true)
            firstAdjacentEnemy = entityAt(attacker.getLocationBelow());
        else if(isEnemyBelowLeft(attacker) == true)
            firstAdjacentEnemy = entityAt(attacker.getLocationBelowLeft());
        else if(isEnemyLeft(attacker) == true)
            firstAdjacentEnemy = entityAt(attacker.getLocationLeft());
       
        //print debug message about intended attack if debug mode is enabled by the user
        if (GameStatus.debugModeOn == true && firstAdjacentEnemy != null){
            System.out.println("<<<World.attackFirstAdjacentEnemy()>>>");
            System.out.println("    Entity "+ attacker.getAppearance() +" @ (r/c) ("+attacker.getLocationRow()+"/"
                               +attacker.getLocationCol()+") attacking for "+attacker.getDamage()+" damage");
        }
        //reduce HP of the first adjacent enemy if one was found
        if (firstAdjacentEnemy != null){
            attacker.attackOpponent(firstAdjacentEnemy);
        }
    }
    
    //this method returns a reference to an entity that has the same location as the entityLocation input
    private Entity entityAt(Location entityLocation){
        Entity entityAt = null;
        for (int i=0; i<entities.length; i++){
            if (entities[i] != null && entityLocation != null){
                if(entities[i].getLocationRow() == entityLocation.getRow() && entities[i].getLocationCol() == entityLocation.getColumn())
                    entityAt = entities[i];
            }
        }
        return entityAt;
    }

    //this method returns a reference to an entity that has the same location as the entityLocation input
    private int entityAtIndex(Location entityLocation){
        int entityAtIndex = -1;
        for (int i=0; i<entities.length; i++){
            if (entities[i] != null && entityLocation != null){
                if(entities[i].getLocationRow() == entityLocation.getRow() && entities[i].getLocationCol() == entityLocation.getColumn())
                    entityAtIndex = i;
            }
        }
        return entityAtIndex;
    }

    //this method check if a game endiong condition is met and sets the keep_playing flag according to the result
    private void checkEndGameConditions(){
        
        //count the entities currently present in the entities array
        countEntities();

        //check if orcs have won
        if (numOfOrcs>0 && numOfElves==0){
            keep_playing = false;
            System.out.println("Orcs win this round!");
        }
        //check if elves have won
        if (numOfElves>0 && numOfOrcs==0){
            keep_playing = false;
            System.out.println("Elves win this round!");
        }
        //check if there is a draw
        if (numOfElves==0 && numOfOrcs==0){
            keep_playing = false;
            System.out.println("A draw occurred!");
        }
        //check if there is a cease fire (no attacks occured for 10 successive turns)
        if (ceaseFireCounter>=10){
            keep_playing = false;
            System.out.println("10 turns since hostilities have last occurred...a ceasefire has been declared!");
        }
    }

    //this method keeps count of consecutive turns with no attacks occurring
    private void ceaseFireCounter(){
        if (entitiesAttackedThisTurn == false)
            ceaseFireCounter++;
        else if (entitiesAttackedThisTurn == true)
            ceaseFireCounter = 0;
        //print a debug message if debug mode is enabled by the user
        if (GameStatus.debugModeOn == true){
            System.out.println("Number of consecutive turns without an attack: "+ceaseFireCounter);
        }
    }

    //this method runs a loop to continue the game until game ending conditions are satisfied
    public void start(){        

        initializeEntities();//create an array of Entity objects that holds all entities in the world
        display();//draw the current world and the Entities in that world
        promptUser();//pause and prompt the user for continuation of the simulation and to toggle debug mode
        
        while (keep_playing==true){//keep playing the game until end conditions are met
            checkEndGameConditions();//check for end game condition
            
            if (keep_playing == true){//continue if end game conditions are not met
                moveEntities();//entities move according to their move set
                ceaseFireCounter();//check if the ceaseFireCounter needs to be incremented           
                display();//draw the current world and the Entities in the world
                promptUser();//pause and prompt the user for continuation of the simulation and to toggle debug mode
            }
        }
        display();//display the final state of the world
    }
}