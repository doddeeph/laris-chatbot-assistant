package id.laris.assistant.service.dto;

import id.laris.assistant.domain.Ratings;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link Ratings} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RatingDTO implements Serializable {

    @NotNull(message = "must not be null")
    private Long id;

    @NotNull(message = "must not be null")
    private Double rating;

    @NotNull(message = "must not be null")
    private Long timestamp;

    private UserDTO users;

    private MovieDTO movies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public UserDTO getUsers() {
        return users;
    }

    public void setUsers(UserDTO users) {
        this.users = users;
    }

    public MovieDTO getMovies() {
        return movies;
    }

    public void setMovies(MovieDTO movies) {
        this.movies = movies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RatingDTO)) {
            return false;
        }

        RatingDTO ratingDTO = (RatingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ratingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RatingsDTO{" +
            "id=" + getId() +
            ", rating=" + getRating() +
            ", timestamp=" + getTimestamp() +
            ", users=" + getUsers() +
            ", movies=" + getMovies() +
            "}";
    }
}
