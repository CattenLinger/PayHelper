package payhelper;

import payhelper.currency.CNY;
import payhelper.currency.Currency;

import java.util.Scanner;
import java.util.Vector;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Created by catten on 15/4/21.
 */

public class PayRecord{

    private String title;
    private double due;
    private double expenses;
    private boolean dividable;
    private Currency currency;

    private int[] changes;// = new int[9];//50 20 10 5 2 1 0.5 0.2 0.1
    //private static String[] value_of_changes = new String[]{"50","20","10","5","2","1","0.5","0.2","0.1"};

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
    //能否完全找零
    public boolean isDividable(){
        return dividable;
    }
    //获取当前记录的货币的所有面值
    public String[] getDenominations() {
        return currency.getDenominationNames();
    }
    //获取针对这个记录的找零方案
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
    }
    //获取找零
    public double getChange(){
        return expenses - due > 0?Double.parseDouble(String.format("%.2f", expenses - due)):0;
    }
    //获取负债
    public double getArrears(){
        return due - expenses > 0?Double.parseDouble(String.format("%.2f", due - expenses)):0;
    }
    //是否负债
    public boolean isInDebt(){
        return getArrears() > 0;
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
        return true;
    }

    public boolean subtract(PayRecord record){
        if(!record.getCurrency().equals(currency) || !record.getTitle().equals(title)) {
            return false;
        }
        due -= record.getDue();
        expenses -= record.getExpenses();
        dividable = _countChanges();
        return true;
    }

    public String toString(){
        return String.format("Title:%s\nPay:%.2f\nPayed:%.2f", title, due, expenses);
    }
}