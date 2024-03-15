package pt.ulisboa.tecnico.tuplespaces.client.observer;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.tuplespaces.client.ResponseCollector;
import pt.ulisboa.tecnico.tuplespaces.replicaXuLiskov.contract.*;

public class GetTupleSpacesStateObserver implements StreamObserver<getTupleSpacesStateResponse>{

    private static boolean DEBUG_FLAG = false;
    ResponseCollector collector;

    /* Helper method to print debug messages.*/
	private static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.print("[DEBUG] " + debugMessage + "\n");
	}

    public GetTupleSpacesStateObserver (ResponseCollector c, boolean debug) {
        collector = c;
        DEBUG_FLAG = debug;
    }

    @Override
    public void onNext(getTupleSpacesStateResponse r) {
        debug("Received response: " + r.getTupleList());
        collector.addAll(r.getTupleList());
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Received error: " + throwable);
    }

    @Override
    public void onCompleted() {}
}