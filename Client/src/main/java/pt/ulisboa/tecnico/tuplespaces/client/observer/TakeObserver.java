package pt.ulisboa.tecnico.tuplespaces.client.observer;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.tuplespaces.client.ResponseCollector;
import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.*;

public class TakeObserver implements StreamObserver<TakeResponse>{

    private static boolean DEBUG_FLAG = false;
    ResponseCollector collector;

    /* Helper method to print debug messages.*/
	private static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.print("[DEBUG] take: " + debugMessage + "\n");
	}

    public TakeObserver(ResponseCollector c, boolean debug) {
        collector = c;
        DEBUG_FLAG = debug;
    }

    @Override
    public void onNext(TakeResponse r) {
        collector.addString(r.getResult());
        debug("Received response: " + r.getResult());
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Received error: " + throwable);
    }

    @Override
    public void onCompleted() {}
}