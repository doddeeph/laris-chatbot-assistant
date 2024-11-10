package id.laris.assistant.service.mapper;

import static id.laris.assistant.domain.RatingsAsserts.*;
import static id.laris.assistant.domain.RatingsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RatingsMapperTest {

    private RatingMapper ratingMapper;

    @BeforeEach
    void setUp() {
        ratingMapper = new RatingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRatingsSample1();
        var actual = ratingMapper.toEntity(ratingMapper.toDto(expected));
        assertRatingsAllPropertiesEquals(expected, actual);
    }
}
