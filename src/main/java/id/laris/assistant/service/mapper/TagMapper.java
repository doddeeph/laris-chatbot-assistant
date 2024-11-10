package id.laris.assistant.service.mapper;

import id.laris.assistant.domain.Movies;
import id.laris.assistant.domain.Tags;
import id.laris.assistant.domain.Users;
import id.laris.assistant.service.dto.MovieDTO;
import id.laris.assistant.service.dto.TagDTO;
import id.laris.assistant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tags} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tags> {
    @Mapping(target = "users", source = "users", qualifiedByName = "usersId")
    @Mapping(target = "movies", source = "movies", qualifiedByName = "moviesId")
    TagDTO toDto(Tags s);

    @Named("usersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUsersId(Users users);

    @Named("moviesId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MovieDTO toDtoMoviesId(Movies movies);
}
