package com.eclectics.collaboration.Tool.configuration;

import com.eclectics.collaboration.Tool.dto.ScratchpadDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.data.redis.url}")
    private String redisUrl;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        URI uri = URI.create(redisUrl);
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName(uri.getHost());
        serverConfig.setPort(uri.getPort());

        if (uri.getUserInfo() != null) {
            String[] userInfo = uri.getUserInfo().split(":");
            if (userInfo.length == 2) {
                serverConfig.setUsername(userInfo[0]);
                serverConfig.setPassword(userInfo[1]);
            } else {
                serverConfig.setPassword(userInfo[0]);
            }
        }

        boolean useSsl = "rediss".equalsIgnoreCase(uri.getScheme());

        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigBuilder =
                LettuceClientConfiguration.builder();
        if (useSsl) {
            clientConfigBuilder.useSsl();
        }
        LettuceClientConfiguration clientConfig = clientConfigBuilder.build();

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        return JsonMapper.builder().build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       ObjectMapper redisObjectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        RedisSerializer<Object> serializer = new GenericJacksonJsonRedisSerializer(redisObjectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     ObjectMapper redisObjectMapper) {

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        java.util.Map<String, RedisCacheConfiguration> perCacheConfigs = new java.util.HashMap<>();

        perCacheConfigs.put("workspace_by_id", defaultCacheConfig.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                        typedSerializer(redisObjectMapper, WorkSpaceResponseDTO.class))));

        perCacheConfigs.put("user_scratchpad", defaultCacheConfig.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                        typedSerializer(redisObjectMapper, ScratchpadDTO.class))));

        perCacheConfigs.put("workspaces_my", defaultCacheConfig.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                        typedListSerializer(redisObjectMapper, WorkSpaceResponseDTO.class))));

        perCacheConfigs.put("workspaces_starred", defaultCacheConfig.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                        typedListSerializer(redisObjectMapper, WorkSpaceResponseDTO.class))));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig) // fallback for any other @Cacheable you add later
                .withInitialCacheConfigurations(perCacheConfigs)
                .build();
    }

    private <T> RedisSerializer<T> typedSerializer(ObjectMapper mapper, Class<T> type) {
        return new RedisSerializer<T>() {
            @Override
            public byte[] serialize(T value) {
                return value == null ? new byte[0] : mapper.writeValueAsBytes(value);
            }
            @Override
            public T deserialize(byte[] bytes) {
                return (bytes == null || bytes.length == 0) ? null : mapper.readValue(bytes, type);
            }
        };
    }

    private <T> RedisSerializer<java.util.List<T>> typedListSerializer(ObjectMapper mapper, Class<T> elementType) {
        tools.jackson.databind.JavaType listType = mapper.getTypeFactory()
                .constructCollectionType(java.util.List.class, elementType);
        return new RedisSerializer<java.util.List<T>>() {
            @Override
            public byte[] serialize(java.util.List<T> value) {
                return value == null ? new byte[0] : mapper.writeValueAsBytes(value);
            }
            @Override
            @SuppressWarnings("unchecked")
            public java.util.List<T> deserialize(byte[] bytes) {
                return (bytes == null || bytes.length == 0) ? null : mapper.readValue(bytes, listType);
            }
        };
    }
}