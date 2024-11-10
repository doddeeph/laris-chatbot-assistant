package id.laris.assistant.service.mapper;

import id.laris.assistant.domain.Movies;
import id.laris.assistant.domain.Ratings;
import id.laris.assistant.domain.Users;
import id.laris.assistant.service.dto.MovieDTO;
import id.laris.assistant.service.dto.RatingDTO;
import id.laris.assistant.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ratings} and its DTO {@link RatingDTO}.
 */
@Mapper(componentModel = "spring")
public interface RatingMapper extends EntityMapper<RatingDTO, Ratings> {
    @Mapping(target = "users", source = "users", qualifiedByName = "usersId")
    @Mapping(target = "movies", source = "movies", qualifiedByName = "moviesId")
    RatingDTO toDto(Ratings s);

    @Named("usersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUsersId(Users users);

    @Named("moviesId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MovieDTO toDtoMoviesId(Movies movies);
}
