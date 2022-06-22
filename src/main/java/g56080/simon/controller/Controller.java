package g56080.simon.controller;

import g56080.simon.model.Model;
import g56080.simon.view.View;

import javafx.stage.Stage;
import javafx.scene.paint.Color;

/**
 * Application controller dedicated to receive notifications from the view(s), for the model.
 */
public class Controller{
    
    private final View view;
    private final Model model;
    private final Stage stage;

    /**
     * Creates a new Controller using the given stage and model. A default view is also created from the 
     * newly created controller and the given model. The stage is used by the view in order to properly 
     * init the user interface.
     *
     * @param stage the application main stage
     * @param model the application model
     */
    public Controller(Stage stage, Model model){
        this.stage = stage;
        this.model = model;
        view = new View(this, model);

        model.init();
        view.start(stage);
    }

    /**
     * Notifies the model to start the timer before playing a normal game.
     */
    public void timerStart(){
        model.timer(controller -> controller.start());
    }

    /**
     * Notifies the model to start the timer before playing the last game.
     */
    public void timerLast(){
        model.timer(controller -> controller.last());
    }

    /**
     * Notifies the model to start the timer before playing the longuest game.
     */
    public void timerLonguest(){
        model.timer(controller -> controller.longuest());
    }

    /**
     * Notifies the model to start a normal game.
     */
    public void start(){
        model.start();
    }

    /**
     * Notifies the model to start the longuest played game.
     */
    public void longuest(){
        model.longuest();
    }

    /**
     * Notifies the model to start the last played game.
     */
    public void last(){
        model.last();
    }

    /**
     * Notifies the model that the current game takes end. The game ends due to either a misplay
     * (GAME_OVER state) or because the timer is running up (TIME_IS_OVER state).
     */
    public void end(){
        model.init();
    }

    /**
     * Notifies the model that a click has occured on a color button (from the first layer).
     *
     * @param clickedColor the color of the button on which the user has clicked.
     */
    public void click(Color clickedColor){
        model.click(clickedColor);
    }

    /**
     * Notifies the model that the game color sequence is over.
     */
    public void sequenceOver(){
        model.sequenceOver();
    }

    /**
     * Notifies the model that the user has completed the current level.
     */
    public void nextLevel(){
        model.nextLevel();
    }
}

