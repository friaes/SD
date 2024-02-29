import sys
import grpc
from concurrent import futures
from NameServerServiceImpl import NameServerServiceImpl

sys.path.insert(1, '../Contract/target/generated-sources/protobuf/python')

import NameServer_pb2 as pb2
import NameServer_pb2_grpc as pb2_grpc
# define the port
PORT = 5001

if __name__ == '__main__':
    try:
        # print received arguments
        print("Received arguments:")
        for i in range(1, len(sys.argv)):
            print("  " + sys.argv[i])

        # get port
        port = PORT

        # create server
        server = grpc.server(futures.ThreadPoolExecutor(max_workers=1))
        # add service
        pb2_grpc.add_NameServerServiceServicer_to_server(NameServerServiceImpl(), server)
        # listen on port
        server.add_insecure_port('[::]:'+str(port))
        # start server
        server.start()
        # print message
        print("Server listening on port " + str(port))
        # print termination message
        print("Press CTRL+C to terminate")
        # wait for server to finish
        server.wait_for_termination()

    except KeyboardInterrupt:
        print("HelloServer stopped")
        exit(0)

class ServerEntry:
    # store host:port and qualifier for each server
    qualifier = ''
    address = ''
    def __init__(self, qualifier, address):
        # check address format

        # check qualifier format
        if qualifier not in ('A', 'B', 'C'):
            # raise  exception ('Not possible to register server')
            pass
        self.qualifier = qualifier
        self.address = address

    def get_qualifier(self):
        return self.qualifier

    def get_address(self):
        return self.address

class ServiceEntry:
    # store service name and dict of server entries
    server_entries = {}
    service_name = ''
    def __init__(self, service):
        self.service = service

    def add_server_entry(self, server_entry):
        self.server_entries[server_entry.get_address()] = server_entry.get_qualifier()

    def remove_server_entry(self, address):
        if self.server_entries[address] != '':
            self.server_entries.pop(address, None)

    def get_server_entries(self):
        return self.server_entries

    def get_service_name(self):
        return self.service_name

class NameServer:
    # store map of service_name->service entry
    service_entries = {}
    def __init__(self):
        pass

    def add_service(self, service_name, service_entry):
        if self.service_entries[service_name]:
            # raise some exception
            pass
        self.service_entries[service_name] = service_entry
        return

    def get_service_entries(self):
        return self.service_entries

    def delete_server(self, service, address):
        deleted = False
        service_entries = ns.get_service_entries()
        service_entry = service_entries[service]
        server_entries = service_entry.get_server_entries()
        for saddress, squalifier in server_entries.items():
            if address == saddress:
                service_entry.remove_server_entry(saddress)
                deleted = True

        if not deleted:
            # raise exception 'Not possible to delete the server')
            return 'Not possible to delete the server'

        return


ns = NameServer()

def register(service, qualifier, address):
    # check address format
    try:
        server_entry = ServerEntry(qualifier, address)
        service_entry = ServiceEntry(service)
        service_entry.add_server_entry(server_entry)
        ns.add_service(service, service_entry)
    except: # catch exception and return it?
        return 'Not possible to register the server'
    return 'Server registered'


def lookup(service, qualifier):
    # check qualifier format
    res = []
    for saddress, squalifier in ns.service_entries[service].get_server_entries().items():
        if qualifier == '' or squalifier == qualifier:
            res.append(saddress)
    return res


def delete(service, address):
    # check address format
    try:
        ns.delete_server(service, address)
    except: # catch exception and return it?
        return 'Not possible to delete the server'
    return 'Server deleted'
