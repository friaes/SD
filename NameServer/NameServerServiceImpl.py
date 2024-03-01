import sys
sys.path.insert(1, '../Contract/target/generated-sources/protobuf/python')
import NameServer_pb2 as pb2
import NameServer_pb2_grpc as pb2_grpc
import NameServer

class NameServerServiceImpl(pb2_grpc.NameServerServiceServicer):

    ns = NameServer.NameServer()
    def __init__(self, *args, **kwargs):
        pass

    def register(self, request, context):
        # print the received request
        print(request)

        # get the name
        service = request.service
        qualifier = request.qualifier
        address = request.address

        result = register(service, qualifier, address)
        # create response
        response = pb2.registerResponse(exception=result)

        # return response
        return response

    def lookup(self, request, context):
        # print the received request
        print(request)

        # get the name
        service = request.service
        qualifier = request.qualifier

        # result is a list
        result = lookup(service, qualifier)

        # create response
        response = pb2.lookupResponse(server=result)

        # return response
        return response

    def delete(self, request, context):
        # print the received request
        print(request)

        # get the name
        service = request.service
        address = request.address

        result = delete(service, address)

        # create response
        response = pb2.deleteResponse(exception = result)

        # return response
        return response