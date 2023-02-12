package com.example.springdatareactiveredisdemo

data class TodoReqBodyDto(
    val id: String? = null,
    val description: String? = null,
    val completed: Boolean? = null,
)
