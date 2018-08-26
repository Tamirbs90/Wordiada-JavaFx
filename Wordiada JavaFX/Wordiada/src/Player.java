import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;
import java.util.List;


/**
 * Created by Tamir on 12/04/2017.
 */
public class Player {

    private StringProperty name= new SimpleStringProperty();
    private IntegerProperty id=new SimpleIntegerProperty();
    private StringProperty type=new SimpleStringProperty();
    private IntegerProperty points=new SimpleIntegerProperty(0);
    private IntegerProperty numOfWords= new SimpleIntegerProperty(0);
    private TreeMap<String,Integer> wordsComposed;
    private int currNumOfAttempts;
    private boolean hasQuit=false;


    public Player(String name, int id, String type) {
        this.name.setValue(name);
        this.id.setValue(id);
        this.type.setValue(type);
        wordsComposed = new TreeMap<>();
    }

    public void setHasQuit(boolean hasQuit) {this.hasQuit = hasQuit;}

    public boolean isHasQuit() {return hasQuit;}

    public IntegerProperty numOfWordsProperty() {return numOfWords;}

    public IntegerProperty idProperty() {return id;}

    public IntegerProperty pointsProperty() {return points;}

    public StringProperty nameProperty() {return name;}

    public StringProperty typeProperty() {return type;}

    public void reset(){
        points.setValue(0);
        numOfWords.setValue(0);
        hasQuit=false;
        wordsComposed.clear();
    }

    public int getNumOfWords() {return numOfWords.getValue();}

    public void incNumOfWords(){numOfWords.setValue(numOfWords.getValue()+1);}

    public String getName() {
        return name.getValue();
    }

    public int getId() { return id.getValue();}

    public void addWord(StringProperty word) {
        if(wordsComposed.get(word.getValue()) == null)
            wordsComposed.put(word.getValue(),1);
        else wordsComposed.put(word.getValue(),wordsComposed.get(word.getValue())+1);
    }


    public List<String> wordComposedList(){
        List<String> s = new ArrayList<>();
        for(String e : wordsComposed.keySet())
            if(wordsComposed.get(e)>1)
                s.add(e+" " + wordsComposed.get(e));
            else s.add(e);
        return s;
    }

    public void addPoints(int toAdd){points.setValue(points.getValue()+toAdd);}

    public StringProperty getType() {
        return type;
    }

    public void setType(String type) {this.type.setValue(type);}

    public int getPoints() {
        return points.getValue();
    }

    public void setNumOfAttempts(int numOfAttempts) {
        this.currNumOfAttempts = numOfAttempts;
    }

    public int getNumOfAttempts() {
        return currNumOfAttempts;
    }
    public void dicNumOfAttempts() {
        currNumOfAttempts--;
    }


}