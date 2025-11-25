package org.baozi.quarkus.id.generate.runtime;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SnowIdServiceNameCache {
    private static final SnowIdServiceNameCache INSTANCE = new SnowIdServiceNameCache();
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    protected static SnowIdServiceNameCache getInstance() {
        return INSTANCE;
    }

    protected Optional<String> getServiceName(final Class<?> entityClass) {
        String className = entityClass.getCanonicalName();
        return Optional.ofNullable(cache.computeIfAbsent(className, k -> findSnowIdServiceName(entityClass)));
    }

    private String findSnowIdServiceName(Class<?> entityClass) {
        if (entityClass == Object.class) return null;
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            SnowId snowId = field.getDeclaredAnnotation(SnowId.class);
            if (snowId != null) return snowId.grpcServiceName();
        }
        return findSnowIdServiceName(entityClass.getSuperclass());
    }
}
