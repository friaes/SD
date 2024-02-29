package pt.ulisboa.tecnico.tuplespaces.client;

import pt.ulisboa.tecnico.tuplespaces.client.grpc.ClientService;

public class ClientMain {

    /** Set flag to true to print debug messages. 
	 * The flag can be set using the -Ddebug command line option. */
	private static boolean DEBUG_FLAG = false;

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
        final String host = args[0];
        final String port = args[1];
        final String target = host + ":" + port;
		debug("Target: " + target + "\n");


        ClientService service = new ClientService(target, DEBUG_FLAG);
        CommandProcessor parser = new CommandProcessor(service);
        parser.parseInput();
        service.shutdown();

    }
}
