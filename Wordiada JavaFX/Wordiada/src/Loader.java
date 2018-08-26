import javafx.beans.property.*;
import javafx.scene.control.Label;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

/**
 * Created by Kobi-PC on 21-Apr-17.
 */

public class Loader {
    private Document document = null;
    private StringProperty typeOfGame = new SimpleStringProperty();
    private StringProperty winnerAccording = new SimpleStringProperty();
    private String dictionaryFileName = null;
    private BooleanProperty goldenFishMode = new SimpleBooleanProperty();
    private int boardSize = 0;
    private int sizeOfStack = -1;
    private int retry = -1;
    private int cubeFace = 0;
    private int dictionarySize = 0;
    private boolean dictionaryHasLoaded = false;
    private List<Player> players = new ArrayList<Player>();
    private boolean succsess=true;
    private boolean goodToGo=true;
    private boolean problemOpeningFile=false;
    private String dictionaryPath;
    private class LathersFdic {
        String latter;
        int score;
        double frequency;
        int numberOfAppirience=0;

        public LathersFdic(String latter, int score, double frequency) {
            this.latter= latter;
            this.score=score;
            this.frequency = frequency;
        }
    }
    private class wordFric{
        String s;
        int value;

        public wordFric(String s,int value){
            this.s = new String(s);
            this.value = value;
        }
    }
    private List<wordFric> wordFricList = new ArrayList<wordFric>();
    private TreeMap<String,Integer> dictionary = new TreeMap<>();
    private TreeMap<String,Integer> sigment1 = new TreeMap<>();
    private TreeMap<String,Integer> sigment2 = new TreeMap<>();
    private TreeMap<String,Integer> sigment3 = new TreeMap<>();

    private Vector<LathersFdic> fricList = new Vector<>();

    public TreeMap<String, Integer> getDictionary() {return dictionary;}

    public void createSigment(){
        int sigmentSize = dictionarySize/3;
        int summer1=0,summer2=0,summer3=0;
        int j=0;
        Set<String> keySet = dictionary.keySet();
        for (String key:keySet) {
            int value = dictionary.get(key);
            wordFricList.add(new wordFric(key,value));
        }
        wordFricList.sort(new Comparator<wordFric>() {
            @Override
            public int compare(wordFric e1, wordFric e2) {
                int res = e1.value - e2.value;
                return res;
            }
        });
        for (wordFric w : wordFricList){
            if(summer3<=sigmentSize){
                sigment3.put(w.s,w.value);
                summer3++;
            }
            else if(summer2<=sigmentSize){
                sigment2.put(w.s,w.value);
                summer2++;
            }
            else {
                sigment1.put(w.s,w.value);
                summer1++;
            }
        }
    }

    public List<String> getTenLessFric(){
        List<String> strings = new ArrayList<>();
        if(wordFricList.size()>=10)
            for(int i = 0; i<10; i++)
                strings.add(wordFricList.get(i).s);
        else{
            for(wordFric e : wordFricList)
                strings.add(e.s);
        }
        return strings;
    }

    public int returnSigment(String s){
        if(sigment1.containsKey(s))
            return 1;
        else if (sigment2.containsKey(s))
            return 2;
        else if (sigment3.containsKey(s))
            return 3;
        else return 0;
    }

    public String getDictionaryPath(){
        return dictionaryPath;
    }

    public BooleanProperty getGoldenFishMode() {
        return goldenFishMode;
    }

