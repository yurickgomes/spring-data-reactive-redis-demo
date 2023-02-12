package com.example.springdatareactiveredisdemo

import com.fasterxml.jackson.annotation.JsonProperty

data class Todo(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("completed")
    val completed: Boolean,
)
