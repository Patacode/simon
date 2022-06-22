package g56080.simon.view;

import g56080.simon.model.Model;

/**
 * The observable interface implemented by the application view(s).
 */
@FunctionalInterface
public interface ObservableListener{

    /**
     * Updates this observable entity depending on the given model state.
     *
     * @param state the model state when updating the view
     */
    void update(Model.State state);
}
