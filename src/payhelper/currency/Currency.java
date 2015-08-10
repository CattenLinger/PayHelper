package payhelper.currency;

/**
 * Created by catten on 15/8/9.
 */
public interface Currency {
    String[] getDenominationNames();
    double[] getDenominations();
    String getCurrencyName();
    boolean equals(Currency currency);
}
