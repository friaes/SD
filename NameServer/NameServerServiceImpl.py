import sys
sys.path.insert(1, '../Contract/target/generated-sources/protobuf/python')
import NameServer_pb2 as pb2
import NameServer_pb2_grpc as pb2_grpc

class NameServerServiceImpl(pb2_grpc.NameServerServiceServicer):

    ns = NameServer()
    def __init__(self, *args, **kwargs):
        pass

    def register(self, request, context):
        # print the received request
        print(request)

        # get the service, qualifier, address
        service = request.service
        qualifier = request.qualifier
        address = request.address

        result = self.ns.register(service, qualifier, address)
        # create response
        response = pb2.RegisterResponse(exception=result)

        # return response
        return response

    def lookup(self, request, context):
        # print the received request
        print(request)

        # get service and qualifier
        service = request.service
        qualifier = request.qualifier

        # result is a list
        result = self.ns.lookup(service, qualifier)

        # create response
        response = pb2.LookupResponse(server=result)

        # return response
        return response

    def delete(self, request, context):
        # print the received request
        print(request)

        # get the name
        service = request.service
        address = request.address

        result = self.ns.delete(service, address)

        # create response
        response = pb2.DeleteResponse(exception = result)

        # return response
        return response