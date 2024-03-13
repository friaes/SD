package pt.ulisboa.tecnico.tuplespaces.client;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.tuplespaces.client.grpc.ClientService;
import pt.ulisboa.tecnico.nameServer.contract.*;


public class ClientMain {
    /* Set flag to true to print debug messages. 
	 * The flag can be set using the -Ddebug command line option. */
	private static boolean DEBUG_FLAG = false;
    private static String targetDNS = "localhost:5001";

    private final static ManagedChannel channelDNS = ManagedChannelBuilder.forTarget(targetDNS).usePlaintext().build();
    private final static NameServerServiceGrpc.NameServerServiceBlockingStub stubDNS = NameServerServiceGrpc.newBlockingStub(channelDNS);
    static final int numServers = 3;

    /* Helper method to print debug messages.*/
	private static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.print("[DEBUG] " + debugMessage);
	}

    public static void main(String[] args) {

        if ((args.length == 1) && args[0].equals("-debug"))
			DEBUG_FLAG = true;

        debug(ClientMain.class.getSimpleName());

        // receive and print arguments
        debug(String.format("Received %d arguments%n", args.length));
        for (int i = 0; i < args.length; i++) {
            debug(String.format("arg[%d] = %s%n", i, args[i]));
        }

        // get the host and the port
        final List<String> targets = new ArrayList<>();
        targets.add(lookupDNS("A", "TupleSpaces"));
        targets.add(lookupDNS("B", "TupleSpaces"));
        targets.add(lookupDNS("C", "TupleSpaces"));
        channelDNS.shutdown();

        for (int i = 0; i < numServers; i++){
            if (targets.get(i) != null)
                debug("Target: " + targets.get(i) + "\n");
            else {
                System.err.println("Not all servers are working");
                return;
            }
        }

        ClientService service = new ClientService(numServers, targets, DEBUG_FLAG);
        CommandProcessor parser = new CommandProcessor(service);
        parser.parseInput();
        service.shutdown();

    }

    public static String lookupDNS(String qualifier, String service){
        String result = new String();
        try {
            NameServer.LookupRequest request = NameServer.LookupRequest.newBuilder().setQualifier(qualifier).setService(service).build();
            result = stubDNS.lookup(request).getAddress();
            debug(result.toString() + "\n");
        } catch (StatusRuntimeException e) {
			System.out.println("Caught Exception with description: " + e.getStatus().getDescription());
		}
        return result;
    }
}
/* Código Anterior
public class ClientMain {

    /* Set flag to true to print debug messages. 
	 * The flag can be set using the -Ddebug command line option. 
	private static boolean DEBUG_FLAG = false;
    private static String targetDNS = "localhost:5001";

    private final static ManagedChannel channelDNS = ManagedChannelBuilder.forTarget(targetDNS).usePlaintext().build();
    private final static NameServerServiceGrpc.NameServerServiceBlockingStub stubDNS = NameServerServiceGrpc.newBlockingStub(channelDNS);
    

	/* Helper method to print debug messages. 
	private static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.print("[DEBUG] " + debugMessage);
	}

    public static void main(String[] args) {


        if ((args.length == 1) && args[0].equals("-debug"))
			DEBUG_FLAG = true;

        debug(ClientMain.class.getSimpleName());

        // receive and print arguments
        debug(String.format("Received %d arguments%n", args.length));
        for (int i = 0; i < args.length; i++) {
            debug(String.format("arg[%d] = %s%n", i, args[i]));
        }

        // get the host and the port
        final List<String> target = lookupDNS("A", "TupleSpaces");
        channelDNS.shutdown();

        if (target != null) {
            debug("Target: " + target.get(0) + "\n");

            ClientService service = new ClientService(target.get(0), DEBUG_FLAG);
            CommandProcessor parser = new CommandProcessor(service);
            parser.parseInput();
            service.shutdown();
        }
    }

    
    public static List<String> lookupDNS(String qualifier, String service){
        List<String> result = null;
        try {
            NameServer.LookupRequest request = NameServer.LookupRequest.newBuilder().setQualifier(qualifier).setService(service).build();
            result = stubDNS.lookup(request).getAddressList();
            debug(result.toString() + "\n");
        } catch (StatusRuntimeException e) {
			System.out.println("Caught Exception with description: " + e.getStatus().getDescription());
		}
        return result;
    }

}
*/