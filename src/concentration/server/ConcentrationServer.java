package concentration.server;

import concentration.common.ConcentrationException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server For Concentration board game. Talks to multiple clients at the same.
 * Uses ConcentrationClientServerThread to handle each client's game request.
 *
 * @author Arya Girisha Rao, Pradeep Kumar Gontla.
 */

public class ConcentrationServer {

    /**
     *
     * @param args CLI Arguments received from the user. Required format is port_number board_dimension
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: Java Concentration Server <PortNumber> <Board Dimensions>");
            System.exit(-1);
        }
        int portNumber = 0;
        int boardDimensions = 0;
        try {
            portNumber = Integer.parseInt(args[0]);
            boardDimensions = Integer.parseInt(args[1]);
        }

        catch (NumberFormatException e){
            System.err.println("Failed to start the server. Error Details: " + e.getMessage());
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new ConcentrationClientServerThread(socket, new ConcentrationBoard(boardDimensions));
                thread.start();
            }
        }
        catch (ConcentrationException | IOException e){
            System.err.println("Failed to start the server. Error Details: " + e.getMessage());
        }
    }
}
