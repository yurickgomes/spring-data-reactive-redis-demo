package com.example.redispoc

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash


@RedisHash
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
data class Todo(
    @Id
    val id: String,
    val title: String,
    val completed: Boolean,
    val metadata: TodoMetadata? = null,
)

fun Todo.toTodoDto(): TodoDto {
    return TodoDto(
        id = id,
        title = title,
        completed = completed,
        metadata = metadata?.toTodoMetadataDto(),
    )
}

fun TodoMetadata.toTodoMetadataDto(): TodoMetadataDto {
    return TodoMetadataDto(
        latestEditionDeviceId = latestEditionDeviceId,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

