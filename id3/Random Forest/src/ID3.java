import javax.swing.tree.TreeNode;
import java.net.Inet4Address;
import java.util.HashMap;

public class ID3 {

    HashMap<Integer, Integer> posWords;
    HashMap<Integer, Integer> negWords;
    HashMap<Integer, Double> IG;

    public void setHashMap(HashMap<Integer, Integer> posWords, HashMap<Integer, Integer> negWords) {
        this.posWords = posWords;
        this.negWords = negWords;
    }

    //calculate entropy
    public HashMap<Integer, Double> entropies() {
        HashMap<Integer, Double> wordsEntropy = new HashMap<Integer, Double>();
        double entropy = 0;
        int posValue;
        int negValue;

        for (HashMap.Entry<Integer, Integer> entry : posWords.entrySet()) { 
            int key = entry.getKey(); 
            posValue= entry.getValue(); 
            negValue = negWords.get(key);
            int total = posValue+negValue;

            if (negValue > 0 && posValue > 0) { 
                entropy = -(Double.valueOf(posValue)/total)*log2(Double.valueOf(posValue)/total) - (Double.valueOf(negValue)/total)*log2(Double.valueOf(negValue)/total);
            } else  { 
                entropy = 0;
            }
            wordsEntropy.put(key, entropy);
        }
        return wordsEntropy;
    }

    public HashMap<Integer, Double> informationGain() { 

        HashMap<Integer, Double> IG = new HashMap<Integer, Double>();
        int posSize = posWords.size();
        int negSize = negWords.size();
        int totalSize = posSize + negSize;
        int key;
        double entrpOfWord, igOfWord;

        double totalEntropy = -(Double.valueOf(posSize)/totalSize)*log2(Double.valueOf(posSize)/totalSize) - (Double.valueOf(negSize)/totalSize)*log2(Double.valueOf(negSize)/totalSize);

        for (HashMap.Entry<Integer, Double> entry : entropies().entrySet()) {
            key = entry.getKey();
            entrpOfWord = entry.getValue();
            igOfWord = totalEntropy - entrpOfWord;

            IG.put(key, igOfWord);
        }

        return IG;

    }

    public int maxIG(HashMap<Integer, Double> IG) {
        int maxKey = Integer.MIN_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (HashMap.Entry<Integer, Double> entry : IG.entrySet()) {
            if ( entry.getValue() > maxValue) {
                maxKey = entry.getKey();
                maxValue = entry.getValue();
            }
        }

        return maxKey;
    }

    public double infoGain(double size, double classcount0, double classcount1, double countAll, double count0, double count1){
        double hx = -(((double)classcount0/size)*log2(classcount0/size))-(((double)classcount1/size)*log2((classcount1/size)));

        double hxw0=  -(((double)count0/countAll)*(((-(double)count0/countAll))*log2((count0/countAll))));
        double hxw1=  -(((double)count1/countAll)*((-(double)count1/countAll)*log2((count1/countAll))));
        double ig = hx+hxw0+hxw1;

        return ig;
    }


    public double[] calculateIG(int [][]reviews, int[]classes){
        int countAll;
        int classcount0 = 0;
        int classcount1 = 0;
        double value = 0;
        double[] vocig = new double[reviews[0].length];
        int b=0;

        for (int i = 0;i< reviews.length;i++) {
            if(classes[i]==0){
                classcount0++; //pros8hkh sto sunolo  classh 0
            }
            if(classes[i]==1){
                classcount1++;//pros8hkh sto sunolo classh 1
            }
        }
        for( int j =0;j<reviews[0].length;j++) {
            countAll=0;
            int count1 = 0;//calss 1
            int count0 = 0;//class 0
            for (int i = 0;i< reviews.length;i++) {
                if ( reviews[i][j]==1) {
                    countAll++;

                    if(classes[i]==0){
                        count0++; //pros8hkh sto sunolo le3i kai classh 0
                    }

                    if(classes[i]==1){
                        count1++;//pros8hkh sto sunolo le3i kai classh 1
                    }
                }
            }
            value=infoGain(reviews.length,classcount0,classcount1,countAll,count0,count1);

            vocig[j]=value;
        }
        return vocig;
    }

    int largest(double[] vocig)
    {
        int i;
        int r=0;
        // Initialize maximum element
        double max = vocig[0];

        // Traverse array elements from second and
        // compare every element with current max
        for (i = 1; i < vocig.length; i++) {
            if (vocig[i] > max) {
                max = vocig[i];
                r = i;
            }
        }
        return r;
    }


    public String pureClass(int key) {
        if (negWords.get(key) == 0 && posWords.get(key) > 0) { 
            return "positive";
        } else if (negWords.get(key) > 0 && posWords.get(key) == 0) {
            return "negative";
        } else
            return "continue";
    }

    private double log2(double value) {
        return (Math.log(value)/Math.log(2.0));
    }


}
