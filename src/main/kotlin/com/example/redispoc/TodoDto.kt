package com.example.redispoc

data class TodoDto(
    val id: String,
    val title: String,
    val completed: Boolean,
    val metadata: TodoMetadataDto? = null,
)

fun TodoDto.toTodo(): Todo {
    return Todo(
        id = id,
        title = title,
        completed = completed,
        metadata = metadata?.toTodoMetadata(),
    )
}

fun TodoMetadataDto.toTodoMetadata(): TodoMetadata {
    return TodoMetadata(
        latestEditionDeviceId = latestEditionDeviceId,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
