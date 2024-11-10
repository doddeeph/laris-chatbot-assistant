package id.laris.assistant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MoviesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Movies getMoviesSample1() {
        return new Movies().id(1L).title("title1").genres("genres1");
    }

    public static Movies getMoviesSample2() {
        return new Movies().id(2L).title("title2").genres("genres2");
    }

    public static Movies getMoviesRandomSampleGenerator() {
        return new Movies().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).genres(UUID.randomUUID().toString());
    }
}
