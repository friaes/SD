package pt.ulisboa.tecnico.tuplespaces.client.grpc;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.tuplespaces.replicaXuLiskov.contract.*;

import pt.ulisboa.tecnico.tuplespaces.client.util.OrderedDelayer;

public class ClientService {

    /* TODO: This class should implement the front-end of the replicated TupleSpaces service 
        (according to the Xu-Liskov algorithm)*/
    private boolean DEBUG_FLAG = false;
    private final ManagedChannel[] channels;
    private final TupleSpacesReplicaGrpc.TupleSpacesReplicaStub[] stubs;
    private int numServers = 0;

    OrderedDelayer delayer;

    public ClientService(int numServers, List<String> targets, boolean debug) {
        this.DEBUG_FLAG = debug;  
        this.numServers = numServers;
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
			System.err.print("[DEBUG] " + debugMessage);
	}

    /* This method allows the command processor to set the request delay assigned to a given server */
    public void setDelay(int id, int delay) {
        delayer.setDelay(id, delay);

        /* TODO: Remove this debug snippet */
        debug("After setting the delay, I'll test it");
        for (Integer i : delayer) {
          debug("Now I can send request to stub[" + i + "]");
      }
      debug("Done.");
    }

    public void shutdown() {
        for (int i = 0; i < numServers; i++) {
            channels[i].shutdownNow();
        }
    }


    /* TODO: individual methods for each remote operation of the TupleSpaces service */

    /* Example: How to use the delayer before sending requests to each server 
     *          Before entering each iteration of this loop, the delayer has already 
     *          slept for the delay associated with server indexed by 'id'.
     *          id is in the range 0..(numServers-1).
    
        for (Integer id : delayer) {
            //stub[id].some_remote_method(some_arguments);
        }

    */
    
}
/* Código Anterior
public class ClientService {

    private final ManagedChannel channelGrpc;
    private final TupleSpacesGrpc.TupleSpacesBlockingStub stubGrpc;
    private boolean DEBUG_FLAG = false;

    public ClientService(String target, boolean debug) {

        this.DEBUG_FLAG = debug;        
        this.channelGrpc = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        this.stubGrpc = TupleSpacesGrpc.newBlockingStub(channelGrpc);
    }

    public void debug(String debugMessage){
		if (DEBUG_FLAG)
			System.err.print("[DEBUG] " + debugMessage);
	}

    public void shutdown() {
        channelGrpc.shutdownNow();
    }

    public void put(String tuple) {
        PutRequest request = PutRequest.newBuilder().setNewTuple(tuple).build();
        debug(request.toString());
		stubGrpc.put(request);
    }

    public String read(String pattern) {
        ReadRequest request = ReadRequest.newBuilder().setSearchPattern(pattern).build();
        debug(request.toString());
        String result = stubGrpc.read(request).getResult();
        debug("Result: " + result + "\n");
        return result;
    }

    public String take(String pattern) {
        TakeRequest request = TakeRequest.newBuilder().setSearchPattern(pattern).build();
        debug(request.toString());
        String result = stubGrpc.take(request).getResult();
        debug("Result: " + result + "\n");
        return result;
    }

    public List<String> getTupleSpacesState(String qualifier) {
        getTupleSpacesStateRequest request = getTupleSpacesStateRequest.newBuilder().build();
        List<String> result = stubGrpc.getTupleSpacesState(request).getTupleList();
        debug(result.toString() + "\n");
        return result;        
    }

}**/

