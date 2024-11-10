package id.laris.assistant.service.impl;

import id.laris.assistant.domain.Users;
import id.laris.assistant.repository.UserRepository;
import id.laris.assistant.service.UserService;
import id.laris.assistant.service.dto.UserDTO;
import id.laris.assistant.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Users}.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Mono<UserDTO> save(UserDTO userDTO) {
        LOG.debug("Request to save Users : {}", userDTO);
        return userRepository.save(userMapper.toEntity(userDTO)).map(userMapper::toDto);
    }

    @Override
    public Mono<UserDTO> update(UserDTO userDTO) {
        LOG.debug("Request to update Users : {}", userDTO);
        return userRepository.save(userMapper.toEntity(userDTO)).map(userMapper::toDto);
    }

    @Override
    public Mono<UserDTO> partialUpdate(UserDTO userDTO) {
        LOG.debug("Request to partially update Users : {}", userDTO);

        return userRepository
            .findById(userDTO.getId())
            .map(existingUsers -> {
                userMapper.partialUpdate(existingUsers, userDTO);

                return existingUsers;
            })
            .flatMap(userRepository::save)
            .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Users");
        return userRepository.findAllBy(pageable).map(userMapper::toDto);
    }

    public Mono<Long> countAll() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDTO> findOne(Long id) {
        LOG.debug("Request to get Users : {}", id);
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Users : {}", id);
        return userRepository.deleteById(id);
    }
}
