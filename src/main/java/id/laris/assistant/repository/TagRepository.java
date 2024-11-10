package id.laris.assistant.repository;

import id.laris.assistant.domain.Tags;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Tags entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends ReactiveCrudRepository<Tags, Long>, TagRepositoryInternal {
    Flux<Tags> findAllBy(Pageable pageable);

    @Query("SELECT * FROM tags entity WHERE entity.user_id = :id")
    Flux<Tags> findByUsers(Long id);

    @Query("SELECT * FROM tags entity WHERE entity.user_id IS NULL")
    Flux<Tags> findAllWhereUsersIsNull();

    @Query("SELECT * FROM tags entity WHERE entity.movie_id = :id")
    Flux<Tags> findByMovies(Long id);

    @Query("SELECT * FROM tags entity WHERE entity.movie_id IS NULL")
    Flux<Tags> findAllWhereMoviesIsNull();

    @Override
    <S extends Tags> Mono<S> save(S entity);

    @Override
    Flux<Tags> findAll();

    @Override
    Mono<Tags> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TagRepositoryInternal {
    <S extends Tags> Mono<S> save(S entity);

    Flux<Tags> findAllBy(Pageable pageable);

    Flux<Tags> findAll();

    Mono<Tags> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Tags> findAllBy(Pageable pageable, Criteria criteria);
}
