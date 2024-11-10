package id.laris.assistant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TagsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Tags getTagsSample1() {
        return new Tags().id(1L).tag("tag1").timestamp(1L);
    }

    public static Tags getTagsSample2() {
        return new Tags().id(2L).tag("tag2").timestamp(2L);
    }

    public static Tags getTagsRandomSampleGenerator() {
        return new Tags().id(longCount.incrementAndGet()).tag(UUID.randomUUID().toString()).timestamp(longCount.incrementAndGet());
    }
}
