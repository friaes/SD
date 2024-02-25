package pt.ulisboa.tecnico.tuplespaces.client.grpc;

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
		PutResponse response = stub.put(request);
    }
}
