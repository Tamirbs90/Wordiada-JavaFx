import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
/**
 * Created by Tamir on 12/04/2017.
 */

public class Tile implements Cloneable{
    public static Label massages = new Label();
    private StringProperty letter = new SimpleStringProperty();
    private int points;
    private boolean isExposed=false;
    private Button cell = new Button();
    public static boolean getWord = false;
    public static String word =new String();
    public static int numOfSelect = 0;
    public static int globalNumOfSelect = 0;
    public static int nowSelecting = 0;
    private boolean chosenLetter = false;
    public static boolean select = true;

    public Tile(char letter, int points){
        this.letter.setValue(String.valueOf(letter));
        this.points= points;
        this.isExposed=false;
        cell.setPrefSize(27, 27);
        cell.setAlignment(Pos.CENTER);
        cell.setText(String.valueOf(this.getLatter()));
        cell.setFont(Font.font(11));
        cell.setOnMouseClicked(e->{
            if(select){
                if(numOfSelect<globalNumOfSelect){
                    if(!getWord){
                        if(!isExposed){
                            isExposed=true;
                            System.out.println("is exposed");
                            numOfSelect++;
                        }
                    }
                    else {
                        if(isExposed && !chosenLetter){
                            word = word + this.letter.getValue();
                            chosenLetter=true;
                            System.out.println("success on selecting letter");
                            System.out.println(word);
                        }
                    }

                }else{
                    massages.setText("You can't peak more then " + globalNumOfSelect + " tiles");
                }
            }else{
                massages.setText("Not able to press board right now");
            }

        });
    }

    public void setGlobalNumOfSelect(int globalNumOfSelect) {
        Tile.globalNumOfSelect = globalNumOfSelect;
    }

    public int getNumOfSelect() {
        return numOfSelect;
    }

    public char getLatter(){
        if(isExposed)
            return letter.getValue().charAt(0);
        else return ' ';
    }

    public Node getCell(){return cell;}

    public StringProperty getLetter() {
        return letter;
    }

    public int getPoints() {
        return points;
    }

    public boolean isExposed() {
        return isExposed;
    }

    public void setExposed(boolean exposed) {
        isExposed = exposed;
    }

    public void unExpose() {
        this.setExposed(false);
    }

    public void updateCell() {
        cell.setText(String.valueOf(this.getLatter()));
    }

    public void setGetWord(boolean getWord) {
        this.getWord = getWord;
    }

    public String getTestWord() {
        return word;
    }

    public boolean isChosenLetter() {
        return chosenLetter;
    }

    public void clearWord(){
        word = "";
    }

    public void setSelect(boolean var){
        select = var;
    }

    public void setText(){cell.setText(this.letter.getValue());}

    public void setMassages(Label m){
        massages=m;
    }

    public void setNumOfSelect() {
        numOfSelect=0;
    }

    public void setChosenLetter(){
        chosenLetter = false;
    }

    public void setChosen(){ chosenLetter=true;}

    @Override
    public Tile clone() throws CloneNotSupportedException {
        Tile temp=(Tile)super.clone();
        temp.cell=new Button(this.cell.getText());
        temp.cell.setPrefSize(27, 27);
        temp.cell.setAlignment(Pos.CENTER);
        temp.cell.setFont(Font.font(11));
        if (this.cell.isDisabled())
            temp.cell.setDisable(true);
        return temp;
    }
}
