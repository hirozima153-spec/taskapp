package com.example.taskapp.task;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTitleContainingIgnoreCaseOrderByDoneAscDueDateAscIdAsc(String keyword);

    List<Task> findAllByOrderByDoneAscDueDateAscIdAsc();
}