    public void readDic() {
        File file;
        try{
            file = new File(dictionaryPath +"\\dictionary\\" + dictionaryFileName);
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            System.out.println("----------------------");
            System.out.println("Loading the dictionary");
            System.out.println("----------------------");
            while ((text = reader.readLine()) != null) {
                String[] words = text.split("[— ]");
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].toUpperCase();
                    words[i] = words[i].replace("!", "");
                    words[i] = words[i].replace(",", "");
                    words[i] = words[i].replace(".", "");
                    words[i] = words[i].replace("-", "");
                    words[i] = words[i].replace("_", "");
                    words[i] = words[i].replace("=", "");
                    words[i] = words[i].replace("\'", "");
                    words[i] = words[i].replace("\"", "");
                    words[i] = words[i].replace("(", "");
                    words[i] = words[i].replace(")", "");
                    words[i] = words[i].replace("{", "");
                    words[i] = words[i].replace("}", "");
                    words[i] = words[i].replace("[", "");
                    words[i] = words[i].replace("]", "");
                    words[i] = words[i].replace("%", "");
                    words[i] = words[i].replace("&", "");
                    words[i] = words[i].replace("#", "");
                    words[i] = words[i].replace("@", "");
                    words[i] = words[i].replace("^", "");
                    words[i] = words[i].replace("*", "");
                    words[i] = words[i].replace(";", "");
                    words[i] = words[i].replace(":", "");
                    words[i] = words[i] = words[i].replace("\\'", "");
                    words[i] = words[i].replace("\"", "");
                    words[i] = words[i].replace("?", "");
                }
                for (int i = 0; i < words.length; i++) {
                    if (words[i].length() > 1) {
                        if (dictionary.get(words[i]) == null) {
                            dictionary.put(words[i], 1);
                        } else {
                            dictionary.put(words[i], dictionary.get(words[i]) + 1);
                        }
                    }
                }
            }
            if (reader != null) {
                reader.close();
                System.out.println("---------------------------");
                System.out.println("Dictionary Finished Loading");
                System.out.println("---------------------------");
            }
        } catch (FileNotFoundException e) {
            problemOpeningFile=true;
        } catch (IOException e) {
            System.out.println("Warning - seems that the dictionary file is corrupted");
            System.out.println("dictionary path: " + dictionaryPath + "\\dictionary");
        }
        dictionarySize = dictionary.size();
        dictionaryHasLoaded =true;
        createSigment();
    }

    public List<Player> getPlayers(){
        return players;
    }

    public boolean getGoodToGo() {
        return goodToGo;
    }

    public Object getDictionaryName() {
        return dictionaryFileName;
    }

    public boolean getProblemWithOpen() {
        return problemOpeningFile;
    }


    public boolean testVars(Label massages) {
        if(typeOfGame==null)
            return false;
        if(document==null)
            return false;
        if(winnerAccording==null)
            return false;
        if(dictionaryFileName==null)
            return false;
        if(boardSize>55 || boardSize<5){
            massages.setText("ERROR: The board size is not valid");
            return false;

        }
        if(sizeOfStack<boardSize*boardSize) {
            massages.setText("ERROR: the size of the deck is not valid");
            return false;
        }
        if(cubeFace<2){
            massages.setText("ERROR: The cube has to little faces");
            return false;
        }
        if(retry<0){
            massages.setText("ERROR: The number of retries is less then 0");
            return false;
        }
        if(players.size()<2){
            massages.setText("ERROR: The number of players is less then 2");
            return false;
        }
        if(players.size()>6){
            massages.setText("ERROR: The number of players is more then 6");
            return false;
        }

        for(int i=0;i<players.size();i++){
            for(int j=0;j<players.size();j++){
                if(i!=j)
                    if(players.get(i).getId() == players.get(j).getId()){
                        massages.setText("ERROR: two players with same id");
                        return false;
                    }

            }
        }

        return true;
    }

    public boolean ifTwoLatters(Label massages){
        for(int i=0; i<fricList.size(); i++){
            for(int j=0; j<fricList.size();j++)
                if((fricList.get(i).latter.equals(fricList.get(j).latter)) && i!=j) {
                    massages.setText("ERROR: There are two same letters in deck");
                    return false;
                }

        }
        return true;
    }

    public void getList(List<Tile> list){ // makes the stack
        int globalFrequency=0;
        double theRetio;
        for(int i=0 ; i < fricList.size() ; i++){
            globalFrequency += fricList.get(i).frequency;
        }
        theRetio = ((double)sizeOfStack)/((double)globalFrequency);
        for(int i=0 ; i < fricList.size() ;i++){
            fricList.get(i).numberOfAppirience= (int) (fricList.get(i).frequency*theRetio + 0.5);
        }
        for(int i=0 ; i < fricList.size() ; i++){
            for(int j=0; j < fricList.get(i).numberOfAppirience ; j++)
                list.add(new Tile(fricList.get(i).latter.charAt(0),fricList.get(i).score));
    }
        long seed = System.nanoTime();
        Collections.shuffle(list, new Random(seed));
    }

    public List<String> getStashFricList(){
        List<String> s = new ArrayList<>();
        for(LathersFdic e : fricList){
            s.add(e.latter + " ("+e.frequency+")");
        }
        return s;
    }

    public boolean tryToReadXML(String path) {
        if (path.substring(path.length() - 4).contains(".xml")) {
            fricList.clear();
            for (int i = path.length() - 1; i > 0; i--)
                if (path.charAt(i) == '\\') {
                    dictionaryPath = path.substring(0, i);
                    i = 0;
                }
            readXML(path);
            return true;
        }
        else {
            return false;
        }
    }

    public void readXML(String s){
        File file = new File(s);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document doc =  documentBuilder.parse(file);
            document = doc;
        } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException e) {
            System.out.println("--------------------------------");
            System.out.println("The XML File Could Not Be Loaded");;
            System.out.println("--------------------------------\n");

            succsess=false;
        }
        NodeList rootNodes = document.getElementsByTagName("GameDescriptor");
        Node rootNode = rootNodes.item(0);
        Element rootElement = (Element) rootNode;

        NodeList gameTypes = rootElement.getElementsByTagName("GameType");
        Node gameType = gameTypes.item(0);
        Element gameTypeElem = (Element) gameType;
        typeOfGame.setValue(gameTypeElem.getTextContent());
        if(typeOfGame==null){
            System.out.println("---------------------");
            System.out.println("Game type not entered");
            System.out.println("---------------------\n");
        }
        winnerAccording.setValue(gameTypeElem.getAttribute("winner-according-to"));
        if(gameTypeElem.getAttribute("gold-fish-mode").equals("true"))
            goldenFishMode.setValue(true);
        else goldenFishMode.setValue(false);
        if(winnerAccording==null){
            System.out.println("----------------------------");
            System.out.println("Winner according not entered");
            System.out.println("----------------------------\n");
        }


        NodeList structureList = rootElement.getElementsByTagName("Structure");
        Node structure1 = structureList.item(0);
        Element structElem = (Element) structure1;
        NodeList dictionaryFiles = structElem.getElementsByTagName("DictionaryFileName");
        Node dictionaryFile = dictionaryFiles.item(0);
        Element fileNameElem = (Element) dictionaryFile;
        dictionaryFileName = fileNameElem.getTextContent();
        if(dictionaryFileName==null){
            System.out.println("--------------------------------");
            System.out.println("Dictionary file name not entered");
            System.out.println("--------------------------------\n");

        }


        NodeList playersList = rootElement.getElementsByTagName("Players"); //for EX2 loads players from xml
        Node playerNode = playersList.item(0);
        Element palyerAte = (Element) playerNode;

        NodeList playersFrom = palyerAte.getElementsByTagName("Player");
        for (int i=0;i<playersFrom.getLength();i++){
            Node playerFrom = playersFrom.item(i);
            Element playerAtr = (Element) playerFrom;
            String id = playerAtr.getAttribute("id");

            NodeList NameList = playerAtr.getElementsByTagName("Name");
            Node Name = NameList.item(0);
            Element NameAtr = (Element) Name;
            String name = NameAtr.getTextContent();

            NodeList typeList = playerAtr.getElementsByTagName("Type");
            Node Type = typeList.item(0);
            Element typeAtr = (Element) Type;
            String type = typeAtr.getTextContent();

            players.add(new Player(name,Integer.parseInt(id),type));
        }

        NodeList Latters = structElem.getElementsByTagName("Letters");
        Node Latter = Latters.item(0);
        Element LatterAtr = (Element) Latter;
        sizeOfStack = Integer.parseInt(LatterAtr.getAttribute("target-deck-size"));
        if(sizeOfStack == -1){
            System.out.println("----------------------------");
            System.out.println("Size of the deck not entered");
            System.out.println("----------------------------\n");

        }

        NodeList LattersForDeck = structElem.getElementsByTagName("Letter");

        for(int i=0; i < LattersForDeck.getLength() ;i++){
            Node LatterForDeck = LattersForDeck.item(i);
            Element LatterElem = (Element) LatterForDeck;
            NodeList signs = LatterElem.getElementsByTagName("Sign");
            Node sign = signs.item(0);
            Element signElem = (Element) sign;
            String name = signElem.getTextContent();
            NodeList scores = LatterElem.getElementsByTagName("Score");
            Node score = scores.item(0);
            Element scoreElem = (Element) score;
            int scoreToAdd = Integer.parseInt(scoreElem.getTextContent());
            NodeList frequencys = LatterElem.getElementsByTagName("Frequency");
            Node frequency = frequencys.item(0);
            Element frequencyElem = (Element) frequency;
            double frequencyToAdd = Double.parseDouble(frequencyElem.getTextContent());
            LathersFdic f = new LathersFdic(name,scoreToAdd,frequencyToAdd);
            fricList.add(f);
        }


        NodeList boardSizes = structElem.getElementsByTagName("BoardSize");
        Node boardSizeXm = boardSizes.item(0);
        Element boardSizeElem = (Element) boardSizeXm;
        boardSize = Integer.parseInt(boardSizeElem.getTextContent());
        if(boardSize==-1){
            System.out.println("----------------------");
            System.out.println("Board size not entered");
            System.out.println("----------------------\n");
        }

        NodeList retries = structElem.getElementsByTagName("RetriesNumber");
        Node retryXm = retries.item(0);
        Element retryElem = (Element) retryXm;
        retry = Integer.parseInt(retryElem.getTextContent());
        if(retry==-1){
            System.out.println("-----------------------------");
            System.out.println("Number of retries not entered");
            System.out.println("-----------------------------\n");
        }

        NodeList cubeFacets = structElem.getElementsByTagName("CubeFacets");
        Node cubeFaceXm = cubeFacets.item(0);
        Element cubeElem = (Element) cubeFaceXm;
        cubeFace = Integer.parseInt(cubeElem.getTextContent());
        if(cubeFace==-1){
            System.out.println("-----------------------------");
            System.out.println("Number of cubeFace not entered");
            System.out.println("-----------------------------\n");

        }
        succsess=true;
    }

    public boolean contains(String s){
        return dictionary.containsKey(s);
    }

    public StringProperty getTypeOfGame() {
        return typeOfGame;
    }

    public StringProperty getWinnerAccording() {
        return winnerAccording;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getRetry() {
        return retry;
    }

    public int getCubeFace() {
        return cubeFace;
    }

}


