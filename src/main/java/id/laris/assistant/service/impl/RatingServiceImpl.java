package id.laris.assistant.service.impl;

import id.laris.assistant.domain.Ratings;
import id.laris.assistant.repository.RatingRepository;
import id.laris.assistant.service.RatingService;
import id.laris.assistant.service.dto.RatingDTO;
import id.laris.assistant.service.mapper.RatingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Ratings}.
 */
@Service
@Transactional
public class RatingServiceImpl implements RatingService {

    private static final Logger LOG = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final RatingRepository ratingRepository;

    private final RatingMapper ratingMapper;

    public RatingServiceImpl(RatingRepository ratingRepository, RatingMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.ratingMapper = ratingMapper;
    }

    @Override
    public Mono<RatingDTO> save(RatingDTO ratingDTO) {
        LOG.debug("Request to save Ratings : {}", ratingDTO);
        return ratingRepository.save(ratingMapper.toEntity(ratingDTO)).map(ratingMapper::toDto);
    }

    @Override
    public Mono<RatingDTO> update(RatingDTO ratingDTO) {
        LOG.debug("Request to update Ratings : {}", ratingDTO);
        return ratingRepository.save(ratingMapper.toEntity(ratingDTO)).map(ratingMapper::toDto);
    }

    @Override
    public Mono<RatingDTO> partialUpdate(RatingDTO ratingDTO) {
        LOG.debug("Request to partially update Ratings : {}", ratingDTO);

        return ratingRepository
            .findById(ratingDTO.getId())
            .map(existingRatings -> {
                ratingMapper.partialUpdate(existingRatings, ratingDTO);

                return existingRatings;
            })
            .flatMap(ratingRepository::save)
            .map(ratingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RatingDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Ratings");
        return ratingRepository.findAllBy(pageable).map(ratingMapper::toDto);
    }

    public Mono<Long> countAll() {
        return ratingRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RatingDTO> findOne(Long id) {
        LOG.debug("Request to get Ratings : {}", id);
        return ratingRepository.findById(id).map(ratingMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Ratings : {}", id);
        return ratingRepository.deleteById(id);
    }
}
