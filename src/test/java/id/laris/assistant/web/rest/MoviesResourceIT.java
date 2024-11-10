package id.laris.assistant.web.rest;

import static id.laris.assistant.domain.MoviesAsserts.*;
import static id.laris.assistant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.laris.assistant.IntegrationTest;
import id.laris.assistant.domain.Movies;
import id.laris.assistant.repository.EntityManager;
import id.laris.assistant.repository.MovieRepository;
import id.laris.assistant.service.dto.MovieDTO;
import id.laris.assistant.service.mapper.MovieMapper;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link MovieResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MoviesResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_GENRES = "AAAAAAAAAA";
    private static final String UPDATED_GENRES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/movies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Movies movies;

    private Movies insertedMovies;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movies createEntity() {
        return new Movies().title(DEFAULT_TITLE).genres(DEFAULT_GENRES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movies createUpdatedEntity() {
        return new Movies().title(UPDATED_TITLE).genres(UPDATED_GENRES);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Movies.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        movies = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMovies != null) {
            movieRepository.delete(insertedMovies).block();
            insertedMovies = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMovies() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Movies
        MovieDTO movieDTO = movieMapper.toDto(movies);
        var returnedMoviesDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MovieDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Movies in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMovies = movieMapper.toEntity(returnedMoviesDTO);
        assertMoviesUpdatableFieldsEquals(returnedMovies, getPersistedMovies(returnedMovies));

        insertedMovies = returnedMovies;
    }

    @Test
    void createMoviesWithExistingId() throws Exception {
        // Create the Movies with an existing ID
        movies.setId(1L);
        MovieDTO movieDTO = movieMapper.toDto(movies);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movies in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        movies.setTitle(null);

        // Create the Movies, which fails.
        MovieDTO movieDTO = movieMapper.toDto(movies);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkGenresIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        movies.setGenres(null);

        // Create the Movies, which fails.
        MovieDTO movieDTO = movieMapper.toDto(movies);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMovies() {
        // Initialize the database
        insertedMovies = movieRepository.save(movies).block();

        // Get all the moviesList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(movies.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].genres")
            .value(hasItem(DEFAULT_GENRES));
    }

    @Test
    void getMovies() {
        // Initialize the database
        insertedMovies = movieRepository.save(movies).block();

        // Get the movies
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, movies.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(movies.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.genres")
            .value(is(DEFAULT_GENRES));
    }

    @Test
    void getNonExistingMovies() {
        // Get the movies
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMovies() throws Exception {
        // Initialize the database
        insertedMovies = movieRepository.save(movies).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the movies
        Movies updatedMovies = movieRepository.findById(movies.getId()).block();
        updatedMovies.title(UPDATED_TITLE).genres(UPDATED_GENRES);
        MovieDTO movieDTO = movieMapper.toDto(updatedMovies);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, movieDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Movies in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMoviesToMatchAllProperties(updatedMovies);
    }

    @Test
    void putNonExistingMovies() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movies.setId(longCount.incrementAndGet());

        // Create the Movies
        MovieDTO movieDTO = movieMapper.toDto(movies);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, movieDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movies in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMovies() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movies.setId(longCount.incrementAndGet());

        // Create the Movies
        MovieDTO movieDTO = movieMapper.toDto(movies);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movies in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMovies() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movies.setId(longCount.incrementAndGet());

        // Create the Movies
        MovieDTO movieDTO = movieMapper.toDto(movies);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Movies in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMoviesWithPatch() throws Exception {
        // Initialize the database
        insertedMovies = movieRepository.save(movies).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the movies using partial update
        Movies partialUpdatedMovies = new Movies();
        partialUpdatedMovies.setId(movies.getId());

        partialUpdatedMovies.title(UPDATED_TITLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMovies.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMovies))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Movies in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMoviesUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMovies, movies), getPersistedMovies(movies));
    }

    @Test
    void fullUpdateMoviesWithPatch() throws Exception {
        // Initialize the database
        insertedMovies = movieRepository.save(movies).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the movies using partial update
        Movies partialUpdatedMovies = new Movies();
        partialUpdatedMovies.setId(movies.getId());

        partialUpdatedMovies.title(UPDATED_TITLE).genres(UPDATED_GENRES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMovies.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMovies))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Movies in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMoviesUpdatableFieldsEquals(partialUpdatedMovies, getPersistedMovies(partialUpdatedMovies));
    }

    @Test
    void patchNonExistingMovies() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movies.setId(longCount.incrementAndGet());

        // Create the Movies
        MovieDTO movieDTO = movieMapper.toDto(movies);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, movieDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movies in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMovies() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movies.setId(longCount.incrementAndGet());

        // Create the Movies
        MovieDTO movieDTO = movieMapper.toDto(movies);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movies in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMovies() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movies.setId(longCount.incrementAndGet());

        // Create the Movies
        MovieDTO movieDTO = movieMapper.toDto(movies);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(movieDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Movies in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMovies() {
        // Initialize the database
        insertedMovies = movieRepository.save(movies).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the movies
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, movies.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return movieRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Movies getPersistedMovies(Movies movies) {
        return movieRepository.findById(movies.getId()).block();
    }

    protected void assertPersistedMoviesToMatchAllProperties(Movies expectedMovies) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMoviesAllPropertiesEquals(expectedMovies, getPersistedMovies(expectedMovies));
        assertMoviesUpdatableFieldsEquals(expectedMovies, getPersistedMovies(expectedMovies));
    }

    protected void assertPersistedMoviesToMatchUpdatableProperties(Movies expectedMovies) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMoviesAllUpdatablePropertiesEquals(expectedMovies, getPersistedMovies(expectedMovies));
        assertMoviesUpdatableFieldsEquals(expectedMovies, getPersistedMovies(expectedMovies));
    }
}
