package OS_TERM_PROJECT;

import java.util.Scanner;

public class ServerExitThread extends Thread{

    /*
    * - The purpose of this class is to exit the program gracefully.
    * - Enter the exit code (X) and the server as well as all of the slaves should exit.
    * - The program exits more gracefully when there are no jobs being currently being sent or executed on.
    * - To exit the Clients you need to exit from within the clients
    */

    public boolean [] exitTheSystem;

    public ServerExitThread(boolean [] exitTheSystem){
        this.exitTheSystem = exitTheSystem;
    }

    public void run(){
        Scanner scan = new Scanner(System.in);
        while(!exitTheSystem[0]){
                System.out.println("Press X to exit : (String)");
                String exitCode = scan.next().strip();
                if (exitCode.equals("x") || exitCode.equals("X")){
                    System.out.println("\n***TERMINATION ACTIVATED***");
                    exitTheSystem[0] = true;
                }
        }

        System.out.println("\n***SERVER POWERING DOWN***");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(1);

    }
}
