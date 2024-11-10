package id.laris.assistant.web.rest;

import id.laris.assistant.domain.Ratings;
import id.laris.assistant.repository.RatingRepository;
import id.laris.assistant.service.RatingService;
import id.laris.assistant.service.dto.RatingDTO;
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
 * REST controller for managing {@link Ratings}.
 */
@RestController
@RequestMapping("/api/public/movie-ratings")
public class RatingResource {

    private static final Logger LOG = LoggerFactory.getLogger(RatingResource.class);

    private static final String ENTITY_NAME = "larisChatbotAssistantRatings";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RatingService ratingService;

    private final RatingRepository ratingRepository;

    public RatingResource(RatingService ratingService, RatingRepository ratingRepository) {
        this.ratingService = ratingService;
        this.ratingRepository = ratingRepository;
    }

    /**
     * {@code POST  /ratings} : Create a new ratings.
     *
     * @param ratingDTO the ratingsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ratingsDTO, or with status {@code 400 (Bad Request)} if the ratings has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RatingDTO>> createRatings(@Valid @RequestBody RatingDTO ratingDTO) throws URISyntaxException {
        LOG.debug("REST request to save Ratings : {}", ratingDTO);
        if (ratingDTO.getId() != null) {
            throw new BadRequestAlertException("A new ratings cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ratingService
            .save(ratingDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/ratings/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /ratings/:id} : Updates an existing ratings.
     *
     * @param id the id of the ratingsDTO to save.
     * @param ratingDTO the ratingsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ratingsDTO,
     * or with status {@code 400 (Bad Request)} if the ratingsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ratingsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RatingDTO>> updateRatings(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RatingDTO ratingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Ratings : {}, {}", id, ratingDTO);
        if (ratingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ratingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ratingRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return ratingService
                    .update(ratingDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /ratings/:id} : Partial updates given fields of an existing ratings, field will ignore if it is null
     *
     * @param id the id of the ratingsDTO to save.
     * @param ratingDTO the ratingsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ratingsDTO,
     * or with status {@code 400 (Bad Request)} if the ratingsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ratingsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ratingsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RatingDTO>> partialUpdateRatings(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RatingDTO ratingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Ratings partially : {}, {}", id, ratingDTO);
        if (ratingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ratingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ratingRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RatingDTO> result = ratingService.partialUpdate(ratingDTO);

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
     * {@code GET  /ratings} : get all the ratings.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ratings in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RatingDTO>>> getAllRatings(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Ratings");
        return ratingService
            .countAll()
            .zipWith(ratingService.findAll(pageable).collectList())
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
     * {@code GET  /ratings/:id} : get the "id" ratings.
     *
     * @param id the id of the ratingsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ratingsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RatingDTO>> getRatings(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Ratings : {}", id);
        Mono<RatingDTO> ratingsDTO = ratingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ratingsDTO);
    }

    /**
     * {@code DELETE  /ratings/:id} : delete the "id" ratings.
     *
     * @param id the id of the ratingsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRatings(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Ratings : {}", id);
        return ratingService
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
