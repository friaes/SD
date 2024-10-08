package pt.ulisboa.tecnico.tuplespaces.client.observer;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.tuplespaces.client.ResponseCollector;
import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.*;

public class ReadObserver implements StreamObserver<ReadResponse>{

    private static boolean DEBUG_FLAG = false;
    ResponseCollector collector;

    /* Helper method to print debug messages.*/
	private static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.print("[DEBUG] " + debugMessage + "\n");
	}

    public ReadObserver (ResponseCollector c, boolean debug) {
        collector = c;
        DEBUG_FLAG = debug;
    }

    @Override
    public void onNext(ReadResponse r) {
        if (collector.getStrings().isEmpty())
            debug("Received response");
        
        collector.addString(r.getResult());
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Received error: " + throwable);
    }

    @Override
    public void onCompleted() {}
}