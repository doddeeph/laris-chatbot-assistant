package id.laris.assistant.service.mapper;

import static id.laris.assistant.domain.TagsAsserts.*;
import static id.laris.assistant.domain.TagsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TagsMapperTest {

    private TagMapper tagMapper;

    @BeforeEach
    void setUp() {
        tagMapper = new TagMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTagsSample1();
        var actual = tagMapper.toEntity(tagMapper.toDto(expected));
        assertTagsAllPropertiesEquals(expected, actual);
    }
}
