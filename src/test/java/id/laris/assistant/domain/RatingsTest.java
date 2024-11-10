package id.laris.assistant.domain;

import static id.laris.assistant.domain.MoviesTestSamples.*;
import static id.laris.assistant.domain.RatingsTestSamples.*;
import static id.laris.assistant.domain.UsersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import id.laris.assistant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RatingsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ratings.class);
        Ratings ratings1 = getRatingsSample1();
        Ratings ratings2 = new Ratings();
        assertThat(ratings1).isNotEqualTo(ratings2);

        ratings2.setId(ratings1.getId());
        assertThat(ratings1).isEqualTo(ratings2);

        ratings2 = getRatingsSample2();
        assertThat(ratings1).isNotEqualTo(ratings2);
    }

    @Test
    void usersTest() {
        Ratings ratings = getRatingsRandomSampleGenerator();
        Users usersBack = getUsersRandomSampleGenerator();

        ratings.setUsers(usersBack);
        assertThat(ratings.getUsers()).isEqualTo(usersBack);

        ratings.users(null);
        assertThat(ratings.getUsers()).isNull();
    }

    @Test
    void moviesTest() {
        Ratings ratings = getRatingsRandomSampleGenerator();
        Movies moviesBack = getMoviesRandomSampleGenerator();

        ratings.setMovies(moviesBack);
        assertThat(ratings.getMovies()).isEqualTo(moviesBack);

        ratings.movies(null);
        assertThat(ratings.getMovies()).isNull();
    }
}
