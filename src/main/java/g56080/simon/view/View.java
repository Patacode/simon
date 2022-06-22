package g56080.simon.view;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;

import g56080.simon.controller.Controller;
import g56080.simon.model.Model;

import javafx.beans.value.ChangeListener;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.Stage;

import javafx.util.Duration;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import java.util.function.Consumer;

/**
 * The observable application view dedicated to the creation of user interface.
 */
public class View implements ObservableListener {
    
    private final Controller controller;
    private final Model model;
    private final EventManager eventManager;
    private final StackLayer stackLayer;
    private Stage stage;
    private Scene scene;
    private final MidiChannel channel;

    /**
     * Default width and height dimension of the application scene.
     */
    public final static double DEFAULT_WIDTH = 800., DEFAULT_HEIGHT = 800.;

    /**
     * Creates a new View using the given controller and model.
     *
     * @param controller the application controller
     * @param model the application model
     */
    public View(Controller controller, Model model){
        this.controller = controller;
        this.model = model;
        stackLayer = new StackLayer();
        eventManager = new EventManager();

        channel = getChannel();
        model.subscribe(this);
    }
    
    @Override
    public void update(Model.State state){
        Queue<Color> sequence = null;
        Layer layer = null;
        switch(state){
            case GAME_NOT_STARTED:
                stackLayer.clear();
                eventManager.clear();
                if(scene != null && stage != null)
                    initLayout(getWindowWidth(), getWindowHeight());
                else
                    initLayout(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                initEvents();
                break;
            case GAME_STARTED_TIMER:
                layer = createTimerLayer();
                stackLayer.removeTopLayer();
                stackLayer.addLayer(layer);
                stackLayer.compose();
                runTimerLayer(layer, 3, model.getActionController());
                break;
            case GAME_STARTED:
                sequence = model.getSequence();
                layer = createLevelLayer(model.getLevel(), model.getTime());

                stackLayer.removeLayers(2);
                stackLayer.addLayer(layer);
                stackLayer.compose();
                playSequence(sequence);
                break;
            case GAME_TURN:
                sequence = model.getSequence();
                layer = createLevelLayer(model.getLevel(), model.getTime());

                stackLayer.addLayer(layer);
                stackLayer.compose();
                updateButtons(false);
                playSequence(sequence);
                break;
            case PLAYER_TURN:
                stackLayer.removeTopLayer();
                stackLayer.compose();
                updateButtons(true);
                break;
            case NEXT_LEVEL: 
                controller.nextLevel();
                break;
            case TIME_IS_OVER:
            case GAME_OVER:
                controller.end();
                break;
        }
    }

    /**
     * Start the application view using and configuring the given stage.
     *
     * @param stage the application main stage
     */
    public void start(Stage stage){
        if(System.getProperty("os.name").toLowerCase().contains("windows"))
            stage.setResizable(false);

        Scene scene = getScene();
        this.stage = stage;
        this.scene = scene;
        configureStage(stage);
        stage.show();

    }

    /**
     * Plays the sequence of color by emulating each color in the sequence by a click on 
     * the corresponding button. The sequence of color will be played faster or slower depending on the
     * speed value the slider holds.
     * 
     * @param sequence the color sequence to be played
     */
    public void playSequence(Queue<Color> sequence){
        Queue<Color> sequenceClone = new ArrayDeque<>(sequence);
        Slider speed = (Slider) eventManager.getTarget("speed").orElse(null);

        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(speed == null ? 1. : speed.getValue() + 0.2), event -> {
            Color color = sequenceClone.poll();
            Optional<Node> target = eventManager.getTarget(color.toString());
            target.ifPresent(node -> {
                Button btn = (Button) node;
                btn.fire();
            });
        }));

