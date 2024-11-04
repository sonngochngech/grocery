package com.grocery.app.services;

import com.grocery.app.dto.TaskDTO;
import com.grocery.app.entities.Task;

import java.util.ArrayList;
import java.util.Optional;

public interface TaskService {
    TaskDTO createTask(long userId, TaskDTO taskDTO);

    Optional<TaskDTO> getTaskById(long userId, long id);

    ArrayList<TaskDTO> getAllTask(long userId, int from, int to);

    TaskDTO updateTask(long userId, TaskDTO taskDTO);

    TaskDTO deleteTask(long userId, long id);


}
