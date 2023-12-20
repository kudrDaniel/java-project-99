package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.exception.TaskStatusAlreadyExistException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException(TaskStatus.class, id));
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
            throw new TaskStatusAlreadyExistException(fields);
        }

        var model = taskStatusMapper.map(dto);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    public TaskStatusDTO update(TaskStatusUpdateDTO dto, Long id) {
        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TaskStatus.class, id));

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
            throw new TaskStatusAlreadyExistException(fields);
        }

        taskStatusMapper.update(dto, model);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    public void deleteById(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
