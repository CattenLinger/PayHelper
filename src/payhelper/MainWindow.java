package payhelper;

import payhelper.currency.CNY;
import payhelper.database.RandomName;
import payhelper.recordManagement.PayRecord;
import payhelper.recordManagement.RecordsManager;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Kongs on 2015/7/14.
 */

public class MainWindow extends JFrame implements ActionListener {
    //存放付款记录的Vector
    //static Vector<PayRecord> payRecords = new Vector<PayRecord>();
    static RecordsManager recordsManager = new RecordsManager(new Vector<PayRecord>(),new CNY());
    //用来生成随机数
    static Random random = new Random(new Date().getTime());
    //表格的表头
    String[] columeName = new String[]{"付款人","已付","应付","找零","负债"};
    //随机名字列表，以后可能会再增加一些_(:з」∠)_
    RandomName randomNames = new RandomName();
    /*
    String[] randomNames =
            "张三 李四 王五 小明 小红 小刚 大雄 多啦A梦 小夫 胖虎 静香 杉木 鹿目圆香 晓美焰 御坂美琴 黑子 星河昴 兔子 柯南 兰 爱丽"
                    .split(" ");
    */
    //声明控件
    JButton btn_random,btn_submit,btn_delete,btn_save;
    JTable tabel;
    JTextArea results;
    DefaultTableModel defaultTableModel;
    JTextField t_name, t_expenses,t_due;

    //构造函数
    public MainWindow(String title){
        super(title);
        //setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600);
        //setResizable(false);

        setUpUIComponents();
        setUpEventListener();
    }

    private void setUpUIComponents() {
        //控件与布局
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3,3,3,3);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 7;
        gbc.gridwidth = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 1;
        defaultTableModel = new DefaultTableModel(null,columeName);
        tabel = new JTable(defaultTableModel);
        add(new JScrollPane(tabel),gbc);

        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(new JLabel("付款人:",JLabel.RIGHT),gbc);

