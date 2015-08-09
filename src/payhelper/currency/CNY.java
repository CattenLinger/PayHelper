package payhelper.currency;

/**
 * Created by catten on 15/8/9.
 */
public class CNY implements Currency{
    String[] s_denominations;
    double[] denominations = new double[]{50,20,10,5,2,1,0.5,0.2,0.1};

    @Override
    public String[] getDenominationNames() {
        return s_denominations;
    }

    @Override
    public double[] getDenominations() {
        return denominations;
    }

    @Override
    public String getCurrecyName() {
        return "CNY";
    }

    @Override
    public boolean equals(Currency currency) {
        if(currency.getCurrecyName().equals(this.getCurrecyName())){
            return true;
        }
        return false;
    }

    public CNY(){
        s_denominations = new String[denominations.length];
        for(int i = 0; i < s_denominations.length;i++){
            s_denominations[i] = String.valueOf(denominations[i]);
        }
    }
}
