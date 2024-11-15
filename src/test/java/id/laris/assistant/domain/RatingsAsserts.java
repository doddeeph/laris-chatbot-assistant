package id.laris.assistant.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class RatingsAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRatingsAllPropertiesEquals(Ratings expected, Ratings actual) {
        assertRatingsAutoGeneratedPropertiesEquals(expected, actual);
        assertRatingsAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRatingsAllUpdatablePropertiesEquals(Ratings expected, Ratings actual) {
        assertRatingsUpdatableFieldsEquals(expected, actual);
        assertRatingsUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRatingsAutoGeneratedPropertiesEquals(Ratings expected, Ratings actual) {
        assertThat(expected)
            .as("Verify Ratings auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRatingsUpdatableFieldsEquals(Ratings expected, Ratings actual) {
        assertThat(expected)
            .as("Verify Ratings relevant properties")
            .satisfies(e -> assertThat(e.getRating()).as("check rating").isEqualTo(actual.getRating()))
            .satisfies(e -> assertThat(e.getTimestamp()).as("check timestamp").isEqualTo(actual.getTimestamp()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRatingsUpdatableRelationshipsEquals(Ratings expected, Ratings actual) {
        assertThat(expected)
            .as("Verify Ratings relationships")
            .satisfies(e -> assertThat(e.getUsers()).as("check users").isEqualTo(actual.getUsers()))
            .satisfies(e -> assertThat(e.getMovies()).as("check movies").isEqualTo(actual.getMovies()));
    }
}
