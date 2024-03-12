import sys
sys.path.insert(1, '../Contract/target/generated-sources/protobuf/python')

DEBUG_FLAG = False
if len(sys.argv) >= 2:
    if sys.argv[1] == "-debug":
        DEBUG_FLAG = True
        print("Debug Mode")

class RegisterError(Exception):
    pass

class LookupError(Exception):
    pass

class DeleteError(Exception):
    pass

class FormatError(Exception):
    pass

def debug(msg="empty message", flag=False):
    if flag:
        print("[DEBUG] " + msg)

class ServerEntry:
    # store host:port and qualifier for each server
    qualifier = ""
    address = ""
    def __init__(self, qualifier, address):
        # check qualifier format
        if qualifier not in ("A", "B", "C"):
            raise RegisterError("Not possible to register server")
        self.qualifier = qualifier
        self.address = address

    def get_qualifier(self):
        return self.qualifier

    def get_address(self):
        return self.address

class ServiceEntry:
    # store service name and dict of server entries
    server_entries = {}
    service_name = ""
    def __init__(self, service):
        self.service_name = service

    def add_server_entry(self, server_entry):
        self.server_entries[server_entry.get_address()] = server_entry.get_qualifier()

    def remove_server_entry(self, address):
        if address in self.server_entries:
            self.server_entries.pop(address, None)
        else:
            raise DeleteError

    def get_server_entries(self):
        return self.server_entries

    def get_service_name(self):
        return self.service_name

class NameServer:
    # store map of service_name->service entry
    service_entries = {}
    def __init__(self):
        pass

    def add_service(self, service_name, service_entry, server_entry):
        if service_name in self.service_entries:
            self.service_entries[service_name].add_server_entry(server_entry)
        else:
            self.service_entries[service_name] = service_entry
        return

    def get_service_entries(self):
        return self.service_entries

    def delete_server(self, service, address):
        service_entries = self.get_service_entries()
        service_entry = service_entries[service]
        server_entries = service_entry.get_server_entries()
        if address not in server_entries:
            raise DeleteError("Not possible to delete the server")

        service_entry.remove_server_entry(address)
        return

    def register(self, service, qualifier, address):
        try:

            debug("register request received", DEBUG_FLAG)
            debug("args:", DEBUG_FLAG)
            debug(str("service - " + service), DEBUG_FLAG)
            debug(str("qualifier - " + qualifier), DEBUG_FLAG)
            debug(str("address - " + address), DEBUG_FLAG)
            server_entry = ServerEntry(qualifier, address)
            service_entry = ServiceEntry(service)
            service_entry.add_server_entry(server_entry)
            self.add_service(service, service_entry, server_entry)
        except RegisterError: # catch exception and return it?
            raise
        return


    def lookup(self, service, qualifier):
        # check qualifier format
        if qualifier not in ("A", "B", "C"):
            raise FormatError("Invalid qualifier")
        debug("lookup request received", DEBUG_FLAG)
        debug("args:", DEBUG_FLAG)
        debug(str("service - " + service), DEBUG_FLAG)
        debug(str("qualifier - " + qualifier), DEBUG_FLAG)
        if service in self.service_entries: 
            for saddress, squalifier in self.service_entries[service].get_server_entries().items():
                if squalifier == qualifier:
                    return saddress
            raise LookupError("Could not find server to fulfill service")
        else: 
            raise LookupError("Could not find requested service")
        return


    def delete(self, service, address):
        debug("delete request received", DEBUG_FLAG)
        debug("args:", DEBUG_FLAG)
        debug(str("service - " + service), DEBUG_FLAG)
        debug(str("address - " + address), DEBUG_FLAG)
        try:
            self.delete_server(service, address)
        except DeleteError:
            raise
        return


