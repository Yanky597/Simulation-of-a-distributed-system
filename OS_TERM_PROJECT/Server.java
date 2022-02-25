package OS_TERM_PROJECT;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    static ArrayList<Packet>    jobList             = new ArrayList<>();    // list of uncompleted jobs
    static ArrayList<Packet>    completedJobList    = new ArrayList<>();    // list of completed jobs
    static ArrayList<Thread>    slaves              = new ArrayList<>();    // collection of slave connections
    static ArrayList<Thread>    clients             = new ArrayList<>();    // collection of client connections
    static ObjectOutputStream   write;
    static ObjectInputStream    read;
    static boolean [] exitThread = {false};


    public static void main(String args[]) throws IOException {

        int portNumber = 30121;
        Scanner scan = new Scanner(System.in);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println( "***SERVER is running***" + "\n\nWAITING FOR CONNECTIONS");
            ServerExitThread exit = new ServerExitThread(exitThread);
            exit.start();
            // listen for clients and slaves
            while(!exitThread[0]){
                try {
                    Socket clientSocket = serverSocket.accept();
                    write = new ObjectOutputStream(clientSocket.getOutputStream());
                    read = new ObjectInputStream(clientSocket.getInputStream());

                    // read response, determine if client or slave
                    Packet serverResponse = (Packet) read.readObject();
                    if(serverResponse.getMessage().equals("CLIENT"))
                        // add client
                        addClient();
                    else if (serverResponse.getMessage().equals("SLAVE"))
                        // add slave
                        addSlave();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (Thread x : clients){
                x.join();
            }

            for (Thread x : slaves){
                x.join();
            }
            exit.join();


        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
        scan.close();
    }

    static private void addClient() throws IOException {
        ServerClientConnection aClientConnection = new ServerClientConnection(exitThread);

        clients.add(new Thread(new ServerClientWriter(aClientConnection, write, completedJobList, jobList)));
        clients.add(new Thread(new ServerClientReader(aClientConnection, read, jobList)));

        clients.get(clients.size()-1).start();
        clients.get(clients.size()-2).start();
    }


    static private void addSlave() throws IOException {
        ServerSlaveConnection connection = new ServerSlaveConnection(exitThread);

        slaves.add(new Thread(new ServerSlaveWriter(connection, write, jobList)));
        slaves.add(new Thread(new ServerSlaveReader(connection, read, completedJobList)));

        slaves.get(slaves.size()-1).start();
        slaves.get(slaves.size()-2).start();
    }
}


