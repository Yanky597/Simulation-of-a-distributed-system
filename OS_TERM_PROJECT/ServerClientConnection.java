package OS_TERM_PROJECT;

import java.util.HashSet;

public class ServerClientConnection {
    
    
    // holds a single connection to a client
    int clientID;
    protected boolean IDisSet = false;

    // IDs of all the connected clients
    private static HashSet<Integer> connectedClients = new HashSet<>();

    //total amount of all jobs accepted by all clients
    public static int totalAmountOfJobs = 0;

    // exits the system
    public boolean [] exitTheSystem;


    // a static data member to hold the amount of clients that are currently running
    public static int amountOfClients;
    // a constructor that increments the amount of clients that are currently running
    public ServerClientConnection(boolean [] exitTheSystem) {
        this.exitTheSystem = exitTheSystem;
        amountOfClients++;
    }


    public static void addToConnectedClients(Integer clientID){
        connectedClients.add(clientID);
    }

    public static boolean clientIsAlreadyConnected(Integer clientID){
        return connectedClients.contains(clientID);
    }


    public void setClientID(int clientID) {
        this.clientID = clientID;
        IDisSet = true;
    }

    public int getClientID() {
        return clientID;
    }

    public boolean getIDisSet(){
        return IDisSet;
    }



}
