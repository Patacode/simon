package g56080.simon.view;

import javafx.scene.paint.Color;

/**
 * The enumeration color for the game buttons.
 */
public enum ButtonColor{

    /**
     * The red color.
     */
    RED(Color.RED, Color.RED.darker()), 

    /**
     * The green color.
     */
    GREEN(Color.GREEN, Color.GREEN.darker()), 

    /**
     * The yellow color.
     */
    YELLOW(Color.YELLOW, Color.YELLOW.darker()), 

    /**
     * The blue color.
     */
    BLUE(Color.BLUE, Color.BLUE.darker());

    private final Color color, altColor;

    private ButtonColor(Color color, Color altColor){
        this.color = color;
        this.altColor = altColor;
    }

    /**
     * Gets the RGB color of the invoking litteral.
     *
     * @return the RGB color associated to the invoking litteral.
     */
    public Color getValue(){
        return color;
    }

    /**
     * Gets the alternative RGB color of the invoking litteral.
     *
     * @return the alternative RGB color associated to the invoking litteral.
     */
    public Color getAltValue(){
        return altColor;
    }

    /**
     * Gets the ButtonColor litteral associated to the given color or null if no litteral
     * matched the color.
     *
     * @param color the RGB color
     * @return the ButtonColor litteral that matched the given color or null if no litteral has been found.
     */
    public static ButtonColor valueOf(Color color){
        ButtonColor res = null;
        ButtonColor[] values = ButtonColor.values();
        for(ButtonColor btnColor : values){
            if(btnColor.getValue().toString().equals(color.toString()))
                res = btnColor;
        }

        return res;
    }
}

