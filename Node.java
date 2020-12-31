import java.io.NotSerializableException;
import java.util.ArrayList;

public class Node extends NotSerializableException {
    int maxLabel;
    Node left;
    Node right;
    int[] labels = new int[10];
    ArrayList<int[]> samples = new ArrayList<int[]>();
    int depth;
    int pixel;

    public Node(int[] labels, ArrayList<int[]> samples, int depth){
        this.labels = labels;
        this.maxLabel = findMaxLabel();
        this.left = null;
        this.right = null;
        this.samples = samples;
        this.depth = depth;
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
                hL = hL + ((double) labels[i] / nL) * (Math.log( (double) nL/labels[i]));
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
        if(img[pixel] > 128){
            return this.right;
        }
        else{
            return this.left;
        }
    }
}
