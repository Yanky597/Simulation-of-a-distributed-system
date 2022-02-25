package OS_TERM_PROJECT;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ServerClientWriter implements Runnable {

    ServerClientConnection connection;
    private ObjectOutputStream write;
    ArrayList<Packet> completedJobList;
    ArrayList<Packet> packetList;

    public ServerClientWriter(ServerClientConnection connection, ObjectOutputStream write, ArrayList<Packet> completedJobList, ArrayList<Packet> packetList) {
        this.connection = connection;
        this.write = write;
        this.completedJobList = completedJobList;
        this.packetList = packetList;
    }

    @Override
    public void run() {

        /*
         * The purpose of this class is a thread that notifies the client of completed jobs.
         */
        System.out.println("***CLIENT WRITER IS CONNECTED***");


        try {

            // waits until the Listener has set the connection of the current ClientConnection
            while (!connection.getIDisSet()) {
                Thread.sleep(1000);
            }

            // waits until any jobs have been added to the Completed jobs list
            while (completedJobList.isEmpty()) {
                Thread.sleep(1000);
                System.out.println("CLIENT WRITER : CompletedJobList is empty");
            }

            int amountOfCompletedJobs = 0;

            while (!connection.exitTheSystem[0]) {

                Thread.sleep(2000);

                // a synchronized block to avoid try to send a job from an empty CompletedJobList
                synchronized (completedJobList) {
                    if (!completedJobList.isEmpty()) {
                        try {
                            System.out.println("\nCLIENT WRITER: SIZE OF JOB LIST : " + packetList.size());
                            System.out.println("CLIENT WRITER: SIZE OF COMPLETED JOB LIST : " + completedJobList.size());
                            System.out.println("CLIENT WRITER: TOTAL JOBS (ALL CLIENTS) : " + ServerClientConnection.totalAmountOfJobs);

                            // goes through the completed job list and notifies the client whos client ID is set in the client connection of a completed job
                            for (int i = 0; i < completedJobList.size(); i++) {

                                if (completedJobList.get(i).getClientID() == connection.getClientID()) {
                                    // sets the amount of total jobs that will be completed for this client

                                    // removes a job from the completed job list
                                    Packet currentPacket = completedJobList.remove(i);

                                    // prints the current job
                                    System.out.println("\nCLIENT WRITER: " + currentPacket + " is complete, notifying client : " + currentPacket.getClientID());
                                    // sends the completed job to the client
                                    write.writeObject(currentPacket);
                                    write.flush();
                                    amountOfCompletedJobs++;
                                }

                            }


                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            System.out.println("CLIENT WRITER: " + completedJobList.size());
                            System.out.println("CLIENT WRITER:  AMOUNT OF COMPLETED JOBS : " + amountOfCompletedJobs);
                        }
                    }
                }
            }
            write.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
