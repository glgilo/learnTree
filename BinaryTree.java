import java.io.NotSerializableException;
import java.util.ArrayList;

public class BinaryTree extends NotSerializableException {
    Node root;
    int hight;
    int maxHight;
    ArrayList<Node> leafs = new ArrayList<Node>();

    public BinaryTree (Node root,int L){
        this.root = root;
        this.hight = 0;
        this.maxHight = L;
        this.leafs.add(root);
    }

    public int getMaxHight(){
        return maxHight;
    }

    public boolean bestSplit() {
        if (leafs.size() < Math.pow(2, maxHight) + 1) {
//            double max = Double.MIN_VALUE;
            Node maxToSplit = null;
            int pixelToCheck = 0;
            int right = 0;
            int left = 0;
            int[] rightLabels = new int[10];
            int[] leftLabels = new int[10];

            int noChange = leafs.size();
            while (noChange == leafs.size()) {
                pixelToCheck = (int)( Math.random() * 784) + 1;
                double max = -1.0;
                noChange = 0;
                for (Node leaf : leafs) {
                    for (int[] sampel : leaf.samples) {
                        if (sampel[pixelToCheck] > 128) {
                            rightLabels[sampel[0]]++;
                            right++;
                        } else {
                            leftLabels[sampel[0]]++;
                            left++;
                        }
                    }
                    if (left == 0 || right == 0)
                        noChange++;

                    double IG = leaf.entropyCalc() - leaf.sunsEntropyCalc(right, left, rightLabels, leftLabels);
                    double nL_IG = IG * leaf.getnL(leaf.labels);
                    if (max < nL_IG) {
                        max = nL_IG;
                        maxToSplit = leaf;
//                        System.out.println("Hi");
                    }
                    right = 0;
                    left = 0;
                    rightLabels = new int[10];
                    leftLabels = new int[10];
                }
            }
            ArrayList<int[]> samplesRight = new ArrayList<int[]>();
            ArrayList<int[]> samplesLeft = new ArrayList<int[]>();
            for (int i = 0; i < maxToSplit.samples.size(); i++) {
                if (maxToSplit.samples.get(i)[pixelToCheck] > 128) {
                    rightLabels[maxToSplit.samples.get(i)[0]]++;
                    samplesRight.add(maxToSplit.samples.get(i));
                } else {
                    leftLabels[maxToSplit.samples.get(i)[0]]++;
                    samplesLeft.add(maxToSplit.samples.get(i));
                }
            }
            Node leftNode = new Node(leftLabels, samplesLeft, maxToSplit.getDepth() + 1);
            Node rightNode = new Node(rightLabels, samplesRight, maxToSplit.getDepth() + 1);
            maxToSplit.setLeft(leftNode);
            maxToSplit.setRight(rightNode);
            maxToSplit.setPixel(pixelToCheck-1);

            maxToSplit.samples = null;
            maxToSplit.labels = null;

            leafs.remove(maxToSplit);
            leafs.add(leftNode);
            leafs.add(rightNode);
            return true;
        }

        else{
                return false;
        }
    }

    public int navigate(int[] img){
        if(root == null){
            System.out.println("The tree is empty");
        }
        else {
            Node temp = root;
            while (temp.left != null && temp.right != null)
                temp = temp.whereToGo(img);
            return temp.maxLabel;
        }
        return -1;
    }


}
