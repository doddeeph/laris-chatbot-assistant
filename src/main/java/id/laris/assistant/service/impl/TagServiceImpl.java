package id.laris.assistant.service.impl;

import id.laris.assistant.domain.Tags;
import id.laris.assistant.repository.TagRepository;
import id.laris.assistant.service.TagService;
import id.laris.assistant.service.dto.TagDTO;
import id.laris.assistant.service.mapper.TagMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Tags}.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private static final Logger LOG = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public Mono<TagDTO> save(TagDTO tagDTO) {
        LOG.debug("Request to save Tags : {}", tagDTO);
        return tagRepository.save(tagMapper.toEntity(tagDTO)).map(tagMapper::toDto);
    }

    @Override
    public Mono<TagDTO> update(TagDTO tagDTO) {
        LOG.debug("Request to update Tags : {}", tagDTO);
        return tagRepository.save(tagMapper.toEntity(tagDTO)).map(tagMapper::toDto);
    }

    @Override
    public Mono<TagDTO> partialUpdate(TagDTO tagDTO) {
        LOG.debug("Request to partially update Tags : {}", tagDTO);

        return tagRepository
            .findById(tagDTO.getId())
            .map(existingTags -> {
                tagMapper.partialUpdate(existingTags, tagDTO);

                return existingTags;
            })
            .flatMap(tagRepository::save)
            .map(tagMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TagDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Tags");
        return tagRepository.findAllBy(pageable).map(tagMapper::toDto);
    }

    public Mono<Long> countAll() {
        return tagRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TagDTO> findOne(Long id) {
        LOG.debug("Request to get Tags : {}", id);
        return tagRepository.findById(id).map(tagMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Tags : {}", id);
        return tagRepository.deleteById(id);
    }
}
