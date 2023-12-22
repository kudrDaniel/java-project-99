package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> findAll() {
        var models = taskStatusRepository.findAll();
        return models.stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO findById(Long id) {
        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TaskStatus.class, "id", id));
        return taskStatusMapper.map(model);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        var fields = new HashMap<String, String>();
        if (taskStatusRepository.existsByName(dto.getName())) {
            fields.put("name", dto.getName());
        }
        if (taskStatusRepository.existsBySlug(dto.getSlug())) {
            fields.put("slug", dto.getSlug());
        }
        if (!fields.isEmpty()) {
            throw new ResourceAlreadyExistException(TaskStatus.class, fields);
        }

        var model = taskStatusMapper.map(dto);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    public TaskStatusDTO update(TaskStatusUpdateDTO dto, Long id) {
        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TaskStatus.class, "id", id));

        var fields = new HashMap<String, String>();
        if (!model.getName().equals(dto.getName().get())
                && taskStatusRepository.existsByName(dto.getName().get())) {
            fields.put("name", dto.getName().get());
        }
        if (!model.getSlug().equals(dto.getSlug().get())
                && taskStatusRepository.existsBySlug(dto.getSlug().get())) {
            fields.put("slug", dto.getSlug().get());
        }
        if (!fields.isEmpty()) {
            throw new ResourceAlreadyExistException(TaskStatus.class, fields);
        }

        taskStatusMapper.update(dto, model);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    public void deleteById(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
