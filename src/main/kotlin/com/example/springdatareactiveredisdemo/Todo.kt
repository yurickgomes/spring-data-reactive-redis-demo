package com.example.springdatareactiveredisdemo

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID
import org.springframework.data.redis.core.RedisHash

@RedisHash
data class Todo(
    @JsonProperty("id")
    val id: String = UUID.randomUUID().toString(),
    @JsonProperty("description")
    val description: String,
    @JsonProperty("completed")
    val completed: Boolean,
)
