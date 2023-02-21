package com.example.springdatareactiveredisdemo

import io.lettuce.core.ReadFrom
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer
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
import java.time.Duration

@Configuration
class RedisConfig(
    @Value("\${spring.redis.host}")
    private val redisHost: String,
    @Value("\${spring.redis.port}")
    private val redisPort: Int,
    @Value("\${spring.redis.ssl}")
    private val redisSsl: Boolean,
    @Value("\${spring.redis.timeout}")
    private val redisTimeout: Long,
    @Value("\${spring.redis.static-topology.replica-nodes:''}")
    private val redisStaticReplicaNodes: String,
) {
    @Bean
    @ConditionalOnProperty(prefix = "spring.data.redis.static-topology", name = ["enabled"], havingValue = "true")
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val clientConfigBuilder = LettuceClientConfiguration
            .builder()
            .commandTimeout(Duration.ofMillis(redisTimeout))

        if (redisSsl) {
            clientConfigBuilder.useSsl()
        }

        val clientConfig = clientConfigBuilder.build()
        // AWS Elasticache doesn't allow topology discovery, so a static topology must be informed
        val serverConfig = RedisStaticMasterReplicaConfiguration(redisHost, redisPort)
        redisStaticReplicaNodes.split(',').forEach {
            val replicaSplit = it.split(':')
            val replicaHost = replicaSplit[0]
            val replicaPort = replicaSplit[1].toInt()
            serverConfig.addNode(replicaHost, replicaPort)
        }

        return LettuceConnectionFactory(serverConfig, clientConfig)
    }

    @Bean
    fun lettuceClientConfigurationBuilderCustomizer(): LettuceClientConfigurationBuilderCustomizer {
        // read from replica on cluster mode
        return LettuceClientConfigurationBuilderCustomizer { it.readFrom(ReadFrom.REPLICA_PREFERRED) }
    }

    @Bean
    fun reactiveRedisTemplate(
        reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, Todo> {
        val keySerializer = StringRedisSerializer()
        val serializer = Jackson2JsonRedisSerializer(Todo::class.java)
        val builder: RedisSerializationContext.RedisSerializationContextBuilder<String, Todo> =
            RedisSerializationContext.newSerializationContext(keySerializer)
        val context = builder.value(serializer).hashKey(serializer).hashValue(serializer).build()

        return ReactiveRedisTemplate(reactiveRedisConnectionFactory, context)
    }
}
