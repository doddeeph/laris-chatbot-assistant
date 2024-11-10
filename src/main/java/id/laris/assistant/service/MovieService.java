package id.laris.assistant.service;

import id.laris.assistant.domain.Movies;
import id.laris.assistant.service.dto.MovieDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Movies}.
 */
public interface MovieService {
    /**
     * Save a movies.
     *
     * @param movieDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MovieDTO> save(MovieDTO movieDTO);

    /**
     * Updates a movies.
     *
     * @param movieDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MovieDTO> update(MovieDTO movieDTO);

    /**
     * Partially updates a movies.
     *
     * @param movieDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MovieDTO> partialUpdate(MovieDTO movieDTO);

    /**
     * Get all the movies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MovieDTO> findAll(Pageable pageable);

    /**
     * Returns the number of movies available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" movies.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MovieDTO> findOne(Long id);

    /**
     * Delete the "id" movies.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
