import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class predict {

    public static void main(String[] args) throws IOException, ClassNotFoundException {


        String fileNameImg = args[1];



        try(FileInputStream fi = new FileInputStream(new File(args[0]))){

            ObjectInputStream oi = new ObjectInputStream(fi);

            BinaryTree predictTree = (BinaryTree) oi.readObject();

            ArrayList<int[]> trainSamples2 = new ArrayList<>();
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

            int allImgs = trainSamples2.size();
            int[][] validImgs = new int[allImgs][786];
            int[][] validLabels = new int[allImgs][1];
            int coundValid = 0;

            for(int[] img: trainSamples2){
                validImgs[coundValid] = img;
                validLabels[coundValid][0] = img[785];
                coundValid++;
            }



            if(predictTree.cond == 1){
                Conditions c = new Conditions(1, allImgs);
                c.case1(validImgs);
            }
            else{
                Conditions c = new Conditions(2,allImgs);
                c.case2(validImgs);
            }


            for(int i = 0; i < validImgs.length; i++){
                System.out.println(predictTree.navigate(validImgs[i]));
            }






        }
        catch (IOException e) {
            System.out.println("Error raised while reading Tree from " + args[0]);
        }





    }

}
