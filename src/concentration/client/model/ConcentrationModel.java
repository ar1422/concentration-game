package concentration.client.model;

import concentration.common.ConcentrationCard;

import java.util.LinkedList;
import java.util.List;

/**
 * Game model for the Concentration game.
 *
 * @author Arya Girisha Rao, Pradeep Kumar Gontla.
 */
public class ConcentrationModel {

    /**
     * Dimension of the board of the Concentration game.
     */
    private int dimension;

    /**
     * Simple version of board of the concentration game to keep track status of each cards.
     */
    private char[][] board;

    /**
     * Number of matches done during the game.
     */
    private int matches;

    /**
     * Number of moves made during the game.
     */
    private int movesMade;

    /**
     * Boolean indicating if the game is over.
     */
    private boolean gameOver;

    /**
     * the observers of this model.
     */
    private final List<Observer<ConcentrationModel>> observers;

    /**
     * Creates a new Concentration game model.
     */
    public ConcentrationModel() {
        this.matches = 0;
        this.movesMade = 0;
        this.gameOver = false;
        this.observers = new LinkedList<>();
    }

    /**
     * Gives number of moves made by the user during the game.
     *
     * @return Number of moves made by the user.
     */
    public int getMovesMade() {
        return movesMade;
    }

    /**
     * Creates new Simple version of the Concentration game board and mark all cards to start states.
     *
     * @param dimension dimension of the Concentration game board.
     */
    public void createBoard(int dimension) {
        this.dimension = dimension;
        this.board = new char[dimension][dimension];
        this.createGrid();
    }

    /**
     * Check if the click made by the user on GUI is valid or not.
     *
     * @param row row of the button clicked.
     * @param col column of the button clicked.
     * @return boolean indicating if the click made is valid or not.
     */
    public boolean isValidClick(int row, int col) {
        return board[row][col] == ConcentrationCard.HIDDEN;
    }

    /**
     * Get status of the cell in the game board.
     *
     * @param row row value of the cell in the game board.
     * @param col column value of the cell in the game board.
     * @return Character indicating the card value or HIDDEN to indicate card is yet to open.
     */
    public char getCellValue(int row, int col) {
        return board[row][col];
    }

    /**
     * Creates the Game board grid with HIDDEN values for all the cards to indicate the start state.
     */
    public void createGrid() {
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                this.board[row][col] = ConcentrationCard.HIDDEN;
            }
        }
    }

    /**
     * Update the status of the grid for a specific cell.
     *
     * @param row    row value of the cell in the grid.
     * @param col    column value of the cell in the grid.
     * @param letter New value to be stored in the cell selected.
     */
    public void modifyGrid(int row, int col, char letter) {
        board[row][col] = letter;

    }

    /**
     * Open a hidden card in the game board.
     *
     * @param row    row value of the card revealed.
     * @param col    column value of the card revealed.
     * @param letter letter of the revealed card.
     */
    public void revealCard(int row, int col, char letter) {
        modifyGrid(row, col, letter);
        movesMade += 1;
        notifyObservers();
    }

    /**
     * Hides the opened cards whenever the last two cards opened is a mismatch.
     *
     * @param openedCards Co-ordinates of previously opened cards.
     */
    public void hideOpenedCards(String[] openedCards) {
        modifyGrid(Integer.parseInt(openedCards[0]), Integer.parseInt(openedCards[1]), ConcentrationCard.HIDDEN);
        modifyGrid(Integer.parseInt(openedCards[2]), Integer.parseInt(openedCards[3]), ConcentrationCard.HIDDEN);
        notifyObservers();
    }

    /**
     * update the total match count in the game.
     */
    public void updateMatchCount() {
        matches += 1;
        notifyObservers();
    }

    /**
     * Check if the game is over or still in progress.
     *
     * @return Boolean indicating if the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Update the status of the Game to over.
     */
    public void setGameOver() {
        gameOver = true;
        notifyObservers();
    }

    /**
     * Get the total count of the matches during the game.
     *
     * @return number of matches.
     */
    public int getMatches() {
        return matches;
    }

    /**
     * Add an Observer to the list to notify the changes in model.
     *
     * @param observer Observer who needs to be notified about the change in the model.
     */
    public void addObserver(Observer<ConcentrationModel> observer) {
        this.observers.add(observer);
    }

    /**
     * Notify all the Observers that the state of the model has changed.
     */
    private void notifyObservers() {
        for (Observer<ConcentrationModel> obs : this.observers) {
            obs.update(this);
        }
    }

    /**
     * Get the dimension of the Concentration game board.
     *
     * @return dimension of the Concentration game board.
     */
    public int getDimension() {
        return dimension;
    }

}
