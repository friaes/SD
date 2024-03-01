# TupleSpaces

Distributed Systems Project 2024
  
**Group A29**
  
**Difficulty level: I am Death incarnate!**


### Code Identification

In all source files (namely in the *groupId*s of the POMs), replace __GXX__ with your group identifier. The group
identifier consists of either A or T followed by the group number - always two digits. This change is important for 
code dependency management, to ensure your code runs using the correct components and not someone else's.

### Team Members


| Number | Name              | User                             | Email                               |
|--------|-------------------|----------------------------------|-------------------------------------|
| 103471 | João Fidalgo      | <https://github.com/JoaoFidalgo1403>   | <joao.fidalgo.1403@tecnico.ulisboa.pt>   |
| 104139 | Rodrigo Friães    | <https://github.com/Friaes>     | <rodrigo.friaes@tecnico.ulisboa.pt>     |
|  89402 | Aldo Cachudo      | <https://github.com/aldomrc> | <mailto:aldomiguel@tecnico.ulisboa.pt> |

## Getting Started

The overall system is made up of several modules. The different types of servers are located in _ServerX_ (where X denotes stage 1, 2 or 3). 
The clients is in _Client_.
The definition of messages and services is in _Contract_. The future naming server
is in _NamingServer_.

See the [Project Statement](https://github.com/tecnico-distsys/TupleSpaces) for a complete domain and system description.

### Prerequisites

The Project is configured with Java 17 (which is only compatible with Maven >= 3.8), but if you want to use Java 11 you
can too -- just downgrade the version in the POMs.

To confirm that you have them installed and which versions they are, run in the terminal:

```s
javac -version
mvn -version
```

### Installation

To compile and install all modules from root directory:

```s
mvn clean install
cd Contract
mvn exec:exec
```

### Name Server

To run from root directory:

```s
cd NameServer
python3 server.py [-debug]
```

### TupleSpaces Server

To compile and run from root directory:

```s
cd ServerR1
mvn compile exec:java [-Dexec.args="2001 A"]
```

### TupleSpaces Client

To compile and run from root directory:

```s
cd Client
mvn compile exec:java
```


## Built With

* [Maven](https://maven.apache.org/) - Build and dependency management tool;
* [gRPC](https://grpc.io/) - RPC framework.
