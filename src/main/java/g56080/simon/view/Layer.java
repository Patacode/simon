package g56080.simon.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.collections.ObservableList;

/**
 * A traversable application layer represented by a pane (LayoutManager) on which child 
 * nodes can be added. 
 */
public class Layer{
    
    private final Pane root;

    /**
     * Creates a new Layer using the given root pane.
     *
     * @param root the layout manager
     */
    public Layer(Pane root){
        this.root = root;
    }

    /**
     * Gets the root pane of this Layer.
     *
     * @return the root pane of this layer.
     */
    public Pane getRoot(){
        return root;
    }

    /**
     * Gets the list of all the children contained in this layer (without the root pane itself).
     *
     * @return the list of child nodes contained in this layer.
     */
    public List<Node> getChildren(){
        List<Node> acc = new ArrayList<>();
        browseChildren(root, acc);
        return acc;
    }

    /**
     * Gets the list of all the children contained in this layer that matched the given 
     * predicate (without the root pane itself).
     *
     * @param predicate the predicate to apply
     * @return the list of child nodes contained in this layer.
     */
    public List<Node> getChildren(Predicate<Node> predicate){
        List<Node> acc = new ArrayList<>();
        browseChildren(root, acc, predicate);
        return acc;
    }

    /**
     * Adds the given child node to this layer root pane.
     *
     * @param child the child node to add
     */
    public void addChild(Node child){
        root.getChildren().add(child);
    }

    /**
     * Adds the given child nodes to this layer root pane.
     *
     * @param children the child nodes to add
     */
    public void addChildren(Node... children){
        root.getChildren().addAll(children);
    }


    private void browseChildren(Pane root, List<Node> acc){
        root.getChildren().stream().forEach(child -> {
            acc.add(child);
            if(child instanceof Pane){
                browseChildren((Pane) child, acc);
            }
        });
    }

    private void browseChildren(Pane root, List<Node> acc, Predicate<Node> predicate){
        root.getChildren().stream().forEach(child -> {
            if(predicate.test(child))
                acc.add(child);
            if(child instanceof Pane){
                browseChildren((Pane) child, acc, predicate);
            }
        });
    }
}

