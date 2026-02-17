package com.example.redispoc

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.lettuce.core.ReadFrom
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule


@Configuration
class Config {
    @Bean
    fun objectMapper(): ObjectMapper {
        return JsonMapper
            .builder()
            .addModule(kotlinModule { })
            .build()
    }

    @Bean
    fun lettuceClientConfigurationBuilderCustomizer(): LettuceClientConfigurationBuilderCustomizer {
        return LettuceClientConfigurationBuilderCustomizer { it.readFrom(ReadFrom.REPLICA_PREFERRED) }
    }

    @Bean
    fun reactiveRedisTemplate(
        objectMapper: ObjectMapper,
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, Todo> {
        val keySerializer = StringRedisSerializer()
        val valueSerializer = JacksonJsonRedisSerializer(Todo::class.java)
        val serializationContext = RedisSerializationContext.newSerializationContext<String, Todo>()
            .key(keySerializer)
            .value(valueSerializer)
            .hashKey(keySerializer)
            .hashValue(GenericJacksonJsonRedisSerializer(objectMapper))
            .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}
