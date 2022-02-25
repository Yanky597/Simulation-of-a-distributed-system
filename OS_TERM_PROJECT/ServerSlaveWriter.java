package OS_TERM_PROJECT;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ServerSlaveWriter implements Runnable {

    /*
     * The purpose of the ServerSlaveWriter class is to sends the jobs to the slaves.
     * Each instance of the ServerSlaveWriter class will be constantly choosing jobs to send to its slave.
     * For each job that is sent to the slave, the ServerSlaveWriter calculates if it should send a job to a slave of the optimized type
     * or not.
     */

    ServerSlaveConnection connection;
    ObjectOutputStream write;
    ArrayList<Packet> jobList;

    public ServerSlaveWriter(ServerSlaveConnection connection, ObjectOutputStream write, ArrayList<Packet> jobList) {
        this.connection = connection;
        this.write = write;
        this.jobList = jobList;
    }


    @Override
    public void run() {

        // Dispatches jobs to the slaves

        System.out.println("\nSLAVE WRITER: ***SLAVE CONNECTING***");

        try {

            // sends a connection notice to the slave its connected to
            write.writeObject(new Packet("SERVER RESPONSE: SLAVE CONNECTED "));

            // waits a moment for the connection to be set in the slave connection. The slave connection is set by the the collector
            while (!connection.getSlaveTypeIsSet()) {
                System.out.println("SLAVE WRITER: INITIALIZING...");
                Thread.sleep(2000);
            }

            // once the connection is set we wait for jobs to be added to the jobs list
            while (jobList.isEmpty()) {
                Thread.sleep(5000);
            }

            // while the job list is not empty we send jobs to the slaves
            System.out.println("SLAVE WRITER: " + connection.getSlaveType());
            while (!connection.exitTheSystem[0]) {
                // method can be found further down
                sendJobsToSlave(jobList);
                Thread.sleep(1000);
            }

            write.close();


        } catch (Exception e) {

            e.printStackTrace();
            e.getMessage();
        }


    }

    private void sendJobsToSlave(ArrayList<Packet> jobList) throws IOException, InterruptedException {
        /*
         * -get the amount of jobs that are of either type A or Type B
         * - if there are more optimized jobs than non optimized jobs for a particular type
         * - get the difference, and for every 5 to 1 of optimized jobs to non optimized jobs
         * - send one non optimized job to a given slave.
         *
         * */
        String optimizedJobType = connection.getSlaveType();
        int optimizedJobs = 0;
        String notOptimizedJobType = "A".equals(connection.getSlaveType()) ? "B" : "A";
        boolean optimizedIsA = "A".equals(connection.getSlaveType());
        int amountOfOptimizedSlaves = optimizedIsA ? ServerSlaveConnection.amountOfASlaves : ServerSlaveConnection.amountOfBSlaves;
        int amountOfNonOptimizedSlaves = !optimizedIsA ? ServerSlaveConnection.amountOfASlaves : ServerSlaveConnection.amountOfBSlaves;

        int nonOptimized = 0;
        int remainingJobs = 0;


        boolean takeAQuickNap = false;

        // The dispatcher is either going to send the first optimized index, or non optimized index that it finds
        int optimizedIndex = 0;
        boolean setOptimized = true;
        int nonOptimizedIndex = 0;
        boolean setNonOptimized = true;

        String terminationCode = "EXIT";
        if (connection.exitTheSystem[0]) {
            write.writeObject(new Packet(terminationCode));
            write.flush();
            ServerSlaveConnection.amountOfSlaves--;
        } else {
            // counts all available optimized jobs
            synchronized (jobList) {
                for (int i = 0; i < jobList.size(); i++) {
                    if (jobList.get(i).getJobType().equals(optimizedJobType)) {
                        optimizedJobs++;
                        if (setOptimized) {
                            optimizedIndex = i;
                            setOptimized = false;
                        }
                    }
                    // counts all available non optimized jobs
                    else if (jobList.get(i).getJobType().equals(notOptimizedJobType)) {
                        nonOptimized++;
                        if (setNonOptimized) {
                            nonOptimizedIndex = i;
                            setNonOptimized = false;
                        }
                    }
                }

                remainingJobs = Math.abs(optimizedJobs - nonOptimized);

                /*
                 * If there are optimized jobs left, The optimized slave should
                 * only execute on those jobs.
                 * If there arent any optimized jobs left, the Dispatcher decides if its worth it to send
                 * a non optimized job to slave.
                 * */

                // if there are only non optimized jobs left, but its less or equal to 5 jobs. the non optimal slave should
                // start executing on those jobs

                int jobsForOptimalSlaves = optimizedJobs;

                /* if there are no more optimized jobs, as well as optimized slaves, the non optimal slave will execute
                 * all of the remaining non optimized jobs
                 * */

                int jobsForNonOptimalSlave = amountOfNonOptimizedSlaves == 0 ? nonOptimized : ((remainingJobs / amountOfNonOptimizedSlaves) / 5);

                if (optimizedJobs == 0 && nonOptimized > 5) {
                    jobsForNonOptimalSlave = (nonOptimized / 5) / amountOfNonOptimizedSlaves;
                }

                System.out.println("\n--------DISPATCHING JOBS--------\n");
                System.out.println("OPTIMIZED TYPE.............. " + optimizedJobType);
                System.out.println("TOTAL OPTIMIZED JOBS........." + optimizedJobs);
                System.out.println("TOTAL NON-OPTIMIZED JOBS....." + nonOptimized);
                System.out.println("OPTIMIZED SLAVES............." + amountOfOptimizedSlaves);
                System.out.println("NON OPTIMIZED SLAVES........." + amountOfNonOptimizedSlaves);
                System.out.println("JOBS FOR OPTIMAL SLAVE......." + jobsForOptimalSlaves);
                System.out.println("JOBS FOR NON OPTIMAL SLAVE..." + jobsForNonOptimalSlave);
                System.out.println();

                synchronized (jobList) {
                    if (jobsForOptimalSlaves > 0) {
                        // sends the jobs to the slaves.
                        Packet currentPacket = this.jobList.remove(optimizedIndex);
                        System.out.println("SLAVE WRITER: " + currentPacket + " is being sent to a Slave ");
                        write.writeObject(currentPacket);
                        write.flush();
                        return;
                    } else if (jobsForNonOptimalSlave > 0) {
                        // sends the jobs to the slaves.
                        Packet currentPacket = this.jobList.remove(nonOptimizedIndex);
                        System.out.println("SLAVE WRITER: " + currentPacket + " is being sent to a Slave ");
                        write.writeObject(currentPacket);
                        write.flush();
                        return;
                    } else {
                        takeAQuickNap = true;
                    }
//            }
                }

            }

            // if there are no jobs for this thread to execute it should take some short breaks
            if (takeAQuickNap) {
                System.out.println("\nSLAVE WRITER : ***NO JOBS TO EXECUTE***");
                Thread.sleep(10000);
            }
        }
    }
}
