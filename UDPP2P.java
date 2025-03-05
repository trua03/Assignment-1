import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.Random;

/**
 * 
 * @author cjaiswal
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

    public UDPP2P() 
    {
        try 
        {
            //create the socket assuming the server is listening on port 9876
            socket = new DatagramSocket(9876);
        } 
        catch (SocketException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndListenSocket() 
    {
        try 
        {
            char ch='y';
            
            // Load configuration from file
            Properties config = new Properties();
            config.load(new FileInputStream("UDPP2P.config"));
            String ipAddress = config.getProperty("IPAddress", "localhost");
            String directoryPath = config.getProperty("Directory");
            String[] peers = config.getProperty("Peers").split(",");

            // Initialize peer addresses and ports
            peerAddresses = new InetAddress[peers.length];
            peerPorts = new int[peers.length];
            for (int i = 0; i < peers.length; i++) {
                String[] peerInfo = peers[i].split(":");
                peerAddresses[i] = InetAddress.getByName(peerInfo[0]);
                peerPorts[i] = Integer.parseInt(peerInfo[1]);
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

                data = sentence.getBytes();
                for (int i = 0; i < peerAddresses.length; i++) {
                    DatagramPacket sendPacket = new DatagramPacket(data, data.length, peerAddresses[i], peerPorts[i]);
                    socket.send(sendPacket);
                }

                //create packet and receive the response from the server
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                String response = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                System.out.println("Response from peer:\n" + response);
                System.out.println("Peer Details:PORT " + incomingPacket.getPort()
                + ", IP Address: " + incomingPacket.getAddress());
                System.out.println("My IP: " + InetAddress.getLocalHost().getHostAddress());
                System.out.println("My Port: " + socket.getLocalPort());

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

    public static void main(String[] args) 
    {
        UDPP2P client = new UDPP2P();
        client.createAndListenSocket();
    }
}
