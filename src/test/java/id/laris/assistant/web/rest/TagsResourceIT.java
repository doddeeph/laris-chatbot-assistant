package id.laris.assistant.web.rest;

import static id.laris.assistant.domain.TagsAsserts.*;
import static id.laris.assistant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.laris.assistant.IntegrationTest;
import id.laris.assistant.domain.Tags;
import id.laris.assistant.repository.EntityManager;
import id.laris.assistant.repository.TagRepository;
import id.laris.assistant.service.dto.TagDTO;
import id.laris.assistant.service.mapper.TagMapper;
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
 * Integration tests for the {@link TagResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TagsResourceIT {

    private static final String DEFAULT_TAG = "AAAAAAAAAA";
    private static final String UPDATED_TAG = "BBBBBBBBBB";

    private static final Long DEFAULT_TIMESTAMP = 1L;
    private static final Long UPDATED_TIMESTAMP = 2L;

    private static final String ENTITY_API_URL = "/api/tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Tags tags;

    private Tags insertedTags;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tags createEntity() {
        return new Tags().tag(DEFAULT_TAG).timestamp(DEFAULT_TIMESTAMP);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tags createUpdatedEntity() {
        return new Tags().tag(UPDATED_TAG).timestamp(UPDATED_TIMESTAMP);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Tags.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        tags = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTags != null) {
            tagRepository.delete(insertedTags).block();
            insertedTags = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTags() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Tags
        TagDTO tagDTO = tagMapper.toDto(tags);
        var returnedTagsDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TagDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Tags in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTags = tagMapper.toEntity(returnedTagsDTO);
        assertTagsUpdatableFieldsEquals(returnedTags, getPersistedTags(returnedTags));

        insertedTags = returnedTags;
    }

    @Test
    void createTagsWithExistingId() throws Exception {
        // Create the Tags with an existing ID
        tags.setId(1L);
        TagDTO tagDTO = tagMapper.toDto(tags);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTagIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tags.setTag(null);

        // Create the Tags, which fails.
        TagDTO tagDTO = tagMapper.toDto(tags);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tags.setTimestamp(null);

        // Create the Tags, which fails.
        TagDTO tagDTO = tagMapper.toDto(tags);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTags() {
        // Initialize the database
        insertedTags = tagRepository.save(tags).block();

        // Get all the tagsList
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
            .value(hasItem(tags.getId().intValue()))
            .jsonPath("$.[*].tag")
            .value(hasItem(DEFAULT_TAG))
            .jsonPath("$.[*].timestamp")
            .value(hasItem(DEFAULT_TIMESTAMP.intValue()));
    }

    @Test
    void getTags() {
        // Initialize the database
        insertedTags = tagRepository.save(tags).block();

        // Get the tags
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tags.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tags.getId().intValue()))
            .jsonPath("$.tag")
            .value(is(DEFAULT_TAG))
            .jsonPath("$.timestamp")
            .value(is(DEFAULT_TIMESTAMP.intValue()));
    }

    @Test
    void getNonExistingTags() {
        // Get the tags
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTags() throws Exception {
        // Initialize the database
        insertedTags = tagRepository.save(tags).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tags
        Tags updatedTags = tagRepository.findById(tags.getId()).block();
        updatedTags.tag(UPDATED_TAG).timestamp(UPDATED_TIMESTAMP);
        TagDTO tagDTO = tagMapper.toDto(updatedTags);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tagDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTagsToMatchAllProperties(updatedTags);
    }

    @Test
    void putNonExistingTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setId(longCount.incrementAndGet());

        // Create the Tags
        TagDTO tagDTO = tagMapper.toDto(tags);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tagDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setId(longCount.incrementAndGet());

        // Create the Tags
        TagDTO tagDTO = tagMapper.toDto(tags);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setId(longCount.incrementAndGet());

        // Create the Tags
        TagDTO tagDTO = tagMapper.toDto(tags);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTagsWithPatch() throws Exception {
        // Initialize the database
        insertedTags = tagRepository.save(tags).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tags using partial update
        Tags partialUpdatedTags = new Tags();
        partialUpdatedTags.setId(tags.getId());

        partialUpdatedTags.tag(UPDATED_TAG).timestamp(UPDATED_TIMESTAMP);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTags.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTags))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tags in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTagsUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTags, tags), getPersistedTags(tags));
    }

    @Test
    void fullUpdateTagsWithPatch() throws Exception {
        // Initialize the database
        insertedTags = tagRepository.save(tags).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tags using partial update
        Tags partialUpdatedTags = new Tags();
        partialUpdatedTags.setId(tags.getId());

        partialUpdatedTags.tag(UPDATED_TAG).timestamp(UPDATED_TIMESTAMP);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTags.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTags))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tags in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTagsUpdatableFieldsEquals(partialUpdatedTags, getPersistedTags(partialUpdatedTags));
    }

    @Test
    void patchNonExistingTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setId(longCount.incrementAndGet());

        // Create the Tags
        TagDTO tagDTO = tagMapper.toDto(tags);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tagDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setId(longCount.incrementAndGet());

        // Create the Tags
        TagDTO tagDTO = tagMapper.toDto(tags);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTags() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tags.setId(longCount.incrementAndGet());

        // Create the Tags
        TagDTO tagDTO = tagMapper.toDto(tags);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tags in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTags() {
        // Initialize the database
        insertedTags = tagRepository.save(tags).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tags
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tags.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tagRepository.count().block();
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

    protected Tags getPersistedTags(Tags tags) {
        return tagRepository.findById(tags.getId()).block();
    }

    protected void assertPersistedTagsToMatchAllProperties(Tags expectedTags) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTagsAllPropertiesEquals(expectedTags, getPersistedTags(expectedTags));
        assertTagsUpdatableFieldsEquals(expectedTags, getPersistedTags(expectedTags));
    }

    protected void assertPersistedTagsToMatchUpdatableProperties(Tags expectedTags) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTagsAllUpdatablePropertiesEquals(expectedTags, getPersistedTags(expectedTags));
        assertTagsUpdatableFieldsEquals(expectedTags, getPersistedTags(expectedTags));
    }
}
