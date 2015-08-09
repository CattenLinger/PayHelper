package payhelper;

import payhelper.currency.CNY;
import payhelper.currency.Currency;

import java.util.*;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Created by catten on 15/8/9.
 */
public class RecordsManager {
    private Map<String,PayRecord> pool;
    private Vector<PayRecord> defaulters;

    private double totalExpenses;
    private double totalDue;
    private double totalArrears;
    private double totalChanges;
    //账单记录列表
    private Vector<PayRecord> recordlist;

    private Currency currentCurrency;

    public RecordsManager(Vector<PayRecord> list,Currency currency){
        recordlist = list;
        currentCurrency = currency;
        //nameBook = new HashSet<String>();
        //pool = new Vector<PayRecord>();
        defaulters = new Vector<PayRecord>();
        pool = new HashMap<String, PayRecord>();
        copyToMap();
    }//*/

    private void copyToMap(){
        for(PayRecord record:recordlist){
            PayRecord temp = pool.put(record.getTitle(),record);
            if(temp != null){
                pool.get(record.getTitle()).merge(temp);
            }
        }
        refreshData();
    }

    private void refreshData(){
        defaulters.clear();
        for (PayRecord record:pool.values()){
            if(record.isInDebt()){
                defaulters.add(record);
            }
        }
    }

    public Currency getCurrentCurrency(){
        return currentCurrency;
    }

    public void addRecord(String title,double Due,double Expenses){
        PayRecord record = new PayRecord(title,Due,Expenses,currentCurrency);
        recordlist.add(record);
        PayRecord temp = pool.put(record.getTitle(),record);
        if(temp != null){
            pool.get(record.getTitle()).merge(temp);
        }
        refreshData();
    }

    public Vector<PayRecord> getRecordList(){
        return recordlist;
    }

    public void removeRecord(int RecordID){
        PayRecord temp = recordlist.get(RecordID);
        PayRecord temp2 = pool.get(temp.getTitle());
        temp2.subtract(temp);
        recordlist.remove(RecordID);
        refreshData();
    }

    public String getInfomation(){
        if(recordlist.size() == 0){
            return "";
        }
        // TODO
       return "";

    }

    public String printAdvise(){
        if(recordlist.size() == 0){
            return "";
        }

        Vector vector;
        StringBuilder buffer = new StringBuilder();
        vector = (Vector)recordlist.clone();
        Vector<PayRecord> newrecordlist = new Vector<PayRecord>();
        int vectorpointer = 0;
        PayRecord pointer = null;
        while(vectorpointer < vector.size()){
            pointer = (PayRecord) vector.get(vectorpointer);
            Vector<PayRecord> temp = new Vector<PayRecord>();
            temp.add(pointer);
            int i = 0;
            while(i < vector.size()){//只要i小于Vector的尺寸就继续遍历，这样子即便数组大小变化了也不用理会。
                PayRecord pointer2 = (PayRecord) vector.get(i);
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
                amount += pointer2.getDue();
                payed += pointer2.getExpenses();
            }
            newrecordlist.add(new PayRecord(title,amount,payed,currentCurrency));
            vectorpointer++;
        }

        double v_amount = 0;
        double v_payedamount = 0;
        for (PayRecord temp : newrecordlist) {
            v_amount += temp.getDue();
            v_payedamount += temp.getExpenses();
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
                    buffer.append(String.format("%s\t:\t%d 张\n",currentCurrency.getDenominations()[i],temp_adv[i]));
                }
            }
        }

        return buffer.toString();
    }

    public String printTable(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(String.format("货币名称：%s\n",currentCurrency.getCurrecyName()));
        buffer.append("|付款人\t|已付\t|应付\t|找零\t|负债\t|\n\n");
        for(PayRecord temp : recordlist){
            buffer.append(String.format("|%s\t|%.2f\t|%.2f\t|%.2f\t|%.2f\t|\n",
                    temp.getTitle(),
                    temp.getDue(),
                    temp.getExpenses(),
                    temp.getChange(),
                    temp.getArrears()
            ));
        }
        buffer.append("\n\n");
        return buffer.toString();
    }

    public static void main(String[] args){
        RecordsManager recordsManager = new RecordsManager(new Vector<PayRecord>(),new CNY());

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
            recordsManager.addRecord(title,pay,payed);
        }

        out.print(recordsManager.printTable());
        out.print(recordsManager.printAdvise());
    }
}
