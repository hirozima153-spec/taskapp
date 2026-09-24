package com.example.taskapp;

import com.example.taskapp.task.Task;
import com.example.taskapp.task.TaskRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** デモ公開用：DBが空のときだけサンプルデータを投入する（app.seed-sample-data=true のとき有効）。 */
@Component
@ConditionalOnProperty(name = "app.seed-sample-data", havingValue = "true")
public class SampleDataLoader implements CommandLineRunner {

    private final TaskRepository repository;

    public SampleDataLoader(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now();
        repository.save(task("見積書を送る", "先方への提出期限に注意", today.minusDays(2), false));
        repository.save(task("週次ミーティングの資料を作る", "進捗と課題を1枚にまとめる", today.plusDays(3), false));
        repository.save(task("牛乳を買う", null, today.plusDays(1), false));
        repository.save(task("部屋の掃除", null, null, false));
        repository.save(task("請求書の確認", "完了済みのサンプル", today.minusDays(5), true));
    }

    private Task task(String title, String memo, LocalDate dueDate, boolean done) {
        Task t = new Task();
        t.setTitle(title);
        t.setMemo(memo);
        t.setDueDate(dueDate);
        t.setDone(done);
        return t;
    }
}
