package id.laris.assistant.service.mapper;

import id.laris.assistant.domain.Movies;
import id.laris.assistant.service.dto.MovieDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Movies} and its DTO {@link MovieDTO}.
 */
@Mapper(componentModel = "spring")
public interface MovieMapper extends EntityMapper<MovieDTO, Movies> {}
