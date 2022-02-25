package OS_TERM_PROJECT;

import java.io.ObjectInputStream;

public class ClientReadThread extends Thread {

    ObjectInputStream reader;
    
    // client ID that is set in the client class
    int clientId;
    // amount of jobs that the client has sent to the master


    public ClientReadThread(ObjectInputStream reader, int clientId){
        this.reader = reader;
        this.clientId = clientId;
    }

    @Override
    public void run()
    {

        try{

            // reads the response from the master
            Packet serverResponse;
            while ((serverResponse = (Packet) reader.readObject()) != null) {

                // checks if the job is a message or a job. 
                // if it is a message check if the message == the job is done message, if it is break out of the loop 
                if(serverResponse.getMessage() != null && !serverResponse.isJobIsDone()){
                    System.out.println(serverResponse.getMessage());
                }

                if(serverResponse.isJobIsDone()){
                    // prints a completed job message for each received jobs
                    System.out.println("\n" + serverResponse + " : has been completed");
                }
            }
            // closes the reader
            reader.close();
        }
        catch (Exception e){
            e.getStackTrace();
        }



    }

}
