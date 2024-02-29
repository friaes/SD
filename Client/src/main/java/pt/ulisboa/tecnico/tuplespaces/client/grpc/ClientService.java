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

}