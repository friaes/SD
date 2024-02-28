package pt.ulisboa.tecnico.tuplespaces.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import static io.grpc.Status.INVALID_ARGUMENT;

public class ServerMain {

    /** Set flag to true to print debug messages. 
	   * The flag can be set using the -Ddebug command line option. */
	  private static boolean DEBUG_FLAG = false;

	  /** Helper method to print debug messages. */
	  private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.print(debugMessage);
	  }

    public static void main(String[] args) throws IOException, InterruptedException {
    
		System.out.println(ServerMain.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", ServerMain.class.getName());
			return;
		}

		if ((args.length == 3) && args[2].equals("-debug"))
			DEBUG_FLAG = true;

		final int port = Integer.parseInt(args[0]);
		final BindableService impl = new ServiceImpl(DEBUG_FLAG);
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

