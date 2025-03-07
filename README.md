# Project Report: P2P and Client-Server Implementations

## Design Overview

### P2P Implementation

The P2P implementation is designed to allow multiple peers to share files and communicate with each other over a network. Each peer maintains a list of files available in a specified directory and periodically sends this list to other peers. The peers also monitor the availability of other peers and update their records accordingly.

#### Key Components

1. **UDPP2P Class**: This class represents a peer in the P2P network. It handles the creation of the socket, sending and receiving messages, and maintaining the list of available files and peer statuses.

2. **Configuration File (UDPP2P.config)**: This file contains the configuration settings for the peer, including the directory to monitor and the list of other peers in the network.

3. **Timer Tasks**: Two timer tasks are scheduled:
   - One to check for offline peers every 30 seconds.
   - Another to print the list of available files every 30 seconds.

#### Workflow

1. **Initialization**: The peer reads the configuration file to get the directory path and peer addresses. It initializes the socket and sets up the timer tasks.

2. **File Sharing**: The peer periodically scans the specified directory for files and sends the list to other peers along with a timestamp.

3. **Receiving Messages**: The peer listens for incoming messages from other peers, updates its records, and prints the list of available files.

4. **Offline Peer Detection**: The peer checks the last response time of other peers and marks them as offline if they haven't responded within the timeout period.

### Client-Server Implementation

The client-server implementation consists of a server that listens for incoming messages from clients and responds with the list of available files and the status of other clients. Clients periodically send their file lists to the server and receive updates from the server.

#### Key Components

1. **UDPServer2 Class**: This class represents the server. It handles the creation of the socket, receiving messages from clients, and maintaining the list of available files and client statuses.

2. **UDPClient2 Class**: This class represents a client. It handles the creation of the socket, sending messages to the server, and receiving responses from the server.

3. **Configuration Files (UDPServer.config and UDPClient2.config)**: These files contain the configuration settings for the server and clients, including the port number and directory to monitor.

4. **Timer Tasks**: The server schedules two timer tasks:
   - One to check for offline clients every 10 seconds.
   - Another to print the list of available files every 30 seconds.

#### Workflow

1. **Server Initialization**: The server reads the configuration file to get the port number and initializes the socket. It sets up the timer tasks.

2. **Client Initialization**: The client reads the configuration file to get the server address and directory path. It initializes the socket.

3. **File Sharing**: Clients periodically scan the specified directory for files and send the list to the server along with a timestamp.

4. **Receiving Messages**: The server listens for incoming messages from clients, updates its records, and sends the list of available files and offline clients back to the clients.

5. **Offline Client Detection**: The server checks the last update time of clients and marks them as offline if they haven't responded within the timeout period.

## Installation and Running Instructions

### Prerequisites

- Java Development Kit (JDK) installed on your machine.
- A text editor or IDE (e.g., Visual Studio Code) to edit the configuration files.

### Steps to Run the P2P Implementation

1. **Compile the Code**:
   Open a terminal and navigate to the directory containing the `UDPP2P.java` file. Run the following command to compile the code:
   ```sh
   javac UDPP2P.java
   ```

2. **Edit the Configuration File**:
   Open the `UDPP2P.config` file and update the `Peers` and `Directory` properties as needed.

3. **Run the Peer**:
   In the terminal, run the following command to start the peer:
   ```sh
   java UDPP2P
   ```

### Steps to Run the Client-Server Implementation

1. **Compile the Code**:
   Open a terminal and navigate to the directory containing the `UDPServer2.java` and `UDPClient2.java` files. Run the following commands to compile the code:
   ```sh
   javac UDPServer2.java
   javac UDPClient2.java
   ```

2. **Edit the Configuration Files**:
   Open the `UDPServer.config` and `UDPClient2.config` files and update the properties as needed.

3. **Run the Server**:
   In the terminal, run the following command to start the server:
   ```sh
   java UDPServer2
   ```

4. **Run the Client**:
   In another terminal, run the following command to start the client:
   ```sh
   java UDPClient2
   ```

By following these steps, you can set up and run the P2P and client-server implementations to share files and monitor the status of peers and clients in the network.
