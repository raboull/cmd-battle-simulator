import javax.sound.sampled.AudioSystem;
/*
  Program summary: Simulates a fight between orcs and elves in a 10x10 array as the world.
  Program details: 
  --Starting execution point of the program containing the main method.
  --Simulates a world where orcs and elves are battling to their unconsciousness.
  --Program runs endlessly until endgame condition occurs (loop can be endless for 
    credit for this feature, end game is separate credit) with a prompt to hit enter at end of each turn.
  --Can toggle debugging mode along with appropriate message as state toggles
  --Each array element is bound with bounding lines
  --Orcs move in the prescribed fashion
  --Elves move in the prescribed fashion
  --Boundary checking for elves and orc movement
  --During movement program ensures that entities only move onto empty locations
  --Entities only move if they are not adjacent to enemy opposition
  --Entities can attack members of the opposing side only
  --Unconscious entities are removed at the end of the turn
  --Can determine when orcs have won
  --Can determine when the elves have won (there are no orcs remaining), game ends with appropriate status message
  --Can determine when there is draw (no entities left), game ends with appropriate status message
  --Can determine when there is a ceasefire (no attackers have occured for 10 successive turns), game ends with appropriate status message

  Limitations:
  --Only serves one purpose, call the start function of a world object.
  --The world size is limited to a 10x10 matrix.
  --Valid input file characters consist only of 'O', 'E', or spaces.
  --Movement is consistent for each enemy without any variations.
  --Attacking an opposing entity focuses on one entity at a time.
  --Either a move or an attack can be performed per turn.
  --only orc and elf attributes are available as constants, no other magical creatures are known to this class.
  --Only works for a 2D world array

  Version: March 6, 2021
  --call to the start method of the world class is added
*/


public class Simulator
{
    public static void main(String [] args)
    {     
      
      World aWorld = new World();
      aWorld.start();
    }
}