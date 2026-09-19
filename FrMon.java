

import java.util.Random;

public class FrMon {
    static double random(){
    Random ra=new Random();
    double a=(ra.nextDouble()+1)*(ra.nextDouble(0.3)+1);
    return a;
    //目前问题：泛型不会写，数据范围不确定，数据的范围的存储还不清晰
    }
}
