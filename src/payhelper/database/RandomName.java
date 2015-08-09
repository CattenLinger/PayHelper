package payhelper.database;

import java.util.Date;
import java.util.Random;

/**
 * Created by catten on 15/8/9.
 */
public class RandomName {
    private Random random;
    private String[] names = new String[]{
            "张三",
            "李四",
    };
    public RandomName(){
        random = new Random(new Date().getTime());
    }

    public String nextName(){
        return names[(int)(random.nextDouble() * names.length)];
    }

    public String[] getNameList(){
        return names.clone();
    }
}
