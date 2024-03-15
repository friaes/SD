# TupleSpaces

Distributed Systems Project 2024
  
**Group A29**
  
**Difficulty level: I am Death incarnate!**

### Team Members


| Number | Name              | User                             | Email                               |
|--------|-------------------|----------------------------------|-------------------------------------|
| 103471 | João Fidalgo      | <https://github.com/JoaoFidalgo1403>   | <joao.fidalgo.1403@tecnico.ulisboa.pt>   |
| 104139 | Rodrigo Friães    | <https://github.com/Friaes>     | <rodrigo.friaes@tecnico.ulisboa.pt>     |
|  89402 | Aldo Cachudo      | <https://github.com/aldomrc> | <aldomiguel@tecnico.ulisboa.pt> |

## Getting Started

The overall system is made up of several modules. The different types of servers are located in _ServerRX_ (where X denotes stage 1, 2 or 3). 
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
python3 --version
```

### Installation

To compile and install all modules from project root:

```s
mvn clean install
```
Then create and activate the virtual environment:
  - Linux
  ```s
  python -m venv .venv
  source .venv/bin/activate
  ```
  - Windows
  ```s
  python -m venv .venv
  .venv\Scripts\activate
  ```
  Then within the environment do:
  ```s
  cd Contract
  mvn exec:exec
  ```

### Name Server

To run from project root:

```s
cd NameServer
python3 server.py [-debug]
```

### TupleSpaces Servers

To compile and run the three servers from project root, open three terminals and do:

First terminal
```s
cd ServerR2
mvn compile exec:java -Dexec.args="2001 A [-debug]"
```
Second terminal
```s
cd ServerR2
mvn compile exec:java -Dexec.args="2002 B [-debug]"
```
Third terminal
```s
cd ServerR2
mvn compile exec:java -Dexec.args="2003 C [-debug]"
```

### TupleSpaces Client

To compile and run from project root:

```s
cd Client
mvn compile exec:java -Dexec.args="<id> [-debug]"
```


## Built With

* [Maven](https://maven.apache.org/) - Build and dependency management tool;
* [gRPC](https://grpc.io/) - RPC framework.
