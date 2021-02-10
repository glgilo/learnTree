import java.util.HashMap;
import java.util.Map;

public class Conditions {

    public static boolean[][] condSol;
    public static int[] exampleIndex = new int [60000];
    HashMap<Integer,Integer> condList;

    public Conditions(int type, int size){
        condList = buildCondList();
        if(type == 1){
            condSol = new boolean[size][784];
        }
        else{
            condSol = new boolean[size][3135];
        }
    }

    public HashMap<Integer,Integer> buildCondList(){
        HashMap<Integer,Integer> condPixel = new HashMap<Integer,Integer>();

        for(int i = 2; i < 26; i++){
            for(int j = 2; j < 26 ; j++){
                condPixel.put(i*28 + j,i*28 + j );
            }
        }
        return condPixel;
    }

    public void case1(int[][] imgs){
        int pixel;

            for (int[] img : imgs) {
                for (Map.Entry<Integer, Integer> entry : condList.entrySet()) {
                    pixel = entry.getKey();
                    condSol[img[784]][pixel] = img[pixel] > 128;

                }
                exampleIndex[img[784]] = img[785];

        }
    }

    public void case2(int[][] imgs){
        int pixel;
        int neighbors;
        for (int[] img : imgs) {
            for (Map.Entry<Integer, Integer> entry : condList.entrySet()) {
                pixel = entry.getKey();
//                condSol[img[784]][pixel] = img[pixel] > 25;
                neighbors = (int)checkWithNeighbors(img, pixel);
                condSol[img[784]][pixel] = neighbors > 25;
//                condSol[img[784]][pixel+784] = checkLine(img,pixel) > 5;
//                condSol[img[784]][pixel+1568] = checkColumn(img, pixel) > 5;
                condSol[img[784]][pixel+784] = rectangle52(img,pixel);

                condSol[img[784]][pixel+1568] = rectangle25(img,pixel);

                condSol[img[784]][pixel+2351] = grad(img,pixel)>50;


//                condSol[img[784]][pixel+2351] = checkWithNeighborsOn2(img,pixel) > 6 ;

            }
            exampleIndex[img[784]] = img[785];

        }
    }

    public double checkWithNeighbors(int[] img, int pixel){
        double gaussAvg =  (double) img[pixel]/4 + (double) img[pixel-1]/8 + (double) img[pixel+1]/8
                + (double) img[pixel+28]/8 + (double) img[pixel-28]/8 + (double) img[pixel-29]/16 + (double) img[pixel+29]/16
                + (double) img[pixel+27]/16 + (double) img[pixel-27]/16;

        return gaussAvg;
    }


    public double checkLine(int[] img, int pixel){
        int y = pixel/28 ;
        double line = 0;
        for(int i = 0; i < 28 ;i++){
            if(img[28*y + i] > 25)
                line++;
        }

        return line;
    }

    public int checkColumn(int[] img, int pixel){
        int x = pixel%28;
        int col =0;
        for(int i = 0; i<28; i++){
            if(img[x+(i*28)] > 25)
                col++;
        }
        return col;
    }

    public double checkWithNeighborsOn2(int[] img, int pixel){
//        double gaussAvg =  (double) img[pixel] + (double) img[pixel-1] + (double) img[pixel+1]
//                + (double) img[pixel+28] + (double) img[pixel-28] + (double) img[pixel-29] + (double) img[pixel+29]
//                + (double) img[pixel+27] + (double) img[pixel-27] + (double) img[pixel-2] + (double) img[pixel+2]
//                + (double) img[pixel+2*28] + (double) img[pixel-28*2] + (double) img[pixel-2*28-1] + (double) img[pixel+2*28+1]
//                + (double) img[pixel+2*28-1] + (double) img[pixel-2*28+1];

        int count = 0;
        int[] pixels = {pixel, pixel-1, pixel+1, pixel+28, pixel-28, pixel-29, pixel+29, pixel+27, pixel -27, pixel-2, pixel+2,pixel+2*28, pixel-2*28, pixel+2*28+1, pixel-2*28-1,pixel+2*28-1, pixel-2*28+1 };
        for(int pix: pixels) {
            if (img[pix] > 25)
                count++;
        }

        return count;
    }


    public double checkWithNeighborsOn(int[] img, int pixel){
        int count = 0;
        int[] pixels = {pixel, pixel-1, pixel+1, pixel+28, pixel-28, pixel-29, pixel+29, pixel+27, pixel -27};
        for(int pix: pixels) {
            if (img[pix] > 25)
                count++;
        }

        return count;
    }


    public double grad(int[] img, int pixel){
        double x =   (double) img[pixel-1]*2 + (double) img[pixel+1]*-2
                 + (double) img[pixel-29] + (double) img[pixel+29]*-1
                + (double) img[pixel+27]*-1 + (double) img[pixel-27];

        double y =  (double) img[pixel+28]*-2 + (double) img[pixel-28]*2 + (double) img[pixel-29] + (double) img[pixel+29]*-1
                + (double) img[pixel+27]*1 + (double) img[pixel-27]/16;

        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }


    public boolean rectangle25(int[] img, int pixel){

        if(pixel%28 <24 ){
            double avg = 0;
            int[] pixels = {pixel, pixel+1, pixel+2, pixel+3, pixel+4, pixel+28, pixel+29, pixel+30, pixel+31, pixel+32};
            for(int pix: pixels)
                avg+= img[pix];
            return (avg/12) > 25;


        }
        return false;

    }

    public boolean rectangle52(int[] img, int pixel){

        if(pixel/28 <24 ){
            double avg = 0;
            int[] pixels = {pixel, pixel+28, pixel+28*2, pixel+28*3, pixel+28*4, pixel+1, pixel+29, pixel+28*2+1, pixel+28*3+1, pixel+28*4+1};
            for(int pix: pixels)
                avg+= img[pix];
            return (avg/12) > 25;


        }
        return false;

    }

    public static boolean ans(int index, int pixel) {
        return condSol[index][pixel];
    }

}
