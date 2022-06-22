package g56080.simon.view;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Deque;

import javafx.scene.layout.StackPane;

/**
 * StackPane layer gathering all the application layers on which layers can be added and removed. All the methods
 * that allow layers to be added or removed change the composition state of the StackLayer and prevent it to be
 * retrieved (by calling {@link getStack()}) before calling the composition method {@link compose()}.
 */
public class StackLayer{ /* layers will be composed starting from the first added layer */
    
    private final StackPane stack;
    private final Deque<Layer> layers;
    private boolean isComposed;

    /**
     * Creates a new empty StackLayer.
     */
    public StackLayer(){
        layers = new ArrayDeque<>();
        stack = new StackPane();
    }

    /**
     * Creates a new StackLayer with the given layers in it.
     *
     * @param layers the layers to be added to this StackLayer
     */
    public StackLayer(Layer... layers){
        this.layers = new ArrayDeque<>(List.of(layers));
        stack = new StackPane();
    }

    /**
     * Gets the StackPane of this stack layer. If the StackPane hasn't been composed once before calling this
     * method, an IllegalStateException will be thrown.
     *
     * @throws IllegalStateException if this stack hasn't been composed
     * @return the StackPane of this stack layer.
     */
    public StackPane getStack(){
        if(!isComposed)
            throw new IllegalStateException("Hasn't been composed since last modification");

        return stack;
    }

    /**
     * Gets the size of this StackLayer (represented by the number of layers in it).
     *
     * @return the size of this StackLayer.
     */
    public int getSize(){
        return layers.size();
    }

    /**
     * Gets a list view of this StackLayer.
     *
     * @return a list view of this StackLayer.
     */
    public List<Layer> getListView(){
        return layers.stream().toList();
    }

    /**
     * Checks if this StackLayer has been previously composed.
     *
     * @return true if it has been composed, false otherwise.
     */
    public boolean isComposed(){
        return isComposed;
    }

    /**
     * Composes this StackLayer by adding each previously added layers into the StackPane. After calling
     * this method, the stack can be retrieved without an exception by calling {@link getStack()}.
     */
    public void compose(){
        if(!isComposed){
            Deque<Layer> layersClone = new ArrayDeque<>(layers);
            stack.getChildren().clear();

            while(!layersClone.isEmpty()){
                Layer current = layersClone.poll();
                stack.getChildren().add(current.getRoot());
            }

            isComposed = true;
        }
    }

    /**
     * Gets the top layer of this StackLayer. The top layer is the last added layer.
     *
     * @return the optional layer on top of this StackLayer.
     */
    public Optional<Layer> getTopLayer(){ /* The most recent one */
        return Optional.ofNullable(layers.peekLast());
    }

    /**
     * Gets the corresponding layer from this StackLayer represented by the given zero-based index.
     *
     * @param layer the layer index
     * @return the optional layer represented by the given layer index.
     */
    public Optional<Layer> getLayer(int layer){
        Layer res = layer >= layers.size() ? null : getListView().get(layer);
        return Optional.ofNullable(res);
    }

    /**
     * Adds the given layer on top of this StackLayer.
     *
     * @param layer the layer to be added
     */
    public void addLayer(Layer layer){
        layers.offer(layer);
        isComposed = false;
    }

    /**
     * Adds the given layers on top of this StackLayer. The given layers are added one by one by
     * overlapping on top of each one another.
     *
     * @param layers the layers to be added
     */
    public void addLayers(Layer... layers){
        Arrays
            .stream(layers)
            .forEach(layer -> this.layers.offer(layer)); 
        isComposed = false;
    }

    /**
     * Removes the top layer of this StackLayer and returns an optional reference to it.
     *
     * @return the optional top layer of this StackLayer.
     */
    public Optional<Layer> removeTopLayer(){
        isComposed = false;
        return Optional.ofNullable(layers.removeLast());
    }

    /**
     * Removes a given number of layers of this StackLayer starting at the top and returns the last removed 
     * layer if any.
     *
     * @param count the number of layers to be removed
     * @return the optional last removed layer.
     */
    public Optional<Layer> removeLayers(int count){ /* retrieve the last removed layer */
        Layer polled = null;
        while(!layers.isEmpty() && count > 0){
            polled = layers.removeLast();
            count--;
        }

        if(polled != null)
            isComposed = false;
        return Optional.ofNullable(polled);
    }

    /**
     * Clears this StackLayer by removing all the layers it contains.
     */
    public void clear(){
        layers.clear();
        isComposed = false;
    }
}

