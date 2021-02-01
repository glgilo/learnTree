import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.HashMap;

public class Node extends NotSerializableException {
    int maxLabel;
    Node left;
    Node right;
    int[] labels = new int[10];
    ArrayList<int[]> samples = new ArrayList<int[]>();
    int depth;
    int pixel;
    HashMap<Integer,Integer> oldPixel;
    HashMap<Integer,Integer> currPixels;
    boolean checkedAsLeaf = false;


    public Node(int[] labels, ArrayList<int[]> samples, int depth, HashMap<Integer,Integer> currPixels){
        this.labels = labels;
        this.maxLabel = findMaxLabel();
        this.left = null;
        this.right = null;
        this.samples = samples;
        this.depth = depth;
        oldPixel = new HashMap<>();
        this.currPixels = currPixels;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public int getMaxLabel() {
        return maxLabel;
    }

    public int getDepth() {
        return depth;
    }


    public void setRight(Node nodeRight){
        this.right = nodeRight;
    }

    public void setLeft(Node nodeLeft){
        this.left = nodeLeft;
    }

    public void setPixel(int pixel){
        this.pixel = pixel;
    }

    public double entropyCalc(){
        return entropyCalc(this.labels);
    }

    public void addPixel(int pixel){
        oldPixel.put(pixel,1);
    }

    public HashMap<Integer,Integer> getCurrPixels() {
        return currPixels;
    }

    public ArrayList<int[]> getSamples() {
        return samples;
    }

    public void setCurrPixels(HashMap<Integer,Integer> currPixels) {
        this.currPixels = currPixels;
    }

    public void setCheckedAsLeaf(boolean checkedAsLeaf) {
        this.checkedAsLeaf = checkedAsLeaf;
    }

    public int findMaxLabel(){
        int Max = 0;
        int label = -1;
        for(int i= 0; i < 10; i++){
            if(Max < labels[i]){
                Max = labels[i];
                label = i;
            }
        }
        return label;

    }


    public double sunsEntropyCalc(int nLa, int nLb, int[] labelsA,int [] labelsB){
        int nL = nLa + nLb;
        return ((double)nLa/nL) * entropyCalc(labelsA) + ((double)nLb/nL) * entropyCalc(labelsB);
    }

    public double entropyCalc(int [] labels){
        int nL = getnL(labels);
        double hL = 0;
        for(int i = 0; i < labels.length; i++){
            if(labels[i] != 0) {
                hL = hL +( ((float) labels[i] / nL) * ((Math.log( (float) nL/labels[i])) / Math.log(2)));
            }
        }
        return hL;
    }

    public int getnL(int[] labels) {
        int nL = 0;
        for (int i = 0; i < labels.length; i++) {
            nL = nL + labels[i];
        }
        return nL;
    }

    public Node whereToGo(int[] img){
        if(checkWithNeighbors(img,pixel) > 128){
            return this.right;
        }
        else{
            return this.left;
        }
    }
    public double checkWithNeighbors(int[] img, int pixel){
        double gaussAvg =  (double) img[pixel]/4 + (double) img[pixel-1]/8 + (double) img[pixel+1]/8
                + (double) img[pixel+28]/8 + (double) img[pixel-28]/8 + (double) img[pixel-29]/16 + (double) img[pixel+29]/16
                + (double) img[pixel+27]/16 + (double) img[pixel-27]/16;

        return gaussAvg;
    }
}
