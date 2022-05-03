package net.intelie.challenges;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventTest {

    @Test
    public void createProperlyAnEvent() throws Exception {
        Event event = new Event("some_type", 123L);

        assertEquals("some_type", event.type());
        assertEquals(123L, event.timestamp());
    }
}