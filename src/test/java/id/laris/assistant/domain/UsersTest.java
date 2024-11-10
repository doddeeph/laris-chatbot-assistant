package id.laris.assistant.domain;

import static id.laris.assistant.domain.UsersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import id.laris.assistant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UsersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Users.class);
        Users users1 = getUsersSample1();
        Users users2 = new Users();
        assertThat(users1).isNotEqualTo(users2);

        users2.setId(users1.getId());
        assertThat(users1).isEqualTo(users2);

        users2 = getUsersSample2();
        assertThat(users1).isNotEqualTo(users2);
    }
}
