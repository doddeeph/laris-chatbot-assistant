package id.laris.assistant.service.dto;

import id.laris.assistant.domain.Movies;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link Movies} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MovieDTO implements Serializable {

    @NotNull(message = "must not be null")
    private Long id;

    @NotNull(message = "must not be null")
    private String title;

    @NotNull(message = "must not be null")
    private String genres;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MovieDTO)) {
            return false;
        }

        MovieDTO movieDTO = (MovieDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, movieDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MoviesDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", genres='" + getGenres() + "'" +
            "}";
    }
}
