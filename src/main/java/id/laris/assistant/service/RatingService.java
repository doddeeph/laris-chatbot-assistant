package id.laris.assistant.service;

import id.laris.assistant.domain.Ratings;
import id.laris.assistant.service.dto.RatingDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Ratings}.
 */
public interface RatingService {
    /**
     * Save a ratings.
     *
     * @param ratingDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RatingDTO> save(RatingDTO ratingDTO);

    /**
     * Updates a ratings.
     *
     * @param ratingDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RatingDTO> update(RatingDTO ratingDTO);

    /**
     * Partially updates a ratings.
     *
     * @param ratingDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RatingDTO> partialUpdate(RatingDTO ratingDTO);

    /**
     * Get all the ratings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RatingDTO> findAll(Pageable pageable);

    /**
     * Returns the number of ratings available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" ratings.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RatingDTO> findOne(Long id);

    /**
     * Delete the "id" ratings.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
