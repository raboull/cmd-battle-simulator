/*
  Class summary: Entity class represents a player entity in the world.
  
  Class details: 
  --Appearance of soldier is either E for elf or O for orcs.
  --Hit points for each soldier type are predefined in this class.
  --Damage points inflicted by each soldier type are predefined in this class.
  --Includes constants that describe various entity attributes.

  Limitations:
  --only orc and elf attributes are available as constants, no other magical creatures are known to this class.
  
  Version: March 7, 2021  
  --added functions to get coordinates of adjacent locations to an entity.
  --added functions to set location of entity with input of a Location object.
  
  Version: March 6, 2021
  --added function that reduces HP of an opponent.
   
  */

public class Entity
{
    public static final char DEFAULT_APPEARANCE = 'X';
    public static final char ELF = 'E';//symbol that represents an elf
    public static final char EMPTY = ' ';
    public static final char ORC = 'O';//symbol that represents an orc
    public static final int DEFAULT_HP = 1;
    public static final int ORC_DAMAGE = 3;//damage points that an arc inflicts per hit
    public static final int ELF_DAMAGE = 7;//damage points that an elf inflicts per hit
    public static final int ORC_HP = 10;//stores the amount of hits an orc can take
    public static final int ELF_HP = 15;//stores the amount of hits an elf can take

    private char appearance;
    private int hitPoints;
    private int damage;
    private Location entityLocation;

    public Entity(){
        setAppearance(DEFAULT_APPEARANCE);
        setHitPoints(DEFAULT_HP);
    }

    public Entity(char newAppearance){
        appearance = newAppearance;
        hitPoints = DEFAULT_HP;
        damage = ORC_DAMAGE;
    }

    public Entity(char newAppearance, int newHitPoints, int newDamage){
        setAppearance(newAppearance);
        setDamage(newDamage);
        setHitPoints(newHitPoints);
    }

    //the following constructor will store location of entity along with other attributes
    public Entity(char newAppearance, int newHitPoints, int newDamage, int newRow, int newCol){
        setAppearance(newAppearance);
        setDamage(newDamage);
        setHitPoints(newHitPoints);
        this.entityLocation = new Location(newRow, newCol);
    }

    public char getAppearance(){
        return(appearance);
    }

    public int getDamage(){
        return(damage);
    }

    public int getHitPoints(){
        return(hitPoints);
    }

    public int getLocationRow(){
        return(this.entityLocation.getRow());
    }

    public int getLocationCol(){
        return(this.entityLocation.getColumn());
    }

    public Location getLocation(){
        return this.entityLocation;
    }

    private void setAppearance(char newAppearance){
        appearance = newAppearance;
    }

    private void setDamage(int newDamage){
        if (newDamage < 1) {
            System.out.println("Damage must be 1 or greater");
        }
        else{
            damage = newDamage;
        }        
    }

    private void setHitPoints(int newHitPoints){
        hitPoints = newHitPoints;
    }

    public void setLocationRow(int newRow){
        if (this.entityLocation == null)
            this.entityLocation = new Location();
        this.entityLocation.setRow(newRow);
    }

    public void setLocationCol(int newCol){
        if (this.entityLocation == null)
            this.entityLocation = new Location();
        this.entityLocation.setColumn(newCol);
    }

    public void setLocation(Location newLocation){
        this.entityLocation.setColumn(newLocation.getColumn());
        this.entityLocation.setRow(newLocation.getRow());
    }

    //this function reduces HP of the opponent
    public void attackOpponent(Entity opponent){
        int attackerDamage = this.getDamage();
        int opponentHP = opponent.getHitPoints();
        int opponentNewHP = opponentHP-attackerDamage;
        
        opponent.setHitPoints(opponentNewHP);

        //print debug message of the attack if debug mode is enabled by the user
        if (GameStatus.debugModeOn == true){
            System.out.println("<<< Entity.attackOpponent >>>");
            System.out.println("        Opponent "+opponent.getAppearance()+ " with (hp): "+opponentHP+" @ (row/column): ("+opponent.getLocationRow() + "/" + opponent.getLocationCol() +") Damage="+attackerDamage+" new hp: "+opponentNewHP);
        }
    }

    //this function returns a location object with coordinates directly above the inputEntity object
    public Location getLocationAbove(){
        Location locationAbove = new Location(this.getLocationRow()-1, this.getLocationCol());
        return locationAbove;
    }

    //this function returns a location object with coordinates directly above-right the inputEntity object
    public Location getLocationAboveRight(){
        Location locationAboveRight = new Location(this.getLocationRow()-1, this.getLocationCol()+1);
        return locationAboveRight;
    }

    //this function returns a location object with coordinates directly right of the inputEntity object
    public Location getLocationRight(){
        Location locationRight = new Location(this.getLocationRow(), this.getLocationCol()+1);
        return locationRight;
    }

    //this function returns a location object with coordinates directly below-right of the inputEntity object
    public Location getLocationBelowRight(){
        Location locationBelowRight = new Location(this.getLocationRow()+1, this.getLocationCol()+1);
        return locationBelowRight;
    }

    //this function returns a location object with coordinates directly below the inputEntity object
    public Location getLocationBelow(){
        Location locationBelow = new Location(this.getLocationRow()+1, this.getLocationCol());
        return locationBelow;
    }

    //this function returns a location object with coordinates directly below-left of the inputEntity object
    public Location getLocationBelowLeft(){
        Location locationBelowLeft = new Location(this.getLocationRow()+1, this.getLocationCol()-1);
        return locationBelowLeft;
    }

    //this function returns a location object with coordinates directly left of the inputEntity object
    public Location getLocationLeft(){
        Location locationLeft= new Location(this.getLocationRow(), this.getLocationCol()-1);
        return locationLeft;
    }

    //this function returns a location object with coordinates directly above-left of the inputEntity object
    public Location getLocationAboveLeft(){
        Location locationAboveLeft = new Location(this.getLocationRow()-1, this.getLocationCol()-1);
        return locationAboveLeft;
    }    
}