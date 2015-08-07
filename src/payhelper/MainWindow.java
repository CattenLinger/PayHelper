package payhelper;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

import static java.lang.System.out;

/**
 * Created by Kongs on 2015/7/14.
 */

public class MainWindow extends JFrame implements ActionListener {
    //存放付款记录的Vector
    static Vector<PayRecord> payRecords = new Vector<PayRecord>();
    //用来生成随机数
    static Random random = new Random(new Date().getTime());
    //表格的表头
    String[] columeName = new String[]{"付款人","已付","应付","找零","负债"};
    //随机名字列表，以后可能会再增加一些_(:з」∠)_
    String[] randomNames = (
            new String(
                    "张三 李四 王五 小明 小红 小刚 大雄 多啦A梦 小夫 胖虎 静香 杉木 鹿目圆香 晓美焰 御坂美琴 黑子 星河昴 兔子 柯南 兰"
            ).split(" ")
    );
    //声明控件
    JButton btn_random,btn_submit,btn_delete,btn_save;
    JTable tabel;
    JTextArea results;
    DefaultTableModel defaultTableModel;
    JTextField t_name,t_payed,t_amount;

    //构造函数
    public MainWindow(String title){
        super(title);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setResizable(false);
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
        gbc.weightx = 0.8;
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
        t_payed = new JTextField();
        add(t_payed,gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(new JLabel("应付:",JLabel.RIGHT),gbc);

        gbc.gridx++;
        gbc.gridwidth = 2;
        t_amount = new JTextField();
        add(t_amount,gbc);

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
        t_amount.setText("");
        t_payed.setText("");
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btn_submit){
            //检测文本框输入是否符合要求
            boolean flag = false;
            if (t_name.getText().equals("")||t_amount.getText().equals("")||t_payed.getText().equals("")){
                flag = true;
            }else{
                if (!isNumeric(t_amount.getText())){
                    flag = true;
                }
                if (!isNumeric(t_payed.getText())){
                    flag = true;
                }
            }
            if(flag){
                JOptionPane.showMessageDialog(this,"输入有误：\n1.可能没有填写付款人\n2.金额字段不是数字\n","错误",JOptionPane.WARNING_MESSAGE);
            }else {
                payRecords.add(new PayRecord(
                        t_name.getText(),
                        Double.parseDouble(t_amount.getText()),
                        Double.parseDouble(t_payed.getText()))
                );
                defaultTableModel.addRow(
                        new String[]{
                                payRecords.lastElement().getTitle(),
                                ((Double) payRecords.lastElement().getPayed()).toString(),
                                ((Double) payRecords.lastElement().getPay()).toString(),
                                ((Double) payRecords.lastElement().getChange()).toString(),
                                ((Double) payRecords.lastElement().getDebt()).toString()
                        }
                );
                clear_textboxs();
                t_name.requestFocus();
                results.setText(PayRecord.printAdvise(payRecords));
            }
        }else if(e.getSource() == btn_delete){
            //删除表格里选中的项目
            while(tabel.getSelectedRows().length != 0){
                payRecords.remove(tabel.getSelectedRow());
                defaultTableModel.removeRow(tabel.getSelectedRow());
                results.setText(PayRecord.printAdvise(payRecords));
            }
        }else if(e.getSource() == btn_random){
            //随机产生名字并填入付款人字段里
            t_name.setText(randomNames[(int)(random.nextDouble() * randomNames.length)]);
        }else if(e.getSource() == btn_save){
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
                    fileWriter.write(PayRecord.printTable(payRecords));
                    fileWriter.write(PayRecord.printAdvise(payRecords));
                    fileWriter.close();
                    JOptionPane.showMessageDialog(this,"保存成功。路径：\n" + savefile.getPath(),"保存",JOptionPane.INFORMATION_MESSAGE);
                }catch (IOException error){
                    JOptionPane.showMessageDialog(this,"无法保存文件，请检查所选位置是否妥当、文件名是否符合规范以及您对所选路径的访问权限。","保存错误",JOptionPane.ERROR_MESSAGE);
                    error.printStackTrace();
                }
            }
        }

        if(payRecords.size() == 0){
            btn_save.setEnabled(false);
        }else {
            btn_save.setEnabled(true);
        }
    }

    public static void main(String[] args){
        //主程序
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.print("Using " + UIManager.getSystemLookAndFeelClassName() + " as look and feel.\n");
        }catch (Exception e){
            System.out.print("Can't get system look and feel.\n");
        }
        MainWindow mainWindow = new MainWindow("找零酱罐子");
        mainWindow.setVisible(true);
    }
}
