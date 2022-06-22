package g56080.simon.model;

import g56080.simon.view.ObservableListener;

/**
 * Observer interface implemented by the application model. An observer is designed to observe 
 * changes in the view by receiving notifications from it and to react to these changes.
 */
public interface Observable{

    /**
     * Subscribes the given observable. Each subscribed observable will
     * receive an update notification when calling the {@link #fireChange()} method.
     *
     * @param obs the observale to subscribe
     */
    void subscribe(ObservableListener obs);

    /**
     * Unsubscribes the given observable to disallow it from receiving update notifications.
     *
     * @param obs the observable to unsubscribe
     */
    void unsubscribe(ObservableListener obs);

    /**
     * Sends an update notification to all the subscribed observables previously recorded.
     */
    void fireChange();
}
