package pt.ulisboa.tecnico.tuplespaces.client.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.*;

import pt.ulisboa.tecnico.sequencer.contract.*;
import pt.ulisboa.tecnico.tuplespaces.client.observer.*;
import pt.ulisboa.tecnico.tuplespaces.client.util.OrderedDelayer;
import pt.ulisboa.tecnico.tuplespaces.client.ResponseCollector;

public class ClientService {

    private boolean DEBUG_FLAG = false;
    private final ManagedChannel[] channels;
    private final TupleSpacesReplicaGrpc.TupleSpacesReplicaStub[] stubs;

    /* Sequencer */
    private final static String targetSequencer = "localhost:8080";
    private final static ManagedChannel channelSequencer = ManagedChannelBuilder.forTarget(targetSequencer).usePlaintext().build();
    private final static SequencerGrpc.SequencerBlockingStub stubSequencer = SequencerGrpc.newBlockingStub(channelSequencer);

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
        Integer seq = getNewSequenceNumber();

        if (seq != null) {
            PutRequest request = PutRequest.newBuilder().setNewTuple(tuple).setSeqNumber(seq).build();

            for (Integer id : delayer)
                this.stubs[id].put(request, new PutObserver(c, DEBUG_FLAG));
            try {
                c.waitUntilAllReceived(numServers);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            debug(c.getStrings().toString());
        }
    }

    Integer getNewSequenceNumber() {
        GetSeqNumberRequest request = GetSeqNumberRequest.newBuilder().build();
        GetSeqNumberResponse response = this.stubSequencer.getSeqNumber(request);

        return response.getSeqNumber();
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
        debug(c.getStrings().toString());

        return c.getString();
    }
    
    public String take(String pattern) {
        ResponseCollector c = new ResponseCollector();
        Integer seq = getNewSequenceNumber();

        if (seq != null) {
            PutRequest request = PutRequest.newBuilder().setSearchPattern(pattern).setSeqNumber(seq).build();

            for (Integer id : delayer)
                this.stubs[id].put(request, new PutObserver(c, DEBUG_FLAG));
            try {
                c.waitUntilAllReceived(numServers);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            debug(c.getStrings().toString());
        }
    
        return null;
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
        
        return c.getStrings();       
    }
    
}
