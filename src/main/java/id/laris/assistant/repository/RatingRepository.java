package id.laris.assistant.repository;

import id.laris.assistant.domain.Ratings;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Ratings entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RatingRepository extends ReactiveCrudRepository<Ratings, Long>, RatingRepositoryInternal {
    Flux<Ratings> findAllBy(Pageable pageable);

    @Query("SELECT * FROM ratings entity WHERE entity.user_id = :id")
    Flux<Ratings> findByUsers(Long id);

    @Query("SELECT * FROM ratings entity WHERE entity.user_id IS NULL")
    Flux<Ratings> findAllWhereUsersIsNull();

    @Query("SELECT * FROM ratings entity WHERE entity.movie_id = :id")
    Flux<Ratings> findByMovies(Long id);

    @Query("SELECT * FROM ratings entity WHERE entity.movie_id IS NULL")
    Flux<Ratings> findAllWhereMoviesIsNull();

    @Override
    <S extends Ratings> Mono<S> save(S entity);

    @Override
    Flux<Ratings> findAll();

    @Override
    Mono<Ratings> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RatingRepositoryInternal {
    <S extends Ratings> Mono<S> save(S entity);

    Flux<Ratings> findAllBy(Pageable pageable);

    Flux<Ratings> findAll();

    Mono<Ratings> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Ratings> findAllBy(Pageable pageable, Criteria criteria);
}
