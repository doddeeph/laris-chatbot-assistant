package id.laris.assistant.service;

import id.laris.assistant.domain.Tags;
import id.laris.assistant.service.dto.TagDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Tags}.
 */
public interface TagService {
    /**
     * Save a tags.
     *
     * @param tagDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TagDTO> save(TagDTO tagDTO);

    /**
     * Updates a tags.
     *
     * @param tagDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TagDTO> update(TagDTO tagDTO);

    /**
     * Partially updates a tags.
     *
     * @param tagDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TagDTO> partialUpdate(TagDTO tagDTO);

    /**
     * Get all the tags.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TagDTO> findAll(Pageable pageable);

    /**
     * Returns the number of tags available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" tags.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TagDTO> findOne(Long id);

    /**
     * Delete the "id" tags.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
