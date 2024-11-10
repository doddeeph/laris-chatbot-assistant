package id.laris.assistant.repository;

import id.laris.assistant.domain.Movies;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Movies entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MovieRepository extends ReactiveCrudRepository<Movies, Long>, MovieRepositoryInternal {
    Flux<Movies> findAllBy(Pageable pageable);

    @Override
    <S extends Movies> Mono<S> save(S entity);

    @Override
    Flux<Movies> findAll();

    @Override
    Mono<Movies> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MovieRepositoryInternal {
    <S extends Movies> Mono<S> save(S entity);

    Flux<Movies> findAllBy(Pageable pageable);

    Flux<Movies> findAll();

    Mono<Movies> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Movies> findAllBy(Pageable pageable, Criteria criteria);
}
