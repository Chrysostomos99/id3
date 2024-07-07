import javax.swing.text.DefaultEditorKit;
import java.util.HashMap;

public class Node {
    Node Lleaf;
    Node Rleaf;
    int bestSplit;
    int[][] exists;
    int [] classes;
    Node(   int bestSplit,int[][] exists,int[] classes){
        Rleaf = null;
        Lleaf = null;
        this.classes = classes;
        this.exists = exists;
        this.bestSplit = bestSplit;
    }


    public int getBestSplit(){
        return bestSplit;
    }
    public int[] getClasses(){
        return classes;
    }
    public  int[][] getExists(){return exists;}

    public void setBestSplit(int bestSplit){
        this.bestSplit = bestSplit;
    }

}

