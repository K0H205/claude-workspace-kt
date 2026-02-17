@file:Suppress("unused")

package com.example.cleanarchitecture.study

import org.slf4j.LoggerFactory

// ============================================================
// Kotlin inline 関数 学習用サンプル
// ============================================================

// ------------------------------------------------------------
// 1. 基本: inline vs 非inline
//    inline を付けると、呼び出し箇所にコードが展開され、
//    ラムダ用のオブジェクト生成が不要になる
// ------------------------------------------------------------

// 非inline — 呼び出しのたびに Function オブジェクトが生成される
fun <T> withoutInline(block: () -> T): T {
    println("before")
    val result = block()
    println("after")
    return result
}

// inline — コードが呼び出し元に展開され、オブジェクト生成ゼロ
inline fun <T> withInline(block: () -> T): T {
    println("before")
    val result = block()
    println("after")
    return result
}

// ------------------------------------------------------------
// 2. 実用例: 計測ユーティリティ
//    短い高階関数は inline の最適な対象
// ------------------------------------------------------------

inline fun <T> measureTimeNanos(block: () -> T): Pair<T, Long> {
    val start = System.nanoTime()
    val result = block()
    val elapsed = System.nanoTime() - start
    return result to elapsed
}

// 使用例:
// val (task, time) = measureTimeNanos { taskRepository.save(task) }

// ------------------------------------------------------------
// 3. reified — inline でのみ使える型パラメータの実体化
//    通常、ジェネリクスの型情報は実行時に消去(型消去)されるが、
//    reified を使うと実行時にも型情報にアクセスできる
// ------------------------------------------------------------

// reified なし — Class を明示的に渡す必要がある
fun <T> createLoggerVerbose(clazz: Class<T>): org.slf4j.Logger {
    return LoggerFactory.getLogger(clazz)
}
// 呼び出し: createLoggerVerbose(TaskService::class.java)

// reified あり — 型推論だけで OK
inline fun <reified T> createLogger(): org.slf4j.Logger {
    return LoggerFactory.getLogger(T::class.java)
}
// 呼び出し: createLogger<TaskService>()

// reified で型チェック
inline fun <reified T> Any.isType(): Boolean = this is T

// reified でリストのフィルタリング
inline fun <reified T> Collection<*>.filterByType(): List<T> {
    return filterIsInstance<T>()
}

// ------------------------------------------------------------
// 4. non-local return
//    inline ラムダの中では、外側の関数を直接 return できる
// ------------------------------------------------------------

data class Item(val name: String, val active: Boolean)

// inline の forEach では non-local return が可能
fun findFirstActive(items: List<Item>): Item? {
    items.forEach { item ->        // forEach は標準ライブラリで inline 定義
        if (item.active) return item  // findFirstActive から直接 return
    }
    return null
}

// 非inline の場合はラベル付き return しかできない
fun findFirstActiveWithLabel(items: List<Item>): Item? {
    var result: Item? = null
    items.forEachManual { item ->
        if (item.active) {
            result = item
            return@forEachManual  // ラムダだけを抜ける（外側の関数は抜けられない）
        }
    }
    return result
}

// 非inline の forEach 相当（比較用）
fun <T> List<T>.forEachManual(action: (T) -> Unit) {
    for (element in this) action(element)
}

// ------------------------------------------------------------
// 5. noinline — 一部のラムダだけインライン化を除外する
//    ラムダを変数に保存したい場合などに使う
// ------------------------------------------------------------

inline fun doWork(
    setup: () -> Unit,
    noinline callback: () -> Unit   // このラムダは変数に保存可能
): () -> Unit {
    setup()                          // こちらはインライン展開される
    return callback                  // noinline なので返り値にできる
}

// ------------------------------------------------------------
// 6. crossinline — non-local return を禁止する
//    ラムダが別のコンテキスト（別スレッド等）で実行される場合に使う
// ------------------------------------------------------------

inline fun runAsync(crossinline block: () -> Unit) {
    val thread = Thread {
        block()  // 別スレッドで実行されるため non-local return は危険
        // crossinline がないと、block 内の return が呼び出し元を
        // 抜けようとするが、別スレッドなので不可能
    }
    thread.start()
}

// ------------------------------------------------------------
// 7. 実践パターン: リトライ処理
// ------------------------------------------------------------

inline fun <T> retry(
    maxAttempts: Int = 3,
    block: (attempt: Int) -> T
): T {
    var lastException: Exception? = null
    for (attempt in 1..maxAttempts) {
        try {
            return block(attempt)
        } catch (e: Exception) {
            lastException = e
            if (attempt == maxAttempts) break
            Thread.sleep(1000L * attempt)  // 簡易バックオフ
        }
    }
    throw lastException!!
}

// 使用例:
// val result = retry(maxAttempts = 3) { attempt ->
//     println("試行 $attempt 回目")
//     repository.save(task)
// }

// ------------------------------------------------------------
// 8. 実践パターン: スコープ関数風のビルダー
// ------------------------------------------------------------

class TaskQuery {
    var status: String? = null
    var priority: String? = null
    var limit: Int = 10

    override fun toString(): String =
        "TaskQuery(status=$status, priority=$priority, limit=$limit)"
}

inline fun buildQuery(configure: TaskQuery.() -> Unit): TaskQuery {
    val query = TaskQuery()
    query.configure()  // インライン展開 — オブジェクト生成なし
    return query
}

// 使用例:
// val query = buildQuery {
//     status = "PENDING"
//     priority = "HIGH"
//     limit = 20
// }

// ------------------------------------------------------------
// 9. 使うべきでない場面（アンチパターン）
// ------------------------------------------------------------

// ❌ 本体が大きすぎる — 呼び出し箇所ごとにコードが複製される
// inline fun heavyOperation(block: () -> Unit) {
//     // ... 50行の処理 ...
//     block()
//     // ... 50行の処理 ...
// }

// ❌ ラムダを取らない — inline のメリットがほぼない
// inline fun add(a: Int, b: Int): Int = a + b

// ❌ 再帰関数 — 自分自身を展開できないためコンパイルエラー
// inline fun factorial(n: Int): Int =
//     if (n <= 1) 1 else n * factorial(n - 1)

// ============================================================
// まとめ
// ============================================================
// inline を使うべき場面:
//   1. ラムダを受け取る短いユーティリティ関数
//   2. reified で型情報を実行時に使いたい場合（inline 必須）
//   3. non-local return を許可したい場合
//   4. DSL やビルダーパターン
//
// inline を避けるべき場面:
//   - 関数本体が大きい（バイナリ膨張）
//   - ラムダを引数に取らない通常の関数
//   - ラムダを変数に保存する必要がある（→ noinline）
//   - 再帰関数、open/override な関数
// ============================================================
