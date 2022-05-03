package net.intelie.challenges;
import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;
import net.intelie.challenges.EventStoreImp;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class EventStoreTest {

    @Test
    public void shouldInsertASingleEvent() throws Exception {
        EventStoreImp store = new EventStoreImp();
        Event event = new Event("some_type", 123L);
        store.insert(event);
        store.printEvents();

        //THIS IS A SIMPLE INSERTION:
        //Testing an insertion of one single event
        assertEquals(1, store.getSize());
    }

   @Test
    public void shouldRemoveAllEventsWhenThereIsASingleEvent() throws Exception {
        EventStoreImp store = new EventStoreImp();
        Event event = new Event("some_type", 123L);
        store.insert(event);
        store.printEvents();

        //THIS IS A SIMPLE DELETION:
        //Testing an insertion and deletion of one single event
        assertEquals(1, store.getSize());

        store.removeAll("some_type");
        store.printEvents();

        assertEquals(0, store.getSize());
    }

    private EventStoreImp preFilledConcurrentlyAStore(int numberOfThreads, String [] types) throws InterruptedException{
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * types.length);

        EventStoreImp store = new EventStoreImp();
        for (String type : types)
            for (int i = 0; i < numberOfThreads; i++) {
                service.submit(() -> {
                    Instant random = RandomTimestamp.timestamp();
                    Event event = new Event(type, random.toEpochMilli());
                    store.insert(event);

                    latch.countDown();
                });
            }

        latch.await();
        store.printEvents();
        return store;
    }

       @Test
    public void shouldInsertSeveralEventsConcurrently() throws Exception {
        int numberOfThreads = 8;
        String[] types = {"type_1","type_2","type_3"};
        EventStoreImp store = preFilledConcurrentlyAStore(numberOfThreads, types);
        //Testing an insertion of several events and verifying if all threads were executed properly for each type.
        assertEquals(numberOfThreads * types.length, store.getSize());
        for (String type : types)
            assertEquals(numberOfThreads, store.getSize(type));
    }

   @Test
    public void shouldRemoveAllEventsOfEachType() throws Exception {
        int numberOfThreads = 10;
        String[] types = {"type_a","type_b","type_c"};
        EventStoreImp store = preFilledConcurrentlyAStore(numberOfThreads, types);
        //Testing a deletion of several events and verifying if all threads were
        // executed properly for each type.
        store.removeAll("type_a");
        assertEquals(20, store.getSize());
        assertEquals(0, store.getSize("type_a"));
        store.printEvents();
        store.removeAll("type_b");
        assertEquals(10, store.getSize());
        assertEquals(0, store.getSize("type_b"));
        store.printEvents();
        store.removeAll("type_c");
        assertEquals(0, store.getSize());
        assertEquals(0, store.getSize("type_c"));
        store.printEvents();
    }
  
    @Test
    public void shouldNotFailWhenRemovingAllEventsOfAnAbsentType() throws Exception {
        EventStoreImp store = new EventStoreImp();
        //Testing a deletion of some type that doesn't exists.
        store.removeAll("type_a");
        assertEquals(0, store.getSize("type_a"));
        assertEquals(0, store.getSize());

        store.printEvents();
    }

    private EventStoreImp preFilledSimpleStore(){
        EventStoreImp store = new EventStoreImp();
        for(int i = 0 ; i < 20 ; i+=5) {
            Event event = new Event("type_a", i);
            System.out.print("Insertion of: ");
            System.out.println(event);
            store.insert(event);
        }
        for(int i = 1 ; i < 20 ; i+=3) {
            Event event = new Event("type_a", i);
            System.out.print("Insertion of: ");
            System.out.println(event);
            store.insert(event);
        }
        store.printEvents();
        return store;
    }

    @Test
    public void shouldQueryEventsByType() throws Exception {
        EventStoreImp store = preFilledSimpleStore();

        EventIterator iterator = store.query("type_a", 9,15);

        int count = 0;
        while(iterator.moveNext()) {
            Event event = iterator.current();
            System.out.println("> " + event.timestamp());
            count++;
        }
        System.out.println("_________________________________");


        assertEquals(11, store.getSize());
        assertEquals(3, count);


        iterator = store.query("type_a", 0,10);
        count = 0;
        while(iterator.moveNext()) count++;
        assertEquals(5, count);

        iterator = store.query("type_a", 11,20);
        count = 0;
        while(iterator.moveNext()) count++;
        assertEquals(4, count);

    }

    @Test
    public void shouldReturnAnEmptyIteratorWhenQueryingForAnAbsentType() throws Exception {
        EventStoreImp store = preFilledSimpleStore();
        EventIterator iterator = store.query("type_b", 9,15);

        int count = 0;
        while(iterator.moveNext()) {
            Event event = iterator.current();
            System.out.println("> " + event.timestamp());
            count++;
        }
        System.out.println("_________________________________");


        assertEquals(11, store.getSize());
        assertEquals(0, count);

    }

    @Test
    public void returnAnEmptyIteratorWhenQueryingSameStartAndEndTime() throws Exception {
        EventStoreImp store = preFilledSimpleStore();
        EventIterator iterator = store.query("type_a", 10,10);

        int count = 0;
        while(iterator.moveNext()) {
            Event event = iterator.current();
            System.out.println("> " + event.timestamp());
            count++;
        }
        System.out.println("_________________________________");


        assertEquals(11, store.getSize());
        assertEquals(0, count);
    }

    @Test
    public void shouldReturnAnEmptyIteratorWhenQueryingOutOfBound() throws Exception {
        EventStoreImp store = preFilledSimpleStore();
        EventIterator iterator = store.query("type_a", 100,150);

        int count = 0;
        while(iterator.moveNext()) {
            Event event = iterator.current();
            System.out.println("> " + event.timestamp());
            count++;
        }
        System.out.println("_________________________________");


        assertEquals(11, store.getSize());
        assertEquals(0, count);
    }
}