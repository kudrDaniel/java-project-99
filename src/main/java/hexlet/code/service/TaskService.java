package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
    private TaskSpecification specBuilder;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> findAll(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        var models = taskRepository.findAll(spec);
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
        var taskStatusModel = taskStatusRepository.findBySlug(dto.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException(TaskStatus.class, "name", dto.getStatus()));
        taskModel.setTaskStatus(taskStatusModel);
        var labelModels = new HashSet<>(labelRepository.findAllById(dto.getTaskLabelIds()));
        taskModel.setLabels(labelModels);
        taskRepository.save(taskModel);
        return taskMapper.map(taskModel);
    }

    public TaskDTO update(TaskUpdateDTO dto, Long id) {
        var taskModel = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Task.class, id));
        if (dto.getStatus().isPresent()) {
            var taskStatusName = dto.getStatus().get();
            var taskStatusModel = taskStatusRepository.findBySlug(taskStatusName)
                    .orElseThrow(() -> new ResourceNotFoundException(TaskStatus.class, "name", taskStatusName));
            taskModel.setTaskStatus(taskStatusModel);
        }
        if (dto.getTaskLabelIds().isPresent()) {
            var labelIds = dto.getTaskLabelIds().get();
            var labelModels = new HashSet<>(labelRepository.findAllById(labelIds));
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
