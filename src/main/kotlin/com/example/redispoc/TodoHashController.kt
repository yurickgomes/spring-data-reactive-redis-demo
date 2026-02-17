package com.example.redispoc

import tools.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/todo-redis-hash")
class TodoHashController(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Todo>,
    private val objectMapper: ObjectMapper,
) {
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findTodo(@PathVariable id: String): Mono<Todo> {
        return reactiveRedisTemplate.opsForHash<String, Any>().entries("todo::$id")
            .collectMap({ it.key }, { it.value })
            .map { objectMapper.convertValue(it, Todo::class.java) }
    }

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun addTodo(@RequestBody todo: TodoDto): Mono<Unit> {
        @Suppress("UNCHECKED_CAST")
        val map = objectMapper.convertValue(todo.toTodo(), Map::class.java) as Map<String, Any>
        return reactiveRedisTemplate
            .opsForHash<String, Any>()
            .putAll("todo::${todo.id}", map)
            .then(Mono.empty())
    }
}
