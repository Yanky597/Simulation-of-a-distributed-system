package OS_TERM_PROJECT;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Client {

    public static void main(String args[]) throws IOException {


        // used for a client Id Number, so that it could be used to send back and forth
        // Every client has a client ID number that gets set over here and is then sent to the master so that the master knows where to send jobs.
          
            
      
        // ID is set with a random positive integer
        Random randNum = new Random();
        int id =  Math.abs(randNum.nextInt());

        // Hardcode in IP and Port here if required
        args = new String[]{"127.0.0.1", "30121"};

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        // creates the socket for the client
        try (Socket clientSocket = new Socket(hostName, portNumber);
             // used for sending and reading Objects
             ObjectOutputStream write = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream read = new ObjectInputStream(clientSocket.getInputStream());

        ){

            try{
                /*
                    The client thread has a read thread for reading incoming messages (ClientReadThread),
                    and a write thread for sending the batches of jobs (ClientWriteThread).
                */
                
                // initialize the threads

                ClientWriteThread writer = new ClientWriteThread(write, id);
                ClientReadThread reader = new ClientReadThread(read, id);
                
                // starts the threads
                writer.start();
                reader.start();

                // ends the threads
                reader.join();
                writer.join();

                // messages to print when all the jobs have been completed
                System.out.println("All jobs have been completed");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }

    }


}
