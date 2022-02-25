package OS_TERM_PROJECT;

import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ServerClientReader implements Runnable {

    // a read stream
    protected ObjectInputStream read;
    // this is a connection to the client connection, mainly used for getting the client ID
    ServerClientConnection connection;
    // the job list we add the jobs to
    ArrayList<Packet> jobList;

    public ServerClientReader(ServerClientConnection connection, ObjectInputStream read, ArrayList<Packet> jobList) {
        this.connection = connection;
        this.read = read;
        this.jobList = jobList;
    }

    @Override
    public void run() {

        // Listens for new jobs from the clients and adds them to the JobList array

        System.out.println("***CLIENT READER IS CONNECTED***");

        try{
            Packet serverResponse;
            while ((serverResponse = (Packet) read.readObject()) != null) {

                if(connection.exitTheSystem[0]){
                    break;
                }

                // sets the client ID if it has not been set
                if(!connection.getIDisSet() && !ServerClientConnection.clientIsAlreadyConnected(serverResponse.getClientID()) && !serverResponse.getIsAMessage()){
                    connection.setClientID(serverResponse.getClientID());
                    ServerClientConnection.addToConnectedClients(serverResponse.getClientID());
                    System.out.println("CLIENT READER: CLIENT ID :" + connection.getClientID() + " is set");
                }
                // if the JOB object is not a message, adds the job to the JobList
                if(!serverResponse.getIsAMessage()){
                    jobList.add(serverResponse);
                    ServerClientConnection.totalAmountOfJobs++;
                    System.out.println("CLIENT READER: Received = " + serverResponse);
                }
                // otherwise we just print the message
                else{
                    System.out.println("CLIENT READER: " +  serverResponse.getMessage());
                }

            }
            read.close();

        }
        catch (Exception e){
//            e.printStackTrace();
            // When all the jobs have been completed this message is printed.
            System.out.println("***JOBS COMPLETED FOR CLIENT " + connection.getClientID() + "***");
        }


    }

}
