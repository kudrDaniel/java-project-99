package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

    private final UserRepository userRepository;

    private final TaskSpecification specBuilder;

    private final TaskMapper taskMapper;

    public List<TaskDTO> findAll(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        var models = taskRepository.findAll(spec);
        return models.stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO findById(Long id) {
        var model = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Task.class, "id", id));
        return taskMapper.map(model);
    }

    public TaskDTO create(TaskCreateDTO dto) {
        var taskModel = taskMapper.map(dto);
        var userModel = dto.getAssigneeId() == null ? null : userRepository.findById(dto.getAssigneeId())
                        .orElseThrow(() -> new ResourceNotFoundException(User.class, "id", dto.getAssigneeId()));
        taskModel.setAssignee(userModel);
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
                .orElseThrow(() -> new ResourceNotFoundException(Task.class, "id", id));
        if (dto.getAssigneeId() != null && dto.getAssigneeId().isPresent()) {
            var userId = dto.getAssigneeId().get();
            var userModel = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(User.class, "id", userId));
            taskModel.setAssignee(userModel);
        }
        if (dto.getStatus() != null && dto.getStatus().isPresent()) {
            var taskStatusSlug = dto.getStatus().get();
            var taskStatusModel = taskStatusRepository.findBySlug(taskStatusSlug)
                    .orElseThrow(() -> new ResourceNotFoundException(TaskStatus.class, "name", taskStatusSlug));
            taskModel.setTaskStatus(taskStatusModel);
        }
        if (dto.getTaskLabelIds() != null && dto.getTaskLabelIds().isPresent()) {
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
