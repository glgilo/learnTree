import java.io.NotSerializableException;
import java.util.ArrayList;

public class BestLeaf extends NotSerializableException {
    Node leaf;
    double nL_IG;
    int[] samplesRight;
    int[] samplesLeft;
    int[] labelsRight = new int[10];
    int[] labelsLeft = new int[10];
    int depth;
    int pixel;
    int cond;

    public BestLeaf(Node leaf, double nL_IG, int[] samplesRight,int[] samplesLeft, int[] labelsRight, int[] labelsLeft, int depth, int pixel , int cond){
        this.leaf = leaf;
        this.nL_IG = nL_IG;
        this.samplesRight = samplesRight;
        this.samplesLeft = samplesLeft;
        this.labelsRight = labelsRight;
        this.labelsLeft = labelsLeft;
        this.depth = depth;
        this.pixel = pixel;
        this.cond = cond;
    }

    public Node getLeaf() {
        return leaf;
    }

    public double getnL_IG() {
        return nL_IG;
    }

    public int[] getSamplesRight() {
        return samplesRight;
    }
    public int[] getSamplesLeft() {
        return samplesLeft;
    }

    public int[] getLabelsRight() {
        return labelsRight;
    }

    public int[] getLabelsLeft() {
        return labelsLeft;
    }

    public int getDepth(){
        return depth;
    }

    public int getPixel() {
        return pixel;
    }
}
