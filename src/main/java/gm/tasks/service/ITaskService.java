package gm.tasks.service;

import gm.tasks.model.Task;

import java.util.List;

public interface ITaskService {
    public List<Task> listTask();

    public Task searchTaskById(Integer idTask);

    public void saveTask(Task task);

    public void deleteTask(Task task);
}
