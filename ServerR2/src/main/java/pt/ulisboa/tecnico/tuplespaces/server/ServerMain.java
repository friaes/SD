package pt.ulisboa.tecnico.tuplespaces.server;

import io.grpc.*;

import java.io.IOException;
import java.util.List;
import pt.ulisboa.tecnico.nameServer.contract.*;

import static io.grpc.Status.INVALID_ARGUMENT;

public class ServerMain {

    /** Set flag to true to print debug messages. 
	   * The flag can be set using the -Ddebug command line option. */
	  private static boolean DEBUG_FLAG = false;
	  private static ManagedChannel channelDNS = null;
      private static NameServerServiceGrpc.NameServerServiceBlockingStub stubDNS = null;
	  private static String targetDNS = "localhost:5001";

	  /** Helper method to print debug messages. */
	  private static void debug(String debugMessage) {
		  if (DEBUG_FLAG)
			  System.err.print("[DEBUG] " + debugMessage);
	  }

    public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(ServerMain.class.getSimpleName());

		if ((args.length == 3) && args[2].equals("-debug"))
			DEBUG_FLAG = true;

		// receive and print arguments
		debug(String.format("Received %d arguments", args.length));
		for (int i = 0; i < args.length; i++) {
			debug(String.format("arg[%d] = %s", i, args[i]));
		}

		// check arguments
		if (args.length <= 1) {
			debug("Argument(s) missing!");
			debug(String.format("Usage: java %s port%n", ServerMain.class.getName()));
			return;
		}

		final int port;
		final int qualifier;
		
		port = Integer.parseInt(args[0]);
		qualifier = Char.parseChar(args[1]);
		

		final BindableService impl = new ServiceImpl(DEBUG_FLAG);
		debug("Port: " + port);
		debug("Qualifier: " + qualifier);

		// Create a new server to listen on port
		Server server = ServerBuilder.forPort(port).addService(impl).build();

		// Start the server
		server.start();

		Runtime.getRuntime().addShutdownHook(new Thread(()-> {
			deleteDNS("TupleSpaces", "localhost:" + port);
			channelDNS.shutdownNow();
		}));


		// Server threads are running in the background.
		System.out.println("Server started");

		
		// Register in DNS
		channelDNS = ManagedChannelBuilder.forTarget(targetDNS).usePlaintext().build();
        stubDNS = NameServerServiceGrpc.newBlockingStub(channelDNS);
		registerDNS(qualifier, "TupleSpaces", "localhost:" + port);

		// Do not exit the main thread. Wait until server is terminated.
		server.awaitTermination();
		
    }

	public static void registerDNS(String qualifier, String service, String address){
		try {
			NameServer.RegisterRequest request = NameServer.RegisterRequest.newBuilder().setQualifier(qualifier).setService(service).setAddress(address).build();
			stubDNS.register(request);
		}
		catch (StatusRuntimeException e) {
			System.out.println("Caught Exception with description: " + e.getStatus().getDescription());
		}
    }

	public static void deleteDNS(String service, String address){
		try {
			NameServer.DeleteRequest request = NameServer.DeleteRequest.newBuilder().setService(service).setAddress(address).build();
			stubDNS.delete(request);
		}
		catch (StatusRuntimeException e) {
			System.out.println("Caught Exception with description: " + e.getStatus().getDescription());
		}
    }
}

