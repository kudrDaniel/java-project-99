package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceAlreadyExistException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.utils.UserUtils;
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
