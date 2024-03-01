import sys
sys.path.insert(1, '../Contract/target/generated-sources/protobuf/python')
import NameServer_pb2 as pb2
import NameServer_pb2_grpc as pb2_grpc
import NameServer
from grpc import RpcError, StatusCode

class NameServerServiceImpl(pb2_grpc.NameServerServiceServicer):

    ns = NameServer.NameServer()
    def __init__(self, *args, **kwargs):
        pass

    def register(self, request, context):
        # print the received request
        print(request)

        # get the service, qualifier, address
        service = request.service
        qualifier = request.qualifier
        address = request.address

        try:
            self.ns.register(service, qualifier, address)
        except NameServer.RegisterError as re:
            context.set_code(StatusCode.INTERNAL)
            context.set_details(str(re))
            raise RpcError(
                Status=StatusCode.INTERNAL,
                details=str(re)
            )
        # create response
        response = pb2.RegisterResponse()

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
        response = pb2.LookupResponse(address=result)

        # return response
        return response

    def delete(self, request, context):
        # print the received request
        print(request)

        # get the name
        service = request.service
        address = request.address

        try:
            self.ns.delete(service, address)
        except NameServer.DeleteError as de:
            context.set_code(StatusCode.INTERNAL)
            context.set_details(de)
            raise RpcError(
                Status=StatusCode.INTERNAL,
                details=str(de)
            )

        # create response
        response = pb2.DeleteResponse()

        # return response
        return response