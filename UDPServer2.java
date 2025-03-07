import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author cjaiswal, trua, aehlers, rbarbarito
 *
 *  
 * 
 */
public class UDPServer2
{
    private DatagramSocket socket = null;
    private Map<String, String> fileRecords = new HashMap<>();
    private String latestTimestamp = "";
    private Map<String, Long> clientLastUpdate = new HashMap<>();
    private static final long TIMEOUT = 31000; // 31 seconds
    private Set<String> lostClients = new HashSet<>();

    public UDPServer2() 
    {
    	try 
    	{
            // Load configuration from file
            Properties config = new Properties();
            config.load(new FileInputStream("UDPServer.config"));
            int port = Integer.parseInt(config.getProperty("Port"));

    		//create the socket assuming the server is listening on port in config
			socket = new DatagramSocket(port);
		} 
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Schedule a task to check for offline clients
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForOfflineClients();
            }
        }, 0, 10000); // Check every 10 seconds

        // Schedule a task to print the list of available files every 30 seconds
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                printAvailableFiles();
            }
        }, 0, 30000); // Print every 30 seconds
    }

    public void createAndListenSocket() 
    {
        try 
        {

            


        	//incoming data buffer
            byte[] incomingData = new byte[1024];

            while (true) 
            {
            	//create incoming packet
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                // System.out.println("Waiting...");
                
                //wait for the packet to arrive and store it in incoming packet
                socket.receive(incomingPacket);
                
                //retrieve the data
                String message = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                
                //terminate if it is "THEEND" message from the client
                if(message.equals("THEEND"))
                {
                	socket.close();
                	break;
                }
                // System.out.println("Received message from client: " + message);
                // System.out.println("Client Details:PORT " + incomingPacket.getPort()
                // + ", IP Address:" + incomingPacket.getAddress());
                
                // Extract timestamp from the message
                String[] messageParts = message.split("\n", 2);
                latestTimestamp = messageParts[0].replace("Timestamp: ", "");

                // Store file names and client information
                String clientInfo = incomingPacket.getAddress().toString() + ":" + incomingPacket.getPort();
                fileRecords.put(clientInfo, messageParts[1]);
                clientLastUpdate.put(clientInfo, System.currentTimeMillis());

                // Prepare response with all file records, latest timestamp, and offline nodes
                StringBuilder responseBuilder = new StringBuilder("Available Files:\n");
                long currentTime = System.currentTimeMillis();
                for (Map.Entry<String, String> entry : fileRecords.entrySet()) {
                    if (currentTime - clientLastUpdate.get(entry.getKey()) <= TIMEOUT) {
                        responseBuilder.append("Client: ").append(entry.getKey()).append("\nFiles:\n")
                                .append(entry.getValue()).append("\n");
                    }
                }
                responseBuilder.append("Latest update timestamp: ").append(latestTimestamp).append("\n");
                responseBuilder.append("Offline Nodes:\n");
                for (String lostClient : lostClients) {
                    responseBuilder.append("Client: ").append(lostClient).append(" is possibly offline.\n");
                }
                String reply = responseBuilder.toString();
                byte[] data = reply.getBytes();

                //retrieve client socket info and create response packet
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                DatagramPacket replyPacket =
                        new DatagramPacket(data, data.length, IPAddress, port);
                socket.send(replyPacket);
            }
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException i) 
        {
            i.printStackTrace();
        } 
    }

    private void checkForOfflineClients() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : clientLastUpdate.entrySet()) {
            if (currentTime - entry.getValue() > TIMEOUT) {
                System.out.println("Client " + entry.getKey() + " is possibly offline.");
                System.out.println("____________________________________________ \n \n");
                lostClients.add(entry.getKey());
                fileRecords.remove(entry.getKey());
            }
        }
    }

    private void printAvailableFiles() {
        StringBuilder responseBuilder = new StringBuilder("Available Files:\n");
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, String> entry : fileRecords.entrySet()) {
            if (currentTime - clientLastUpdate.get(entry.getKey()) <= TIMEOUT) {
                responseBuilder.append("Client: ").append(entry.getKey()).append("\nFiles:\n")
                        .append(entry.getValue()).append("\n");
            }
        }
        responseBuilder.append("Latest update timestamp: ").append(latestTimestamp).append("\n");
        responseBuilder.append("Offline Nodes:\n");
        for (String lostClient : lostClients) {
            responseBuilder.append("Client: ").append(lostClient).append(" is possibly offline.\n");
        }
        System.out.println(responseBuilder.toString());
        System.out.println("____________________________________________ \n \n");
    }

    public static void main(String[] args) 
    {
        UDPServer2 server = new UDPServer2();
        server.createAndListenSocket();
    }
}
