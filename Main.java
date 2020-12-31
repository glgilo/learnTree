


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileOutputStream;


public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("Please enter parameters");
        Scanner scanner = new Scanner(System.in);
        int P = scanner.nextInt();
        int L = scanner.nextInt();


        String fileNameImg = "C:/Users/gilie/IdeaProjects/learntree/sampels/train-images.idx3-ubyte";
        String fileNameLabel = "C:/Users/gilie/IdeaProjects/learntree/sampels/train-labels.idx1-ubyte";

        DataInputStream inImage = new DataInputStream(new BufferedInputStream(new FileInputStream(fileNameImg)));
        DataInputStream inLabel = new DataInputStream(new BufferedInputStream(new FileInputStream(fileNameLabel)));



//        FileInputStream  inImage = new FileInputStream(fileNameImg);
//        FileInputStream inLabel = new FileInputStream(fileNameLabel);

//------------extract image properties--------------------
        int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());

        int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
        int numberOfRows  = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
        int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
        System.out.println("numImages: " +numberOfImages);
        System.out.println("numberOfRows: " +numberOfRows);
        System.out.println("numberOfColumns: " +numberOfColumns);
//--------------------------------------------------------

//------------extract label properties--------------------
        int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());

        int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
        System.out.println("numberOfLabels: " +numberOfLabels);
//---------------------------------------------------------

        int numberOfPixels = numberOfRows * numberOfColumns;

//----------------train initialize-------------------------
        ArrayList<int[]> trainSamples = new ArrayList<int[]>();
        int[] trainLabels = new int[10];
//--------------------------------------------------------




        int label = 0;
        int wrongLabel = 0;
        int allImgs = 60000;


        int validImg = (allImgs*P)/100;
        int[][] validImgs = new int[validImg][numberOfPixels];
        int[][] validLabels = new int[validImg][1];
        int[] validLabelsArray = new int[10];


        if(numberOfImages == numberOfLabels) {

            int coundValid = 0;
            for(int i = 0; i < allImgs ; i++){
                if(i%1000 == 0){
                    System.out.println("number of image that was processed: " + i);
                }


                if( coundValid < validImg && Math.random() < 0.15 ){

                    label = inLabel.readUnsignedByte();
                    validLabels[coundValid][0] = label;
                    if( label >=0 && label <=9) {
                        validLabelsArray[label]++;
                    }
                    else
                        wrongLabel++;
                    for(int j  = 0 ; j< numberOfPixels; j++){
                        validImgs[coundValid][j] = inImage.readUnsignedByte();
                    }
                    coundValid++;
                }
                else{
                    int[] imgPixels = new int[numberOfPixels+1];
                    label = inLabel.readUnsignedByte();
                    imgPixels[0] = label;
                    if( label >=0 && label <=9) {
                        trainLabels[label]++;
                    }
                    else
                        wrongLabel++;
                    for(int j  = 1 ; j< numberOfPixels+1; j++){
                        imgPixels[j] = inImage.readUnsignedByte();
                    }
                    trainSamples.add(imgPixels);
                }

            }





//            for (int i = 0; i < allImgs - validImg; i++) {
//                int[] imgPixels = new int[numberOfPixels+1];
//                if(i%1000 == 0){
//                    System.out.println("number of image that was processed: " + i);
//                }
//                label = inLabel.read();
//                imgPixels[0] = label;
//                if( label >=0 && label <=9) {
//                    trainLabels[label]++;
//                }
//                else
//                    wrongLabel++;
//                for(int j  = 1 ; j< numberOfPixels; j++){
//                    imgPixels[j] = inImage.read();
//                }
//                trainSamples.add(imgPixels);
//            }
//
//            for(int i = 0; i< validImg ; i++){
//                if(i%1000 == 0){
//                    System.out.println("number of image that was processed: " + i);
//                }
//                label = inLabel.read();
//                validLabels[i][0] = label;
//                if( label >=0 && label <=9) {
//                    validLabelsArray[label]++;
//                }
//                else
//                    wrongLabel++;
//                for(int j  = 0 ; j< numberOfPixels; j++){
//                    validImgs[i][j] = inImage.read();
//                }
//            }
            System.out.println("number of valid: " + coundValid);
        }




        else{
            System.out.println("Number of images not equal to labels");
        }


        System.out.println("wrong labels: " + wrongLabel);
        for(int i = 0; i<10; i++){
            System.out.println(trainLabels[i]);
        }

        BinaryTree[] trees = new BinaryTree[L+1];
        Node root;
        for (int i = 0; i <  trees.length; i++) {
            root = new Node(trainLabels, trainSamples, 0);
            BinaryTree Tree = new BinaryTree(root, i);
            while (Tree.bestSplit()){}
            trees[i] = Tree;
        }
