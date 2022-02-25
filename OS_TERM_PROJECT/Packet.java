package OS_TERM_PROJECT;

import java.io.Serializable;

public class Packet implements Serializable {

    private int ID;
    private String jobType;
    private int clientID;
    private String message;
    private boolean isAMessage = false;
    private boolean jobIsDone = false;

    // job packet constructor
    public Packet(int ID, String jobType, int clientID){
        this.ID = ID;
        this.jobType = jobType;
        this.clientID = clientID;
    }

    // message packet constructor
    public Packet(String message){
        this.message = message;
        this.isAMessage = true;
    }

    // if it is not a job, message will return true
    public boolean getIsAMessage(){
        return isAMessage;
    }

    public String getJobType() {
        return jobType;
    }

    public int getClientID() {
        return clientID;
    }

    public int getID() {
        return ID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message =  message;
    }

    public String toString(){
        return "JOB ID: " + ID + ", JOB TYPE: " + jobType + ", CLIENT# : " + clientID;
    }


    public boolean isJobIsDone() {
        return jobIsDone;
    }

    public void setJobIsDone(boolean jobIsDone) {
        this.jobIsDone = jobIsDone;
    }

}
