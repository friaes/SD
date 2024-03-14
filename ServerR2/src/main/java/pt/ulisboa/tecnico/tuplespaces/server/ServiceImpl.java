package pt.ulisboa.tecnico.tuplespaces.server;

import io.grpc.stub.StreamObserver;
import java.util.WeakHashMap;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.tecnico.tuplespaces.server.domain.ServerState;

/* these imported classes are generated by the TupleSpaces_Replica_XuLiskov contract */
import pt.ulisboa.tecnico.tuplespaces.replicaXuLiskov.contract.*;

import static io.grpc.Status.INVALID_ARGUMENT;

public class ServiceImpl extends TupleSpacesReplicaGrpc.TupleSpacesReplicaImplBase{
    
	private boolean DEBUG_FLAG = false;
    private ServerState state = new ServerState();

	public ServiceImpl(boolean debug){
		this.DEBUG_FLAG = debug;
	}

	public void debug(String debugMessage){
		if (DEBUG_FLAG)
			System.err.print("[DEBUG] " + debugMessage);
	}

    @Override
	public void put(PutRequest request, StreamObserver<PutResponse> responseObserver) {
		// StreamObserver is used to represent the gRPC stream between the server and
		// client in order to send the appropriate responses (or errors, if any occur).

		debug("Request: " + request.toString());
        String newTuple = request.getNewTuple();

        state.put(newTuple);

		PutResponse response = PutResponse.newBuilder().setAck("ACK ").build();
		debug("Response: " + response.toString());

		// Send a single response through the stream.
		responseObserver.onNext(response);
		// Notify the client that the operation has been completed.
		responseObserver.onCompleted();
	}

    @Override
	public void read(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {

		debug("Request: " + request.toString());
        String searchPattern = request.getSearchPattern();

        ReadResponse response = ReadResponse.newBuilder().setResult(state.read(searchPattern)).setAck("ACK").build();
		debug("Response: " + response.toString());


		// Send a single response through the stream.
		responseObserver.onNext(response);
		// Notify the client that the operation has been completed.
		responseObserver.onCompleted();
    }

    @Override
	public void takePhase1(TakePhase1Request request, StreamObserver<TakePhase1Response> responseObserver) {

		debug("Request: " + request.toString());
        String searchPattern = request.getSearchPattern();
		Integer clientId = request.getClientId();

        TakePhase1Response response = TakePhase1Response.newBuilder().setReservedTuple(state.takePhase1(searchPattern, clientId)).build();
		debug("Response: " + response.getReservedTuple());

		// Send a single response through the stream.
		responseObserver.onNext(response);
		// Notify the client that the operation has been completed.
		responseObserver.onCompleted();
    }

	@Override
	public void takePhase1Release(TakePhase1ReleaseRequest request, StreamObserver<TakePhase1ReleaseResponse> responseObserver) {

		debug("Request: " + request.toString());
		String reservedTuple = request.getReservedTuple();
		Integer clientId = request.getClientId();

		state.takePhase1Release(reservedTuple, clientId);
		TakePhase1ReleaseResponse response = TakePhase1ReleaseResponse.newBuilder().build();
		debug("Response being sent..." );

		// Send a single response through the stream.
		responseObserver.onNext(response);
		// Notify the client that the operation has been completed.
		responseObserver.onCompleted();
	}

	@Override
	public void takePhase2(TakePhase2Request request, StreamObserver<TakePhase2Response> responseObserver) {

		debug("Request: " + request.toString());
		String tuple = request.getTuple();
		Integer clientId = request.getClientId();

		state.takePhase2(tuple, clientId);
		TakePhase2Response response = TakePhase2Response.newBuilder().build();
		debug("Response being sent...");

		// Send a single response through the stream.
		responseObserver.onNext(response);
		// Notify the client that the operation has been completed.
		responseObserver.onCompleted();
	}

    @Override
	public void getTupleSpacesState(getTupleSpacesStateRequest request, StreamObserver<getTupleSpacesStateResponse> responseObserver) {

		debug("Request: " + request.toString());
		// You must use a builder to construct a new Protobuffer object
		getTupleSpacesStateResponse response = getTupleSpacesStateResponse.newBuilder().addAllTuple(state.getTupleSpacesState()).build();
		debug("Response: " + response.toString());

		// Use responseObserver to send a single response back
		responseObserver.onNext(response);

		// When you are done, you must call onCompleted
		responseObserver.onCompleted();
	}
        
}
