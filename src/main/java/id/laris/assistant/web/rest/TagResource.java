package id.laris.assistant.web.rest;

import id.laris.assistant.domain.Tags;
import id.laris.assistant.repository.TagRepository;
import id.laris.assistant.service.TagService;
import id.laris.assistant.service.dto.TagDTO;
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
 * REST controller for managing {@link Tags}.
 */
@RestController
@RequestMapping("/api/public/movie-tags")
public class TagResource {

    private static final Logger LOG = LoggerFactory.getLogger(TagResource.class);

    private static final String ENTITY_NAME = "larisChatbotAssistantTags";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TagService tagService;

    private final TagRepository tagRepository;

    public TagResource(TagService tagService, TagRepository tagRepository) {
        this.tagService = tagService;
        this.tagRepository = tagRepository;
    }

    /**
     * {@code POST  /tags} : Create a new tags.
     *
     * @param tagDTO the tagsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tagsDTO, or with status {@code 400 (Bad Request)} if the tags has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TagDTO>> createTags(@Valid @RequestBody TagDTO tagDTO) throws URISyntaxException {
        LOG.debug("REST request to save Tags : {}", tagDTO);
        if (tagDTO.getId() != null) {
            throw new BadRequestAlertException("A new tags cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tagService
            .save(tagDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/tags/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tags/:id} : Updates an existing tags.
     *
     * @param id the id of the tagsDTO to save.
     * @param tagDTO the tagsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagsDTO,
     * or with status {@code 400 (Bad Request)} if the tagsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tagsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TagDTO>> updateTags(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TagDTO tagDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Tags : {}, {}", id, tagDTO);
        if (tagDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tagRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tagService
                    .update(tagDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /tags/:id} : Partial updates given fields of an existing tags, field will ignore if it is null
     *
     * @param id the id of the tagsDTO to save.
     * @param tagDTO the tagsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagsDTO,
     * or with status {@code 400 (Bad Request)} if the tagsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tagsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tagsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TagDTO>> partialUpdateTags(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TagDTO tagDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Tags partially : {}, {}", id, tagDTO);
        if (tagDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tagRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TagDTO> result = tagService.partialUpdate(tagDTO);

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
     * {@code GET  /tags} : get all the tags.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TagDTO>>> getAllTags(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Tags");
        return tagService
            .countAll()
            .zipWith(tagService.findAll(pageable).collectList())
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
     * {@code GET  /tags/:id} : get the "id" tags.
     *
     * @param id the id of the tagsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tagsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TagDTO>> getTags(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Tags : {}", id);
        Mono<TagDTO> tagsDTO = tagService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tagsDTO);
    }

    /**
     * {@code DELETE  /tags/:id} : delete the "id" tags.
     *
     * @param id the id of the tagsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTags(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Tags : {}", id);
        return tagService
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
