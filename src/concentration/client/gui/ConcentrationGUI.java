package concentration.client.gui;

import concentration.client.controller.ConcentrationController;
import concentration.client.model.ConcentrationModel;
import concentration.client.model.Observer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * GUI for Concentration game. This class acts as View  of the MVC architecture for this Game.
 *
 * @author Arya Girisha Rao, Pradeep Kumar Gontla.
 */

public class ConcentrationGUI extends Application implements Observer<ConcentrationModel> {

    /**
     * Text field to display number of moves made by the user.
     */
    private Text movesMade;

    /**
     * GridPane to hold all the Buttons used in the game as cards.
     */
    private GridPane gridPane;

    /**
     * Text field to display status of the game.
     */
    private Text status;
    /**
     * Text field to display number of matches in the game.
     */
    private Text matchCount;

    /**
     * Concentration model for the game.
     */
    private ConcentrationModel model;

    /**
     * Object which is having all the available images of pokemon for every possible character of the card.
     */
    private ConcentrationCardImages pokeMonImages;
    /**
     * Controller for the game.
     */
    private ConcentrationController controller;

    /**
     * Initialization method. Creating the board model object and adding the UI as the observer.
     */
    public void init() {
        List<String> args = getParameters().getRaw();
        if (!validateCLICommand(args)) {
            System.out.println("Usage: ConcentrationGUI <host_name> <port_number>");
            System.exit(0);
        }

        model = new ConcentrationModel();
        model.addObserver(this);

        controller = new ConcentrationController(args.get(0), Integer.parseInt(args.get(1)), model);
        pokeMonImages = new ConcentrationCardImages();
    }

    /**
     * Method to check if the user command provided is valid or not.
     *
     * @return boolean indicating whether the length of the command and format is as expected
     */

    public boolean validateCLICommand(List<String> arguments) {
        return arguments.size() == 2 && arguments.get(1).chars().allMatch(Character::isDigit);
    }

    /**
     * Main Entry point for JAVAFX Application which creates
     *
     * @param stage The primary stage for the application, onto which the application scene can be set.
     * @throws Exception Part of the signature method.
     */
    @Override
    public void start(Stage stage) throws Exception {

        controller.startConcentration();
        matchCount = new Text("Matches: 0");
        status = new Text("Status: NOT_STARTED");
        movesMade = new Text(" Moves: 0");

        BorderPane borderPane = new BorderPane();
        FlowPane flowPane = new FlowPane();
        gridPane = makeGridPane();
        borderPane.setCenter(gridPane);

        flowPane.getChildren().addAll(movesMade, matchCount, status);
        flowPane.setHgap(120);
        borderPane.setBottom(flowPane);
        Scene scene = new Scene(borderPane);
        stage.setTitle("Connect Four GUI");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    /**
     * creates the GridPane with ROWS * COLS number of buttons.
     * Each button has an image as part of its Graphic, displaying whether the cell is played.
     *
     * @return gridPane containing ROWS * COLS number of buttons
     */
    private GridPane makeGridPane() {
        GridPane gridPane = new GridPane();
        int dimension = model.getDimension();
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                Button button = new Button();
                button.setGraphic(new ImageView(pokeMonImages.defaultPokeBall));
                int rowValue = row;
                int colValue = col;
                button.setOnAction((actionEvent) -> {
                    if (model.isValidClick(rowValue, colValue)) this.controller.revealHiddenCard(rowValue, colValue);
                });
                gridPane.add(button, col, row);
            }
        }
        return gridPane;
    }

    /**
     * Update all the buttons' image with the new status.
     * Get the pokemon assigned to each character from Image Map and display accordingly.
     *
     * @param concentrationModel Concentration model for the game.
     */
    private void updateGridPane(ConcentrationModel concentrationModel) {
        Function<Character, Image> getImage = (charValue) -> {
            if (charValue == '.') return pokeMonImages.defaultPokeBall;
            else return pokeMonImages.imageMap.get(charValue);
        };
        Function<Integer, Button> getGridButton = (position -> (Button) gridPane.getChildren().get(position));
        int dimension = concentrationModel.getDimension();

        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                getGridButton.apply(row * dimension + col).setGraphic(new ImageView(getImage.apply(concentrationModel.getCellValue(row, col))));
            }
        }
    }

    /**
     * Update the moves count in the game with the updated value from the model.
     *
     * @param concentrationModel Concentration model for the game.
     */
    private void updateMovesCount(ConcentrationModel concentrationModel) {
        movesMade.setText("Moves: " + concentrationModel.getMovesMade());
    }

    /**
     * Update status of the game with the updated value from the model.
     *
     * @param concentrationModel Concentration model for the game.
     */
    private void updateStatus(ConcentrationModel concentrationModel) {
        BiConsumer<Text, Boolean> setGameOverStatus = ((text, aBoolean) -> {
            if (aBoolean) text.setText("Status: GAME_OVER");
            else text.setText("Status: IN_PROGRESS");
        });
        setGameOverStatus.accept(status, concentrationModel.isGameOver());

    }

    /**
     * Update the Number of Matches with the updated value from the model.
     *
     * @param concentrationModel Concentration model for the game.
     */
    private void updateMatchCount(ConcentrationModel concentrationModel) {
        matchCount.setText("Matches: " + concentrationModel.getMatches());
    }

    /**
     * Method to disable all the button and essentially disable the game.
     * Once the game results in IN_PROGRESS or GAME Over, window is not closed, but all the actions are disabled.
     */
    private void disableGame() {
        ObservableList<Node> listOfButtons = gridPane.getChildren();
        Consumer<Node> disableButton = node -> node.setDisable(true);
        listOfButtons.forEach(disableButton);
    }

    /**
     * Method to refresh the UI everytime model has updated its state/status.
     *
     * @param concentrationModel Concentration model for the game.
     */
    private void refresh(ConcentrationModel concentrationModel) {
        updateMovesCount(concentrationModel);
        updateMatchCount(concentrationModel);
        updateStatus(concentrationModel);
        updateGridPane(concentrationModel);
        if (concentrationModel.isGameOver()) disableGame();
    }

    /**
     * Performs update whenever the Model indicates that it's state has changed.
     * Gets called from notifyObservers function of the model.
     *
     * @param concentrationModel Concentration model for the game.
     */
    @Override
    public void update(ConcentrationModel concentrationModel) {
        if (Platform.isFxApplicationThread()) {
            this.refresh(concentrationModel);
        } else {
            Platform.runLater(() -> this.refresh(concentrationModel));
        }
    }

    /**
     * Method before exit when the application is closed. Used to close the resource.
     */
    @Override
    public void stop() {
        controller.closeSocket();
    }
}
