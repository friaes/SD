package pt.ulisboa.tecnico.tuplespaces.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import static io.grpc.Status.INVALID_ARGUMENT;

public class ServerMain {

    /** Set flag to true to print debug messages. 
	   * The flag can be set using the -Ddebug command line option. */
	  private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	  /** Helper method to print debug messages. */
	  private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	  }

    public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println(ServerMain.class.getSimpleName());

		// receive and print arguments
		debug(String.format("Received %d arguments", args.length));
		for (int i = 0; i < args.length; i++) {
			debug(String.format("arg[%d] = %s", i, args[i]));
		}

		// check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", ServerMain.class.getName());
			return;
		}

		final int port = Integer.parseInt(args[1]);
		final BindableService impl = new ServiceImpl();
		debug("Port: " + port);

		// Create a new server to listen on port
		Server server = ServerBuilder.forPort(port).addService(impl).build();

		// Start the server
		server.start();

		// Server threads are running in the background.
		System.out.println("Server started");

		// Do not exit the main thread. Wait until server is terminated.
		server.awaitTermination();
    }
}

