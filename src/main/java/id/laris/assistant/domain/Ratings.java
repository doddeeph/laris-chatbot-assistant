package id.laris.assistant.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Ratings.
 */
@Table("ratings")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ratings implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("rating")
    private Double rating;

    @NotNull(message = "must not be null")
    @Column("timestamp")
    private Long timestamp;

    @org.springframework.data.annotation.Transient
    private Users users;

    @org.springframework.data.annotation.Transient
    private Movies movies;

    @Column("user_id")
    private Long userId;

    @Column("movie_id")
    private Long movieId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ratings id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRating() {
        return this.rating;
    }

    public Ratings rating(Double rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public Ratings timestamp(Long timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Users getUsers() {
        return this.users;
    }

    public void setUsers(Users users) {
        this.users = users;
        this.userId = users != null ? users.getId() : null;
    }

    public Ratings users(Users users) {
        this.setUsers(users);
        return this;
    }

    public Movies getMovies() {
        return this.movies;
    }

    public void setMovies(Movies movies) {
        this.movies = movies;
        this.movieId = movies != null ? movies.getId() : null;
    }

    public Ratings movies(Movies movies) {
        this.setMovies(movies);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long users) {
        this.userId = users;
    }

    public Long getMovieId() {
        return this.movieId;
    }

    public void setMovieId(Long movies) {
        this.movieId = movies;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ratings)) {
            return false;
        }
        return getId() != null && getId().equals(((Ratings) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ratings{" +
            "id=" + getId() +
            ", rating=" + getRating() +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
