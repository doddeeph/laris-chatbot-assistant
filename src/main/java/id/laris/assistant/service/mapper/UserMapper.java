package id.laris.assistant.service.mapper;

import id.laris.assistant.domain.Users;
import id.laris.assistant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Users} and its DTO {@link UserDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDTO, Users> {}
