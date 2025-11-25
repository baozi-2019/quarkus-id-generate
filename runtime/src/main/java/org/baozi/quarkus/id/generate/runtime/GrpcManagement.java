package org.baozi.quarkus.id.generate.runtime;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.time.Duration;

public class GrpcManagement {
    private static final GrpcManagement INSTANCE = new GrpcManagement();

    private final Cache<String, ManagedChannel> channelCache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofSeconds(20)).build();


    public static GrpcManagement getInstance() {
        return INSTANCE;
    }

    protected ManagedChannel getChannel(final String host, final int port) {
        String key = host + ":" + port;
        ManagedChannel managedChannel = channelCache.get(key, k -> reconnectChannel(host, port));

        if (managedChannel == null || managedChannel.isShutdown()) {
            managedChannel = reconnectChannel(host, port);
        }
        return managedChannel;
    }

    private ManagedChannel reconnectChannel(final String host, final int port) {
        return ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .enableRetry()
                .maxRetryAttempts(3)
                .build();
    }
}
