package OS_TERM_PROJECT;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

public class Slave {

    public static void main(String args[]) throws IOException {

        Scanner scan = new Scanner(System.in);

        // set the slave type
        System.out.println("SET SLAVE TYPE TO A/B : (STRING)");
        String slaveType = scan.next().strip().toUpperCase();

        while (!(slaveType.equals("A") || slaveType.equals("B"))) {
            // slave type
            System.out.println("INCORRECT ENTRY");
            System.out.println("SET SLAVE TYPE TO A/B : (STRING)");
            slaveType = scan.next().toUpperCase();
        }

        Random randNum = new Random();
        // sets a slave ID
        Integer SlaveAID = Math.abs(randNum.nextInt());
        // notifies the master of the type of program that is connected
        final String slave = "SLAVE";

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

        ) {


            // notify master of a connecting slave
            write.writeObject(new Packet(slave));
            Thread.sleep(100);


            System.out.println("SLAVE TYPE IS SET TO " + slaveType + ": CONNECTED = TRUE");
            // send the job type of the slave to the master to be set.
            write.writeObject(new Packet(slaveType));

            int amountOfCompletedJobs = 0;
            String exitCode = "EXIT";
            Packet serverResponse;
            while ((serverResponse = (Packet) read.readObject()) != null) {

                if (serverResponse.getIsAMessage() && serverResponse.getMessage().equals(exitCode)) {
                    break;
                }


                if (serverResponse.getIsAMessage()) {
                    System.out.println(serverResponse.getMessage());
                } else {
                    System.out.println("\nJOB RECEIVED : " + serverResponse);
                }

                /*
                 *  1. gets the job
                 *  2. checks the type
                 *  3. if the type == the type of "this" slave
                 *  4. sleep for 2 seconds
                 *  5. else sleep for 10 seconds
                 */

                if (!serverResponse.getIsAMessage()) {
                    if (serverResponse.getJobType().equals(slaveType)) {
                        System.out.println("SLAVE " + slaveType + " : " + SlaveAID + ": is executing an optimal JOB");
                        Thread.sleep(2000);
//                        Thread.sleep(200);
                    } else {
                        System.out.println("SLAVE " + slaveType + " : " + SlaveAID + ": is executing a non optimal JOB");
                        Thread.sleep(10000);
//                        Thread.sleep(1000);
                    }
                    amountOfCompletedJobs++;
                    serverResponse.setJobIsDone(true);
                }

                // sets the message of the job
                serverResponse.setMessage("SLAVE " + slaveType + " : " + serverResponse.getID() + ": COMPLETE");
                System.out.println("SLAVE" + slaveType + " : " + serverResponse.getMessage());
                System.out.println("AMOUNT OF COMPLETED JOBS : " + amountOfCompletedJobs);
                // sends the completed job object back to the master
                write.writeObject(serverResponse);
                // flush method seems to be required for send freeing up the memory
                write.flush();
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("\n***SLAVE DISCONNECTING FROM SERVER***");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}

