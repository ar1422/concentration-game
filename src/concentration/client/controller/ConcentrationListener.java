package concentration.client.controller;

import concentration.client.model.ConcentrationModel;
import concentration.common.ConcentrationException;

import java.io.BufferedReader;
import java.util.Arrays;

/**
 * Listener for Concentration board game. Keeps checking if there are any message from the server.
 * On reading the message, calls model with corresponding method to update the board state accordingly.
 * Does not hold any information about the game. Uses Concentration Protocol to communicate with the Server.
 *
 * @author Arya Girisha Rao, Pradeep Kumar Gontla.
 */

public class ConcentrationListener extends Thread {

    /**
     * reader for the socket connected with server.
     */
    private final BufferedReader bufferedReader;

    /**
     * Concentration game model to update the game status accordingly.
     */
    private final ConcentrationModel model;

    /**
     * Creates new Concentration Listener. Runs as a thread.
     *
     * @param bufferedReader reader with the socket connected with server.
     * @param model          Concentration game model to update the game status accordingly.
     */
    public ConcentrationListener(BufferedReader bufferedReader, ConcentrationModel model) {
        this.model = model;
        this.bufferedReader = bufferedReader;
    }

    /**
     * Executes the command received
     *
     * @param serverInput Message received from the server.
     * @throws ConcentrationException Error while processing,
     */
    public void processServerCommand(String serverInput) throws ConcentrationException {
        try {
            String[] serverInputList = serverInput.split(" ");
            switch (serverInputList[0]) {
                case "CARD" -> model.revealCard(Integer.parseInt(serverInputList[1]), Integer.parseInt(serverInputList[2]), serverInputList[3].charAt(0));
                case "MATCH" -> model.updateMatchCount();
                case "MISMATCH" -> model.hideOpenedCards(Arrays.copyOfRange(serverInputList, 1, 5));
                case "GAME_OVER" -> model.setGameOver();
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new ConcentrationException(e);
        }

    }

    /**
     * Starts the listener thread. Keeps checking if there are any messages and processes read messages.
     */
    public void run() {
        String serverInput;
        try {
            while ((serverInput = bufferedReader.readLine()) != null) {
                processServerCommand(serverInput);
            }
        } catch (Exception e) {
            System.err.println("Ending Game Listener. Reason: " + e.getMessage());
        }
    }

}
