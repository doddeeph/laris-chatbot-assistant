package id.laris.assistant.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class TagsAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTagsAllPropertiesEquals(Tags expected, Tags actual) {
        assertTagsAutoGeneratedPropertiesEquals(expected, actual);
        assertTagsAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTagsAllUpdatablePropertiesEquals(Tags expected, Tags actual) {
        assertTagsUpdatableFieldsEquals(expected, actual);
        assertTagsUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTagsAutoGeneratedPropertiesEquals(Tags expected, Tags actual) {
        assertThat(expected)
            .as("Verify Tags auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTagsUpdatableFieldsEquals(Tags expected, Tags actual) {
        assertThat(expected)
            .as("Verify Tags relevant properties")
            .satisfies(e -> assertThat(e.getTag()).as("check tag").isEqualTo(actual.getTag()))
            .satisfies(e -> assertThat(e.getTimestamp()).as("check timestamp").isEqualTo(actual.getTimestamp()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTagsUpdatableRelationshipsEquals(Tags expected, Tags actual) {
        assertThat(expected)
            .as("Verify Tags relationships")
            .satisfies(e -> assertThat(e.getUsers()).as("check users").isEqualTo(actual.getUsers()))
            .satisfies(e -> assertThat(e.getMovies()).as("check movies").isEqualTo(actual.getMovies()));
    }
}