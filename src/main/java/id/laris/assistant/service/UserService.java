package id.laris.assistant.service;

import id.laris.assistant.domain.Users;
import id.laris.assistant.service.dto.UserDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Users}.
 */
public interface UserService {
    /**
     * Save a users.
     *
     * @param userDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<UserDTO> save(UserDTO userDTO);

    /**
     * Updates a users.
     *
     * @param userDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<UserDTO> update(UserDTO userDTO);

    /**
     * Partially updates a users.
     *
     * @param userDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<UserDTO> partialUpdate(UserDTO userDTO);

    /**
     * Get all the users.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<UserDTO> findAll(Pageable pageable);

    /**
     * Returns the number of users available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" users.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<UserDTO> findOne(Long id);

    /**
     * Delete the "id" users.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
