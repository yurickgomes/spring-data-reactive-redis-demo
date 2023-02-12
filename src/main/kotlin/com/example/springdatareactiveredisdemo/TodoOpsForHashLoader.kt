package com.example.springdatareactiveredisdemo

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component

@Component
class TodoOpsForHashLoader(
    private val reactiveRedisOperations: ReactiveRedisOperations<String, Todo>,
) {
    suspend fun put(hashKey: String, value: Todo): Boolean {
        return reactiveRedisOperations
            .opsForHash<String, Todo>()
            .put("todo:$hashKey", hashKey, value)
            .awaitSingle()
    }

    suspend fun get(hashKey: String): Todo? {
        return reactiveRedisOperations
            .opsForHash<String, Todo>()
            .get("todo:$hashKey", hashKey)
            .awaitSingleOrNull()
    }

    suspend fun del(hashKey: String): Boolean {
        return reactiveRedisOperations
            .opsForHash<String, Todo>()
            .delete("todo:$hashKey")
            .awaitSingle()
    }
}
