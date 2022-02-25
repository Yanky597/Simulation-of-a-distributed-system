package OS_TERM_PROJECT;


public class ServerSlaveConnection {
    
    int slaveID;
    // a data member to hold the slave type of a current connected slave. This is mainly used when deciding to dispatch a job to a given slave
    String slaveType;
    protected boolean slaveTypeIsSet = false;
    public static int amountOfSlaves = 0;
    boolean slaveIsRunning = false;
    public static int amountOfASlaves = 0;
    public static int amountOfBSlaves = 0;
    public static int completedJobs = 0;
    public boolean [] exitTheSystem;

    // get the amount of slaves that are currently running
    public ServerSlaveConnection(boolean [] exitTheSystem) {
        this.exitTheSystem = exitTheSystem;
        amountOfSlaves++;
    }


    public void setSlaveType(String slaveType) {
        this.slaveType = slaveType;
        slaveTypeIsSet = true;
    }

    public String getSlaveType() {
        return slaveType;
    }

    public boolean getSlaveTypeIsSet(){
        return slaveTypeIsSet;
    }

    public boolean isSlaveIsRunning() {
        return slaveIsRunning;
    }

    public void setSlaveIsRunning(boolean value){
        slaveIsRunning = value;
    }

    public static void incrementASlaves(){
        amountOfASlaves++;
    }

    public static void incrementBSlaves(){
        amountOfBSlaves++;
    }



}
