package g56080.simon;

import g56080.simon.controller.Controller;
import g56080.simon.model.Model;

import javafx.application.Application;

import javafx.stage.Stage;

/**
 * Application entry point to run the application.
 */
public class App extends Application{

    /**
     * Main method launching the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage mainStage){
        Model model = new Model();
        Controller controller = new Controller(mainStage, model);
    }

    @Override
    public void stop() throws Exception{
        super.stop();
        System.exit(0);
    }
}
