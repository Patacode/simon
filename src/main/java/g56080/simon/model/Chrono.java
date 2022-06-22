package g56080.simon.model;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.util.Duration;

/**
 * A simple timer class implemented using a PauseTransition and an event handler describing
 * the task to be performed when the timer takes end.
 */
public class Chrono{
    
    private PauseTransition timer;
    private final EventHandler<ActionEvent> task;
    private int time; /* seconds */
    private final static int BASE_TIME = 5;

    /**
     * Creates a new Chrono using the given task to be executed when the timer takes end. The chrono will use 
     * the default BASE_TIME value to start (which is 5 seconds).
     *
     * @param task the task to be executed on ending
     */
    public Chrono(EventHandler<ActionEvent> task){
        time = BASE_TIME;
        this.task = task;
        timer = new PauseTransition();
    }

    /**
     * Starts this Chrono using its current 'time' value.
     */
    public void start(){
        timer.setDuration(Duration.seconds(time));
        timer.setOnFinished(task);
        timer.play();
    }

    /**
     * Cancels this Chrono by stopping it.
     */
    public void cancel(){
        timer.stop();
    }

    /**
     * Upgrades this Chrono by incrementing its 'time' value by one.
     */
    public void upgrade(){
        time++;
    }

    /**
     * Initializes this Chrono by setting its 'time' value to the default
     * BASE_TIME (which is 5 seconds).
     */
    public void init(){
        time = BASE_TIME;
    }

    /**
     * Updates the 'time' value of this Chrono using the given level. The first level is 1 and represents a timer of
     * BASE_TIME seconds (5 typically) and for each other level the 'time' value of this Chrono will be computed 
     * like so: <code>time = BASE_TIME + (level - 1)</code>.
     *
     * @param level the level to be used by this Chrono
     * @throws IllegalArgumentException if the given level is less than 1.
     */
    public void setLevel(int level){
        if(level < 1)
            throw new IllegalArgumentException("Invalid level");

        time = BASE_TIME + (level - 1);
    }

    /**
     * Gets the 'time' value of this Chrono.
     *
     * @return the time value of this Chrono.
     */
    public int getTime(){
        return time;
    }
}
