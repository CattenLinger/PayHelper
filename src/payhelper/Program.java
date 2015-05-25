package payhelper;

import static java.lang.System.out;
import static java.lang.System.in;

import java.util.ArrayList;
import java.util.Scanner;

public class Program{
	public static void main(String[] args){
		ArrayList<PayRecord> listPayRecord = new ArrayList<PayRecord>();
		int[] temp_adv = new int[]{0,0,0,0,0,0,0,0,0};
		boolean canDiv = true;

		String title;
		float pay;
		float payed;
		Scanner scanner = new Scanner(in);
		int j = 256;
		out.printf("Please input records follow tips.\nFill\"quit\" finish inputting.\n");
		while(j-- != 0){
			out.print("Please input name: ");
			title = scanner.next();
			if(title.equals("quit")) break;
			out.print("Amount:");
			pay = scanner.nextFloat();
			out.print("Payed:");
			payed = scanner.nextFloat();
			listPayRecord.add(new PayRecord(title,pay,payed));//*/
		}

		out.print("Information:\n|Title\t|Amount\t|Payed\t|Change\t|Debt\t|\n");
		float sum = 0;
		float sum_pay = 0;
		for(PayRecord temp : listPayRecord){
			out.printf("|%s\t|%2.2f\t|%2.2f\t|%2.2f\t|%2.2f\t|\n",
						temp.getTitle(),
						temp.getPay(),
						temp.getPayed(),
						temp.getChange(),
						temp.getDebt()
			);
			sum += temp.getPayed();
			sum_pay += temp.getPay();
		}
		out.printf("Total payed:%.2f\tChange:%.2f\n", sum, sum - sum_pay);
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