import java.io.*;
import java.util.*;

public class ReadF {

    public static void main(String args[]) throws IOException {

        HashMap<Integer, String> vocabulary = new HashMap<Integer, String>(); //to hashmap periexei tin leksi, kai tin thesi stin opoia vrisketai sto arxeio vocabulary

        int i = 0;
        int n =0;
        int m = 500;

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("imdb.vocab"));
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            Scanner reader = new Scanner(scanner.nextLine());
            while (reader.hasNext()) {
                i++;
                String data = reader.next();
                if (i > n && i <= m) {
                    vocabulary.put(i, data);
                }
                if(i == m) {
                    break;
                }
            }
        }
        HashMap<Integer,ArrayList<String>> frequency = new HashMap<Integer, ArrayList<String>>();


        ArrayList<String> words;
        int fileCounter = 0;
        BufferedReader reader;
        String line;
        try {
            reader = new BufferedReader(new FileReader("labeledBow.feat"));
            while ((line = reader.readLine()) != null) {
                fileCounter++;
                words = new ArrayList<>();

                String[] values = line.split(" ");
                if (values.length == 0) continue;

                for  ( i = 1; i < values.length; i++ ) {
                    String[] split = values[i].split(":");

                    if (Integer.parseInt(split[0]) > n && Integer.parseInt(split[0]) <= m) 
                        words.add(split[0]);
                }
                frequency.put(fileCounter, words);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        int[][] posVectors = new int[fileCounter/2][m-n];
        int[][] negVectors = new int[fileCounter/2][m-n];
        int[][] Vectors = new int[fileCounter][m-n];
        int[] classes = new int[fileCounter];
        ArrayList<String> temp;

        for (i = 0; i < fileCounter/2; i++) {
            temp = frequency.get(i+1); 
            for (int j = 0; j < m-n; j++) {
                if (vocabulary.containsKey(j+1) && temp.contains(Integer.toString(j+1)) ) {
                    posVectors[i][j] = 1;
                } else {
                    posVectors[i][j] = 0;
                }
            }

        }

        for (i = fileCounter/2; i < fileCounter; i++) {
            temp = frequency.get(i+1); 
            for (int j = 0; j < m-n; j++) {
                if (vocabulary.containsKey(j+1) && temp.contains(Integer.toString(j+1)) ) {
                    negVectors[i-fileCounter/2][j] = 1;
                } else {
                    negVectors[i-fileCounter/2][j] = 0;
                }
            }
        }

        HashMap<Integer, Integer> posWords = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> negWords = new HashMap<Integer, Integer>();

        for (int j = 0; j < m-n; j++) { 
            int posValue = countWords(posVectors, fileCounter, j);
            int negValue = countWords(negVectors, fileCounter, j);
            posWords.put(j,posValue);
            negWords.put(j,negValue);

        }

            for (i = 0; i < fileCounter/2; i++) { 
            classes [i] = 1;
            for (int j = 0; j < m-n; j++) {
                Vectors[i][j] = posVectors[i][j];
            }
        }
        for (i = fileCounter/2; i < fileCounter; i++) { 
            classes[i] = 0;
            for (int j = 0; j < m - n; j++) {
                Vectors[i][j] = negVectors[i-fileCounter/2][j];
            }
        }

        ID3 classifier = new ID3();

        classifier.setHashMap(posWords, negWords);

        classifier.entropies();

        int bestSplit = classifier.maxIG(classifier.informationGain());

        Node root = new Node(bestSplit,Vectors,classes);
        Recursive r  = new Recursive();
        r.addRecursive(classes,vocabulary,Vectors);
        //ftiaxnw polla vectors gia ta trees
        int min = 5;
        int max = 10;
        int random_int = (int)(Math.random() * (max - min + 1) + min);
        for (int k =0;k<random_int;k++){
            System.out.println(k);
            Random randFeatures = new Random(); //instance of random class
            int upperboundFeatures = Vectors[0].length;
            int int_randomFeatures = randFeatures.nextInt(upperboundFeatures);

            HashMap<Integer,String> newVocabulary = new HashMap<>();
            int[][] newVector = new int[Vectors.length][int_randomFeatures];
            int[] newClasses = new int[Vectors.length];
            for (int g=0;g<Vectors.length;g++){//TODO
                 Random rand = new Random(); //instance of random class
                int upperbound = Vectors.length;
                int int_random = rand.nextInt(upperbound);
                newClasses[g]= classes[int_random];
                for (int f=0;f<newVector[0].length;f++){
                    Random randFeaturesIn = new Random(); //instance of random class
                    int upperboundFeaturesIn = Vectors[0].length;
                    int int_randomFeaturesIn = randFeatures.nextInt(upperboundFeaturesIn);
                    newVocabulary.put(int_randomFeaturesIn,vocabulary.get(randFeaturesIn));
                    newVector[g][f] =Vectors[int_random][f];
                }
            }
            ID3 classifier1 = new ID3();


            int bestSplit1 = classifier1.largest(classifier1.calculateIG(newVector,newClasses));

            Node root1 = new Node(bestSplit1,Vectors,classes);
            Recursive r1  = new Recursive();
            r.addRecursive(newClasses,newVocabulary,newVector);
        }
    }

    public static int countWords(int[][] words, int fileCounter, int j) { //na eleksw an einai swsta
        int sum = 0;
        for (int i = 0; i <fileCounter/2; i++) {
            if (words[i][j] == 1) {
                sum++;
            }
        }
        return sum;
    }
}