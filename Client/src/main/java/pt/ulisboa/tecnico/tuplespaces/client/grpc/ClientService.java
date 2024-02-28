package pt.ulisboa.tecnico.tuplespaces.client.grpc;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.*;

public class ClientService {
    /*TODO: The gRPC client-side logic should be here.
    This should include a method that builds a channel and stub,
    as well as individual methods for each remote operation of this service. */

    private final ManagedChannel channel;
    private final TupleSpacesGrpc.TupleSpacesBlockingStub stub;
    private boolean DEBUG_FLAG = false;

    public ClientService(String target, boolean debug) {

        this.DEBUG_FLAG = debug;        
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        this.stub = TupleSpacesGrpc.newBlockingStub(channel);
    }

    public void debug(String debugMessage){
		if (DEBUG_FLAG)
			System.err.println("[DEBUG] " + debugMessage);
	}

    public void shutdown() {
        channel.shutdownNow();
    }

    public void put(String tuple) {
        PutRequest request = PutRequest.newBuilder().setNewTuple(tuple).build();
        debug(request.toString());
		stub.put(request);
    }

    public String read(String pattern) {
        ReadRequest request = ReadRequest.newBuilder().setSearchPattern(pattern).build();
        debug(request.toString());
        String result = stub.read(request).getResult();
        debug(result);
        return result;
    }

    public String take(String pattern) {
        TakeRequest request = TakeRequest.newBuilder().setSearchPattern(pattern).build();
        debug(request.toString());
        String result = stub.take(request).getResult();
        debug(result);
        return result;
    }

    public List<String> getTupleSpacesState(String qualifier) {
        getTupleSpacesStateRequest request = getTupleSpacesStateRequest.newBuilder().build();
        debug(request.toString());
        return stub.getTupleSpacesState(request).getTupleList();
        
    }

}