package id.laris.assistant.service.dto;

import id.laris.assistant.domain.Users;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link Users} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserDTO implements Serializable {

    @NotNull(message = "must not be null")
    private Long id;

    @NotNull(message = "must not be null")
    private String firstName;

    @NotNull(message = "must not be null")
    private String lastName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDTO)) {
            return false;
        }

        UserDTO userDTO = (UserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UsersDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            "}";
    }
}
