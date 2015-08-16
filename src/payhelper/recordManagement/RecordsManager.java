package payhelper.recordManagement;

import payhelper.currency.CNY;
import payhelper.currency.Currency;

import java.util.*;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Created by catten on 15/8/9.
 */
public class RecordsManager {
//TODO 找零分配还没完成
    private Map<String,PayRecord> pool;
    private Vector<PayRecord> defaulters;
    HashMap<PayRecord,int[]> defaultersWithAdvise;
    private boolean changingWasChecked = false; //列表被更改之后就会立起这个flag
    private int[] cashPool; //零钱池
    private PayRecord sum; //总帐目
    //账单记录列表
    private Vector<PayRecord> recordList;

    private Currency currentCurrency;

    public RecordsManager(Vector<PayRecord> list,Currency currency){
        recordList = list;
        currentCurrency = currency;

        //初始化管理数据用的容器
        defaulters = new Vector<PayRecord>();
        pool = new HashMap<String, PayRecord>();
        cashPool = new int[currentCurrency.getDenominations().length];
        //整理数据
        if(recordList.size() != 0){
            refreshData();
        }
    }//*/

    //把账单数据复制到Map中，相同名称的帐目会被合并。
    private void copyToMap(){
        for(PayRecord record: recordList){
            PayRecord temp = pool.put(record.getTitle(),record);
            if(temp != null){
                pool.get(record.getTitle()).merge(temp);
            }
        }
    }

    //刷新数据，负债人列表、零钱池以及总帐目等
    private void refreshData(){
        //清理旧的数据
        defaulters.clear();
        for (int i = 0; i < cashPool.length; i++) {
            cashPool[i] = 0;
        }
        sum = new PayRecord("SUM",0,0,currentCurrency);
        pool.clear();
        copyToMap();

        //计算新的数据
        for (PayRecord record:pool.values()){
            //负债人
            if(record.isInDebt()){
                //汇总负债人
                defaulters.add(record);
            }
            //零钱池
            int[] changes = record.getChanges();
            for (int i = 0; i < cashPool.length; i++){
                cashPool[i] += changes[i];
            }
        }
        for(PayRecord payRecord : recordList){
            //总帐目
            if (!sum.merge(payRecord)) {
                System.out.println("Error in merging record to SUM");
            }
        }
        shortDefaulters();
        generateAdvise();
        changingWasChecked = true;
    }
    //返还一个总帐目的副本
    public PayRecord getTotalAccount(){
        if(!changingWasChecked){
            refreshData();
        }
        return sum.clone();
    }

    //按照负债数额逆序排序负债人列表
    private void shortDefaulters(){
        if(defaulters.size() <= 0){
            return;
        }
        Vector<PayRecord> result = new Vector<PayRecord>();
        while (defaulters.size() > 0){
            PayRecord bigDefaulter = defaulters.get(0);
            for (int i = 0; i < defaulters.size(); i++) {
                if (bigDefaulter.getArrears() < defaulters.get(i).getArrears()){
                    bigDefaulter = defaulters.get(i);
                }
            }
            result.add(bigDefaulter);
            defaulters.remove(bigDefaulter);
        }
        defaulters = result;
    }

    public HashMap<PayRecord,int[]> getDefaulters(){
        if(!changingWasChecked){
            refreshData();
        }
        return defaultersWithAdvise;
    }

    //收款人返还的找零组合
    public int[] getChanges(){
        if(!changingWasChecked){
            refreshData();
        }
        return cashPool;
    }

    private void generateAdvise(){
        if(defaulters.size() <= 0){
            defaultersWithAdvise = new HashMap<PayRecord, int[]>();
        }
        HashMap<PayRecord,int[]> result = new HashMap<PayRecord, int[]>();
        _distributing(cashPool,sum.getChange());
        for(PayRecord record : defaulters){
            result.put(record,_distributing(cashPool,record.getChange()));
        }
        defaultersWithAdvise = result;
    }

    //从零钱池中分配零钱
    private int[] _distributing(int[] thePool,double amount){
        int theAmount = (int)(amount * 100);
        int[] result = new int[thePool.length];
        for (int i = 0; i < thePool.length; i++) {
            if(thePool[i] == 0 || amount <(int)(currentCurrency.getDenominations()[i] * 100)){
                continue;
            }
            int count = theAmount / (int)(currentCurrency.getDenominations()[i] * 100);
            if(count != 0){
                result[i] = count;
                thePool[i] -= count;
                theAmount -= count * (int)(currentCurrency.getDenominations()[i] * 100);
            }
        }
        return result;
    }

