package OS_TERM_PROJECT;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;

public class ServerSlaveReader implements Runnable {

    // reads the completed jobs from the slave

    ArrayList<Packet> completedPacketList;
    Random randNum = new Random();
    ObjectInputStream read;
    ServerSlaveConnection connection;

    public ServerSlaveReader(ServerSlaveConnection connection, ObjectInputStream read, ArrayList<Packet> completedPacketList) {
        this.connection = connection;
        this.read = read;
        this.completedPacketList = completedPacketList;
    }

    @Override
    public void run() {

        // collects the responses from the slaves and either adds them to the completed job list or prints them

        try {

            /*
            * The first while loop sets the Slave type in the slaveConnection.
            */
            
            Packet serverResponse;
            while ((serverResponse = (Packet) read.readObject()) != null) {

                if(connection.exitTheSystem[0]){
                    break;
                }

                if (serverResponse.getIsAMessage()) {
                    connection.setSlaveType(serverResponse.getMessage());
                    if(serverResponse.getMessage().equals("A")){
                        ServerSlaveConnection.incrementASlaves();
                    }
                    else{
                        ServerSlaveConnection.incrementBSlaves();
                    }
//                    System.out.println("COLLECTOR: Slave (#" + slaveConnection.amountOfSlaves + ") of type: " + connection.getSlaveType() + " is connected");
                    connection.setSlaveIsRunning(true);
                    System.out.println("SLAVE READER:" + " Slave is running has been set to : " + connection.isSlaveIsRunning());
                    break;

                } else {
                    System.out.println("SLAVE READER: " + serverResponse);
                }
            }
            
            /*
            * The second while loop collects the completed jobs from the slaves and adds them to the CompletedJobList array
            */

//            Job serverResponse;
            while ((serverResponse = (Packet) read.readObject()) != null) {

                if(connection.exitTheSystem[0]){

                    break;
                }

                if(serverResponse.isJobIsDone()){
                    synchronized (completedPacketList){
                        completedPacketList.add(serverResponse);
                    }
                    ServerSlaveConnection.completedJobs++;
                    System.out.println("SLAVE READER: COMPLETED JOBS : " + ServerSlaveConnection.completedJobs);

                }
                System.out.println("SLAVE READER: " + serverResponse.getMessage());

            }

            read.close();

        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("\n***SLAVE READER DISCONNECTING***");

        }

    }

}




