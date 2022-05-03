package net.intelie.challenges;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/*An implementation EventStore*/
public class EventStoreImp implements EventStore {

    private ConcurrentHashMap<String, List<Event>> events;
    private AtomicInteger size = new AtomicInteger(0);

    public EventStoreImp(){
        events = new ConcurrentHashMap<>();
    }

    public int getSize() {
        return size.get();
    }

    public int getSize(String type) {
        List<Event> theEvents = events.get(type);
        return  theEvents == null ? 0 : theEvents.size();
    }

    public void printEvents(){
        events.forEach((eventType, theEvents) -> {
            theEvents.forEach((event) -> {
                System.out.println(event);
            });
        });
    }

    /* Stores an event*/
    @Override
    public void insert(Event event) {
        //SYNCHRONIZED usage in order to prevent multiple access to
        //the test if the event type is not registered yet
        synchronized (events) {
            if(!events.containsKey(event.type()))
            events.put(event.type(), Collections.synchronizedList(new ArrayList()));
        }
        events.get(event.type()).add(event);
        size.incrementAndGet();
    }

    /* Removes all events of specific type*/
    @Override
    public void removeAll(String type) {
        synchronized (events) {
            if(events.containsKey(type)) {
                size.addAndGet(events.get(type).size() * -1);
                events.get(type).clear();
            }
        }
    }

    /**
     * Retrieves an iterator for events based on their type and timestamp.
     *
     * @param type      The type we are querying for.
     * @param startTime Start timestamp (inclusive).
     * @param endTime   End timestamp (exclusive).
     * @return An iterator where all its events have same type as
     * {@param type} and timestamp between {@param startTime}
     * (inclusive) and {@param endTime} (exclusive).
     */
    @Override
    public EventIterator query(String type, long startTime, long endTime) {
        List<Event> theEvents = null;

        if(events.containsKey(type))
            theEvents = events.get(type)
                    .stream()
                    .filter(e -> e.timestamp() >= startTime && e.timestamp() < endTime)
                    .collect(Collectors.toList());

        return new EventIteratorImp(theEvents);
    }
}