    public Currency getCurrentCurrency(){
        return currentCurrency;
    }

    public void addRecord(String title,double Due,double Expenses){
        //在受托管的列表上添加记录
        recordList.add(new PayRecord(title,Due,Expenses,currentCurrency));
        //列表已改变，所以标记为未刷新
        changingWasChecked = false;
    }

    public Vector<PayRecord> getRecordList(){
        return recordList;
    }

    public void removeRecord(int RecordID){
        recordList.remove(RecordID);
        changingWasChecked = false;
    }

    @Deprecated
    public String printAdvise(){
        if(recordList.size() == 0){
            return "";
        }

        Vector vector;
        StringBuilder buffer = new StringBuilder();
        vector = (Vector) recordList.clone();
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
        buffer.append(String.format("货币名称：%s\n",currentCurrency.getName()));
        buffer.append("|付款人\t|已付\t|应付\t|找零\t|负债\t|\n\n");
        for(PayRecord temp : recordList){
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

        Random random = new Random(new Date().getTime());
        String title;

        double pay;
        double payed;
        for (int i = 0; i < 10; i++) {
            pay = (int)(random.nextFloat() * 100);
            payed = (int)(random.nextFloat() * 100);
            title = String.format("Acc. %d",i);
            recordsManager.addRecord(title,pay,payed);
        }

        out.print(recordsManager.printTable());
        out.printf("Total Paied:%.2f\n", recordsManager.getTotalAccount().getExpenses());
        out.printf("Total Due  :%.2f\n",recordsManager.getTotalAccount().getDue());
        if(recordsManager.getTotalAccount().isInDebt()){
            out.printf("Total Arrear:%.2f\n",Math.abs(recordsManager.getTotalAccount().getBalance()));
        }else {
            out.printf("Return Change:%.2f\n",recordsManager.getTotalAccount().getBalance());
        }
        //out.print(recordsManager.printAdvise());
        int[] cashPool = new int[recordsManager.getCurrentCurrency().getDenominations().length];
        int[] ArrearPool = new int[cashPool.length];
        StringBuilder buffer = new StringBuilder();
        Vector<PayRecord> vector = recordsManager.getRecordList();
        for(PayRecord payRecord : vector){
            if(payRecord.isInDebt()){
                for (int i = 0; i < ArrearPool.length; i++) {
                    ArrearPool[i] += payRecord.getCombination()[i];
                }
                //buffer.append(payRecord.getTitle() + "\t");
            }else{
                for (int i = 0; i < cashPool.length; i++) {
                    cashPool[i] += payRecord.getCombination()[i];
                }
            }
        }
        out.print("Debt List:\n");
        Map<PayRecord,int[]> temp = recordsManager.getDefaulters();
        for (PayRecord record : temp.keySet()){
            buffer.append(String.format("%s\n",record.getTitle()));
            int[] tempchanges = temp.get(record);
            for (int i = 0; i < record.getCurrency().getDenominations().length; i++) {
                if(tempchanges[i] != 0){
                    buffer.append(String.format(
                                    "%s\t:\t%d\n",
                                    record.getCurrency().getDenominationNames()[i],
                                    tempchanges[i])
                    );
                }
            }
        }
        out.print(buffer.toString());
        System.out.print("cashPool:\n");
        for (int i = 0; i < cashPool.length; i++) {
            if(cashPool[i] != 0){
                System.out.printf(
                        "%s\t:\t%d\n",
                        recordsManager.getCurrentCurrency().getDenominationNames()[i],
                        cashPool[i]
                );
            }
        }
        if(recordsManager.getTotalAccount().getBalance() > 0){
            System.out.printf("Receiver Return:\n");
            for (int i = 0; i < cashPool.length; i++) {
                if(recordsManager.getTotalAccount().getCombination()[i] != 0){
                    System.out.printf(
                            "%s\t:\t%d\n",
                            recordsManager.getCurrentCurrency().getDenominationNames()[i],
                            recordsManager.getTotalAccount().getCombination()[i]
                    );
                }
            }
        }
        if(recordsManager.getTotalAccount().getBalance() < 0){
            System.out.printf("ArrearPool:\n");
            for(int i = 0; i < ArrearPool.length; i++){
                if(ArrearPool[i] != 0){
                    System.out.printf(
                            "%s\t:\t%d\n",
                            recordsManager.getCurrentCurrency().getDenominationNames()[i],
                            ArrearPool[i]
                    );
                }
            }
        }
    }
}
