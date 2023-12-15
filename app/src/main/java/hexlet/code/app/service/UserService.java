package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.EmailAlreadyExistException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

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
            throw new EmailAlreadyExistException(dto.getEmail());
        }
        var model = userMapper.map(dto);
        userRepository.save(model);
        return userMapper.map(model);
    }

    public UserDTO update(UserUpdateDTO dto, Long id) {
        var model = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
        userMapper.update(dto, model);
        userRepository.save(model);
        return userMapper.map(model);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
