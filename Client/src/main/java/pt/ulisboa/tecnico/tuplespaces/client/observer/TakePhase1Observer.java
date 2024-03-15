package pt.ulisboa.tecnico.tuplespaces.client.observer;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.tuplespaces.client.ResponseCollector;
import pt.ulisboa.tecnico.tuplespaces.replicaXuLiskov.contract.*;

public class TakePhase1Observer implements StreamObserver<TakePhase1Response>{

    private static boolean DEBUG_FLAG = false;
    ResponseCollector collector;

    /* Helper method to print debug messages.*/
	private static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.print("[DEBUG] tp1 " + debugMessage + "\n");
	}

    public TakePhase1Observer(ResponseCollector c, boolean debug) {
        collector = c;
        DEBUG_FLAG = debug;
    }

    @Override
    public void onNext(TakePhase1Response r) {
        collector.addString(r.getReservedTuple());
        debug("Received response: " + r.getReservedTuple());
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Received error: " + throwable);
    }

    @Override
    public void onCompleted() {}
}