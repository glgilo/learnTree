
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
    int cond;
    int iter;



    int[] condi = new int[4];


    public BinaryTree (Node root,int L, int cond){
        this.root = root;
        this.hight = 0;
        this.maxHight = L;
        this.leafs.add(root);
        this.cond = cond;
        if(cond ==1)
            iter = 1;
        if(cond == 2)
            iter = 4;
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
            int[] rightLabels;
            int[] leftLabels;
            int[] maxRightLabels = new int[10];
            int[] maxLeftLabels = new int[10];
            int[] maxSamplesRight1 = null;
            int[] maxSamplesLeft1 = null;
            int condition = 0;

            double max = -1.0;
            int pixel = 0;
            for (Node leaf : leafs) {
                if (!leaf.checkedAsLeaf) {
                    leaf.setCheckedAsLeaf(true);

                  long start = System.nanoTime();

                    for (Map.Entry<Integer, Integer> entry : leaf.getCurrPixels().entrySet()) {
                        pixel = entry.getValue();
                        for(int i = 0; i < iter; i ++) {
                            rightLabels = new int[10];
                            leftLabels = new int[10];

                            if(i != 0)
                                pixel = pixel +784;

                            int[] RL = calcRL(leaf.samplesIndex, pixel, rightLabels, leftLabels);
                            right = RL[0];
                            left = RL[1];

                            if(right == 0 && left == 0)
                                System.out.println("hi");/////////////////////////////////////////////////////////////////////////////////


                            double IG = leaf.entropyCalc() - leaf.sunsEntropyCalc(right, left, rightLabels, leftLabels);
                            double nL_IG = IG * (double) leaf.getnL(leaf.labels);
                            if (max < nL_IG) {
                                max = nL_IG;
                                maxToSplit = leaf;
                                maxRightLabels = rightLabels;
                                maxLeftLabels = leftLabels;

                                maxSamplesRight1 = new int[right];
                                maxSamplesLeft1 = new int[left];

                                calcSamples(leaf.samplesIndex, pixel, maxSamplesRight1, maxSamplesLeft1);
                                pixelToCheck = pixel - i*784;
                                condition = i;
                            }
                        }

                    }




                    if(maxToSplit == null)
                        System.out.println("check");/////////////////////////////////////////////////////////////////////////////////

                    if (maxToSplit != null)
                        bestLeaf.add(new BestLeaf(maxToSplit, max, maxSamplesRight1, maxSamplesLeft1, maxRightLabels, maxLeftLabels, maxToSplit.getDepth(), pixelToCheck, condition));
                    max = -1.0;




                }

            }
            BestLeaf  newBest = bestLeaf.poll();

            condi[newBest.cond]++;

            if (newBest == null)
                System.out.println("sup");/////////////////////////////////////////////////////////////////////////////////
            HashMap<Integer,Integer> forSuns = newBest.getLeaf().getCurrPixels();
            forSuns.remove(newBest.getPixel());
            Node leftNode = new Node(newBest.getLabelsLeft(),newBest.getSamplesLeft(), newBest.getDepth() + 1, forSuns);
            Node rightNode = new Node(newBest.getLabelsRight(), newBest.getSamplesRight(), newBest.getDepth() + 1, forSuns);
            newBest.getLeaf().setLeft(leftNode);
            newBest.getLeaf().setRight(rightNode);
            newBest.getLeaf().setPixel(newBest.getPixel());
            newBest.getLeaf().setCond(newBest.cond);
            newBest.getLeaf().samplesIndex = null;
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

    public HashMap<Integer,Integer> condList(){
        HashMap<Integer,Integer> condPixel = new HashMap<Integer,Integer>();

        for(int i = 2; i < 26; i++){
            for(int j = 2; j < 26 ; j++){
                condPixel.put(i*28 + j,i*28 + j );
            }
        }
        return condPixel;
    }

    public  void calcSamples (int[] samples, int pixel , int[] samplesRight1, int[] samplesLeft1){
        int right = 0, left = 0;
        for(int  index : samples){
            if (Conditions.ans(index, pixel)) {
                samplesRight1[right] = index;
                right++;
            } else {
                samplesLeft1[left] = index;
                left++;
            }
        }



    }
    public  int[]  calcRL (int[] samples, int pixel  , int[] rightLabels, int[] leftLabels){
        int right = 0, left = 0;
        for(int  index : samples){
            if (Conditions.ans(index, pixel)) {///////checking regular without gauss kernel
                rightLabels[Conditions.exampleIndex[index]]++;
                right++;
            } else {
                leftLabels[Conditions.exampleIndex[index]]++;
                left++;
            }
        }
        int[] RL = {right,left};

        return RL;


    }

}
