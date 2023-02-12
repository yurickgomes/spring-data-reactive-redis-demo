package com.example.springdatareactiveredisdemo

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component

@Component
class TodoLoader(
    private val reactiveRedisOperations: ReactiveRedisOperations<String, Todo>,
) {
    suspend fun set(key: String, value: Todo): Boolean {
        return reactiveRedisOperations
            .opsForValue()
            .set(key, value)
            .awaitSingle()
    }

    suspend fun get(key: String): Todo? {
        return reactiveRedisOperations
            .opsForValue()
            .get(key)
            .awaitSingleOrNull()
    }
}
