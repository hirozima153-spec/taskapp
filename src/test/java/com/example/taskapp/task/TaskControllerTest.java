package com.example.taskapp.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired MockMvc mvc;
    @Autowired TaskRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void 一覧画面が表示される() throws Exception {
        mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/list"));
    }

    @Test
    void 登録できる() throws Exception {
        mvc.perform(post("/tasks").param("title", "牛乳を買う").param("dueDate", "2026-10-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        assertThat(repository.findAll()).singleElement()
                .satisfies(t -> assertThat(t.getTitle()).isEqualTo("牛乳を買う"));
    }

    @Test
    void タイトル未入力はエラーで保存されない() throws Exception {
        mvc.perform(post("/tasks").param("title", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/form"))
                .andExpect(model().attributeHasFieldErrors("task", "title"));

        assertThat(repository.count()).isZero();
    }

    @Test
    void 更新できる() throws Exception {
        Task saved = save("旧タイトル");

        mvc.perform(post("/tasks/{id}", saved.getId()).param("title", "新タイトル"))
                .andExpect(status().is3xxRedirection());

        assertThat(repository.findById(saved.getId())).get()
                .satisfies(t -> assertThat(t.getTitle()).isEqualTo("新タイトル"));
    }

    @Test
    void 完了を切り替えられる() throws Exception {
        Task saved = save("宿題");

        mvc.perform(post("/tasks/{id}/toggle", saved.getId())).andExpect(status().is3xxRedirection());

        assertThat(repository.findById(saved.getId())).get()
                .satisfies(t -> assertThat(t.isDone()).isTrue());
    }

    @Test
    void 削除できる() throws Exception {
        Task saved = save("消すタスク");

        mvc.perform(post("/tasks/{id}/delete", saved.getId())).andExpect(status().is3xxRedirection());

        assertThat(repository.count()).isZero();
    }

    @Test
    void タイトルで検索できる() throws Exception {
        save("牛乳を買う");
        save("掃除をする");

        mvc.perform(get("/tasks").param("keyword", "牛乳"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tasks", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void 存在しないIDは404() throws Exception {
        mvc.perform(get("/tasks/{id}/edit", 99999)).andExpect(status().isNotFound());
    }

    @Test
    void 編集フォームに期限がISO形式で表示される() throws Exception {
        Task t = new Task();
        t.setTitle("期限あり");
        t.setDueDate(java.time.LocalDate.of(2026, 10, 5));
        Task saved = repository.save(t);

        mvc.perform(get("/tasks/{id}/edit", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"2026-10-05\"")));
    }

    private Task save(String title) {
        Task t = new Task();
        t.setTitle(title);
        return repository.save(t);
    }
}
