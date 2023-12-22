package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceAlreadyExistException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LabelService {
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> findAll() {
        var models = labelRepository.findAll();
        return models.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO findById(Long id) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Label.class, id));
        return labelMapper.map(model);
    }

    public LabelDTO create(LabelCreateDTO dto) {
        if (labelRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistException(Label.class, Map.of("name", dto.getName()));
        }

        var model = labelMapper.map(dto);
        labelRepository.save(model);
        return labelMapper.map(model);
    }

    public LabelDTO update(LabelUpdateDTO dto, Long id) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Label.class, id));

        if (labelRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistException(Label.class, Map.of("name", dto.getName()));
        }

        labelMapper.update(dto, model);
        labelRepository.save(model);
        return labelMapper.map(model);
    }

    public void deleteById(Long id) {
        labelRepository.deleteById(id);
    }
}
