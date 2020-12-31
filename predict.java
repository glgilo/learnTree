import java.io.*;

public class predict {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String fileNameImg = "C:/Users/gilie/IdeaProjects/predictAlgo/tests/t10k-images.idx3-ubyte";
        String fileNameLabel = "C:/Users/gilie/IdeaProjects/predictAlgo/tests/t10k-labels.idx1-ubyte";

        DataInputStream inImage = new DataInputStream(new BufferedInputStream(new FileInputStream(fileNameImg)));
        DataInputStream inLabel = new DataInputStream(new BufferedInputStream(new FileInputStream(fileNameLabel)));


        FileInputStream fi = new FileInputStream(new File(args[0]));
        ObjectInputStream oi = new ObjectInputStream(fi);

        BinaryTree predictTree = (BinaryTree) oi.readObject();


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
        int numOfPicture = 10000;
        int countBullEye = 0;
        for(int i = 0; i < numOfPicture ; i ++){
            int[] img = new int[784];

            for(int j = 0 ; j < numberOfPixels; j++){
                img[j] = inImage.readUnsignedByte();
            }
            int ourLabel =  predictTree.navigate(img);
            int realLabel = inLabel.readUnsignedByte();
//            System.out.println("our label: " + ourLabel + " real label " + realLabel);
            if(ourLabel == realLabel)
                countBullEye++;
        }
        System.out.println((countBullEye*100)/numOfPicture);
    }

}
