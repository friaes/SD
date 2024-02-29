package pt.ulisboa.tecnico.tuplespaces.client;

import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.tuplespaces.client.grpc.ClientService;
import pt.ulisboa.tecnico.nameServer.contract.*;


public class ClientMain {

    /** Set flag to true to print debug messages. 
	 * The flag can be set using the -Ddebug command line option. */
	private static boolean DEBUG_FLAG = false;
    private static String targetDNS = "localhost: 5001";

    private final static ManagedChannel channelDNS = ManagedChannelBuilder.forTarget(targetDNS).usePlaintext().build();
    private final static NameServerServiceGrpc.NameServerServiceBlockingStub stubDNS = NameServerServiceGrpc.newBlockingStub(channelDNS);
    

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.print(debugMessage);
	}

    public static void main(String[] args) {


        if ((args.length == 3) && args[2].equals("-debug"))
			DEBUG_FLAG = true;

        debug(ClientMain.class.getSimpleName());

        // receive and print arguments
        debug(String.format("Received %d arguments%n", args.length));
        for (int i = 0; i < args.length; i++) {
            debug(String.format("arg[%d] = %s%n", i, args[i]));
        }

        // check arguments
        if (args.length != 3) {
            if (args.length == 2)
                System.err.println("Usage: mvn exec:java -Dexec.args=<host> <port>");
            else {
                System.err.println("Argument(s) missing!");
                return;
            }
        }

        // get the host and the port
        final List<String> target = lookupDNS("A", "TupleSpaces");
		debug("Target: " + target.get(0) + "\n");


        ClientService service = new ClientService(target.get(0), DEBUG_FLAG);
        CommandProcessor parser = new CommandProcessor(service);
        parser.parseInput();
        service.shutdown();

    }

    
    public static List<String> lookupDNS(String qualifier, String service){
        NameServer.LookupRequest request = NameServer.LookupRequest.newBuilder().setQualifier(qualifier).setService(service).build();
        List<String> result = stubDNS.lookup(request).getAddressList();
        debug(result.toString() + "\n");
        return result;
    }

}
