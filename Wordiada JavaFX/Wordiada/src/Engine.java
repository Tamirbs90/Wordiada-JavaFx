import javafx.beans.property.*;
import javafx.scene.control.Label;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kobi-PC on 21-Apr-17.
 */

public class Engine {
    private Loader load = new Loader();
    private List<Player> players = new ArrayList<Player>();
    private Board gameBoard = new Board();
    private IntegerProperty numOfPlays = new SimpleIntegerProperty(0);
    private int numOfAttempts;
    private int cubeFace;
    private StringProperty gameType= new SimpleStringProperty();
    private List<Tile> theStash = new ArrayList<Tile>();
    private boolean stopTheGame = false;
    private boolean loader_ended = false;
    private boolean initiated = false;
    private int turnOf = 0; // 0-payer1, 1-payer2
    private long startTime = System.nanoTime();
    private boolean startAnotherGame = false;
    private boolean firstLoop = true;
    private BooleanProperty goldenFishMode= new SimpleBooleanProperty();
    private String letters = "";
    private StringProperty currentPlayerId=new SimpleStringProperty();
    private IntegerProperty currentPlayerNumWords=new SimpleIntegerProperty();
    private IntegerProperty boardSize = new SimpleIntegerProperty();
    private StringProperty winningAccording = new SimpleStringProperty();

    public boolean initLoader(String s){
        load = new Loader();
        return load.tryToReadXML(s);
    }

    public boolean wrongXML(Label massages){
        if(load.testVars(massages) && load.ifTwoLatters(massages))
            return true;
        else return false;
    }

    public void init() {
        if (load.getGoodToGo()) { //option 2
            gameBoard.setSize(load.getBoardSize());
            boardSize.setValue(getBoardSize());
            cubeFace = load.getCubeFace();
            gameType = load.getTypeOfGame();
            numOfAttempts = load.getRetry();
            winningAccording = load.getWinnerAccording();
            load.getList(theStash);
            goldenFishMode = load.getGoldenFishMode();
            if(load.getDictionaryName()!=null){
                load.readDic();
            }else {
                System.out.println("---------------------------------------------------");
                System.out.println("Dictionary file name is not correct or does't exist");
                System.out.println("The path of the dictionary: " + load.getDictionaryPath()+"\\dictionary\\"+load.getDictionaryName());
                System.out.println("---------------------------------------------------");
                stopTheGame=true;
            }
            if(load.getProblemWithOpen()){
                System.out.println("---------------------------------------------------");
                System.out.println("Dictionary File name is not correct or does't exist");
                System.out.println("The path of the dictionary: " + load.getDictionaryPath()+"\\dictionary\\"+load.getDictionaryName());
                System.out.println("---------------------------------------------------\n");
                stopTheGame=true;
            }

            if(!stopTheGame){
                players = load.getPlayers();
                goldenFishMode = load.getGoldenFishMode();
                loader_ended = true;
                showGame(players.get(turnOf)); //show the start statistics in option 2
                initiated = true;
            }
        } else stopTheGame = true;

    }

    public void restartNewGame(){
        turnOf=0;
        numOfPlays.setValue(0);
        for (Player p: players){
            p.reset();
        }
        load.getList(theStash);
    }

    public boolean isStartAnotherGame() {return startAnotherGame;}

    public void moveToNextPlayer(){
        boolean stop= false;
        while (!stop) {
            if (turnOf==players.size()-1)
                turnOf=0;
            else
                turnOf++;
            if (!players.get(turnOf).isHasQuit())
                stop=true;
        }
    }

    public TreeMap<String,Integer> getDictionary(){return load.getDictionary();}

    public boolean allPlayersQuit(){
        int numOfQuits=0;
        for (Player player: players) {
            if (player.isHasQuit())
                numOfQuits++;
        }
        if (numOfQuits==players.size()-1)
            return true;
        return false;
    }

    public boolean gameIsOver(Board board){
        if(!initiated)
            return false;
        if(goldenFishMode.getValue()) {
            if (theStash.size() == 0){
                startAnotherGame=true;
                return true;
            }
        }
        if((theStash.size()==0 && !isLegalWordExist(board.getExposedLetters())) || allPlayersQuit()){
            startAnotherGame=true;
            return true;
        }
        return false;

    }

