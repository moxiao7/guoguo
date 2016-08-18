package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Guo server implementation.
 */
public class GuoServer {

  private static final Logger logger = Logger.getLogger(GuoServer.class.getName());

  private static final int PORT = 50051;

  private Server server;

  private void start() throws IOException {
    this.server = ServerBuilder.forPort(PORT).addService(new GreeterImpl())
        .build().start();
    logger.info("Server started on " + PORT);
    Runtime.getRuntime().addShutdownHook(
        new Thread() {
          @Override
          public void run() {
            System.err.println("Shutting down server");
            GuoServer.this.stop();
            System.err.println("Server down");
          }
        }
    );
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    GuoServer server = new GuoServer();
    server.start();
    server.blockUntilShutdown();
  }

  private static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> observer) {
      logger.info("received: " + request.getName());
      HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
      observer.onNext(reply);
      observer.onCompleted();
    }
  }
}
