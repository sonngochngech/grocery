package com.grocery.app.controllers;

import com.grocery.app.dto.TaskDTO;
import com.grocery.app.dto.request.createRequest.CreateTaskRequest;
import com.grocery.app.dto.request.updateRequest.UpdateTaskRequest;
import com.grocery.app.entities.Task;
import com.grocery.app.services.impl.TaskServiceImpl;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public class TaskController {
    private TaskServiceImpl taskService;

    public ResponseEntity<TaskDTO> createTask(CreateTaskRequest createTaskRequest){
        return null;
    }

    public ResponseEntity<TaskDTO> getTaskById(long userId, long taskId){
        return null;
    }

    public ResponseEntity<ArrayList<TaskDTO>> getAllTask(long userId, int from, int to){
        return null;
    }

    public ResponseEntity<TaskDTO> updateTask(UpdateTaskRequest updateTaskRequest){
        return null;
    }

    public ResponseEntity<TaskDTO> deleteTask(long userId, long taskId){
        return null;
    }
}
