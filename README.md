# Clean Architecture - Kotlin + Spring Boot

クリーンアーキテクチャの学習用サンプルプロジェクト。タスク管理ドメインを題材に、レイヤー分離・依存性逆転を実装。

## アーキテクチャ

```
Presentation (REST Controller, DTO)
    ↓ depends on
Application (UseCase interfaces & Service)
    ↓ depends on
Domain (Entity, Repository interface) ← 最内層、フレームワーク依存ゼロ
    ↑ implements
Infrastructure (JPA Entity, Spring Data Repo, Adapter, Config)
```

## パッケージ構成

```
com.example.cleanarchitecture/
├── domain/
│   ├── model/          Task, TaskStatus, TaskPriority
│   ├── repository/     TaskRepository (インターフェース)
│   └── exception/      DomainException
├── application/
│   ├── usecase/        CreateTask, GetTask, ListTasks, CompleteTask, DeleteTask
│   └── service/        TaskService (ユースケース実装)
├── infrastructure/
│   ├── entity/         TaskJpaEntity
│   ├── repository/     SpringDataTaskRepository
│   ├── adapter/        TaskPersistenceAdapter
│   └── configuration/  BeanConfiguration
└── presentation/
    ├── rest/           TaskController
    ├── dto/            CreateTaskRequest, TaskResponse
    └── exception/      GlobalExceptionHandler
```

## 技術スタック

- Kotlin 2.1 / Spring Boot 3.4 / Gradle Kotlin DSL
- Spring Data JPA + H2 (インメモリDB)
- JUnit 5 + MockK + SpringMockK

## REST API

| Method | Path | 説明 |
|--------|------|------|
| POST | `/api/tasks` | タスク作成 |
| GET | `/api/tasks/{id}` | タスク取得 |
| GET | `/api/tasks?status=XXX` | タスク一覧（フィルタ可） |
| PATCH | `/api/tasks/{id}/complete` | タスク完了 |
| DELETE | `/api/tasks/{id}` | タスク削除 |

## 実行方法

```bash
# ビルド
./gradlew build

# テスト
./gradlew test

# 起動
./gradlew bootRun
```

## 学習ポイント

1. **依存性逆転** - Domain 層で `TaskRepository` インターフェースを定義し、Infrastructure 層で実装
2. **JPA エンティティとドメインエンティティの分離** - アノテーション汚染を防止
3. **明示的 Bean 登録** - `TaskService` に `@Service` を付けず `BeanConfiguration` で登録
4. **ユースケースごとのインターフェース** - インターフェース分離の原則を適用
5. **テスタビリティ** - Domain テストはフレームワーク不要、Application テストはモックのみ
