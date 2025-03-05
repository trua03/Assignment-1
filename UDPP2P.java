import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;
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
            
            // Load IP address from configuration file
            Properties config = new Properties();
            config.load(new FileInputStream("UDPP2P.config"));
            String ipAddress = config.getProperty("IPAddress", "localhost");
            String directoryPath = config.getProperty("Directory");

            //create socket for the destination/server
            InetAddress IPAddress = InetAddress.getByName(ipAddress);
            int serverPort = 9876;
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

            	data = sentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, serverPort);
                socket.send(sendPacket);
               
                //create packet and recieve the response from the server
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                String response = new String(incomingPacket.getData());
                System.out.println("Response from server:" + response);
                System.out.println("Server Details:PORT " + incomingPacket.getPort()
                + ", IP Address: " + incomingPacket.getAddress());
                sendPacket = null; incomingPacket = null;
                System.out.println("Chat more? Y/N...");
                ch = in.nextLine().charAt(0);
            }while(ch=='y' || ch=='Y');
            
            //send THEEND message to server to terminate
            sentence = "THEEND";
            data = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
            socket.send(sendPacket);
            socket.close();
        }
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
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
