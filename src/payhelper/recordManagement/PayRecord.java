package payhelper.recordManagement;

import payhelper.currency.CNY;
import payhelper.currency.Currency;

import java.util.Scanner;

/**
 * Created by catten on 15/4/21.
 */

public class PayRecord{

    private String title;
    private double due;
    private double expenses;
    private Currency currency;

    private int[] changes;// = new int[9];//50 20 10 5 2 1 0.5 0.2 0.1
    private boolean dividable;
    private int[] combination;
    private boolean leakedCombination;
    //private static String[] value_of_changes = new String[]{"50","20","10","5","2","1","0.5","0.2","0.1"};

    @Deprecated // TODO 清理过时方法
    private boolean _countChanges(){
        //double和float误差烦人，所以这里的计算先全部乘上10
        int temp = (int)(getChange() * 10);
        double[] denominations = currency.getDenominations();
        for(int i = 0; i < denominations.length ; i++){
            changes[i] = (int)(temp / (denominations[i] * 10));
            temp -= changes[i] * (denominations[i] * 10);
        }
        //dividable = temp == 0;
        return temp == 0;
    }

    private boolean generateCombination(){
        int temp = (int)(Math.abs(getBalance()) * 10);
        double[] denominations = currency.getDenominations();
        for(int i = 0; i < denominations.length ; i++){
            combination[i] = (int)(temp / (denominations[i] * 10));
            temp -= combination[i] * (denominations[i] * 10);
        }
        //dividable = temp == 0;
        return temp == 0;
    }
    //能否完全找零
    public boolean isDividable(){
        return dividable;
    }
    //获取当前记录的货币的所有面值
    public String[] getDenominations() {
        return currency.getDenominationNames();
    }
    //获取针对这个记录的找零方案
    @Deprecated // TODO 清理过时方法
    public int[] getChanges() {
        return changes;
    }
    //获取应付
    public double getDue() {
        return due;
    }
    //获取实付
    public double getExpenses() {
        return expenses;
    }
    //获取付款人
    public String getTitle() {
        return title;
    }

    public PayRecord(String Title,double Due,double expenses,Currency currency){
        title = Title;
        due = Double.parseDouble(String.format("%.2f",Due));
        this.expenses = Double.parseDouble(String.format("%.2f", expenses));
        this.currency = currency;
        changes = new int[currency.getDenominations().length];
        dividable = _countChanges();
        combination = new int[currency.getDenominations().length];
        leakedCombination = generateCombination();
    }

    //获取找零
    @Deprecated // TODO 清理过时方法
    public double getChange(){

        return expenses - due > 0?Double.parseDouble(String.format("%.2f", expenses - due)):0;
    }
    //获取负债
    @Deprecated // TODO 清理过时方法
    public double getArrears(){
        return due - expenses > 0?Double.parseDouble(String.format("%.2f", due - expenses)):0;
    }

    //获取余额
    public double getBalance(){
        return  expenses - due;
    }
    //获取纸币组合
    public int[] getCombination(){
        return combination;
    }

    public boolean isLeakedCombination(){
        return leakedCombination;
    }

    //是否负债
    public boolean isInDebt(){
        return getBalance() < 0;
    }

    public Currency getCurrency(){
        return currency;
    }

    public boolean merge(PayRecord record){
        if(!record.getCurrency().equals(currency)){
            return false;
        }
        due += record.getDue();
        expenses += record.getExpenses();
        dividable = _countChanges();
        leakedCombination = generateCombination();
        return true;
    }

    public boolean subtract(PayRecord record){
        if(!record.getCurrency().equals(currency) || !record.getTitle().equals(title)) {
            return false;
        }
        due -= record.getDue();
        expenses -= record.getExpenses();
        dividable = _countChanges();
        leakedCombination = generateCombination();
        return true;
    }

    public String toString(){
        return String.format("Title:%s;Pay:%.2f;Payed:%.2f;Currency:%s;", title, due, expenses, currency.getName());
    }

    public PayRecord clone(){
        return new PayRecord(title,due,expenses,currency);
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        String title;
        double due;
        double expenses;

        System.out.printf("Please input title:");
        title = scanner.next();
        System.out.printf("Please input due:");
        due = scanner.nextDouble();
        System.out.printf("Please input expenses:");
        expenses = scanner.nextDouble();
        PayRecord payRecord = new PayRecord(title,due,expenses,new CNY());

        System.out.printf("payRecord.toString()\n%s\n", payRecord.toString());
        System.out.printf("payRecord.getBalance()\n%.2f\n", payRecord.getBalance());
        if(payRecord.getBalance() < 0){
            System.out.printf("This account is in debt.\n");
        }
        System.out.printf("payRecord.getCombination():\n");
        for (int i = 0; i < payRecord.getCombination().length;i++){
            if(payRecord.getCombination()[i] != 0){
                System.out.printf(
                        "%sx%d\n",
                        payRecord.getCurrency().getDenominationNames()[i],
                        payRecord.getCombination()[i]
                );
            }
        }
    }
}