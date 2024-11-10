package id.laris.assistant.web.rest;

import id.laris.assistant.domain.Movies;
import id.laris.assistant.repository.MovieRepository;
import id.laris.assistant.service.MovieService;
import id.laris.assistant.service.dto.MovieDTO;
import id.laris.assistant.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link Movies}.
 */
@RestController
@RequestMapping("/api/public/movies")
public class MovieResource {

    private static final Logger LOG = LoggerFactory.getLogger(MovieResource.class);

    private static final String ENTITY_NAME = "larisChatbotAssistantMovies";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MovieService movieService;

    private final MovieRepository movieRepository;

    public MovieResource(MovieService movieService, MovieRepository movieRepository) {
        this.movieService = movieService;
        this.movieRepository = movieRepository;
    }

    /**
     * {@code POST  /movies} : Create a new movies.
     *
     * @param movieDTO the moviesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new moviesDTO, or with status {@code 400 (Bad Request)} if the movies has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MovieDTO>> createMovies(@Valid @RequestBody MovieDTO movieDTO) throws URISyntaxException {
        LOG.debug("REST request to save Movies : {}", movieDTO);
        if (movieDTO.getId() != null) {
            throw new BadRequestAlertException("A new movies cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return movieService
            .save(movieDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/movies/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /movies/:id} : Updates an existing movies.
     *
     * @param id the id of the moviesDTO to save.
     * @param movieDTO the moviesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated moviesDTO,
     * or with status {@code 400 (Bad Request)} if the moviesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the moviesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MovieDTO>> updateMovies(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MovieDTO movieDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Movies : {}, {}", id, movieDTO);
        if (movieDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, movieDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return movieRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return movieService
                    .update(movieDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /movies/:id} : Partial updates given fields of an existing movies, field will ignore if it is null
     *
     * @param id the id of the moviesDTO to save.
     * @param movieDTO the moviesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated moviesDTO,
     * or with status {@code 400 (Bad Request)} if the moviesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the moviesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the moviesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MovieDTO>> partialUpdateMovies(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MovieDTO movieDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Movies partially : {}, {}", id, movieDTO);
        if (movieDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, movieDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return movieRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MovieDTO> result = movieService.partialUpdate(movieDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /movies} : get all the movies.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of movies in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MovieDTO>>> getAllMovies(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Movies");
        return movieService
            .countAll()
            .zipWith(movieService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /movies/:id} : get the "id" movies.
     *
     * @param id the id of the moviesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the moviesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MovieDTO>> getMovies(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Movies : {}", id);
        Mono<MovieDTO> moviesDTO = movieService.findOne(id);
        return ResponseUtil.wrapOrNotFound(moviesDTO);
    }

    /**
     * {@code DELETE  /movies/:id} : delete the "id" movies.
     *
     * @param id the id of the moviesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMovies(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Movies : {}", id);
        return movieService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
