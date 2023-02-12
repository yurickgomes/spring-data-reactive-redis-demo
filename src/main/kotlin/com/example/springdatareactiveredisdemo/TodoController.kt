package com.example.springdatareactiveredisdemo

import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/todo")
class TodoController(
    private val todoLoader: TodoLoader,
) {
    @GetMapping("/{id}", produces = ["application/json"])
    suspend fun getTodo(@PathVariable id: String): Todo? {
        return todoLoader.get(id) ?: throw TodoNotFoundException()
    }

    @PostMapping(produces = ["application/json"])
    @ResponseStatus(code = HttpStatus.CREATED)
    suspend fun createTodo(@RequestBody todoReqBodyDto: TodoReqBodyDto) {
        require(todoReqBodyDto.description != null) { "description is required" }
        val key = UUID.randomUUID().toString()
        val newTodo = Todo(
            id = key,
            description = todoReqBodyDto.description,
            completed = todoReqBodyDto.completed ?: false,
        )
        todoLoader.set(key, newTodo)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(TodoNotFoundException::class)
    fun handleTodoNotFoundException(e: TodoNotFoundException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.NOT_FOUND)
    }
}
