package Temele.L13;

import java.io.Serializable;
import java.util.ArrayList;

public class Card implements Serializable {
    private static final long serialVersionUID = -2211377966283268100L;
    private final String nume;
    private int cod;
    private float bani;
    private ArrayList<String> transactionHistory = new ArrayList<>();

    public Card(String nume, int cod, int bani) {
        this.nume = nume;
        this.cod = Math.abs(cod);
        this.bani = bani;
    }

    public String getNume() {
        return nume;
    }

    public Boolean checkPin(int pin) {
        if (pin == cod)
            return true;
        else
            return false;
    }

    public float getBani() {
        return bani;
    }

    public String[] getTransactionHistory() {
        String[] transactions = new String[transactionHistory.size()];
        for (int i = 0; i < transactionHistory.size(); i++) {
            transactions[i] = transactionHistory.get(i);
        }
        return transactions;

    }

    public void appendTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public float modifyBani(float scadere, float adunare) {
        bani -= (scadere - adunare);
        return bani;
    }

    public int pinLenght() {
        return ("" + cod).length();
    }

    public void changePassword(int oldpin, int newpin, int newpin2) {
        if (cod == oldpin) {
            if (newpin == newpin2) {
                cod = newpin;
                System.out.println("Changed passwords!");
            } else {
                System.out.println("New passwords don't match!");
            }
        } else {
            System.out.println("The old pin is wrong!");
        }
    }
}
