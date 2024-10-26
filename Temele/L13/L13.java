package Temele.L13;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarculaLaf;

public class L13 {
    public static int cardLogIn = 0;
    static ArrayList<Card> inventoryCards = new ArrayList<>();

    public L13(int cardLogIn) {
        L13.cardLogIn = cardLogIn;
    }

    public static void main(String[] args) {
        // Create the first frame for login
        // inventoryCards.add(new Card("DEFAULTACCOUNT", 229291032, 0));
        loadCardData();
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        JFrame frame = new JFrame();

        Card card = inventoryCards.get(cardLogIn);
        ATM atm = new ATM(card);
        frame.setSize(300, 300);
        frame.add(atm.getLoginUI());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        while (true) {
            if (atm.donePass) {
                frame.setVisible(false);
                frame.dispose();
                break;
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Create a new frame for the ATM UI
        atm.getUI(args);
    }

    public static HashMap<Integer, String> getInventory() {
        HashMap<Integer, String> hm = new HashMap<>();
        int i = 0;
        for (Card cardInInv : inventoryCards) {
            hm.put(i, cardInInv.getNume());
            i++;
        }
        return hm;
    }

    public static Card getCard(int index) {
        int i = 0;
        for (Card cardInInv : inventoryCards) {
            if (i == index) {
                return cardInInv;
            }
            i++;
        }
        return null;
    }

    public static void saveCardData() {
        try {
            FileOutputStream fileOut = new FileOutputStream(
                    System.getProperty("user.dir") + "/cardData.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            System.out.println("Saving data...");
            out.writeObject((Integer) inventoryCards.size());
            for (Card obj : inventoryCards) {
                out.writeObject(obj);
            }
            out.close();
            fileOut.close();
            System.out.println("Data saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCardData() {
        try {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            FileInputStream fileIn = new FileInputStream(System.getProperty("user.dir") + "/cardData.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Integer size = (Integer) in.readObject();
            inventoryCards.clear();
            for (int i = 0; i < size; i++) {
                Card obj = (Card) in.readObject();
                inventoryCards.add(obj);

            }
            in.close();
            fileIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
