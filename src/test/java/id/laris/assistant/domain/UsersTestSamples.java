package id.laris.assistant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UsersTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Users getUsersSample1() {
        return new Users().id(1L).firstName("firstName1").lastName("lastName1");
    }

    public static Users getUsersSample2() {
        return new Users().id(2L).firstName("firstName2").lastName("lastName2");
    }

    public static Users getUsersRandomSampleGenerator() {
        return new Users().id(longCount.incrementAndGet()).firstName(UUID.randomUUID().toString()).lastName(UUID.randomUUID().toString());
    }
}
