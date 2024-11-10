package id.laris.assistant.domain;

import static id.laris.assistant.domain.MoviesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import id.laris.assistant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MoviesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Movies.class);
        Movies movies1 = getMoviesSample1();
        Movies movies2 = new Movies();
        assertThat(movies1).isNotEqualTo(movies2);

        movies2.setId(movies1.getId());
        assertThat(movies1).isEqualTo(movies2);

        movies2 = getMoviesSample2();
        assertThat(movies1).isNotEqualTo(movies2);
    }
}
