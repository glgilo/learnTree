


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.util.StringTokenizer;


public class learntree {

    public static void main(String[] args) throws IOException, BadArgumentException {

        int type = Integer.parseInt(args[0]);
        int P = Integer.parseInt(args[1]);
        int L = Integer.parseInt(args[2]);


        String fileNameImg = args[3];

        ArrayList<int[]> trainSamples2 = new ArrayList<int[]>();
        int label2 =0;
        int index2 = 0;
        String line;
        try (BufferedReader inImage = new BufferedReader(new FileReader(fileNameImg))) {
            while ((line = inImage.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line,",");
                    int[] temp = new int[786];
                    label2 = Integer.parseInt((String)st.nextElement());
                    temp[785] = label2;
                    temp[784] = index2;
                    for(int j  = 0 ; j< 784; j++){
                        temp[j] = Integer.parseInt((String)st.nextElement());
                    }

                    trainSamples2.add(temp);

                index2++;

            }
        } catch (IOException e) {
            System.out.println("Error raised while reading images from " + fileNameImg);
        }








//----------------train initialize-------------------------
        int[] trainLabels = new int[10];
//--------------------------------------------------------


        int allImgs = index2;


        int validImg = (allImgs * P) / 100;
        int[][] validImgs = new int[validImg][786];
        int[][] validLabels = new int[validImg][1];
        int[] validLabelsArray = new int[10];

        int[][] trainSamples = new int[allImgs - validImg][786];

        int coundValid = 0;
        int countTrain = 0;

        int index = 0 ;

        int[] imgIndexes = new int[allImgs-validImg];

        double rand = (double)P/100 + 0.05;


        for(int[] img: trainSamples2){
            if (coundValid < validImg && Math.random() < rand){
                validImgs[coundValid] = img;
                validLabels[coundValid][0] = img[785];
                validLabelsArray[img[785]]++;
                coundValid++;
            }
            else{
                trainSamples[countTrain] = img;
                trainLabels[img[785]]++;
                imgIndexes[countTrain] = img[784];

                countTrain++;
            }
        }



//        String line;
//        try (BufferedReader inImage = new BufferedReader(new FileReader(fileNameImg))) {
//            while ((line = inImage.readLine()) != null) {
//                StringTokenizer st = new StringTokenizer(line,",");
//                if( coundValid < validImg && Math.random() < rand ){
//
//                    label = Integer.parseInt((String)st.nextElement());
//                    validLabels[coundValid][0] = label;
//                    if( label >=0 && label <=9) {
//                        validLabelsArray[label]++;
//                    }
//                    else
//                        wrongLabel++;
//                    for(int j  = 0 ; j< 784; j++){
//                        validImgs[coundValid][j] = Integer.parseInt((String)st.nextElement());
//                    }
//                    validImgs[coundValid][784] = index;
//                    coundValid++;
//                }
//                else{
//                    label = Integer.parseInt((String)st.nextElement());
//                    trainSamples[countTrain][785] = label;
//                    trainSamples[countTrain][784] = index;
//
//                    imgIndexes[countTrain] = index;
//
//                    if( label >=0 && label <=9) {
//                        trainLabels[label]++;
//                    }
//                    else
//                        wrongLabel++;
//                    for(int j  = 0 ; j< 784; j++){
//                        trainSamples[countTrain][j] = Integer.parseInt((String)st.nextElement());
//                    }
//
//                    countTrain++;
//                }
//
//                index++;
//
//            }
//        } catch (IOException e) {
//            System.out.println("Error raised while reading images from " + fileNameImg);
//        }




        if(type == 2) {
            Conditions c = new Conditions(2, allImgs);
            c.case2(trainSamples);
            c.case2(validImgs);
        }
        else if(type == 1){
            Conditions c = new Conditions(1, allImgs);
            c.case1(trainSamples);
            c.case1(validImgs);
        }
        else{
            throw new BadArgumentException("Wrong version of Algorithm");
        }

        Node root = new Node(trainLabels, imgIndexes, 0,null);

        int maxTreeIndex = -1;
        double maxPrecent = -1;

        BinaryTree Tree = new BinaryTree(root, 0, type);

        for (int i = 1; i < L+2; i++) {
            while (Tree.bestSplit()) {}
            double tempPrecent = checkTreeQuality(Tree, validImgs, validLabels);
            System.out.println("Precent in the loop: " + tempPrecent + " index of tree: " + (i-1));
            if(maxPrecent < tempPrecent){
                maxPrecent = tempPrecent;
                maxTreeIndex = i-1;
            }
            Tree.setMaxHight(i);

        }





//-------------------build the best tree------



        int[][] checkAllSamples = new int[allImgs][786];

        for(int i = 0; i < allImgs ; i++){
            if(i < trainSamples.length ){
                checkAllSamples[i] = trainSamples[i];
            }
            else{
                validImgs[i - trainSamples.length][785] = validLabels[i - trainSamples.length][0];
                checkAllSamples[i] = validImgs[i - trainSamples.length];

            }
        }


        int[] allSamples = new int[allImgs];


        for(int i = 0; i < allImgs ; i++) {
            allSamples[i] = i;
        }

            int[] allLabel = new int[10];
        for (int i = 0; i < 10; i++) {
            allLabel[i] = validLabelsArray[i] + trainLabels[i];
        }

        root = new Node(allLabel, allSamples, 0,null);
        BinaryTree bestTree = new BinaryTree(root, maxTreeIndex, type);
        while (bestTree.bestSplit()){}
        double precent  = checkAll(bestTree, checkAllSamples);
        System.out.println("num: " + allImgs);
        System.out.println("error: " + (int)(100-precent));
        System.out.println("size: " + (bestTree.leafs.size()-1));

//--------------write output
        FileOutputStream f = new FileOutputStream(new File(args[4]));
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(bestTree);


    }

    public static double checkTreeQuality(BinaryTree Tree,int[][] validImgs, int[][] validLabels ){
        int bullEye = 0;
        int ourLabel= 0;
        for(int i = 0; i < validImgs.length; i++) {
            ourLabel = Tree.navigate(validImgs[i]);
            if(ourLabel == validLabels[i][0])
                bullEye++;
        }
        return (bullEye*100.0)/validImgs.length;
    }

    public static double checkAll(BinaryTree Tree, int[][] allSamples ){
        int bullEye = 0;
        int ourLabel;
        int size = allSamples.length;
        for (int[] allSample : allSamples) {
            ourLabel = Tree.navigate(allSample);
            if (ourLabel == allSample[785])
                bullEye++;
        }
        return (bullEye*100.0)/size;
    }


}
