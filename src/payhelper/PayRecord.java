package payhelper;

import java.lang.management.PlatformLoggingMXBean;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.Scanner;
import java.util.Vector;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Created by catten on 15/4/21.
 */

public class PayRecord{

    private String title;
    private double pay;
    private double payed;
    private boolean dividable;

    private int[] changes = new int[9];//50 20 10 5 2 1 0.5 0.2 0.1
    private static String[] value_of_changes = new String[]{"50","20","10","5","2","1","0.5","0.2","0.1"};

    private boolean _countChanges(){
        //double和float误差烦人，所以这里的计算先全部乘上10
        int temp = (int)(getChange() * 10);
        double[] vale_of_changes = new double[]{50,20,10,5,2,1,0.5,0.2,0.1};
        for(int i = 0; i < 9 ; i++){
            changes[i] = (int)(temp / (vale_of_changes[i] * 10));
            temp -= changes[i] * (vale_of_changes[i] * 10);
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

    public double getPay() {
        return pay;
    }

    public double getPayed() {
        return payed;
    }

    public String getTitle() {
        return title;
    }

    public PayRecord(String Title,double Price,double Payed){
        title = Title;
        pay = Double.parseDouble(String.format("%.2f",Price));
        payed = Double.parseDouble(String.format("%.2f",Payed));
        dividable = _countChanges();
    }

    public double getChange(){
        return payed - pay > 0?Double.parseDouble(String.format("%.2f",payed - pay)):0;
    }

    public double getDebt(){
        return pay - payed > 0?Double.parseDouble(String.format("%.2f",pay - payed)):0;
    }

    public static String getCurrency()
    {
        return "CNY";
    }

    public String toString(){
        return String.format("Title:%s\nPay:%.2f\nPayed:%.2f", title, pay, payed);
    }

    public static String printTable(Vector<PayRecord> recordlist){
        StringBuilder buffer = new StringBuilder();
        buffer.append("|付款人\t|已付\t|应付\t|找零\t|负债\t|\n\n");
        for(PayRecord temp : recordlist){
            buffer.append(String.format("|%s\t|%.2f\t|%.2f\t|%.2f\t|%.2f\t|\n",
                    temp.getTitle(),
                    temp.getPay(),
                    temp.getPayed(),
                    temp.getChange(),
                    temp.getDebt()
            ));
        }
        buffer.append("\n\n");
        return buffer.toString();
    }

    public static String printAdvise(Vector<PayRecord> recordlist){
        if(recordlist.size() == 0){
            return "";
        }

        Vector<PayRecord> vector;
        StringBuilder buffer = new StringBuilder();
        vector = (Vector)recordlist.clone();
        Vector<PayRecord> newrecordlist = new Vector<PayRecord>();
        int vectorpointer = 0;
        PayRecord pointer = null;
        while(vectorpointer < vector.size()){
            pointer = vector.get(vectorpointer);
            Vector<PayRecord> temp = new Vector<PayRecord>();
            temp.add(pointer);
            int i = 0;
            while(i < vector.size()){//只要i小于Vector的尺寸就继续遍历，这样子即便数组大小变化了也不用理会。
                PayRecord pointer2 = vector.get(i);
                if(pointer != pointer2 && pointer.getTitle().equals(pointer2.getTitle())){
                    temp.add(pointer2);
                    vector.remove(pointer2);
                    //因为删了指针指向的那个，所以要退回去一个，这样就能够遍历得到替补掉第i个的那个对象。
                    i--;
                }
                i++;
            }
            String title = pointer.getTitle();
            double amount = 0;
            double payed = 0;
            for(PayRecord pointer2:temp){
                amount += pointer2.getPay();
                payed += pointer2.getPayed();
            }
            newrecordlist.add(new PayRecord(title,amount,payed));
            vectorpointer++;
        }

        double v_amount = 0;
        double v_payedamount = 0;
        for (int i = 0; i < newrecordlist.size(); i++){
            PayRecord temp = newrecordlist.get(i);
            v_amount += temp.getPay();
            v_payedamount += temp.getPayed();
        }
        double v_deb = 0;
        double v_changes = 0;
        if(v_amount > v_payedamount){
            v_deb = v_amount - v_payedamount;
        }else{
            v_changes = v_payedamount - v_amount;
        }
        buffer.append(String.format("付款总额：%.2f\n应付：%.2f\n欠款：%.2f\n找零：%.2f\n",
                        v_payedamount,
                        v_amount,
                        v_deb,
                        v_changes)
        );
        int[] temp_adv = new int[]{0,0,0,0,0,0,0,0,0};
        boolean canDiv = true;
        boolean haveChanges = false;
        for(PayRecord temp : newrecordlist){
            for(int i = 0; i < 9;i++){
                temp_adv[i] += temp.getChanges()[i];
                if(temp.getChanges()[i] != 0){
                    haveChanges |= true;
                }
            }
            canDiv &= temp.isDividable();
        }
        if((v_changes * 100 % 10) != 0 || !canDiv){
            buffer.append("或许金额包含“分”所以不能完全找零。\n但依旧输出建议找零组合。\n");
        }
        if(v_changes > 0 && haveChanges){
            buffer.append("建议找零组合：\n");
            for(int i = 0; i < 9;i++){
                if(temp_adv[i] != 0){
                    buffer.append(String.format("%s\t:\t%d 张\n",PayRecord.getValue_of_changes()[i],temp_adv[i]));
                }
            }
        }

        return buffer.toString();
    }

    public static void main(String[] args){
        Vector<PayRecord> listPayRecord = new Vector<PayRecord>();
        int[] temp_adv = new int[]{0,0,0,0,0,0,0,0,0};
        boolean canDiv = true;

        String title;
        double pay;
        double payed;
        Scanner scanner = new Scanner(in);
        int j = 256;
        out.printf("Please input records follow tips.\nFill\"quit\" finish inputting.\n");
        while(j-- != 0){
            out.print("Please input name: ");
            title = scanner.next();
            if(title.equals("quit")) break;
            out.print("Amount:");
            pay = scanner.nextDouble();
            out.print("Payed:");
            payed = scanner.nextDouble();
            listPayRecord.add(new PayRecord(title,pay,payed));
        }

        PayRecord.printTable(listPayRecord);
        out.print("Advised Changes match:\n");
        for(PayRecord temp : listPayRecord){
            for(int i = 0; i < 9;i++){
                temp_adv[i] += temp.getChanges()[i];
            }
            canDiv &= temp.isDividable();
        }
        for(int i = 0; i < 9;i++){
            if(temp_adv[i] != 0){
                out.printf("%s\t:\t%d pic(s)\n",PayRecord.getValue_of_changes()[i],temp_adv[i]);
            }
        }
        if(!canDiv) out.print("It seems that it can't be divide.\n");
    }
}