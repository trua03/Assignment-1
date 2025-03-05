# UDP Networking Examples

This project contains examples of UDP networking code. The examples include a UDP client, server, and peer-to-peer (P2P) communication. Below are the details and instructions for running each example.

## Files

- `UDPClient2.java`: A UDP client that sends directory file names to the server.
- `UDPClient2.config`: Configuration file for `UDPClient2.java`.
- `UDPServer2.java`: A UDP server that receives file names from clients and responds with a list of available files.
- `UDPServer.config`: Configuration file for `UDPServer2.java`.
- `UDPP2P.java`: A UDP peer-to-peer (P2P) client that communicates with other peers to share file lists.
- `UDPP2P.config`: Configuration file for `UDPP2P.java`.

## Configuration Files

### `UDPClient2.config`

```plaintext
IPAddress=localhost
Directory=./
ServerPort=9876
```

- `IPAddress`: The IP address of the server.
- `Directory`: The directory path to list files from.
- `ServerPort`: The port number of the server.

### `UDPServer.config`

```plaintext
Port=9876
```

- `Port`: The port number the server listens on.

### `UDPP2P.config`

```plaintext
Peers=192.168.1.185:9876
Directory=./
```

- `Peers`: A comma-separated list of peer addresses and ports.
- `Directory`: The directory path to list files from.

## Running the Examples

### UDP Client-Server Example

1. **Run the Server:**
   - Open a terminal and navigate to the project directory.
   - Compile and run `UDPServer2.java`:
     ```sh
     javac UDPServer2.java
     java UDPServer2
     ```

2. **Run the Client:**
   - Open another terminal and navigate to the project directory.
   - Compile and run `UDPClient2.java`:
     ```sh
     javac UDPClient2.java
     java UDPClient2
     ```

3. **Interaction:**
   - The client will send the list of files in the specified directory to the server.
   - The server will respond with the list of available files from all clients.

### UDP Peer-to-Peer (P2P) Example

1. **Run the P2P Client:**
   - Open a terminal and navigate to the project directory.
   - Compile and run `UDPP2P.java`:
     ```sh
     javac UDPP2P.java
     java UDPP2P
     ```

2. **Interaction:**
   - The P2P client will communicate with other peers specified in the configuration file.
   - It will share and receive file lists from peers.

## Notes

- Ensure that the configuration files are in the same directory as the corresponding Java files.
- Modify the configuration files as needed to match your network setup.
- The examples use port `9876` by default. Ensure that this port is not blocked by your firewall.

## Author

- Thomas Rua
- Github COPILOT
