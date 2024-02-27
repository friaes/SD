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

    public ClientService(String target) {
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        this.stub = TupleSpacesGrpc.newBlockingStub(channel);
    }

    public void shutdown() {
        channel.shutdownNow();
    }

    public void put(String tuple) {
        PutRequest request = PutRequest.newBuilder().setNewTuple(tuple).build();
		stub.put(request);
    }

    public String read(String pattern) {
        ReadRequest request = ReadRequest.newBuilder().setSearchPattern(pattern).build();
        return stub.read(request).getResult();
    }

    public String take(String pattern) {
        TakeRequest request = TakeRequest.newBuilder().setSearchPattern(pattern).build();
        return stub.take(request).getResult();
    }

    public List<String> getTupleSpacesState(String qualifier) {
        getTupleSpacesStateRequest request = getTupleSpacesStateRequest.newBuilder().build();
        return stub.getTupleSpacesState(request).getTupleList();
        
    }

}