package com.example.springdatareactiveredisdemo

import io.lettuce.core.ReadFrom
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    @Value("\${redis.master.host}")
    private val redisMasterHost: String,
    @Value("\${redis.master.port}")
    private val redisMasterPort: Int,
    @Value("\${redis.replica.host}")
    private val redisReplicaHost: String,
    @Value("\${redis.replica.port}")
    private val redisReplicaPort: Int,
    @Value("\${redis.read-from-replica}")
    private val readFromReplica: Boolean,
    @Value("\${redis.cluster-mode}")
    private val clusterMode: Boolean,
) {
    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        if (readFromReplica) {
            val clientConfig = LettuceClientConfiguration
                .builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build()
            // AWS Elasticache doesn't allow topology discovery, so a static topology must be informed
            val serverConfig = RedisStaticMasterReplicaConfiguration(redisMasterHost, redisMasterPort)
            serverConfig.addNode(redisReplicaHost, redisReplicaPort)

            return LettuceConnectionFactory(serverConfig, clientConfig)
        }

        return LettuceConnectionFactory(redisMasterHost, redisMasterPort)
    }

    @Bean
    fun reactiveRedisTemplate(
        reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, Todo> {
        val keySerializer = StringRedisSerializer()
        val serializer = Jackson2JsonRedisSerializer(Todo::class.java)
        val builder: RedisSerializationContext.RedisSerializationContextBuilder<String, Todo> =
            RedisSerializationContext.newSerializationContext(keySerializer)
        val context = builder
            .value(serializer)
            .hashKey(serializer)
            .hashValue(serializer)
            .build()

        return ReactiveRedisTemplate(reactiveRedisConnectionFactory, context)
    }
}
