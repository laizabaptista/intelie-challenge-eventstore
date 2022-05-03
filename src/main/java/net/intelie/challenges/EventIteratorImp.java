package net.intelie.challenges;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/*An implementation of a EventIterator for an in memory EventStore using Lists*/

public class EventIteratorImp implements EventIterator {
    private List<Event> list;
    private AtomicInteger position = new AtomicInteger(-1);
    private AtomicBoolean movedNext = new AtomicBoolean(true);

    public EventIteratorImp(List<Event> list) {
        this.list = list;
    }

    private boolean hasNext(){
        if(list == null)
            return false;
        return list.size() > (position.get() + 1);
    }

    /**
     * Move the iterator to the next event, if any.
     *
     * @return false if the iterator has reached the end, true otherwise.
     */
    @Override
    public boolean moveNext() {
        if(hasNext()) {
            position.incrementAndGet();
            return true;
        }
        movedNext.set(false);
        return false;
    }

    /**
     * Gets the current event ref'd by this iterator.
     *
     * @return the event itself.
     * @throws IllegalStateException if {@link #moveNext} was never called
     *                               or its last result was {@code false}.
     */
    @Override
    public Event current() {
        if (position.get() >= 0 && movedNext.get())
            return list.get(position.get());
        throw new IllegalStateException();
    }

    /**
     * Remove current event from its store.
     *
     * @throws IllegalStateException if {@link #moveNext} was never called
     *                               or its last result was {@code false}.
     */
    @Override
    public void remove() {
        if (position.get() >= 0 && movedNext.get()) {
            list.remove(position.get());
            position.decrementAndGet();
        }
        else
            throw new IllegalStateException();
    }

    @Override
    public void close() throws Exception {

    }
}
