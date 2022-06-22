package g56080.simon.model;

/**
 * A mutable level object that can be upgraded and which is used (by the model typically) 
 * to generate the proper number of colors to be played as the player progresses through 
 * the game.
 */
public class Level{
    
    private int level;
    private int count;

    private final static int BASE_LEVEL = 1, BASE_COUNT = 1;

    /**
     * Creates a new Level using the default BASE_LEVEL and BASE_COUNT values (respectively 1 and 1).
     */
    public Level(){
        this(BASE_LEVEL);
    }

    /**
     * Creates a new Level using the given level value.
     *
     * @param level the initial level of this object
     */
    public Level(int level){
        setLevel(level);
    }

    /**
     * Gets the current level of this Level object.
     *
     * @return the current level of this object.
     */
    public int getLevel(){
        return level;
    }

    /**
     * Gets the current count value associated to the current level of this object. The count value
     * starts at BASE_COUNT (which is 1) and is incremented by the current level - 1.
     *
     * @return the count value of this object.
     */
    public int getCount(){
        return count;
    }

    /**
     * Upgrades this Level object by incrementing its level and count values by one.
     */
    public void upgrade(){
        level++;
        count++;
    }

    /**
     * Initializes this Level object by setting its level and count values to BASE_LEVEL and BASE_COUNT
     * respectively.
     */
    public void init(){
        level = BASE_LEVEL;
        count = BASE_COUNT;
    }

    /**
     * Sets the current level value of this object to the given level. The count value associated to this object
     * is also updated accordingly following the formula: <code>BASE_COUNT + (level - 1)</code>.
     *
     * @param level the level to be used by this object
     * @throws IllegalArgumentException if the given level is less than BASE_LEVEL
     */
    public void setLevel(int level){
        if(level < BASE_LEVEL)
            throw new IllegalArgumentException("Invalid level");
        
        this.level = level;
        count = BASE_COUNT + (level - 1);
    }
}
