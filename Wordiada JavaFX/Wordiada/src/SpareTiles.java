import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;

/**
 * Created by Tamir on 12/04/2017.
 */
public class SpareTiles {

    private List<Tile> tiles;
    private int howManyLeft;
    private Map<StringProperty, Integer> frequencies;

    public SpareTiles(){
        tiles=new ArrayList<>();
        frequencies=new HashMap<StringProperty, Integer>();
        for (Tile tile: tiles){
            Integer value=frequencies.get(tile.getLetter());
            if (value==null)
                value=1;
            else
                value++;
            frequencies.put(tile.getLetter(),value);
        }
    }

    public void buildSpareTiles(List<Tile> lst){
        for (Tile tile : lst){
            tiles.add(tile);
        }
        howManyLeft=tiles.size();
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public int getHowManyLeft() {
        howManyLeft = tiles.size();
        return howManyLeft;
    }

    public void setHowManyLeft(int toReduce) {
        howManyLeft-=toReduce;
    }


    public void getFrequencies(VBox box){
        for (Map.Entry<StringProperty, Integer> e: frequencies.entrySet()){
            Label label= new Label(e.getKey()+" - "+e.getValue()+"/"+howManyLeft);
            box.getChildren().add(label);
        }

    }

    public void clearTiles(){
        tiles.clear();
    }
}
