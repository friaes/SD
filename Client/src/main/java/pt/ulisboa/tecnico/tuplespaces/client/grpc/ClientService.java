package pt.ulisboa.tecnico.tuplespaces.client.grpc;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.tuplespaces.replicaXuLiskov.contract.*;
import pt.ulisboa.tecnico.tuplespaces.client.observer.*;
import pt.ulisboa.tecnico.tuplespaces.client.util.OrderedDelayer;
import pt.ulisboa.tecnico.tuplespaces.client.ResponseCollector;

public class ClientService {

    /* TODO: This class should implement the front-end of the replicated TupleSpaces service 
        (according to the Xu-Liskov algorithm)*/
    private boolean DEBUG_FLAG = false;
    private final ManagedChannel[] channels;
    private final TupleSpacesReplicaGrpc.TupleSpacesReplicaStub[] stubs;
    private int numServers = 0;
    private final int id;

    OrderedDelayer delayer;

    public ClientService(int numServers, List<String> targets, boolean debug, int id) {
        this.DEBUG_FLAG = debug;
        this.numServers = numServers;
        this.id = id;
        this.channels = new ManagedChannel[numServers];
        this.stubs = new TupleSpacesReplicaGrpc.TupleSpacesReplicaStub[numServers];

        for (int i = 0; i < numServers; i++) {
			channels[i] = ManagedChannelBuilder.forTarget(targets.get(i)).usePlaintext().build();
			stubs[i] = TupleSpacesReplicaGrpc.newStub(channels[i]);
		}
        /* The delayer can be used to inject delays to the sending of requests to the 
            different servers, according to the per-server delays that have been set  */
        delayer = new OrderedDelayer(numServers);
    }

    public void debug(String debugMessage){
		if (DEBUG_FLAG)
			System.err.print("[DEBUG] " + debugMessage + "\n");
	}

    /* This method allows the command processor to set the request delay assigned to a given server */
    public void setDelay(int id, int delay) {
        delayer.setDelay(id, delay);

        debug("After setting the delay, I'll test it");
        for (Integer i : delayer) {
          debug("Now I can send request to stub[" + i + "]");
      }
      debug("Done.");
    }

    public void shutdown() {
        for (ManagedChannel ch : channels)
			ch.shutdown();
    }

    public void put(String tuple) {
        ResponseCollector c = new ResponseCollector();
        PutRequest request = PutRequest.newBuilder().setNewTuple(tuple).build();

        for (Integer id : delayer)
		    this.stubs[id].put(request, new PutObserver(c, DEBUG_FLAG));
        try {
            c.waitUntilAllReceived(numServers);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        debug(c.getStringsList().toString());
    }

    public String read(String pattern) {
        ResponseCollector c = new ResponseCollector();
        ReadRequest request = ReadRequest.newBuilder().setSearchPattern(pattern).build();
        
        for (Integer id : delayer) 
            this.stubs[id].read(request, new ReadObserver(c, DEBUG_FLAG));
            
        try {
            c.waitUntilAllReceived(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        debug(c.getStringsList().toString());

        return c.getString();
    }

    public String take(String pattern) {
        ArrayList<String> reservedTuples = takePhase1(pattern);

        if (reservedTuples.size() != 3) {
            System.err.println("Unimplemented case no answer from one or more of the servers");
            return "NO ANSWER";
        }

        /*while (reservedTuples.size() != 3) {
                reservedTuples = takePhase1(pattern);
            }
        }*/
        if (reservedTuples.stream().anyMatch(tuple -> tuple.equals("REFUSED"))) {
            debug("Tuples: " + reservedTuples.toString());
            debug("Unimplemented case tuple does not exist in one or more servers");
            return "REFUSED";
        }
        if (reservedTuples.stream().anyMatch(tuple -> tuple.equals("LOCKED"))) {
            takePhase1Release(pattern);
            debug("Tuples: " + reservedTuples.toString());
            debug("Unimplemented case tuple has been locked by another client in one or more servers");
            return "LOCKED";
            // take(pattern);
        }
        String anyTuple = reservedTuples.get(0);
        if (reservedTuples.stream().allMatch(tuple -> tuple.equals(anyTuple))) {
            takePhase2(anyTuple);
        }
        return anyTuple;

    }

    public ArrayList<String> takePhase1(String pattern) {
        ResponseCollector c = new ResponseCollector();
        TakePhase1Request request = TakePhase1Request.newBuilder()
                .setSearchPattern(pattern)
                .setClientId(id)
                .build();

        debug("takePhase1: " + request.toString());

        for (Integer id : delayer)
            this.stubs[id].takePhase1(request, new TakePhase1Observer(c, DEBUG_FLAG));

        try {
            c.waitUntilAllReceived(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        debug("takePhase1: " + c.getStrings());

        return c.getStringsList();
    }


    public void takePhase1Release(String pattern) {
        ResponseCollector c = new ResponseCollector();
        TakePhase1ReleaseRequest request = TakePhase1ReleaseRequest.newBuilder()
                .setReservedTuple(pattern)
                .setClientId(id)
                .build();

        debug("takePhase1Release: " + request.toString());

        for (Integer id : delayer)
            this.stubs[id].takePhase1Release(request, new TakePhase1ReleaseObserver(c, DEBUG_FLAG));

        try {
            c.waitUntilAllReceived(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        debug("takePhase1Release: All acks received");
    }

    public void takePhase2(String pattern) {
        ResponseCollector c = new ResponseCollector();
        TakePhase2Request request = TakePhase2Request.newBuilder()
                .setTuple(pattern)
                .setClientId(id)
                .build();

        debug("takePhase2: " + request.getTuple());
        debug(String.valueOf(request.getClientId()));

        for (Integer id : delayer)
            this.stubs[id].takePhase2(request, new TakePhase2Observer(c, DEBUG_FLAG));

        try {
            c.waitUntilAllReceived(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        debug("takePhase2: All acks received");
    }

    public List<String> getTupleSpacesState(Integer id) {
        ResponseCollector c = new ResponseCollector();
        getTupleSpacesStateRequest request = getTupleSpacesStateRequest.newBuilder().build();
        
        stubs[id].getTupleSpacesState(request, new GetTupleSpacesStateObserver(c, DEBUG_FLAG));

        try {
            c.waitUntilAllReceived(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        return c.getStringsList();       
    }
    
}
