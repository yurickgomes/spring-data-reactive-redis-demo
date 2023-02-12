package com.example.springdatareactiveredisdemo

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/todo-hash")
class TodoHashController(
    private val todoOpsForHashLoader: TodoOpsForHashLoader,
) {
    @PostMapping(produces = ["application/json"])
    @ResponseStatus(code = HttpStatus.CREATED)
    suspend fun createTodoHash(@RequestBody todoReqBodyDto: TodoReqBodyDto) {
        require(todoReqBodyDto.description != null) { "description is required" }
        val newTodo = Todo(
            description = todoReqBodyDto.description,
            completed = todoReqBodyDto.completed ?: false,
        )
        val key = newTodo.id
        todoOpsForHashLoader.put(key, newTodo)
    }

    @GetMapping("/{id}", produces = ["application/json"])
    suspend fun getTodoHash(@PathVariable id: String): Todo {
        return todoOpsForHashLoader.get(id) ?: throw TodoNotFoundException()
    }

    @PutMapping("/{id}", produces = ["application/json"])
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    suspend fun updateTodoHash(@PathVariable id: String, @RequestBody todoReqBodyDto: TodoReqBodyDto) {
        require(todoReqBodyDto.id != null) { "id is required" }
        require(todoReqBodyDto.description != null) { "description is required" }
        require(todoReqBodyDto.completed != null) { "completed is required" }
        val newTodo = Todo(
            id = todoReqBodyDto.id,
            description = todoReqBodyDto.description,
            completed = todoReqBodyDto.completed,
        )
        todoOpsForHashLoader.put(id, newTodo)
    }

    @DeleteMapping("/{id}", produces = ["application/json"])
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    suspend fun deleteTodoHash(@PathVariable id: String) {
        todoOpsForHashLoader.del(id)
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
