package id.laris.assistant.repository;

import id.laris.assistant.domain.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Users entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRepository extends ReactiveCrudRepository<Users, Long>, UserRepositoryInternal {
    Flux<Users> findAllBy(Pageable pageable);

    @Override
    <S extends Users> Mono<S> save(S entity);

    @Override
    Flux<Users> findAll();

    @Override
    Mono<Users> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UserRepositoryInternal {
    <S extends Users> Mono<S> save(S entity);

    Flux<Users> findAllBy(Pageable pageable);

    Flux<Users> findAll();

    Mono<Users> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Users> findAllBy(Pageable pageable, Criteria criteria);
}
