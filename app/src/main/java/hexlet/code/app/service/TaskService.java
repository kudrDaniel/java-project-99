package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> findAll() {
        var models = taskRepository.findAll();
        return models.stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO findById(Long id) {
        var model = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Task.class, id));
        return taskMapper.map(model);
    }

    public TaskDTO create(TaskCreateDTO dto) {
        var taskModel = taskMapper.map(dto);
        var taskStatusModel = taskStatusRepository.findByName(dto.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException(TaskStatus.class, "name", dto.getStatus()));
        taskModel.setTaskStatus(taskStatusModel);
        var labelModels = labelRepository.findAllById(dto.getTaskLabelIds());
        taskModel.setLabels(labelModels);
        taskRepository.save(taskModel);
        return taskMapper.map(taskModel);
    }

    public TaskDTO update(TaskUpdateDTO dto, Long id) {
        var taskModel = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Task.class, id));
        if (dto.getStatus().isPresent()) {
            var taskStatusName = dto.getStatus().get();
            var taskStatusModel = taskStatusRepository.findByName(taskStatusName)
                    .orElseThrow(() -> new ResourceNotFoundException(TaskStatus.class, "name", taskStatusName));
            taskModel.setTaskStatus(taskStatusModel);
        }
        if (dto.getTaskLabelIds().isPresent()) {
            var labelIds = dto.getTaskLabelIds().get();
            var labelModels = labelRepository.findAllById(labelIds);
            taskModel.setLabels(labelModels);
        }
        taskMapper.update(dto, taskModel);
        taskRepository.save(taskModel);
        return taskMapper.map(taskModel);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }
}
