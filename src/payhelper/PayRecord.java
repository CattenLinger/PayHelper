package payhelper;

/**
 * Created by catten on 15/4/21.
 */

public class PayRecord{

    private String title;
    private float pay;
    private float payed;
    private boolean dividable;

    private int[] changes = new int[9];//50 20 10 5 2 1 0.5 0.2 0.1
    private static String[] value_of_changes = new String[]{"50","20","10","5","2","1","0.5","0.2","0.1"};

    private boolean _countChanges(){
        float temp = getChange();
        float[] vale_of_changes = new float[]{50,20,10,5,2,1,1/2,1/5,1/10};
        for(int i = 0; i < 9 ; i++){
            changes[i] = (int)(temp / vale_of_changes[i]);
            temp -= changes[i] * vale_of_changes[i];
        }
        //dividable = temp == 0;
        return temp == 0;
    }

    public boolean isDividable(){
        return dividable;
    }

    public static String[] getValue_of_changes() {
        return value_of_changes;
    }

    public int[] getChanges() {
        return changes;
    }

    public float getPay() {
        return pay;
    }

    public float getPayed() {
        return payed;
    }

    public String getTitle() {
        return title;
    }

    public PayRecord(String Title,float Price,float Payed){
        title = Title;
        pay = Price;
        payed = Payed;
        dividable = _countChanges();
    }

    public float getChange(){
        return payed - pay > 0?payed - pay:0;
    }

    public float getDebt(){
        return pay - payed > 0?pay - payed:0;
    }

    public String toString(){
        return String.format("Title:%s\nPay:%.2f\nPayed:%.2f",title,pay,payed);
    }
}