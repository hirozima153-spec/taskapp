package com.example.taskapp.task;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Task> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAllByOrderByDoneAscDueDateAscIdAsc();
        }
        return repository.findByTitleContainingIgnoreCaseOrderByDoneAscDueDateAscIdAsc(keyword.trim());
    }

    @Transactional(readOnly = true)
    public Task get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task save(Task task) {
        return repository.save(task);
    }

    public void toggleDone(Long id) {
        Task task = get(id);
        task.setDone(!task.isDone());
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }
}
