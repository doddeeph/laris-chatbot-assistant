package id.laris.assistant.service.mapper;

import static id.laris.assistant.domain.MoviesAsserts.*;
import static id.laris.assistant.domain.MoviesTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MoviesMapperTest {

    private MovieMapper movieMapper;

    @BeforeEach
    void setUp() {
        movieMapper = new MovieMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMoviesSample1();
        var actual = movieMapper.toEntity(movieMapper.toDto(expected));
        assertMoviesAllPropertiesEquals(expected, actual);
    }
}
