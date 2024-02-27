import sys
#sys.path.insert(1, '../contract/target/generated-sources/protobuf/python')
#import HelloWorld_pb2 as pb2
#import HelloWorld_pb2_grpc as pb2_grpc

class NamingServerServiceImpl(pb2_grpc.HelloWorldServiceServicer):

    def __init__(self, *args, **kwargs):
        pass

    def register(self, request, context):
        # print the received request
        print(request)

        # get the name
        name = request.name

        # create response
        #response = pb2.HelloResponse(greeting="Hello " + name)

        # return response
        return response
