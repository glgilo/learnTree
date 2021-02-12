import java.util.Comparator;
import java.io.NotSerializableException;


class nL_IG_Comparator extends NotSerializableException  implements Comparator<BestLeaf>{
    public int compare(BestLeaf l1, BestLeaf l2){
        if(l1.getnL_IG() - l2.getnL_IG() > 0)
            return -1;
        if(l1.getnL_IG() - l2.getnL_IG() < 0)
            return 1;
        return 0;
    }
}
