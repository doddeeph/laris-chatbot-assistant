package id.laris.assistant.service.mapper;

import static id.laris.assistant.domain.UsersAsserts.*;
import static id.laris.assistant.domain.UsersTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsersMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUsersSample1();
        var actual = userMapper.toEntity(userMapper.toDto(expected));
        assertUsersAllPropertiesEquals(expected, actual);
    }
}