//-----------------------------check
        System.out.println("left max label: " + trees[0].root.left.maxLabel);
        System.out.println("num of left maxlabel left(right) " +trees[0].root.left.labels [trees[0].root.left.maxLabel] );
        System.out.println("num of left maxlabel right(wrong) " +trees[0].root.left.labels [trees[0].root.right.maxLabel] );
        System.out.println("right max label: " + trees[0].root.right.maxLabel);
        System.out.println("num of right maxlabel right(right) " +trees[0].root.right.labels [trees[0].root.right.maxLabel] );
        System.out.println("num of right maxlabel left(wrong) " +trees[0].root.right.labels [trees[0].root.left.maxLabel] );
        for(int i = 0; i<10; i++){
            System.out.println(validLabelsArray[i]);
        }

//----------------------------------------------
        double maxPrecent = -1;
        int maxTreeIndex = -1;
        for(int i = 0; i < trees.length; i++){
            double tempPrecent = checkTreeQuality(trees[i], validImgs, validLabels);
            System.out.println("Precent in the loop: " + tempPrecent + " index of tree: " + i);
            if(maxPrecent < tempPrecent){
                maxPrecent = tempPrecent;
                maxTreeIndex = i;
            }
        }
        System.out.println("Best L for the tree: " + trees[maxTreeIndex].getMaxHight());
        System.out.println("Best L index: " + maxTreeIndex);
        System.out.println("Sucusses of the tree: " + maxPrecent);

//-------------------build the best tree------


        ArrayList<int[]> allSamples = new ArrayList<int[]>();
        for(int i = 0; i < allImgs ; i++){
            if(i < trainSamples.size() ){
                allSamples.add(trainSamples.get(i));
            }
            else{
                int[] img = new int[785];
                System.arraycopy(validLabels[i- (allImgs - validImg)], 0, img, 0, validLabels[i- (allImgs - validImg)].length);
                System.arraycopy(validImgs[i- (allImgs - validImg)], 0, img, 1 , validImgs[i- (allImgs - validImg)].length);
                allSamples.add(img);
            }
        }
        int[] allLabel = new int[10];
        for (int i = 0; i < 10; i++) {
            allLabel[i] = validLabelsArray[i] + trainLabels[i];
        }

        root = new Node(allLabel, allSamples, 0);
        BinaryTree Tree = new BinaryTree(root, maxTreeIndex);
        while (Tree.bestSplit()){}


        FileOutputStream f = new FileOutputStream(new File("Tree.txt"));
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(Tree);


    }

    public static double checkTreeQuality(BinaryTree Tree,int[][] validImgs, int[][] validLabels ){
        int bullEye = 0;
        int ourLabel= 0;
        for(int i = 0; i < validImgs.length; i++) {
            ourLabel = Tree.navigate(validImgs[i]);
            if(ourLabel == validLabels[i][0])
                bullEye++;
        }
        //--------------
        System.out.println("pixel to check"+Tree.root.pixel);
        System.out.println("bulleye: "+bullEye);
        int x = 3;
        //-------------------
        return (bullEye*100.0)/validImgs.length;
    }


}
