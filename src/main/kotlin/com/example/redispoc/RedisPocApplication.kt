package com.example.redispoc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisPocApplication

fun main(args: Array<String>) {
	runApplication<RedisPocApplication>(*args)
}