        gbc.gridx++;
        gbc.weightx = 0.2;
        t_name = new JTextField();
        add(t_name,gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        btn_random = new JButton("随机");
        add(btn_random,gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        add(new JLabel("已付:",JLabel.RIGHT),gbc);

        gbc.gridx++;
        gbc.gridwidth = 2;
        t_expenses = new JTextField();
        add(t_expenses,gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(new JLabel("应付:",JLabel.RIGHT),gbc);

        gbc.gridx++;
        gbc.gridwidth = 2;
        t_due = new JTextField();
        add(t_due,gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btn_submit = new JButton("添加");
        add(btn_submit,gbc);

        gbc.gridx += 2;
        gbc.gridwidth = 1;
        btn_delete = new JButton("删除");
        add(btn_delete,gbc);

        gbc.gridx = 1;
        gbc.gridy ++;
        gbc.gridwidth = 3;
        add(new JLabel("结果",JLabel.CENTER),gbc);

        gbc.gridy++;
        gbc.weighty = 0.5;
        gbc.weightx = 0.4;
        results = new JTextArea();
        results.setEditable(false);
        add(new JScrollPane(results), gbc);

        gbc.gridy++;
        gbc.weighty = 0;
        btn_save = new JButton("保存");
        add(btn_save,gbc);
        btn_save.setEnabled(false);
    }

    private void setUpEventListener() {
        //事件监听器设置
        btn_delete.addActionListener(this);
        btn_random.addActionListener(this);
        btn_submit.addActionListener(this);
        btn_save.addActionListener(this);
    }

    private void clear_textboxs(){
        //用于清除文本框
        t_due.setText("");
        t_expenses.setText("");
        t_name.setText("");
    }

    private boolean isNumeric(String str){
        //判断字符串是否为数字
        try{
            byte flag = 0;
            for (int i = 0; i < str.length(); i++){
                //System.out.println(str.charAt(i));
                if (!Character.isDigit(str.charAt(i))){
                    if(str.charAt(i) == '.'){
                        flag++;
                    }else {
                        return false;
                    }
                }
            }
            if(flag > 1){
                return false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void outputResults(){
        PayRecord totalAccount = recordsManager.getTotalAccount();
        StringBuilder temp1 = new StringBuilder();
        temp1.append(String.format("当前货币：%s\n",totalAccount.getCurrency().getName()));
        temp1.append(String.format(
                        "总金额：%.2f\n已付：%.2f\n",
                        totalAccount.getDue(),
                        totalAccount.getExpenses())
        );
        if(totalAccount.isInDebt()){
            temp1.append(String.format("欠款：%.2f\n",Math.abs(totalAccount.getBalance())));
        }else{
            temp1.append(String.format("找零：%.2f\n",totalAccount.getBalance()));
            temp1.append("收款人找零：\n");
            int[] changes = totalAccount.getCombination();
            for (int i = 0; i < changes.length; i++){
                if(changes[i] != 0){
                    temp1.append(
                            String.format("%s\t:\t%d\n",totalAccount.getCurrency().getDenominationNames()[i],changes[i])
                    );
                }
            }
        }
        //TODO 更多的输出内容
        if(recordsManager.getDefaulters().size() > 0){
            temp1.append("负债人:\n");
            for (PayRecord payRecord : recordsManager.getDefaulters().keySet()){
                temp1.append(payRecord.getTitle() + "\n\t");
                int[] changes = recordsManager.getDefaulters().get(payRecord);
                for(int i = 0; i < changes.length; i++){
                    if(changes[i] != 0){
                        temp1.append(String.format("%s\t:\t%d\n",payRecord.getCurrency().getDenominationNames()[i],changes[i]));
                    }
                }
            }
        }
        results.setText(temp1.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btn_submit){
            //检测文本框输入是否符合要求
            boolean flag = false;
            if (t_name.getText().equals("")||t_due.getText().equals("")||t_expenses.getText().equals("")){
                flag = true;
            }else{
                if (!isNumeric(t_due.getText())){
                    flag = true;
                }
                if (!isNumeric(t_expenses.getText())){
                    flag = true;
                }
            }
            if(flag){
                JOptionPane.showMessageDialog(this,"输入有误：\n1.可能没有填写付款人\n2.金额字段不是数字\n","错误",JOptionPane.WARNING_MESSAGE);
            }else {
                recordsManager.addRecord(
                        t_name.getText(),
                        Double.parseDouble(t_due.getText()),
                        Double.parseDouble(t_expenses.getText())
                );
                PayRecord payRecord = recordsManager.getRecordList().lastElement();
                defaultTableModel.addRow(
                        new String[]{
                                payRecord.getTitle(),
                                ((Double) payRecord.getExpenses()).toString(),
                                ((Double) payRecord.getDue()).toString(),
                                ((Double) payRecord.getChange()).toString(),
                                ((Double) payRecord.getArrears()).toString()
                        }
                );
                clear_textboxs();
                t_name.requestFocus();
                outputResults();
            }
        }else if(e.getSource() == btn_delete){
            //删除表格里选中的项目
            while(tabel.getSelectedRows().length != 0){
                recordsManager.removeRecord(tabel.getSelectedRow());
                defaultTableModel.removeRow(tabel.getSelectedRow());
                outputResults();
            }
        }else if(e.getSource() == btn_random){
            //随机产生名字并填入付款人字段里
            t_name.setText(randomNames.nextName());
        }else if(e.getSource() == btn_save){
            //保存列表到文本文件
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setMultiSelectionEnabled(false);
            jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            jFileChooser.setDialogTitle("保存记录");
            jFileChooser.setFileFilter(new FileNameExtensionFilter("文本文档","*.txt"));
            File savefile;
            jFileChooser.setVisible(true);
            if(jFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                try{
                    savefile = jFileChooser.getSelectedFile();
                    FileWriter fileWriter = new FileWriter(savefile);
                    fileWriter.write(recordsManager.printTable());
                    fileWriter.write(recordsManager.printAdvise());
                    fileWriter.close();
                    JOptionPane.showMessageDialog(
                            this,
                            "保存成功。路径：\n" + savefile.getPath(),
                            "保存",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }catch (IOException error){
                    JOptionPane.showMessageDialog(
                            this,
                            "无法保存文件，请检查所选位置是否妥当、文件名是否符合规范以及您对所选路径的访问权限。",
                            "保存错误",
                            JOptionPane.ERROR_MESSAGE
                    );
                    error.printStackTrace();
                }
            }
        }
        //
        if(recordsManager.getRecordList().size() == 0){
            btn_save.setEnabled(false);
        }else {
            btn_save.setEnabled(true);
        }
    }
}
