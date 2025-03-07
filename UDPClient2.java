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
 * @author cjaiswal, trua, aehlers, rbarbarito
 *
 *  
 * 
 */
public class UDPClient2 
{
    private DatagramSocket socket;
    private Scanner in = new Scanner(System.in);
    private Random random = new Random();

    public UDPClient2() 
    {
    	//create a client socket with random port number chose by DatagramSocket
    	try 
    	{
			socket = new DatagramSocket();
            System.out.println("Client IP: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Client Port: " + socket.getLocalPort());
		} 
    	catch (SocketException | UnknownHostException e) 
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
            config.load(new FileInputStream("UDPClient2.config"));
            String ipAddress = config.getProperty("IPAddress");
            String directoryPath = config.getProperty("Directory");
            int serverPort = Integer.parseInt(config.getProperty("ServerPort"));

            //create socket for the destination/server
            InetAddress IPAddress = InetAddress.getByName(ipAddress);
            byte[] incomingData = new byte[1024];
            String sentence = "";
        	byte data[] = new byte[1024];

            do
            {
            	//construct the client packet & send it
            	
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
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, serverPort);
                socket.send(sendPacket);
               
                //create packet and recieve the response from the server
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                String response = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                System.out.println("Response from server:\n" + response);
                System.out.println("Server Details:PORT " + incomingPacket.getPort()
                + ", IP Address: " + incomingPacket.getAddress());
                sendPacket = null; incomingPacket = null;
                System.out.println("My IP: " + InetAddress.getLocalHost().getHostAddress());
                System.out.println("My Port: " + socket.getLocalPort());
                System.out.println("__________________________________\n \n");

                // Wait for a random interval between 0-30 seconds
                int waitTime = random.nextInt(31) * 1000;
                Thread.sleep(waitTime);
            }while(true);
            
            //send THEEND message to server to terminate
            // sentence = "THEEND";
            // data = sentence.getBytes();
            // DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
            // socket.send(sendPacket);
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
        UDPClient2 client = new UDPClient2();
        client.createAndListenSocket();
    }
}
