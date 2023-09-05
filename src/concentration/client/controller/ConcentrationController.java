package concentration.client.controller;

import concentration.client.model.ConcentrationModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Controller for Concentration board game. Sends the user action requests in GUI to server.
 * Does not hold any information about the game. Uses Concentration Protocol to communicate with the Server.
 *
 * @author Arya Girisha Rao, Pradeep Kumar Gontla.
 */

public class ConcentrationController {

    /**
     * Socket established with the host
     */
    private Socket server;
    /**
     * Concentration Game model.
     */
    private final ConcentrationModel model;
    /**
     * Buffered Reader established with server.
     */
    private BufferedReader serverReader;
    /**
     * printWriter established with server to send messages to server.
     */
    private PrintWriter serverWriter;

    /**
     * Concentration game Listener. Used to read responses from server.
     */
    ConcentrationListener concentrationListener;

    /**
     * Creates a new Concentration Controller Object. Used by GUI to update according to user action.
     *
     * @param hostName   hostName of the server to play Concentration game with.
     * @param portNumber port number of the server to play Concentration game with.
     * @param model      Concentration Game model.
     */
    public ConcentrationController(String hostName, int portNumber, ConcentrationModel model) {
        this.model = model;
        try {
            server = new Socket(hostName, portNumber);
            serverReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            serverWriter = new PrintWriter(server.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the details of the user activity to server using ConcentrationProtocol
     *
     * @param row row value of the card clicked for reveal.
     * @param col column value of the card clicked for reveal.
     */
    public void revealHiddenCard(int row, int col) {
        serverWriter.println(String.format(concentration.common.ConcentrationProtocol.REVEAL_MSG, row, col));
    }

    /**
     * Close the sockets in the end if the game is ended abruptly by the user.
     */
    public void closeSocket() {

        if (server != null && server.isConnected()) {
            try {
                server.close();
            } catch (IOException e) {
                System.err.println("Failed to close the socket. Error details: " + e.getMessage());
            }
        }
    }

    /**
     * Start the Controller. Reads the Board dimension from server and starts the Listener thread.
     *
     * @throws Exception Throws common exception if there are any issues with connecting with server, message from sever.
     */
    public void startConcentration() throws Exception {
        try {
            String serverInput = serverReader.readLine();
            model.createBoard(Integer.parseInt(serverInput.split(" ")[1]));
        } catch (IOException e) {
            throw new Exception("Failed to Start Concentration Controller. Error details: " + e.getMessage());
        }
        concentrationListener = new ConcentrationListener(serverReader, model);
        concentrationListener.start();
    }
}

