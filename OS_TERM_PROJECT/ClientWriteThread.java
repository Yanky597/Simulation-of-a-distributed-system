package OS_TERM_PROJECT;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientWriteThread extends Thread {

    // this is the writer stream
    ObjectOutputStream writer;
    // client Id that is set in the master
    int clientId;
    final String client = "CLIENT";

    // a list of the jobs sent
    ArrayList<Packet> jobsToSend = new ArrayList<>();

    // the job types, can be changed to int []

    // types of jobs for the slaves
    public static String[] jobTypes = {"A", "B"};


    // a constructor for the thread that is used for sending the jobs
    public ClientWriteThread(ObjectOutputStream writer, int clientId) {
        this.writer = writer;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);


        try {
            System.out.println("***CONNECTING TO SERVER***");
            writer.writeObject(new Packet(client));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        int jobID = 1;
        boolean done = false;
        String jobType = "";
        System.out.println("Send jobs by entering A or B : (Enter X to exit)" );
        while (!done) {


            jobType = scan.next().strip().toUpperCase();
            if(jobType.equals("A") || jobType.equals("B")){
                try {
                    // sends objects(JOB) to the master
                    Packet newPacket =  new Packet(jobID, jobType, clientId);
                    writer.writeObject(newPacket);
                    // flush method seems to be required for send freeing up the memory
                    writer.flush();
                    jobID++;
                    System.out.println("Job: " + newPacket + ", has been sent to the server");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (jobType.equals("X")){
                done = true;
                System.out.println("CLIENT : " + clientId  + " HAS NO MORE JOBS TO SEND");
            }
            else{
                System.out.println("ERROR: Send jobs by entering A or B : (Enter X to exit)" );
            }

        }

        scan.close();
    }


}
