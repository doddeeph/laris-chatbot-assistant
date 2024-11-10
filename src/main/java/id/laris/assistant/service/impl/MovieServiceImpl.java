package id.laris.assistant.service.impl;

import id.laris.assistant.domain.Movies;
import id.laris.assistant.repository.MovieRepository;
import id.laris.assistant.service.MovieService;
import id.laris.assistant.service.dto.MovieDTO;
import id.laris.assistant.service.mapper.MovieMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Movies}.
 */
@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieServiceImpl.class);

    private final MovieRepository movieRepository;

    private final MovieMapper movieMapper;

    public MovieServiceImpl(MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public Mono<MovieDTO> save(MovieDTO movieDTO) {
        LOG.debug("Request to save Movies : {}", movieDTO);
        return movieRepository.save(movieMapper.toEntity(movieDTO)).map(movieMapper::toDto);
    }

    @Override
    public Mono<MovieDTO> update(MovieDTO movieDTO) {
        LOG.debug("Request to update Movies : {}", movieDTO);
        return movieRepository.save(movieMapper.toEntity(movieDTO)).map(movieMapper::toDto);
    }

    @Override
    public Mono<MovieDTO> partialUpdate(MovieDTO movieDTO) {
        LOG.debug("Request to partially update Movies : {}", movieDTO);

        return movieRepository
            .findById(movieDTO.getId())
            .map(existingMovies -> {
                movieMapper.partialUpdate(existingMovies, movieDTO);

                return existingMovies;
            })
            .flatMap(movieRepository::save)
            .map(movieMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MovieDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Movies");
        return movieRepository.findAllBy(pageable).map(movieMapper::toDto);
    }

    public Mono<Long> countAll() {
        return movieRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MovieDTO> findOne(Long id) {
        LOG.debug("Request to get Movies : {}", id);
        return movieRepository.findById(id).map(movieMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Movies : {}", id);
        return movieRepository.deleteById(id);
    }
}
