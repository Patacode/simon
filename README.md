# Simon

GUI application to play the game __Simon__. This is a memory game in which the player
has to remember each color combination the game generates and try transcribing each
combination to continue to the next level until the player fails.

![Simon logo](/Simon/ressources/simon-pic.jpg)

## Getting started

To run the application, you can either use `make` or `maven` directly.

There are 5 different guidelines:
* exec (to compile and execute the program)
* doc (to generate the javadoc unde `/doc`)
* clean (to clean the project)

The default guideline compiles and executes the program.

For example, to run it using `make` with the current directory pointing to `Simon`:

```
[~/56080-atlg3/Simon] make
```

Or using `maven` directly, in the same directory:

```
[~/56080-atlg3/Simon] mvn javafx:run
```

The application use maven for its management and JavaFx for the graphical interface. Moreover, 
the tools java and javac are needed in order for the project to be compiled and launched.

__Note__: To generate the javadoc you must define and export the path to the `lib/` directory
in which the JavaFx dependencies are installed, called `JFX_PATH`.

