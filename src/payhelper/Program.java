package payhelper;

import javax.swing.*;

/**
 * Created by catten on 15/8/9.
 */
public class Program {
    public static void main(String[] args){
        //主程序
        if(args.length == 0){
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
}
