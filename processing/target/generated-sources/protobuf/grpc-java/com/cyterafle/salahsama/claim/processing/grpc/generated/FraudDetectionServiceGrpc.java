package com.cyterafle.salahsama.claim.processing.grpc.generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service de détection de fraude
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.62.2)",
    comments = "Source: fraud_detection.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FraudDetectionServiceGrpc {

  private FraudDetectionServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "fraud.FraudDetectionService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest,
      com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse> getAnalyzeClaimMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AnalyzeClaim",
      requestType = com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest.class,
      responseType = com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest,
      com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse> getAnalyzeClaimMethod() {
    io.grpc.MethodDescriptor<com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest, com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse> getAnalyzeClaimMethod;
    if ((getAnalyzeClaimMethod = FraudDetectionServiceGrpc.getAnalyzeClaimMethod) == null) {
      synchronized (FraudDetectionServiceGrpc.class) {
        if ((getAnalyzeClaimMethod = FraudDetectionServiceGrpc.getAnalyzeClaimMethod) == null) {
          FraudDetectionServiceGrpc.getAnalyzeClaimMethod = getAnalyzeClaimMethod =
              io.grpc.MethodDescriptor.<com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest, com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AnalyzeClaim"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FraudDetectionServiceMethodDescriptorSupplier("AnalyzeClaim"))
              .build();
        }
      }
    }
    return getAnalyzeClaimMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest,
      com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse> getGetCustomerRiskScoreMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetCustomerRiskScore",
      requestType = com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest.class,
      responseType = com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest,
      com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse> getGetCustomerRiskScoreMethod() {
    io.grpc.MethodDescriptor<com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest, com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse> getGetCustomerRiskScoreMethod;
    if ((getGetCustomerRiskScoreMethod = FraudDetectionServiceGrpc.getGetCustomerRiskScoreMethod) == null) {
      synchronized (FraudDetectionServiceGrpc.class) {
        if ((getGetCustomerRiskScoreMethod = FraudDetectionServiceGrpc.getGetCustomerRiskScoreMethod) == null) {
          FraudDetectionServiceGrpc.getGetCustomerRiskScoreMethod = getGetCustomerRiskScoreMethod =
              io.grpc.MethodDescriptor.<com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest, com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetCustomerRiskScore"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FraudDetectionServiceMethodDescriptorSupplier("GetCustomerRiskScore"))
              .build();
        }
      }
    }
    return getGetCustomerRiskScoreMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FraudDetectionServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FraudDetectionServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FraudDetectionServiceStub>() {
        @java.lang.Override
        public FraudDetectionServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FraudDetectionServiceStub(channel, callOptions);
        }
      };
    return FraudDetectionServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FraudDetectionServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FraudDetectionServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FraudDetectionServiceBlockingStub>() {
        @java.lang.Override
        public FraudDetectionServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FraudDetectionServiceBlockingStub(channel, callOptions);
        }
      };
    return FraudDetectionServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FraudDetectionServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FraudDetectionServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FraudDetectionServiceFutureStub>() {
        @java.lang.Override
        public FraudDetectionServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FraudDetectionServiceFutureStub(channel, callOptions);
        }
      };
    return FraudDetectionServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service de détection de fraude
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Analyse une réclamation pour détecter la fraude
     * </pre>
     */
    default void analyzeClaim(com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest request,
        io.grpc.stub.StreamObserver<com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAnalyzeClaimMethod(), responseObserver);
    }

    /**
     * <pre>
     * Obtient le score de risque d'un client
     * </pre>
     */
    default void getCustomerRiskScore(com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest request,
        io.grpc.stub.StreamObserver<com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetCustomerRiskScoreMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service FraudDetectionService.
   * <pre>
   * Service de détection de fraude
   * </pre>
   */
  public static abstract class FraudDetectionServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return FraudDetectionServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service FraudDetectionService.
   * <pre>
   * Service de détection de fraude
   * </pre>
   */
  public static final class FraudDetectionServiceStub
      extends io.grpc.stub.AbstractAsyncStub<FraudDetectionServiceStub> {
    private FraudDetectionServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FraudDetectionServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FraudDetectionServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Analyse une réclamation pour détecter la fraude
     * </pre>
     */
    public void analyzeClaim(com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest request,
        io.grpc.stub.StreamObserver<com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAnalyzeClaimMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Obtient le score de risque d'un client
     * </pre>
     */
    public void getCustomerRiskScore(com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest request,
        io.grpc.stub.StreamObserver<com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetCustomerRiskScoreMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service FraudDetectionService.
   * <pre>
   * Service de détection de fraude
   * </pre>
   */
  public static final class FraudDetectionServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FraudDetectionServiceBlockingStub> {
    private FraudDetectionServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FraudDetectionServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FraudDetectionServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Analyse une réclamation pour détecter la fraude
     * </pre>
     */
    public com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse analyzeClaim(com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAnalyzeClaimMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Obtient le score de risque d'un client
     * </pre>
     */
    public com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse getCustomerRiskScore(com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetCustomerRiskScoreMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service FraudDetectionService.
   * <pre>
   * Service de détection de fraude
   * </pre>
   */
  public static final class FraudDetectionServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<FraudDetectionServiceFutureStub> {
    private FraudDetectionServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FraudDetectionServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FraudDetectionServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Analyse une réclamation pour détecter la fraude
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse> analyzeClaim(
        com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAnalyzeClaimMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Obtient le score de risque d'un client
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse> getCustomerRiskScore(
        com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetCustomerRiskScoreMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ANALYZE_CLAIM = 0;
  private static final int METHODID_GET_CUSTOMER_RISK_SCORE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ANALYZE_CLAIM:
          serviceImpl.analyzeClaim((com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest) request,
              (io.grpc.stub.StreamObserver<com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse>) responseObserver);
          break;
        case METHODID_GET_CUSTOMER_RISK_SCORE:
          serviceImpl.getCustomerRiskScore((com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest) request,
              (io.grpc.stub.StreamObserver<com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getAnalyzeClaimMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisRequest,
              com.cyterafle.salahsama.claim.processing.grpc.generated.FraudAnalysisResponse>(
                service, METHODID_ANALYZE_CLAIM)))
        .addMethod(
          getGetCustomerRiskScoreMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskRequest,
              com.cyterafle.salahsama.claim.processing.grpc.generated.CustomerRiskResponse>(
                service, METHODID_GET_CUSTOMER_RISK_SCORE)))
        .build();
  }

  private static abstract class FraudDetectionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FraudDetectionServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.cyterafle.salahsama.claim.processing.grpc.generated.FraudDetectionProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FraudDetectionService");
    }
  }

  private static final class FraudDetectionServiceFileDescriptorSupplier
      extends FraudDetectionServiceBaseDescriptorSupplier {
    FraudDetectionServiceFileDescriptorSupplier() {}
  }

  private static final class FraudDetectionServiceMethodDescriptorSupplier
      extends FraudDetectionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    FraudDetectionServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (FraudDetectionServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FraudDetectionServiceFileDescriptorSupplier())
              .addMethod(getAnalyzeClaimMethod())
              .addMethod(getGetCustomerRiskScoreMethod())
              .build();
        }
      }
    }
    return result;
  }
}
