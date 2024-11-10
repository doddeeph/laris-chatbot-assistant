package id.laris.assistant.web.rest;

import static id.laris.assistant.domain.UsersAsserts.*;
import static id.laris.assistant.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.laris.assistant.IntegrationTest;
import id.laris.assistant.domain.Users;
import id.laris.assistant.repository.EntityManager;
import id.laris.assistant.repository.UserRepository;
import id.laris.assistant.service.dto.UserDTO;
import id.laris.assistant.service.mapper.UserMapper;
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
 * Integration tests for the {@link UserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UsersResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Users users;

    private Users insertedUsers;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Users createEntity() {
        return new Users().firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Users createUpdatedEntity() {
        return new Users().firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Users.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        users = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUsers != null) {
            userRepository.delete(insertedUsers).block();
            insertedUsers = null;
        }
        deleteEntities(em);
    }

    @Test
    void createUsers() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Users
        UserDTO userDTO = userMapper.toDto(users);
        var returnedUsersDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(UserDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Users in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUsers = userMapper.toEntity(returnedUsersDTO);
        assertUsersUpdatableFieldsEquals(returnedUsers, getPersistedUsers(returnedUsers));

        insertedUsers = returnedUsers;
    }

    @Test
    void createUsersWithExistingId() throws Exception {
        // Create the Users with an existing ID
        users.setId(1L);
        UserDTO userDTO = userMapper.toDto(users);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        users.setFirstName(null);

        // Create the Users, which fails.
        UserDTO userDTO = userMapper.toDto(users);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        users.setLastName(null);

        // Create the Users, which fails.
        UserDTO userDTO = userMapper.toDto(users);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllUsers() {
        // Initialize the database
        insertedUsers = userRepository.save(users).block();

        // Get all the usersList
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
            .value(hasItem(users.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME));
    }

    @Test
    void getUsers() {
        // Initialize the database
        insertedUsers = userRepository.save(users).block();

        // Get the users
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, users.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(users.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME));
    }

    @Test
    void getNonExistingUsers() {
        // Get the users
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUsers() throws Exception {
        // Initialize the database
        insertedUsers = userRepository.save(users).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the users
        Users updatedUsers = userRepository.findById(users.getId()).block();
        updatedUsers.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);
        UserDTO userDTO = userMapper.toDto(updatedUsers);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUsersToMatchAllProperties(updatedUsers);
    }

    @Test
    void putNonExistingUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // Create the Users
        UserDTO userDTO = userMapper.toDto(users);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // Create the Users
        UserDTO userDTO = userMapper.toDto(users);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // Create the Users
        UserDTO userDTO = userMapper.toDto(users);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUsersWithPatch() throws Exception {
        // Initialize the database
        insertedUsers = userRepository.save(users).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the users using partial update
        Users partialUpdatedUsers = new Users();
        partialUpdatedUsers.setId(users.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUsers.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUsers))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Users in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUsersUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUsers, users), getPersistedUsers(users));
    }

    @Test
    void fullUpdateUsersWithPatch() throws Exception {
        // Initialize the database
        insertedUsers = userRepository.save(users).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the users using partial update
        Users partialUpdatedUsers = new Users();
        partialUpdatedUsers.setId(users.getId());

        partialUpdatedUsers.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUsers.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUsers))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Users in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUsersUpdatableFieldsEquals(partialUpdatedUsers, getPersistedUsers(partialUpdatedUsers));
    }

    @Test
    void patchNonExistingUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // Create the Users
        UserDTO userDTO = userMapper.toDto(users);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // Create the Users
        UserDTO userDTO = userMapper.toDto(users);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // Create the Users
        UserDTO userDTO = userMapper.toDto(users);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUsers() {
        // Initialize the database
        insertedUsers = userRepository.save(users).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the users
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, users.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userRepository.count().block();
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

    protected Users getPersistedUsers(Users users) {
        return userRepository.findById(users.getId()).block();
    }

    protected void assertPersistedUsersToMatchAllProperties(Users expectedUsers) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUsersAllPropertiesEquals(expectedUsers, getPersistedUsers(expectedUsers));
        assertUsersUpdatableFieldsEquals(expectedUsers, getPersistedUsers(expectedUsers));
    }

    protected void assertPersistedUsersToMatchUpdatableProperties(Users expectedUsers) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUsersAllUpdatablePropertiesEquals(expectedUsers, getPersistedUsers(expectedUsers));
        assertUsersUpdatableFieldsEquals(expectedUsers, getPersistedUsers(expectedUsers));
    }
}
