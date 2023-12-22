package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUtils userUtils;

    public List<UserDTO> findAll() {
        var models = userRepository.findAll();
        return models.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO findById(Long id) {
        var model = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
        return userMapper.map(model);
    }

    public UserDTO create(UserCreateDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistException(User.class, Map.of("email", dto.getEmail()));
        }
        var model = userMapper.map(dto);
        userRepository.save(model);
        return userMapper.map(model);
    }

    public UserDTO update(UserUpdateDTO dto, Long id) {
        if (!Objects.equals(userUtils.getCurrentUser().getId(), id)) {
            throw new AccessDeniedException("Access denied to update user with id:" + id);
        }
        var model = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
        userMapper.update(dto, model);
        userRepository.save(model);
        return userMapper.map(model);
    }

    public void deleteById(Long id) {
        if (!Objects.equals(userUtils.getCurrentUser().getId(), id)) {
            throw new AccessDeniedException("Access denied to update user with id:" + id);
        }
        userRepository.deleteById(id);
    }
}
