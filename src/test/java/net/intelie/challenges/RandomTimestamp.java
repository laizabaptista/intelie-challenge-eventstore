package net.intelie.challenges;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTimestamp {
    /* return a random instant in time*/
    public static Instant timestamp() {
        return Instant.ofEpochSecond(ThreadLocalRandom.current().nextInt());
    }
}
