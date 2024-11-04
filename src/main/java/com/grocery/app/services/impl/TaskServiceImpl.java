package com.grocery.app.services.impl;

import com.grocery.app.dto.TaskDTO;
import com.grocery.app.entities.Task;
import com.grocery.app.services.TaskService;

import java.util.ArrayList;

public class TaskServiceImpl implements TaskService {
    @Override
    public TaskDTO createTask(long userId, TaskDTO taskDTO) {
        return null;
    }

    @Override
    public TaskDTO getTaskById(long userId, long id) {
        return null;
    }

    @Override
    public ArrayList<TaskDTO> getAllTask(long userId, int from, int to) {
        return null;
    }

    @Override
    public TaskDTO updateTask(long userId, TaskDTO taskDTO) {
        return null;
    }

    @Override
    public TaskDTO deleteTask(long userId, long id) {
        return null;
    }
}
