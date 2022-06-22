package g56080.simon.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * The event manager gathering all the nodes on which events are registered and allowing to add or remove events on them.
 */
public class EventManager{

    /**
     * Hashable wrapper type for EventType.
     *
     * @param <E> the event type
     */
    public static class WrappedEventType<E extends Event>{
        
        private final EventType<E> evType;

        /**
         * Creates a new WrappedEventType for the given event type.
         *
         * @param evType the event type to wrap
         */
        public WrappedEventType(EventType<E> evType){
            this.evType = evType;
        }

        @Override
        public int hashCode(){
            return evType.getName().hashCode();
        }

        /**
         * Gets the wrapped event type.
         *
         * @return the wrapped event type.
         */
        public EventType<E> getEventType(){
            return evType;
        }
    }
    
    private final Map<String, Node> targets;
    private final Map<String, Map<WrappedEventType<?>, EventHandler<?>>> events;

    /**
     * Creates a new empty EventManager with no target nodes recorded.
     */
    public EventManager(){
        targets = new HashMap<>();
        events  = new HashMap<>(); 
    }

    /**
     * Creates a new EventManager using the given targets. Each target is represented by a string id
     * and the node linked to it.
     *
     * @param targets the event targets
     */
    public EventManager(Map.Entry<String, Node>... targets){
        this.targets = new HashMap<>();
        events = new HashMap<>();
        addTargets(targets);
    }

    /**
     * Gets the optional target node linked to the given string id.
     *
     * @param id the node id
     * @return the optional node linked to the given id.
     */
    public Optional<Node> getTarget(String id){
        return Optional.ofNullable(targets.get(id));
    }

    /**
     * Adds the given target to this EventManager. This method is a convenient 
     * alternative to {@link #addTarget(Map.Entry)} method. If a node is already
     * linked to the given id, this new node will not be added.
     *
     * @param id the target id
     * @param target the target node
     */
    public void addTarget(String id, Node target){
        addTarget(Map.entry(id, target));
    }
    
    /**
     * Adds the given target entry to this EventManager. If a node is already
     * linked to the given id, this new node will not be added.
     *
     * @param target the event target
     */
    public void addTarget(Map.Entry<String, Node> target){
        targets.putIfAbsent(target.getKey().toLowerCase(), target.getValue());
        events.putIfAbsent(target.getKey().toLowerCase(), new HashMap<>());
    }

    /**
     * Adds all the given targets to this EventManager. If a node is already
     * linked to the currently processed target id, this new node will not be added.
     *
     * @param targets the event targets
     */
    public void addTargets(Map.Entry<String, Node>... targets){
        for(Map.Entry<String, Node> target : targets){
            this.targets.putIfAbsent(target.getKey().toLowerCase(), target.getValue());
            events.putIfAbsent(target.getKey().toLowerCase(), new HashMap<>());
        } 
    }

    /**
     * Add an event filter to the node linked to the given id if it exists. The event filter is represented by an event type
     * and an event handler describing the action to perform when the event occurs on the targeted node.
     *
     * @param <T> the event type
     * @param id the target id
     * @param evType the event type 
     * @param evHandler the event handler
     *
     * @return true if the event filter has been correctly added, false otherwise.
     */
    /* Occurs during capturing phase: when the event go down the target node */
    public <T extends Event> boolean addEventFilter(String id, EventType<T> evType, EventHandler<T> evHandler){
        id = id.toLowerCase();
        if(!targets.containsKey(id))
            return false;

        events.get(id).put(new WrappedEventType<T>(evType), evHandler);
        targets.get(id).addEventFilter(evType, evHandler);
        return true;
    }

    /**
     * Add an event handler to the node linked to the given id if it exists. The event handler is represented by an event type
     * and an event handler describing the action to perform when the event occurs on the targeted node.
     *
     * @param <T> the event type
     * @param id the target id
     * @param evType the event type 
     * @param evHandler the event handler
     *
     * @return true if the event handler has been correctly added, false otherwise.
     */
    /* Occurs during bubbling phase: when the event returns up to the root node */
    public <T extends Event> boolean addEventHandler(String id, EventType<T> evType, EventHandler<T> evHandler){
        id = id.toLowerCase();
        if(!targets.containsKey(id))
            return false;

        events.get(id).put(new WrappedEventType<T>(evType), evHandler);
        targets.get(id).addEventHandler(evType, evHandler);
        return true;
    }

    /**
     * Removes the events from the node linked to the given id if it exists.
     *
     * @param <T> the event type of events to remove
     * @param id the target id
     */
    public <T extends Event> void removeEventsFrom(String id){
        id = id.toLowerCase();
        Node target = targets.get(id);
        if(target != null){
            Map<WrappedEventType<?>, EventHandler<?>> map = events.get(id);
            for(Map.Entry<WrappedEventType<?>, EventHandler<?>> entry : map.entrySet()){
                target.removeEventHandler((EventType<T>) entry.getKey().getEventType(), (EventHandler<T>) entry.getValue());
                target.removeEventFilter((EventType<T>) entry.getKey().getEventType(), (EventHandler<T>) entry.getValue());
            }
        }
    }

    /**
     * Removes the target node linked to the given id if it exists and returns and optional reference to it.
     *
     * @param <T> the event type of events to remove from the target
     * @param id the target id
     * @return the optional node linked to the given id
     */
    public <T extends Event> Optional<Node> removeTarget(String id){
        id = id.toLowerCase();
        Node target = targets.get(id);
        if(target != null){
            Map<WrappedEventType<?>, EventHandler<?>> map = events.get(id);
            for(Map.Entry<WrappedEventType<?>, EventHandler<?>> entry : map.entrySet()){
                target.removeEventHandler((EventType<T>) entry.getKey().getEventType(), (EventHandler<T>) entry.getValue());
                target.removeEventFilter((EventType<T>) entry.getKey().getEventType(), (EventHandler<T>) entry.getValue());
            }
        }

        events.remove(id);
        return Optional.ofNullable(targets.remove(id));
    }

    /**
     * Clears this EventManager by removing all the targeted nodes and events linked to them.
     */
    public void clear(){
        events.clear();
        targets.clear();
    }
}

