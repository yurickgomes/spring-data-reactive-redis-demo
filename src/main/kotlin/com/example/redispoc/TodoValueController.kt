package com.example.redispoc

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/todo-redis-value")
class TodoValueController(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Todo>,
) {
    @GetMapping("/{id}")
    fun findTodo(@PathVariable id: String): Mono<TodoDto> {
        return reactiveRedisTemplate.opsForValue().get(id).map { it.toTodoDto() }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addTodo(@RequestBody todo: TodoDto): Mono<Unit> {
        return reactiveRedisTemplate
            .opsForValue()
            .set(todo.id, todo.toTodo())
            .then(Mono.empty())
    }
}
