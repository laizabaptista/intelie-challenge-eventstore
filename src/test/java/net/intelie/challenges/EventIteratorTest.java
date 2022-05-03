package net.intelie.challenges;

import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;
import net.intelie.challenges.EventIteratorImp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventIteratorTest {

    private List<Event> basicList (){
        List<Event> events = new ArrayList<>();

        for(int i = 0; i < 10; i++)
            events.add(new Event("type_a", i*2));

        return events;
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowsIllegalStateExceptionWhenGetCurrentOfANullEventList() throws Exception {
        EventIterator iterator = new EventIteratorImp(null);
        iterator.current();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowsIllegalStateExceptionWhenGetCurrentOfAnEmptyEventList() throws Exception {
        EventIterator iterator = new EventIteratorImp(new ArrayList<>());
        iterator.current();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowsIllegalStateExceptionWhenGetCurrentOfHadNotMovedList() throws Exception {
        EventIterator iterator = new EventIteratorImp(basicList());
        iterator.current();
    }

    @Test
    public void shouldMoveToNextElementIfItExists() throws Exception {
        EventIterator iterator = new EventIteratorImp(basicList());
        iterator.moveNext();
        assertEquals(0, iterator.current().timestamp());
        iterator.moveNext();
        assertEquals(2, iterator.current().timestamp());
    }

    @Test
    public void shouldRemoveTheThirdElementOfAnEventList() throws Exception {
        List<Event> events = basicList();
        EventIterator iterator = new EventIteratorImp(events);

        assertEquals(10, events.size());
        assertEquals(4, events.get(2).timestamp());
        iterator.moveNext();
        iterator.moveNext();
        iterator.moveNext();
        iterator.remove();
        assertEquals(9, events.size());
        assertEquals(6, events.get(2).timestamp());
    }

    @Test
    public void shouldRemoveTheFirstElementOfAnEventList() throws Exception {
        List<Event> events = basicList();
        EventIterator iterator = new EventIteratorImp(events);

        assertEquals(10, events.size());
        assertEquals(4, events.get(2).timestamp());
        iterator.moveNext();
        iterator.moveNext();
        iterator.moveNext();
        iterator.remove();
        assertEquals(9, events.size());
        assertEquals(6, events.get(2).timestamp());
    }

    @Test
    public void shouldRemoveAllEventsOfAnEventList() throws Exception {
        List<Event> events = basicList();
        EventIterator iterator = new EventIteratorImp(events);

        assertEquals(10, events.size());
        while(iterator.moveNext())
            iterator.remove();
        assertEquals(0, events.size());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowsIllegalStateExceptionWhenRemoveEventWithoutPreviousMoveNext() throws Exception {
        List<Event> events = basicList();
        EventIterator iterator = new EventIteratorImp(events);

        iterator.remove();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowsIllegalStateExceptionWhenRemoveEventAfterGoThroughEntireList() throws Exception {
        List<Event> events = basicList();
        EventIterator iterator = new EventIteratorImp(events);

        while(iterator.moveNext());
        iterator.remove();
    }
}
