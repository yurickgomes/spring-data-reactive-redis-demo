package com.example.springdatareactiveredisdemo

import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
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
    private val todoOpsForValueLoader: TodoOpsForValueLoader,
) {

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
        todoOpsForValueLoader.set(key, newTodo)
    }

    @GetMapping("/{id}", produces = ["application/json"])
    suspend fun getTodo(@PathVariable id: String): Todo? {
        return todoOpsForValueLoader.get(id) ?: throw TodoNotFoundException()
    }

    @DeleteMapping("/{id}", produces = ["application/json"])
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    suspend fun deleteTodo(@PathVariable id: String) {
        todoOpsForValueLoader.del(id)
    }

    @PostMapping("/{id}", produces = ["application/json"])
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    suspend fun updateTodo(@PathVariable id: String, @RequestBody todoReqBodyDto: TodoReqBodyDto) {
        require(todoReqBodyDto.id != null) { "id is required" }
        require(todoReqBodyDto.description != null) { "description is required" }
        require(todoReqBodyDto.completed != null) { "completed is required" }
        val newTodo = Todo(
            id = todoReqBodyDto.id,
            description = todoReqBodyDto.description,
            completed = todoReqBodyDto.completed,
        )
        todoOpsForValueLoader.set(id, newTodo)
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
