import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author cjaiswal, trua, aehlers, rbarbarito
 *
 *  
 * 
 */
public class UDPP2P
{
    private DatagramSocket socket;
    private Scanner in = new Scanner(System.in);
    private Random random = new Random();
    private InetAddress[] peerAddresses;
    private int[] peerPorts;
    private Map<String, Long> peerLastResponse = new HashMap<>();
    private Map<String, String> peerFiles = new HashMap<>();
    private static final long TIMEOUT = 30000; // 30 seconds
    private String myAddress;
    private Set<String> lostPeers = new HashSet<>();

    public UDPP2P() 
    {
        try 
        {
            //create the socket assuming the server is listening on port 9876
            socket = new DatagramSocket(9876);
            myAddress = InetAddress.getLocalHost().getHostAddress() + ":" + socket.getLocalPort();
        } 
        catch (SocketException | UnknownHostException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Schedule a task to check for offline peers and print file list
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForOfflinePeers();
                printFileList();
            }
        }, 0, 30000); // Check and print every 30 seconds
    }

    public void createAndListenSocket() 
    {
        try 
        {
            char ch='y';
            
            // Load configuration from file
            Properties config = new Properties();
            config.load(new FileInputStream("UDPP2P.config"));
            String directoryPath = config.getProperty("Directory");
            String[] peers = config.getProperty("Peers").split(",");

            // Initialize peer addresses and ports
            peerAddresses = new InetAddress[peers.length];
            peerPorts = new int[peers.length];
            for (int i = 0; i < peers.length; i++) {
                String[] peerInfo = peers[i].split(":");
                peerAddresses[i] = InetAddress.getByName(peerInfo[0]);
                peerPorts[i] = Integer.parseInt(peerInfo[1]);
                peerLastResponse.put(peerInfo[0] + ":" + peerInfo[1], System.currentTimeMillis());
            }

            byte[] incomingData = new byte[1024];
            String sentence = "";
            byte data[] = new byte[1024];

            do
            {
                //create directory list
                File directory = new File(directoryPath);
                File[] filesList = directory.listFiles();
                StringBuilder sentenceBuilder = new StringBuilder();
                for (File file : filesList) {
                    if (file.isFile()) {
                        sentenceBuilder.append(file.getName()).append("\n");
                    }
                }
                sentence = sentenceBuilder.toString();

                // Add timestamp to the message
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                sentence = "Timestamp: " + timestamp + "\n" + sentence;

                // Include own files in the peerFiles map
                peerFiles.put(myAddress, sentence);

                data = sentence.getBytes();
                for (int i = 0; i < peerAddresses.length; i++) {
                    DatagramPacket sendPacket = new DatagramPacket(data, data.length, peerAddresses[i], peerPorts[i]);
                    socket.send(sendPacket);
                }

                //create packet and receive the response from the server
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                String response = new String(incomingPacket.getData(), 0, incomingPacket.getLength());

                // Update last response time and files for the peer
                String peerKey = incomingPacket.getAddress().getHostAddress() + ":" + incomingPacket.getPort();
                peerLastResponse.put(peerKey, System.currentTimeMillis());
                peerFiles.put(peerKey, response);

                // Remove peer from lostPeers if it returns online
                lostPeers.remove(peerKey);

                // Wait for a random interval between 0-30 seconds
                int waitTime = random.nextInt(31) * 1000;
                Thread.sleep(waitTime);
            }while(true);
            
            //send THEEND message to peers to terminate
            // sentence = "THEEND";
            // data = sentence.getBytes();
            // for (int i = 0; i < peerAddresses.length; i++) {
            //     DatagramPacket sendPacket = new DatagramPacket(data, data.length, peerAddresses[i], peerPorts[i]);
            //     socket.send(sendPacket);
            // }
            // socket.close();
        }
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException | InterruptedException e) 
        {
            e.printStackTrace();
        }
    }

    private void checkForOfflinePeers() {
        long currentTime = System.currentTimeMillis();
        Set<String> keysToRemove = new HashSet<>();
        for (Map.Entry<String, Long> entry : peerLastResponse.entrySet()) {
            if (currentTime - entry.getValue() > TIMEOUT) {
                System.out.println("Peer " + entry.getKey() + " is possibly offline.");
                lostPeers.add(entry.getKey());
                keysToRemove.add(entry.getKey());
                peerFiles.remove(entry.getKey());
            }
        }
        for (String key : keysToRemove) {
            peerLastResponse.remove(key);
        }
    }

    private void printFileList() {
        System.out.println("Available Files:");
        for (Map.Entry<String, String> entry : peerFiles.entrySet()) {
            if (entry.getKey().equals(myAddress)) {
                System.out.println("Peer: " + entry.getKey() + " (Myself)\nFiles:\n" + entry.getValue());
            } else {
                System.out.println("Peer: " + entry.getKey() + "\nFiles:\n" + entry.getValue());
            }
        }
        System.out.println("Offline Peers:");
        for (String peer : lostPeers) {
            System.out.println("Peer: " + peer + " is offline.");
        }
        System.out.println("__________________________________________________");
    }

    public static void main(String[] args) 
    {
        UDPP2P client = new UDPP2P();
        client.createAndListenSocket();
    }
}
