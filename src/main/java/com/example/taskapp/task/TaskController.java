package com.example.taskapp.task;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("tasks", service.search(keyword));
        model.addAttribute("keyword", keyword);
        return "tasks/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("task", new Task());
        return "tasks/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("task") Task task, BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "tasks/form";
        }
        service.save(task);
        redirect.addFlashAttribute("message", "タスクを登録しました");
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("task", service.get(id));
        return "tasks/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("task") Task task,
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "tasks/form";
        }
        Task existing = service.get(id);
        existing.setTitle(task.getTitle());
        existing.setMemo(task.getMemo());
        existing.setDueDate(task.getDueDate());
        service.save(existing);
        redirect.addFlashAttribute("message", "タスクを更新しました");
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        service.toggleDone(id);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        service.delete(id);
        redirect.addFlashAttribute("message", "タスクを削除しました");
        return "redirect:/tasks";
    }
}
