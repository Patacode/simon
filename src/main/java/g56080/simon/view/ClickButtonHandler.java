package g56080.simon.view;

import java.util.Arrays;
import java.util.Random;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Synthesizer;

import g56080.simon.controller.Controller;

import javafx.animation.PauseTransition;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import javafx.util.Duration;

/**
 * Event handler for the click on a color button represented by an ActionEvent.
 */
public class ClickButtonHandler implements EventHandler<ActionEvent>{

    private ButtonColor btnColor;
    private final int noteNumber;
    private final CheckBox sound;
    private final Controller controller;
    private final boolean isClick;
    private final MidiChannel channel;

    /**
     * Creates a new ClickButtonHandler for the click on a color button using the given arguments.
     *
     * @param controller the application controller to notify the model on click
     * @param btnColor the button color on which the click occurs
     * @param noteNumber the note to be played when the click on the button occurs
     * @param sound whether the button click should produce a sound or not
     * @param isClick whether the button click should notify the model or not (by the given controller)
     * @param channel the channel used to produce a sound
     */
    public ClickButtonHandler(Controller controller, ButtonColor btnColor, int noteNumber, CheckBox sound, boolean isClick, MidiChannel channel){
        this.controller = controller;
        this.btnColor = btnColor;
        this.noteNumber = noteNumber;
        this.sound = sound;
        this.isClick = isClick;
        this.channel = channel;
    }

    @Override
    public void handle(ActionEvent event){
        Button src = (Button) event.getSource();
        PauseTransition pt = new PauseTransition(Duration.seconds(0.2));

        if(channel != null)
            channel.noteOn(noteNumber, sound == null || !sound.isSelected() ? 70 : 0);
        src.setBackground(new Background(new BackgroundFill(btnColor.getAltValue(), null, null)));
        pt.setOnFinished(ev -> {
            src.setBackground(new Background(new BackgroundFill(btnColor.getValue(), null, null)));
            if(channel != null){
                channel.noteOff(noteNumber);
            }
            if(isClick)
                controller.click(btnColor.getValue());
        });

        pt.play();
    }
}
