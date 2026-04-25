package com.example.webbansach_backend.config.redis.cluster;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class ClusterConfig {
    @Bean
    public LettuceConnectionFactory connectionFactory(){
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration() ;
        redisClusterConfiguration.clusterNode("192.168.0.105" , 6379) ;
        redisClusterConfiguration.clusterNode("192.168.0.105" , 6380) ;
        redisClusterConfiguration.clusterNode("192.168.0.105" , 6381) ;

        // cấu hình topology
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.
                builder().
                refreshPeriod(Duration.ofMinutes(10)).build() ;
        // cấu hình client option (gắn topology)
        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder().
                topologyRefreshOptions(clusterTopologyRefreshOptions).
                build() ;
        // cấu hình cho connection
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder().
                readFrom(ReadFrom.REPLICA_PREFERRED).
                clientOptions(clusterClientOptions).
                build() ;
        return new LettuceConnectionFactory(redisClusterConfiguration , lettuceClientConfiguration) ;
    }
}
