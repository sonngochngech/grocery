package com.grocery.app.services.impl;

import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.TaskDTO;
import com.grocery.app.entities.Task;
import com.grocery.app.repositories.TaskRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskRepo taskRepository;

    @Autowired
    private UserRepo userRepository;

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        // Map DTO to entity
        Task task = modelMapper.map(taskDTO, Task.class);

        // Save the entity
        Task savedTask = taskRepository.save(task);

        // Map entity back to DTO and return
        return modelMapper.map(savedTask, TaskDTO.class);
    }

    @Override
    public Optional<TaskDTO> getTaskById(long userId, long id) {
        // Retrieve task and check user ownership
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId() == userId)
                .map(task -> modelMapper.map(task, TaskDTO.class));
    }

    @Override
    public ArrayList<TaskDTO> getAllTask(long userId, int from, int to) {
        List<Task> tasks = taskRepository.findAllByUserId(userId);

        // Clamp "to" parameter
        to = Math.min(to, tasks.size());

        // Validate pagination bounds
        if (from < 0 || from > to) {
            throw new IndexOutOfBoundsException("Invalid pagination parameters.");
        }

        // Paginate and map tasks to DTOs
        List<Task> paginatedTasks = tasks.subList(from, to);

        return paginatedTasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public TaskDTO updateTask(TaskDTO taskDTO) {
        Task existingTask = taskRepository.findById(taskDTO.getId()).orElse(null);

        if (existingTask != null) {
            modelMapper.map(taskDTO, existingTask);
            Task updatedTask = taskRepository.save(existingTask);
            return modelMapper.map(updatedTask, TaskDTO.class);
        }

        return null;
    }

    @Override
    public TaskDTO deleteTask(long userId, long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);

        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();

            // Check ownership and set status if applicable
            if (task.getUser().getId() == userId && !Objects.equals(task.getStatus(), StatusConfig.DELETED.getStatus())) {
                task.setStatus(StatusConfig.DELETED.getStatus()); // Assume DELETED is the status to mark as deleted
                Task deletedTask = taskRepository.save(task);
                return modelMapper.map(deletedTask, TaskDTO.class);
            }
        }

        return null;
    }

    // Getters and Setters for testing purposes (if needed)
    public TaskRepo getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepo taskRepository) {
        this.taskRepository = taskRepository;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
