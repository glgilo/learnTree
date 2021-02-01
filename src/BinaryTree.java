import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class BinaryTree extends NotSerializableException {
    Node root;
    int hight;
    int maxHight;
    ArrayList<Node> leafs = new ArrayList<Node>();
    PriorityQueue<BestLeaf> bestLeaf = new PriorityQueue<BestLeaf>(new nL_IG_Comparator());

    public BinaryTree (Node root,int L){
        this.root = root;
        this.hight = 0;
        this.maxHight = L;
        this.leafs.add(root);
        this.root.setCurrPixels(condList());
    }

    public int getMaxHight(){
        return maxHight;
    }

    public void setMaxHight(int maxHight) {
        this.maxHight = maxHight;
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
            ArrayList<int[]> samplesRight1 = new ArrayList<int[]>();
            ArrayList<int[]> samplesLeft1 = new ArrayList<int[]>();
            int[] maxRightLabels = new int[10];
            int[] maxLeftLabels = new int[10];
            ArrayList<int[]> maxSamplesRight1 = new ArrayList<int[]>();
            ArrayList<int[]> maxSamplesLeft1 = new ArrayList<int[]>();


            double max = -1.0;
            int pixel = 0;
            for (Node leaf : leafs) {
                ArrayList<Integer> pixelToRemove = new ArrayList<>();
                if (!leaf.checkedAsLeaf) {
                    leaf.setCheckedAsLeaf(true);
                    for (Map.Entry<Integer, Integer> entry : leaf.getCurrPixels().entrySet()) {
                        right = 0;
                        left = 0;
                        rightLabels = new int[10];
                        leftLabels = new int[10];
                        samplesRight1 = new ArrayList<int[]>();
                        samplesLeft1 = new ArrayList<int[]>();

                        pixel = entry.getValue();
                        for (int[] sampel : leaf.samples) {
                            if (sampel[pixel] > 128) {///////checking regular without gauss kernel
                                rightLabels[sampel[784]]++;
                                right++;
                                samplesRight1.add(sampel);
                            } else {
                                leftLabels[sampel[784]]++;
                                left++;
                                samplesLeft1.add(sampel);
                            }
                        }
                        double IG = leaf.entropyCalc() - leaf.sunsEntropyCalc(right, left, rightLabels, leftLabels);
                        double nL_IG = IG * (double) leaf.getnL(leaf.labels);
                        if (max < nL_IG) {
                            max = nL_IG;
                            maxToSplit = leaf;
                            maxRightLabels = rightLabels;
                            maxLeftLabels = leftLabels;
                            maxSamplesRight1 = samplesRight1;
                            maxSamplesLeft1 = samplesLeft1;
                            pixelToCheck = pixel;
                        }
                        if (right == 0 || left == 0)
                            pixelToRemove.add(pixel);


                    }

                    if (maxToSplit != null)
                        bestLeaf.add(new BestLeaf(maxToSplit, max, maxSamplesRight1, maxSamplesLeft1, maxRightLabels, maxLeftLabels, maxToSplit.getDepth(), pixelToCheck));
                    max = -1.0;

                    for (Integer toRemove : pixelToRemove)
                        leaf.getCurrPixels().remove(toRemove);


                }

            }
//            leafs = new ArrayList<>();

            BestLeaf  newBest = bestLeaf.poll();
//            while(newBest.getLeaf().getRight() != null && newBest.getLeaf().getLeft() != null)
//            {
//                newBest = bestLeaf.poll();
//            }
//            if (newBest == null)
//                System.out.println("sup");
            HashMap<Integer,Integer> forSuns = newBest.getLeaf().getCurrPixels();
            forSuns.remove(newBest.getPixel());
            Node leftNode = new Node(newBest.getLabelsLeft(),newBest.getSamplesLeft(), newBest.getDepth() + 1, forSuns);
            Node rightNode = new Node(newBest.getLabelsRight(), newBest.getSamplesRight(), newBest.getDepth() + 1, forSuns);

//            ArrayList<int[]> samplesRight = new ArrayList<int[]>();
//            ArrayList<int[]> samplesLeft = new ArrayList<int[]>();
//            for (int i = 0; i < maxToSplit.samples.size(); i++) {
//                if (maxToSplit.samples.get(i)[pixelToCheck] > 128) {
//                    rightLabels[maxToSplit.samples.get(i)[784]]++;
//                    samplesRight.add(maxToSplit.samples.get(i));
//                } else {
//                    leftLabels[maxToSplit.samples.get(i)[784]]++;
//                    samplesLeft.add(maxToSplit.samples.get(i));
//                }
//            }
//            Node leftNode = new Node(leftLabels, samplesLeft, maxToSplit.getDepth() + 1);
//            Node rightNode = new Node(rightLabels, samplesRight, maxToSplit.getDepth() + 1);
//            maxToSplit.setLeft(leftNode);
//            maxToSplit.setRight(rightNode);
//            maxToSplit.setPixel(pixelToCheck);
//
//            maxToSplit.samples = null;
//            maxToSplit.labels = null;
//
//            leafs.remove(maxToSplit);
            newBest.getLeaf().setLeft(leftNode);
            newBest.getLeaf().setRight(rightNode);
            newBest.getLeaf().setPixel(newBest.getPixel());
            newBest.getLeaf().samples = null;
            newBest.getLeaf().labels = null;
            leafs.add(leftNode);
            leafs.add(rightNode);
            leafs.remove(newBest.getLeaf());
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

    public double checkWithNeighbors(int[] img, int pixel){
        double gaussAvg =  (double) img[pixel]/4 + (double) img[pixel-1]/8 + (double) img[pixel+1]/8
                + (double) img[pixel+28]/8 + (double) img[pixel-28]/8 + (double) img[pixel-29]/16 + (double) img[pixel+29]/16
                + (double) img[pixel+27]/16 + (double) img[pixel-27]/16;

        return gaussAvg;
    }

    public HashMap<Integer,Integer> condList(){
        HashMap<Integer,Integer> condPixel = new HashMap<Integer,Integer>();

        for(int i = 3; i < 27; i++){
            for(int j = 2; j < 26 ; j++){
                condPixel.put(i*28 + j,i*28 + j );
            }
        }
        return condPixel;
    }
}
