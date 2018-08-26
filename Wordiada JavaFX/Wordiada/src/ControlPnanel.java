import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import java.lang.Thread;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ProgressBar;
import javafx.stage.Window;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Created by Kobi-PC on 31-May-17.
 */
public class ControlPnanel extends Application {
    Engine engine = new Engine();
    Board board = new Board();
    List<Board> boards= new ArrayList<Board>();
    List<Integer> numPlays=new ArrayList<>();
    List<String> wordsComposed= new ArrayList<>();
    List<List<String>> playerLists= new ArrayList<>();
    List<Integer> points= new ArrayList<>();
    List<Integer> turnOf= new ArrayList<>();
    private int boardsIndex;
    private int numOfSelect;
    private boolean startTheGame = true;
    private boolean ableToTestWord=false;
    private boolean ableToThrowCube=false;
    private boolean ableToExpose=false;
    private boolean ableToStartGame=false;
    private boolean ableToLoadXml=true;
    private boolean ableToQuit=false;
    private int diff;
    Stage window;

    @FXML
    Button loadXmlbtn = new Button();
    @FXML
    Button startGamebtn = new Button();
    @FXML
    Button quitGamebtn = new Button();
    @FXML
    Button throwCubebtn = new Button();
    @FXML
    Button selectSubmitbtn = new Button();
    @FXML
    Button testWordbtn = new Button();
    @FXML
    Button nextbtn = new Button();
    @FXML
    Button prevbtn = new Button();
    @FXML
    Label boardSize = new Label();
    @FXML
    Label goldenFish = new Label();
    @FXML
    Label gameType= new Label();
    @FXML
    Label winningAccording = new Label();
    @FXML
    Label stashSize = new Label();
    @FXML
    ListView<String> spareTaleFric = new ListView<>();
    @FXML
    ListView<String> rareDic = new ListView<>();
    @FXML
    Label numOfPlays=new Label();
    @FXML
    Label playerName = new Label();
    @FXML
    Label playerId = new Label();
    @FXML
    Label playerType = new Label();
    @FXML
    Label playerPoints = new Label();
    @FXML
    Label playerWords = new Label();
    @FXML
    Label wordComposed=new Label();
    @FXML
    ListView<String> wordComposedList = new ListView<>();
    @FXML
    ListView<String> playerList = new ListView<>();
    @FXML
    GridPane pane = new GridPane();
    @FXML
    Label massages = new Label("Welcome to wordiada, please Load XML File");

