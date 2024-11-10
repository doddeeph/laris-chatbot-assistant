package id.laris.assistant.domain;

import static id.laris.assistant.domain.MoviesTestSamples.*;
import static id.laris.assistant.domain.TagsTestSamples.*;
import static id.laris.assistant.domain.UsersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import id.laris.assistant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TagsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tags.class);
        Tags tags1 = getTagsSample1();
        Tags tags2 = new Tags();
        assertThat(tags1).isNotEqualTo(tags2);

        tags2.setId(tags1.getId());
        assertThat(tags1).isEqualTo(tags2);

        tags2 = getTagsSample2();
        assertThat(tags1).isNotEqualTo(tags2);
    }

    @Test
    void usersTest() {
        Tags tags = getTagsRandomSampleGenerator();
        Users usersBack = getUsersRandomSampleGenerator();

        tags.setUsers(usersBack);
        assertThat(tags.getUsers()).isEqualTo(usersBack);

        tags.users(null);
        assertThat(tags.getUsers()).isNull();
    }

    @Test
    void moviesTest() {
        Tags tags = getTagsRandomSampleGenerator();
        Movies moviesBack = getMoviesRandomSampleGenerator();

        tags.setMovies(moviesBack);
        assertThat(tags.getMovies()).isEqualTo(moviesBack);

        tags.movies(null);
        assertThat(tags.getMovies()).isNull();
    }
}
