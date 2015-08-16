package payhelper.currency;

/**
 * Created by catten on 15/8/9.
 */
public class CNY implements Currency{
    String[] s_denominations = new String[]{"50","20","10","5","1","0.5","0.2","0.1"};
    double[] denominations = new double[]{50,20,10,5,1,0.5,0.2,0.1};

    @Override
    public String[] getDenominationNames() {
        return s_denominations;
    }

    @Override
    public double[] getDenominations() {
        return denominations;
    }

    @Override
    public String getName() {
        return "CNY";
    }

    @Override
    public boolean equals(Currency currency) {
        if(currency.getName().equals(this.getName())){
            return true;
        }
        return false;
    }

    public CNY(){
        //
    }
}
