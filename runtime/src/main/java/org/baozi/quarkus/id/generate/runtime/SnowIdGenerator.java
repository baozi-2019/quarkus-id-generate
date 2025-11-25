package org.baozi.quarkus.id.generate.runtime;

import io.grpc.ManagedChannel;
import io.smallrye.stork.Stork;
import io.smallrye.stork.api.ServiceInstance;
import org.baozi.quarkus.id.generate.runtime.grpc.IdGenerateGrpcServiceGrpc;
import org.baozi.quarkus.id.generate.runtime.grpc.IdGenerateReply;
import org.baozi.quarkus.id.generate.runtime.grpc.IdGenerateRequest;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serial;
import java.util.Optional;

public class SnowIdGenerator implements IdentifierGenerator {
    @Serial
    private static final long serialVersionUID = -3985287514627001044L;

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        GrpcManagement grpcManagement = GrpcManagement.getInstance();

        SnowIdServiceNameCache snowIdServiceNameCache = SnowIdServiceNameCache.getInstance();
        Optional<String> serviceNameOpt = snowIdServiceNameCache.getServiceName(o.getClass());
        if (serviceNameOpt.isEmpty()) {
            throw new IdGenException("未找到对应注解字段");
        }

        String serviceName = serviceNameOpt.get();

        ManagedChannel channel;
        do {
            ServiceInstance serviceInstance = Stork.getInstance().getService(serviceName).selectInstance()
                    .await().indefinitely();
            channel = grpcManagement.getChannel(serviceInstance.getHost(), serviceInstance.getPort());
        } while (channel.isTerminated());

        IdGenerateGrpcServiceGrpc.IdGenerateGrpcServiceBlockingStub stub = IdGenerateGrpcServiceGrpc.newBlockingStub(channel);

        IdGenerateReply idGenerateReply = stub.snowflakeId(IdGenerateRequest.newBuilder().build());


        return idGenerateReply.getId();
    }
}
