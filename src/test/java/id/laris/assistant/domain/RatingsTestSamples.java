package id.laris.assistant.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RatingsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Ratings getRatingsSample1() {
        return new Ratings().id(1L).timestamp(1L);
    }

    public static Ratings getRatingsSample2() {
        return new Ratings().id(2L).timestamp(2L);
    }

    public static Ratings getRatingsRandomSampleGenerator() {
        return new Ratings().id(longCount.incrementAndGet()).timestamp(longCount.incrementAndGet());
    }
}