    public boolean isLegalWordExist(String exposedLetters){
        for (Map.Entry<String,Integer> e : getDictionary().entrySet()){
            if (haveSameLetters(e.getKey(),exposedLetters))
                return true;
        }
        return false;
    }

    public boolean haveSameLetters(String word1, String word2) {
        Map<Character, Integer> frequencies1=findFrequencies(word1);
        Map<Character, Integer> frequencies2=findFrequencies(word2);
        for (int i=0;i<word1.length(); i++){
            if(frequencies2.get(word1.charAt(i))==null)
                return false;
            else if (frequencies1.get(word1.charAt(i)) > frequencies2.get(word1.charAt(i)))
                return false;
        }
        return true;
    }

    public int getSigment(String str){ return load.returnSigment(str); }

    public IntegerProperty currentPlayerNumWordsProperty() {return currentPlayerNumWords;}

    public StringProperty currentPlayerIdProperty() {return currentPlayerId;}

    public StringProperty gameTypeProperty() {return gameType;}

    public BooleanProperty goldenFishModeProperty() {return goldenFishMode;}

    public IntegerProperty boardSizeProperty() {return boardSize;}

    public int getTurnOf() {return turnOf;}

    public IntegerProperty numOfPlaysProperty() {return numOfPlays;}

    public void incNumOfPlays(){numOfPlays.setValue(numOfPlays.getValue()+1);}

    public List<Tile> getTheStash() {return theStash;}

    public int getBoardSize() {return load.getBoardSize();}

    public void showGame(Player player) {// option 3.
        System.out.println();
        System.out.println("there are " + theStash.size() + " spare tiles left");
        System.out.println("it's " + player.getName() + "'s turn");
        System.out.println();
    }

    public boolean isLegalWord(String word) {
        return load.contains(word);
    }

    public Map<Character, Integer> findFrequencies(String str) {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (int j = 0; j < str.length(); j++) {
            Integer value = frequencies.get(str.charAt(j));
            if (value == null)
                value = 1;
            else
                value++;
            frequencies.put(str.charAt(j), value);
        }
        return frequencies;
    }

    public int throwCube(Board board){
        int numOfSelections = (int) (Math.random() * cubeFace) + 2;
        if (numOfSelections > board.tilesUnExposed()) {
            numOfSelections = board.tilesUnExposed();
        }
        return numOfSelections;
    }

    public String findPlayerWithMostPoints(){
        String playerName="";
        int maxPoints=0;
        for (Player player: players){
            if (allPlayersQuit()){
                if (!player.isHasQuit())
                    return player.getName();
            }
            else if (player.getPoints() > maxPoints && !player.isHasQuit()){
                maxPoints=player.getPoints();
                playerName=player.getName();
            }
        }
        return playerName;
    }

    public String findPlayerWithMostWords() {
        String playerName="";
        int maxWords = 0;
        for (Player player : players) {
            if (player.getNumOfWords() > maxWords) {
                maxWords = player.getNumOfWords();
                playerName = player.getName();
            }
        }
        return playerName;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<String> getRareDic() {
        return load.getTenLessFric();
    }

    public int getNumOfAttempts(){
        return numOfAttempts;
    }

    public List<String> getPlayersList(){
        List<String> s = new ArrayList<>();
        for(Player p:players){
            s.add(p.getName()+" " + p.getType().getValue() +" ("+p.getId()+")");
        }
        return s;
    }

    public List<String> getPointsList(boolean showType){
        List<String> s = new ArrayList<>();
        for(Player p:players){
            if(!showType)
                s.add(p.getName()+" | ID:"+p.getId()+" | Points:" +p.getPoints());
            else s.add(p.getName()+"| Type:"+p.getType().getValue() +" | ID:"+p.getId());

        }
        return s;
    }

    public StringProperty getWinnigAccording() {
        return winningAccording;
    }

    public List<String> getStashFricList(){
        return load.getStashFricList();
    }

    public int getStashSize(){
        return theStash.size();
    }

}