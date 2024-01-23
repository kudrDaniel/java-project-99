package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    public List<LabelDTO> findAll() {
        var models = labelRepository.findAll();
        return models.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO findById(Long id) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Label.class, "id", id));
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
                .orElseThrow(() -> new ResourceNotFoundException(Label.class, "id", id));

        if (labelRepository.existsByName(dto.getName().get())) {
            throw new ResourceAlreadyExistException(Label.class, Map.of("name", dto.getName().get()));
        }

        labelMapper.update(dto, model);
        labelRepository.save(model);
        return labelMapper.map(model);
    }

    public void deleteById(Long id) {
        labelRepository.deleteById(id);
    }
}
