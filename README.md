# TaskApp（TODO管理アプリ）

Spring Boot で作った簡単なタスク管理Webアプリです。

## 機能
- タスクの一覧・登録・編集・削除
- 完了／未完了の切り替え
- タイトル検索
- 入力チェック（タイトル必須・100文字以内、メモ500文字以内）
- 期限切れの未完了タスクを赤字表示

## 技術
Java 21 / Spring Boot 4.1 / Spring Data JPA / Thymeleaf / H2（ファイルDB） / JUnit 5 + MockMvc

## 起動方法
```bash
./mvnw spring-boot:run
```
ブラウザで http://localhost:8080 を開きます。データは `./data/` に保存されます。

## テスト
```bash
./mvnw test
```

## デモ公開（Render の無料枠）
1. このリポジトリを Render の「New > Web Service」で選ぶ
2. Runtime に **Docker** を選ぶ（`Dockerfile` を自動検出）
3. Instance Type は **Free** を選んで作成

- デモ用にメモリDB＋サンプルデータで起動します（再起動で初期状態に戻ります）
- 無料枠は一定時間アクセスがないとスリープするため、初回表示に30〜60秒かかることがあります
- 環境変数：`SEED_SAMPLE_DATA=true`（サンプル投入）、`DB_URL`（DB接続先。未指定はファイルDB）、`PORT`（待受ポート）

## 構成
```
src/main/java/com/example/taskapp/
  HomeController.java        / → /tasks へリダイレクト
  SampleDataLoader.java      デモ用サンプルデータ投入
  task/Task.java             エンティティ＋入力チェック
  task/TaskRepository.java   検索・並び順
  task/TaskService.java      業務ロジック
  task/TaskController.java   画面遷移
src/main/resources/templates/  Thymeleaf 画面
src/test/.../TaskControllerTest.java  画面・DB結合テスト（8件）
```