    Stage loadFile = new Stage();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("wordiada.fxml"));
        primaryStage.setTitle("Wordiada By - Tamir & Kobi");
        primaryStage.setScene(new Scene(root));
        pane.setHgap(2);
        pane.setVgap(2);
        pane.setPrefSize(10,10);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new javafx.geometry.Insets(25));
        primaryStage.show();
    }

    public void quitGame(){
        if (ableToQuit) {
            engine.getPlayers().get(engine.getTurnOf()).setHasQuit(true);
            ableToTestWord = false;
            if (engine.gameIsOver(board)) {
                gameOver();
            }
            else {
                moveToNextPlayer();
                playerUpdate(false);
                if (engine.getPlayers().get(engine.getTurnOf()).getType().getValue().equals("Computer")) {
                    makeComputerMove();
                }
            }
        }
    }

    public void reset(){
        boards.clear();
        points.clear();
        playerLists.clear();
        turnOf.clear();
        numPlays.clear();
        wordsComposed.clear();
    }

    public void throwCube()     {
        if (ableToThrowCube) {
            ableToQuit=false;
            board.setWordMode(false);
            quitGamebtn.setDisable(true);
            numOfSelect = engine.throwCube(board);
            massages.setText("Select " + numOfSelect + " Tiles");
            board.setGlobalNumOfSelect(numOfSelect);
            board.setSelect(true);
            ableToThrowCube=false;
            ableToExpose=true;
        }
        throwCubebtn.setDisable(true);
        selectSubmitbtn.setDisable(false);
    }

    public void expose() {
        if(board.getNumOfSelect() < numOfSelect && !engine.getPlayers().get(engine.getTurnOf()).getType().getValue().equals("Computer")){
            massages.setText("You have to select "+ numOfSelect +" tiles");
            ableToExpose=false;
        }
        else ableToExpose=true;
        if (ableToExpose) {
            ableToTestWord=true;
            pane.getChildren().clear();
            board.updatePane(pane);
            board.setWordMode(true);
            board.setNumOfSelect();
            engine.getPlayers().get(engine.getTurnOf()).setNumOfAttempts(engine.getNumOfAttempts());
            massages.setText("Try to compose a word");
            ableToExpose=false;
            selectSubmitbtn.setDisable(true);
            testWordbtn.setDisable(false);
        }

    }

    public void testWord() throws CloneNotSupportedException {
        board.setWordMode(true);
        if (ableToTestWord) {
            System.out.println("testing word" + board.getTestWord());
            engine.incNumOfPlays();
            turnOf.add(engine.getTurnOf());
            String word = board.getTestWord();
            wordsComposed.add(word);
            playerLists.add(engine.getPointsList(false));
            createNewBoard();
            numPlays.add(engine.numOfPlaysProperty().getValue());
            points.add(engine.getPlayers().get(engine.getTurnOf()).pointsProperty().getValue());
            if (engine.getPlayers().get(engine.getTurnOf()).getNumOfAttempts() >= 0) {
                if (engine.isLegalWord(word)) {//legal word
                    if (engine.getWinnigAccording().getValue().equals("WordCount")) {//word count
                        engine.getPlayers().get(engine.getTurnOf()).addWord((new SimpleStringProperty(word)));
                        engine.getPlayers().get(engine.getTurnOf()).addPoints(1);
                    } else {//word score
                        int score = board.calculateWordValue() * engine.getSigment(word);
                        engine.getPlayers().get(engine.getTurnOf()).addPoints(score);
                        engine.getPlayers().get(engine.getTurnOf()).addWord((new SimpleStringProperty(word + " (" + score + ")")));
                    }
                    System.out.println("legal word");
                    massages.setText("Word is legal");
                    engine.getPlayers().get(engine.getTurnOf()).incNumOfWords();
                    board.updateBoard(engine.getTheStash());
                    board.goldenFishMode(engine.goldenFishModeProperty().getValue());
                    pane.getChildren().clear();
                    board.updatePane(pane);
                    ableToTestWord = false;
                    if (!engine.gameIsOver(board))
                        moveToNextPlayer();//////////////////////////////
                }//legal word
                else {//////////ilegal word
                    System.out.println("illegal word");
                    if (engine.getPlayers().get(engine.getTurnOf()).getNumOfAttempts() != 0) {
                        massages.setText("Illegal word. You have " + engine.getPlayers().get(engine.getTurnOf()).getNumOfAttempts() + " more attempts");
                        engine.getPlayers().get(engine.getTurnOf()).dicNumOfAttempts();
                    }
                    else if (!engine.gameIsOver(board)) {
                        board.goldenFishMode(engine.goldenFishModeProperty().getValue());
                        pane.getChildren().clear();
                        board.updatePane(pane);
                        moveToNextPlayer();
                    }
                }
                board.clearWord();
                board.setChosenLetter();
                if (engine.gameIsOver(board))//game over
                    gameOver();
                else
                    playerUpdate(false);
            } else board.setWordMode(false);
            if (engine.getPlayers().get(engine.getTurnOf()).getType().getValue().equals("Computer")) {
                makeComputerMove();
            }
        }
    }

    public void gameOver(){
        massages.setText("The winner is " + engine.findPlayerWithMostPoints());
        boardsIndex=boards.size()-1;
        ableToTestWord=false;
        ableToThrowCube=false;
        ableToExpose=false;
        ableToStartGame=true;
        ableToLoadXml=true;
        startGamebtn.setDisable(false);
        loadXmlbtn.setDisable(false);
        throwCubebtn.setDisable(true);
        quitGamebtn.setDisable(true);
        testWordbtn.setDisable(true);
        nextbtn.setDisable(true);
        prevbtn.setDisable(false);
    }

    public void moveToNextPlayer(){
        engine.moveToNextPlayer();
        if (board.allTilesExposed()) {
            throwCubebtn.setDisable(true);
            selectSubmitbtn.setDisable(true);
            testWordbtn.setDisable(false);
            quitGamebtn.setDisable(false);
            ableToThrowCube = false;
            ableToTestWord = true;
            ableToQuit=true;
            board.setWordMode(true);
            engine.getPlayers().get(engine.getTurnOf()).setNumOfAttempts(engine.getNumOfAttempts());
            massages.setText("It's "+ engine.getPlayers().get(engine.getTurnOf()).getName()+"'s turn. Try to compose word.");
        }
        else{
            ableToThrowCube = true;
            ableToQuit = true;
            throwCubebtn.setDisable(false);
            testWordbtn.setDisable(true);
            quitGamebtn.setDisable(false);
            massages.setText("It's " + engine.getPlayers().get(engine.getTurnOf()).getName() + "'s turn. please throw cube.");
        }
    }

    public void createNewBoard() throws  CloneNotSupportedException {
        Board newboard=new Board();
        newboard.setSize(board.getSize());
        newboard.buildArray();
        for (int i=0; i<newboard.getSize(); i++){
            for (int j=0; j<newboard.getSize(); j++){
                newboard.setTile(board.getTile(i,j),i,j);
            }
        }
        boards.add(newboard);
    }

    public void startGame() {
        if (engine.isStartAnotherGame()){
            engine.restartNewGame();
            reset();
            wordComposedList.setVisible(true);
            wordComposed.setVisible(true);
            playerWords.setVisible(true);
        }

        if (ableToStartGame) {
            boardUpdate();
            board.setSelect(false);
            board.setMassages(massages);
            ableToThrowCube=true;
            massages.setText("It's "+engine.getPlayers().get(engine.getTurnOf()).getName()+"'s turn. please throw cube.");
            playerUpdate(false);
            if(engine.getPlayers().get(engine.getTurnOf()).getType().getValue().equals("Computer"))
                makeComputerMove();
            ableToStartGame=false;
            ableToLoadXml=false;
            ableToQuit=true;
        }
        loadXmlbtn.setDisable(true);
        quitGamebtn.setDisable(false);
        throwCubebtn.setDisable(false);
        startGamebtn.setDisable(true);
        nextbtn.setDisable(true);
        prevbtn.setDisable(true);
        stashSize.textProperty().bind(new SimpleStringProperty(String.valueOf(engine.getStashSize())));
    }

    public void afterGame(){
        numOfPlays.textProperty().bind(new SimpleStringProperty(String.valueOf(numPlays.get(boardsIndex))));
        playerName.textProperty().bind(new SimpleStringProperty(engine.getPlayers().get(turnOf.get(boardsIndex)).getName()));
        playerType.textProperty().bind(engine.getPlayers().get(turnOf.get(boardsIndex)).getType());
        playerId.textProperty().bind(new SimpleStringProperty(String.valueOf(engine.getPlayers().get(turnOf.get(boardsIndex)).getId())));
        playerPoints.textProperty().bind(new SimpleStringProperty(String.valueOf(points.get(boardsIndex))));
        playerWords.setVisible(false);
        wordComposed.setVisible(false);
        wordComposedList.setVisible(false);
        playerList.setItems(FXCollections.observableArrayList(playerLists.get(boardsIndex)));
        if (engine.isLegalWord(wordsComposed.get(boardsIndex)))
            massages.setText("Legal word composed on this play: "+wordsComposed.get(boardsIndex));
        else
            massages.setText("Illegal word composed on this play: "+wordsComposed.get(boardsIndex));
    }

    public void prev(){
        if (engine.gameIsOver(board)) {
            nextbtn.setDisable(false);
            boardsIndex--;
            if (boardsIndex == 0)
                prevbtn.setDisable(true);
            pane.getChildren().clear();
            boards.get(boardsIndex).updateReplayPane(pane);
            afterGame();
        }
    }

    public void next(){
        if (engine.gameIsOver(board)) {
            prevbtn.setDisable(false);
            boardsIndex++;
            if (boardsIndex == boards.size() - 1)
                nextbtn.setDisable(true);
            pane.getChildren().clear();
            boards.get(boardsIndex).updateReplayPane(pane);
            afterGame();
        }
    }

    private void boardUpdate() {
        board.setSize(Integer.valueOf(getBoardSize().getValue()));
        board.buildBoard(engine.getTheStash(), pane);

    }

    public void playerUpdate(boolean showType) {
        numOfPlays.textProperty().bind(new SimpleStringProperty(String.valueOf(engine.numOfPlaysProperty().getValue())));
        playerName.textProperty().bind(engine.getPlayers().get(engine.getTurnOf()).nameProperty());
        playerId.textProperty().bind(new SimpleStringProperty(String.valueOf(engine.getPlayers().get(engine.getTurnOf()).getId())));
        playerType.textProperty().bind(engine.getPlayers().get(engine.getTurnOf()).getType());
        playerPoints.textProperty().bind(new SimpleStringProperty(String.valueOf(engine.getPlayers().get(engine.getTurnOf()).getPoints())));
        playerWords.textProperty().bind(new SimpleStringProperty(String.valueOf(engine.getPlayers().get(engine.getTurnOf()).getNumOfWords())));
        wordComposedList.setItems(FXCollections.observableArrayList(engine.getPlayers().get(engine.getTurnOf()).wordComposedList()));
        playerList.setItems(FXCollections.observableArrayList(engine.getPointsList(showType)));
        stashSize.textProperty().bind(new SimpleStringProperty(String.valueOf(engine.getStashSize())));
    }

    public StringProperty getGoldenFish() {
        return new SimpleStringProperty(engine.goldenFishModeProperty().getValue() ? "yes" : "no");
    }

    public StringProperty getGameType() {
        return new SimpleStringProperty(engine.gameTypeProperty().getValue());
    }

    public StringProperty getBoardSize() {
        return new SimpleStringProperty(String.valueOf(engine.boardSizeProperty().getValue()));
    }

    public void loadXml() {
        engine = new Engine();
        if(ableToStartGame){
            spareTaleFric.getItems().clear();
            rareDic.getItems().clear();
            playerList.getItems().clear();
            wordComposedList.getItems().clear();
        }
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(null);
        if (!engine.initLoader(file.getPath())) {
            massages.setText("not an xml file");
        }else{
            massages.setText("loading xml file");
            if (engine.wrongXML(massages)) {
                engine.init();
                massages.setText("loading dictionary file");
                startGamebtn.setDisable(false);

                bindComponents();

                massages.setText("Please select 'Start Game' to start the game");
                ableToStartGame = true;
                if (engine.getPlayers().get(engine.getTurnOf()).getType().equals("Computer")) {
                    startGame();
                    makeComputerMove();
                }
                loadFile.hide();
                startTheGame =true;
            } else startTheGame = false;
        }
    }

    public void makeComputerMove(){
        quitGamebtn.setDisable(true);
        engine.incNumOfPlays();
        playerLists.add(engine.getPointsList(false));
        turnOf.add(engine.getTurnOf());
        numPlays.add(engine.numOfPlaysProperty().getValue());
        points.add(engine.getPlayers().get(engine.getTurnOf()).pointsProperty().getValue());
        TaskUpdater taskUpdater =new  TaskUpdater();
        new Thread(taskUpdater).start();
    }

    class TaskUpdater extends Task<Integer> {////////////////////////////////
        @Override
        protected Integer call() throws Exception {
            try {
                    boolean threwCube = false;
                    Thread.sleep(1700);
                    if (!board.allTilesExposed()) {
                        Platform.runLater((Runnable) () -> throwCube());
                        threwCube = true;
                        Thread.sleep(1700);
                    }
                    String letters = "";
                    int breaker = numOfSelect;
                    while (breaker != 0) {
                        boolean validInput = false;
                        while (!validInput) {
                            for (int m = 0; m < board.getSize() && breaker != 0; m++)
                                for (int n = 0; n < board.getSize() && breaker != 0; n++) {
                                    if (!board.allTilesExposed()) {
                                        if (!board.isExposed(n, m)) {
                                            board.exposeTile(n, m);
                                            letters = letters + board.getLetter(n, m).getValue();
                                            board.getTile(n,m).setChosen();
                                            validInput = true;
                                            breaker--;
                                        }
                                    }
                                    else{
                                        Thread.sleep(1700);
                                        letters=board.composeWord();
                                        validInput=true;
                                        breaker=0;
                                    }
                                }
                        }
                    }
                    if (threwCube) {
                        Platform.runLater((Runnable) () -> expose());
                        Thread.sleep(1700);
                    }
                    createNewBoard();////////////////////////////////
                    if (engine.isLegalWord(letters)) {
                        engine.getPlayers().get(engine.getTurnOf()).addWord(new SimpleStringProperty(letters));
                        board.updateBoard(engine.getTheStash());
                        engine.getPlayers().get(engine.getTurnOf()).incNumOfWords();
                        if (engine.getWinnigAccording().getValue().equals("WordCount")) {//word count
                            engine.getPlayers().get(engine.getTurnOf()).addWord((new SimpleStringProperty(letters)));
                            engine.getPlayers().get(engine.getTurnOf()).addPoints(1);
                        } else {//word score
                            int score = board.calculateWordValue() * engine.getSigment(letters);
                            engine.getPlayers().get(engine.getTurnOf()).addPoints(score);
                            engine.getPlayers().get(engine.getTurnOf()).addWord((new SimpleStringProperty(letters + " (" + score + ")")));
                        }
                    }
                    board.goldenFishMode(engine.goldenFishModeProperty().getValue());
                    Platform.runLater((Runnable) () -> pane.getChildren().clear());
                    board.setChosenLetter();
                    board.clearWord();
                    wordsComposed.add(letters);
                    Platform.runLater((Runnable) () -> board.updatePane(pane));
                    Platform.runLater((Runnable) () -> moveToNextPlayer());
                    Platform.runLater((Runnable) () -> playerUpdate(false));
                    Thread.sleep(2500);
                    if (engine.getPlayers().get(engine.getTurnOf()).getType().getValue().equals("Computer") && !engine.gameIsOver(board)) {
                        makeComputerMove();
                    }
                    if (engine.gameIsOver(board))
                        Platform.runLater((Runnable) () -> massages.setText("The winner is " + engine.findPlayerWithMostPoints()));
            }catch (CloneNotSupportedException e){
                massages.setText("There was problem cant make computer move");
            }catch(Exception e){
                massages.setText("There was problem cant make computer move");
            }
            return null;
        }
    }

    public void redStyle(){
        Scene scene = pane.getScene();
        scene.getStylesheets().add("style.css");
    }

    public void blueStyle(){
        Scene scene = pane.getScene();
        scene.getStylesheets().add("blue.css");
    }

    public void coolStyle(){
        Scene scene = pane.getScene();
        scene.getStylesheets().add("cool.css");
    }

    public void bindComponents(){
        boardSize.textProperty().bind(getBoardSize());
        goldenFish.textProperty().bind(getGoldenFish());
        gameType.textProperty().bind(getGameType());
        gameType.textProperty().bind(getGameType());
        stashSize.textProperty().bind(new SimpleStringProperty(String.valueOf(engine.getStashSize())));
        winningAccording.textProperty().bind(engine.getWinnigAccording());
        rareDic.setItems(FXCollections.observableArrayList(engine.getRareDic()));
        spareTaleFric.setItems(FXCollections.observableArrayList(engine.getStashFricList()));
        playerList.setItems(FXCollections.observableArrayList(engine.getPlayersList()));
        playerUpdate(true);
    }
}