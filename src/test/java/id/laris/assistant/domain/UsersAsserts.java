package id.laris.assistant.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class UsersAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersAllPropertiesEquals(Users expected, Users actual) {
        assertUsersAutoGeneratedPropertiesEquals(expected, actual);
        assertUsersAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersAllUpdatablePropertiesEquals(Users expected, Users actual) {
        assertUsersUpdatableFieldsEquals(expected, actual);
        assertUsersUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersAutoGeneratedPropertiesEquals(Users expected, Users actual) {
        assertThat(expected)
            .as("Verify Users auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersUpdatableFieldsEquals(Users expected, Users actual) {
        assertThat(expected)
            .as("Verify Users relevant properties")
            .satisfies(e -> assertThat(e.getFirstName()).as("check firstName").isEqualTo(actual.getFirstName()))
            .satisfies(e -> assertThat(e.getLastName()).as("check lastName").isEqualTo(actual.getLastName()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersUpdatableRelationshipsEquals(Users expected, Users actual) {
        // empty method
    }
}