        tl.setCycleCount(sequence.size());
        tl.setOnFinished(event -> controller.sequenceOver());
        tl.play();
    }

    /**
     * Gets the effective width of the application window (represented by the stage). The effective width
     * is obtained by substracting the left and right inset of the overall window width.
     *
     * @return the window width
     */
    public double getWindowWidth(){
        return stage.getWidth() - leftInset() - rightInset();
    }

    /**
     * Gets the effective height of the application window (represented by the stage). The effective height
     * is obtained by substracting the top and bottom inset of the overall window height.
     *
     * @return the window height
     */
    public double getWindowHeight(){
        return stage.getHeight() - topInset() - bottomInset();
    }

    /**
     * Gets top inset of the application window.
     *
     * @return the windows' top inset
     */
    public double topInset(){
        return scene.getY();
    }

    /**
     * Gets left inset of the application window.
     *
     * @return the windows' left inset
     */
    public double leftInset(){
        return scene.getX();
    }

    /**
     * Gets bottom inset of the application window.
     *
     * @return the windows' bottom inset
     */
    public double bottomInset(){
        return stage.getHeight() - scene.getHeight() - scene.getY();
    }

    /**
     * Gets right inset of the application window.
     *
     * @return the windows' right inset
     */
    public double rightInset(){
        return stage.getWidth() - scene.getWidth() - scene.getX();
    }

    




    // Private methods
    private void updateButtons(boolean isClick){
        int note = 72;
        Slider slider = (Slider) eventManager.getTarget("speed").orElse(null);
        CheckBox cb = (CheckBox) eventManager.getTarget("checkbox").orElse(null);
        for(ButtonColor btnColor : ButtonColor.values()){
            eventManager.removeEventsFrom(btnColor.getValue().toString());
            eventManager.addEventHandler(
                    btnColor.getValue().toString(), 
                    ActionEvent.ACTION,
                    new ClickButtonHandler(controller, btnColor, note, cb, isClick, channel));
            note += 2;
        }
    }


    private Scene getScene(){
        Scene scene = new Scene(stackLayer.getStack(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        return scene;
    }

    private void configureStage(Stage stage){
        stage.setTitle("Simon");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setMinWidth(DEFAULT_WIDTH);
        stage.setMinHeight(DEFAULT_HEIGHT);
        stage.widthProperty().addListener(getWidthSizeListener());
        stage.heightProperty().addListener(getHeightSizeListener());
    }

    private ChangeListener<Number> getWidthSizeListener(){
       return (ov, oldv, newv) -> {
            double winWidth = getWindowWidth();

            // Third layer
            stackLayer.getLayer(2).ifPresent(layer -> {
                Insets currentInset = StackPane.getMargin(layer.getRoot());
                double newTopInset = currentInset.getTop();
                double newLeftRightInset = DEFAULT_WIDTH / 4. + (winWidth - DEFAULT_WIDTH) / 2.;
                double newBottomInset = currentInset.getBottom();
                Insets newInset = new Insets(newTopInset, newLeftRightInset, newBottomInset, newLeftRightInset);
                StackPane.setMargin(layer.getRoot(), newInset);
            });

            // Second layer
            if(model.getState().equals(Model.State.GAME_NOT_STARTED)){
                stackLayer.getLayer(1).ifPresent(layer -> {
                    Rectangle rect = (Rectangle) layer.getChildren().get(0);
                    rect.setWidth(winWidth);
                });
            }

            // First layer
            stackLayer.getLayer(0).ifPresent(layer -> {
                List<Node> children = layer.getChildren(node -> node instanceof Button);
                children.stream().forEach(child -> {
                    Button btn = (Button) child;
                    btn.setMinWidth(winWidth / 2.);
                });
            });
       };
    }

    private ChangeListener<Number> getHeightSizeListener(){
        return (ov, oldv, newv) -> {
            double winHeight = getWindowHeight();

            // Third layer
            stackLayer.getLayer(2).ifPresent(layer -> {
                Insets currentInset = StackPane.getMargin(layer.getRoot());
                double newTopBottomInset = DEFAULT_HEIGHT / 4. + (winHeight - DEFAULT_HEIGHT) / 2.; 
                double newRightInset = currentInset.getRight();
                double newLeftInset  = currentInset.getLeft();
                Insets newInset = new Insets(newTopBottomInset, newRightInset, newTopBottomInset, newLeftInset);
                StackPane.setMargin(layer.getRoot(), newInset);
            });

            // Second layer
            if(model.getState().equals(Model.State.GAME_NOT_STARTED)){
                stackLayer.getLayer(1).ifPresent(layer -> {
                    Rectangle rect = (Rectangle) layer.getChildren().get(0);
                    rect.setHeight(winHeight);
                });
            }

            // First layer
            stackLayer.getLayer(0).ifPresent(layer -> {
                List<Node> children = layer.getChildren(node -> node instanceof Button);
                children.stream().forEach(child -> {
                    Button btn = (Button) child;
                    btn.setMinHeight(winHeight / 2.);
                });
            });
        };
    }

    private void initLayout(double width, double height){
        Layer flayer = createFirstLayer(width, height);
        Layer slayer = createSecondLayer(width, height);
        Layer tlayer = createThirdLayer();
        double tbMargin = DEFAULT_HEIGHT / 4. + (height - DEFAULT_HEIGHT) / 2.;
        double lrMargin = DEFAULT_WIDTH / 4. + (width - DEFAULT_WIDTH) / 2.;
        Insets margin = new Insets(tbMargin, lrMargin, tbMargin, lrMargin);

        stackLayer.addLayers(flayer, slayer, tlayer);
        StackPane.setMargin(tlayer.getRoot(), margin);
        stackLayer.compose();
    }

    private void initEvents(){ /* Adds events to previously added nodes */
        initEventTargets();

        eventManager.addEventHandler("startMenu", MouseEvent.MOUSE_ENTERED, event -> {
            Node node = (Node) event.getSource();
            node.getScene().setCursor(Cursor.HAND);
        });
        eventManager.addEventHandler("startMenu", MouseEvent.MOUSE_EXITED, event -> {
            Node node = (Node) event.getSource();
            node.getScene().setCursor(Cursor.CROSSHAIR);
        });
        
        // Start - last - longuest buttons
        eventManager.addEventHandler("start", MouseEvent.MOUSE_CLICKED, event -> controller.timerStart());
        eventManager.addEventHandler("last", MouseEvent.MOUSE_CLICKED, event -> controller.timerLast());
        eventManager.addEventHandler("longuest", MouseEvent.MOUSE_CLICKED, event -> controller.timerLonguest());

        // First layer buttons
        int note = 72;
        Slider slider = (Slider) eventManager.getTarget("speed").orElse(null);
        CheckBox cb = (CheckBox) eventManager.getTarget("checkbox").orElse(null);
        for(ButtonColor btnColor : ButtonColor.values()){
            eventManager.addEventHandler(
                    btnColor.getValue().toString(), 
                    ActionEvent.ACTION,
                    new ClickButtonHandler(controller, btnColor, note, cb, false, channel));
            note += 2;
        }
    }

    private void initEventTargets(){ /* Records the event-targeted nodes to EventManager */
        // Start button
        stackLayer.getTopLayer().ifPresent(layer -> {
            List<Node> children = layer.getChildren(node -> node instanceof Button);
            children.stream().forEach(child -> {
                Button target = (Button) child;
                eventManager.addTarget(target.getText(), target);
            });
        });
         
        // first layer buttons
        stackLayer.getLayer(0).ifPresent(layer -> {
            List<Node> children = layer.getChildren(node -> node instanceof Button);
            children.stream().forEach(node -> {
                Button btn = (Button) node;
                Color color = (Color) btn.getBackground().getFills().get(0).getFill();
                eventManager.addTarget(color.toString(), btn);
            });
        });

        // checkbox
        stackLayer.getTopLayer().ifPresent(layer -> {
            List<Node> children = layer.getChildren(node -> node instanceof CheckBox);
            CheckBox cb = (CheckBox) children.get(0);
            eventManager.addTarget("checkBox", cb);
        });

        // slider
        stackLayer.getTopLayer().ifPresent(layer -> {
            List<Node> children = layer.getChildren(node -> node instanceof Slider);
            Slider slider = (Slider) children.get(0);
            eventManager.addTarget("speed", slider);
        });

        // start menu
        stackLayer.getTopLayer().ifPresent(layer -> eventManager.addTarget("startMenu", layer.getRoot()));
    }





    /*
     * LAYERS
     **/

    private Layer createFirstLayer(double width, double height){
        BorderPane bp = new BorderPane();
        Node topNode    = createFirstLayerNodes(width, height, ButtonColor.GREEN.getValue(), ButtonColor.RED.getValue());
        Node bottomNode = createFirstLayerNodes(width, height, ButtonColor.YELLOW.getValue(), ButtonColor.BLUE.getValue());

        bp.setTop(topNode);
        bp.setBottom(bottomNode);

        return new Layer(bp);
    }

    private Layer createSecondLayer(double width, double height){
        Layer layer = new Layer(new Pane());
        Rectangle rect = new Rectangle(width, height, new Color(0., 0., 0., .5));
        layer.addChild(rect);
        return layer;
    }

    private Layer createThirdLayer(){ /* Center box */
        GridPane grid = new GridPane();

        VBox topBox = createTopBox();
        HBox buttonBox = createButtonBox();
        VBox checkBoxInfo = createCheckBoxInfo();
        grid.getChildren().addAll(topBox, buttonBox, checkBoxInfo);
        
        GridPane.setConstraints(topBox, 0, 0);
        GridPane.setConstraints(buttonBox, 0, 1);
        GridPane.setConstraints(checkBoxInfo, 0, 2);
        GridPane.setMargin(buttonBox, new Insets(20., 0., 20., 0.));

        grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderStroke.MEDIUM)));
        grid.setPadding(new Insets(10));
        grid.setBackground(new Background(new BackgroundFill(Color.color(1, 1, 1), null, null)));
        grid.setAlignment(Pos.CENTER);
        
        return new Layer(grid);
    }

    private Layer createLevelLayer(int level, int time){
        BorderPane pane = new BorderPane();
        Label lvl = new Label(String.format("LEVEL %d", level));
        Label timer = new Label(String.format("TIME %ds", time));

        lvl.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        timer.setFont(Font.font("Verdana", FontWeight.BOLD, 30));

        lvl.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        timer.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        lvl.setPadding(new Insets(10));
        timer.setPadding(new Insets(10));
        pane.setLeft(lvl);
        pane.setRight(timer);
        BorderPane.setMargin(lvl, new Insets(5));
        BorderPane.setMargin(timer, new Insets(5));

        return new Layer(pane);
    }

    private Layer createTimerLayer(){
        BorderPane pane = new BorderPane();
        Label label = new Label("Ready ?!");

        label.setFont(Font.font("Verdana", FontWeight.BOLD, 100));
        label.setTextFill(Color.WHITE);
        pane.setCenter(label);
        BorderPane.setAlignment(label, Pos.CENTER);

        return new Layer(pane);
    }

    private void runTimerLayer(Layer layer, int time, Consumer<Controller> controllerAction){
        Label timer = (Label) layer.getChildren(node -> node instanceof Label).get(0);
        int channelProgram = channel.getProgram();
        PauseTransition pt = new PauseTransition(Duration.seconds(0.2));
        pt.setOnFinished(event -> channel.noteOff(72));

        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if(isNumber(timer.getText()) && Integer.valueOf(timer.getText()) > 1){
                timer.setText(Integer.valueOf(timer.getText()) - 1 + "");
                channel.noteOn(72, 100);
            } else if(isNumber(timer.getText())){
                channel.noteOn(60, 100);
                timer.setText("Start !");
            } else if(!timer.getText().equals("Start !")){
                timer.setText(time + "");
                channel.noteOn(72, 100);
            }
            pt.play();
        }));

        channel.programChange(0, 56);
        tl.setCycleCount(time + 2);
        tl.setOnFinished(event -> {
            channel.noteOff(60);
            channel.programChange(0, channelProgram);
            controllerAction.accept(controller);
        });
        tl.play();
    }




    /*
     * COMPONENTS
     **/

    private boolean isNumber(String text){
        return Character.isDigit(text.charAt(0));
    }

    private MidiChannel getChannel(){
        Synthesizer synth = null;
        try{
            synth = MidiSystem.getSynthesizer(); 
            synth.open();
        } catch(MidiUnavailableException exc){
            return null;
        }

        MidiChannel channel = synth.getChannels()[0];
        channel.programChange(0, Model.genRandom(0, 128));
        return channel;
    }

    private Node createFirstLayerNodes(double width, double height, Color... colors){
        HBox container = new HBox();
        Arrays.stream(colors).forEach(color -> {
            Button btn = createButton(color);
            btn.setMinSize(width / 2., height / 2.);
            container.getChildren().add(btn);
        });

        return container;
    }

    private Button createButton(Color color){
        return createButton(null, color);
    }

    private Button createButton(String name, Color color){
        Button btn = new Button(name == null ? "" : name);
        btn.setBackground(new Background(new BackgroundFill(color, null, null)));

        return btn;
    }

    private VBox createTopBox(){
        VBox topBox = new VBox(5.);
        Label title = createTitle("Simon");
        VBox slider = createSlider();

        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().addAll(title, slider);

        return topBox;
    }

    private HBox createButtonBox(){
        HBox box = new HBox(5.);
        Button left = createButton("Longuest", Color.YELLOW);
        Button middle = createButton("Start", Color.RED);
        Button right = createButton("Last", Color.YELLOW);

        box.getChildren().addAll(left, middle, right);
        box.getChildren().stream().forEach(child -> {
            Button conv = (Button) child;
            conv.setPadding(new Insets(15, 20, 15, 20));
            conv.setBackground(new Background(new BackgroundFill(conv.getBackground().getFills().get(0).getFill(), new CornerRadii(7.), null)));
            conv.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5.), BorderStroke.THIN)));
        });
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox createCheckBoxInfo(){
        VBox box = new VBox(5.);
        Label label = new Label("info");
        CheckBox checkbox = new CheckBox("Silent mode");
        label.setTextFill(Color.color(0, 0, 0, 0.6));

        box.getChildren().addAll(label, checkbox);
        box.setAlignment(Pos.CENTER);

        return box;
    }

    private Label createTitle(String labelText){
        Label label = new Label(labelText);
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 50.));

        return label;
    }

    private VBox createSlider(){
        VBox sliderBox = new VBox(2.);
        Slider slider = new Slider(0.5, 1.5, 1);
        Label label = new Label("speed");

        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.setBlockIncrement(0.25f);
        slider.setMajorTickUnit(0.25f);

        sliderBox.setAlignment(Pos.CENTER);
        sliderBox.getChildren().addAll(slider, label);
        
        return sliderBox;
    }
}

