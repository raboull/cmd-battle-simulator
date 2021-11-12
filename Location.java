/*
  Class summary: Entity class represents an ordered paif of 2D coordinates
  
  Class details: 
  --the class object attributes hold a set of 2D coordinates.
  --getter and setter methods provide the coordinates in a requested format

  Limitations:
  --Only works for a set of 2D coordinates
  
  Version: March 7, 2021  
  --added a setter function that takes a Location object as input 

*/

public class Location{
    private int row;
    private int column;

    public Location (){
        //initialize location attributes to invalid values
        row = -1;
        column = -1; 
    }
    
    public Location(int newRow, int newColumn){
        row = newRow;
        column = newColumn;
    }

    public int getColumn(){
        return(column);
    }

    public int getRow(){
        return(row);
    }

    public void setColumn(int newColumn){
        column = newColumn;
    }

    public void setRow(int newRow){
        row = newRow;
    }

    public void setRowAndColumn(int newRow, int newColumn){
        this.row = newRow;
        this.column = newColumn;
    }
}