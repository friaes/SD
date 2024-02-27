import sys
sys.path.insert(1, '../Contract/target/generated-sources/protobuf/python')

# define the port
PORT = 5001

if __name__ == '__main__':
    try:
        # print received arguments
        print("Received arguments:")
        for i in range(1, len(sys.argv)):
            print("  " + sys.argv[i])

        # TODO

    except KeyboardInterrupt:
        print("HelloServer stopped")
        exit(0)

class ServerEntry:
    # store host:port and qualifier for each server


class ServiceEntry:

    # store service_name and set of server entries

class NamingServer:

    # store map of service_name->service entry