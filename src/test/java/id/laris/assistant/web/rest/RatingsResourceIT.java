package id.laris.assistant.web.rest;

import static id.laris.assistant.domain.RatingsAsserts.*;
import static id.laris.assistant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.laris.assistant.IntegrationTest;
import id.laris.assistant.domain.Ratings;
import id.laris.assistant.repository.EntityManager;
import id.laris.assistant.repository.RatingRepository;
import id.laris.assistant.service.dto.RatingDTO;
import id.laris.assistant.service.mapper.RatingMapper;
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
 * Integration tests for the {@link RatingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RatingsResourceIT {

    private static final Double DEFAULT_RATING = 1D;
    private static final Double UPDATED_RATING = 2D;

    private static final Long DEFAULT_TIMESTAMP = 1L;
    private static final Long UPDATED_TIMESTAMP = 2L;

    private static final String ENTITY_API_URL = "/api/ratings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Ratings ratings;

    private Ratings insertedRatings;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ratings createEntity() {
        return new Ratings().rating(DEFAULT_RATING).timestamp(DEFAULT_TIMESTAMP);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ratings createUpdatedEntity() {
        return new Ratings().rating(UPDATED_RATING).timestamp(UPDATED_TIMESTAMP);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Ratings.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        ratings = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRatings != null) {
            ratingRepository.delete(insertedRatings).block();
            insertedRatings = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRatings() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Ratings
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);
        var returnedRatingsDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RatingDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Ratings in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRatings = ratingMapper.toEntity(returnedRatingsDTO);
        assertRatingsUpdatableFieldsEquals(returnedRatings, getPersistedRatings(returnedRatings));

        insertedRatings = returnedRatings;
    }

    @Test
    void createRatingsWithExistingId() throws Exception {
        // Create the Ratings with an existing ID
        ratings.setId(1L);
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ratings in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ratings.setRating(null);

        // Create the Ratings, which fails.
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ratings.setTimestamp(null);

        // Create the Ratings, which fails.
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRatings() {
        // Initialize the database
        insertedRatings = ratingRepository.save(ratings).block();

        // Get all the ratingsList
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
            .value(hasItem(ratings.getId().intValue()))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING.doubleValue()))
            .jsonPath("$.[*].timestamp")
            .value(hasItem(DEFAULT_TIMESTAMP.intValue()));
    }

    @Test
    void getRatings() {
        // Initialize the database
        insertedRatings = ratingRepository.save(ratings).block();

        // Get the ratings
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, ratings.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(ratings.getId().intValue()))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING.doubleValue()))
            .jsonPath("$.timestamp")
            .value(is(DEFAULT_TIMESTAMP.intValue()));
    }

    @Test
    void getNonExistingRatings() {
        // Get the ratings
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRatings() throws Exception {
        // Initialize the database
        insertedRatings = ratingRepository.save(ratings).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ratings
        Ratings updatedRatings = ratingRepository.findById(ratings.getId()).block();
        updatedRatings.rating(UPDATED_RATING).timestamp(UPDATED_TIMESTAMP);
        RatingDTO ratingDTO = ratingMapper.toDto(updatedRatings);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ratingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ratings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRatingsToMatchAllProperties(updatedRatings);
    }

    @Test
    void putNonExistingRatings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratings.setId(longCount.incrementAndGet());

        // Create the Ratings
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ratingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ratings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRatings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratings.setId(longCount.incrementAndGet());

        // Create the Ratings
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ratings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRatings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratings.setId(longCount.incrementAndGet());

        // Create the Ratings
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ratings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRatingsWithPatch() throws Exception {
        // Initialize the database
        insertedRatings = ratingRepository.save(ratings).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ratings using partial update
        Ratings partialUpdatedRatings = new Ratings();
        partialUpdatedRatings.setId(ratings.getId());

        partialUpdatedRatings.rating(UPDATED_RATING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRatings.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRatings))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ratings in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRatingsUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRatings, ratings), getPersistedRatings(ratings));
    }

    @Test
    void fullUpdateRatingsWithPatch() throws Exception {
        // Initialize the database
        insertedRatings = ratingRepository.save(ratings).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ratings using partial update
        Ratings partialUpdatedRatings = new Ratings();
        partialUpdatedRatings.setId(ratings.getId());

        partialUpdatedRatings.rating(UPDATED_RATING).timestamp(UPDATED_TIMESTAMP);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRatings.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRatings))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ratings in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRatingsUpdatableFieldsEquals(partialUpdatedRatings, getPersistedRatings(partialUpdatedRatings));
    }

    @Test
    void patchNonExistingRatings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratings.setId(longCount.incrementAndGet());

        // Create the Ratings
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ratingDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ratings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRatings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratings.setId(longCount.incrementAndGet());

        // Create the Ratings
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ratings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRatings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratings.setId(longCount.incrementAndGet());

        // Create the Ratings
        RatingDTO ratingDTO = ratingMapper.toDto(ratings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ratings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRatings() {
        // Initialize the database
        insertedRatings = ratingRepository.save(ratings).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ratings
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, ratings.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ratingRepository.count().block();
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

    protected Ratings getPersistedRatings(Ratings ratings) {
        return ratingRepository.findById(ratings.getId()).block();
    }

    protected void assertPersistedRatingsToMatchAllProperties(Ratings expectedRatings) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRatingsAllPropertiesEquals(expectedRatings, getPersistedRatings(expectedRatings));
        assertRatingsUpdatableFieldsEquals(expectedRatings, getPersistedRatings(expectedRatings));
    }

    protected void assertPersistedRatingsToMatchUpdatableProperties(Ratings expectedRatings) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRatingsAllUpdatablePropertiesEquals(expectedRatings, getPersistedRatings(expectedRatings));
        assertRatingsUpdatableFieldsEquals(expectedRatings, getPersistedRatings(expectedRatings));
    }
}
