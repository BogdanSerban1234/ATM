package Temele.L13;

import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

public class ATM {
    boolean stopDisplay = false;
    Card Card;
    boolean PinCorect = false;
    public boolean donePass = false;
    PopupFactory popupFactory = new PopupFactory();
    public Popup popup = null;

    public ATM(Card card) {
        Card = card;
    }

    /*
     * void load(Card card) {
     * Card = card;
     * if (Card != null) {
     * int code = Integer.parseInt(JOptionPane.showInputDialog(null,
     * "Introduce cod:", null));
     * if (Card.checkPin(code)) {
     * JOptionPane.showMessageDialog(null, "Bun venit " + Card.getNume() + "!");
     * PinCorect = true;
     * } else {
     * JOptionPane.showMessageDialog(null, "Cod Gresit!");
     * }
     * } else {
     * JOptionPane.showMessageDialog(null, "Introduceti cardul prima data!");
     * }
     * }
     * 
     * Card eject() {
     * if (Card != null) {
     * if (PinCorect) {
     * JOptionPane.showMessageDialog(null, "La revedere " + Card.getNume() + "!");
     * Card oldCard = Card;
     * Card = null;
     * return oldCard;
     * } else {
     * JOptionPane.showMessageDialog(null, "Prima data trebuie sa va logati.");
     * load(Card);
     * return Card;
     * }
     * } else {
     * JOptionPane.showMessageDialog(null, "Introduceti cardul prima data!");
     * return null;
     * }
     * }
     * LEGACY CODE
     */
    void deposit(float bani) {
        if (Card != null) {
            if (donePass) {
                Card.modifyBani(0, Math.abs(bani));
                JOptionPane.showMessageDialog(null, "Succes de depozitare!");
            } else {
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Introduceti cardul prima data!");
        }
    }

    void withdraw(int bani) {
        if (Card != null) {
            if (donePass) {
                if ((Card.getBani() - Math.abs(bani)) < 0) {
                    JOptionPane.showMessageDialog(null, "Nu sunt destui bani pe card!");
                } else {
                    Card.modifyBani(Math.abs(bani), 0);
                    JOptionPane.showMessageDialog(null, "Succes de luare de bani!");
                }
            } else {
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Introduceti cardul prima data!");
        }
    }

    Card transfer(Card transferLaCard, int bani) {
        if (transferLaCard != null) {
            if (Card != null) {
                if (donePass) {
                    if ((Card.getBani() - Math.abs(bani)) < 0) {
                        JOptionPane.showMessageDialog(null, "Nu sunt destui bani pe card!");
                        return transferLaCard;
                    } else {
                        Card.modifyBani(Math.abs(bani), 0);
                        transferLaCard.modifyBani(0, Math.abs(bani));
                        JOptionPane.showMessageDialog(null, "Succes de transfer!");
                        addTransaction(Card,
                                "Ai trimis $" + Math.abs(bani) + " la contul " + transferLaCard.getNume());
                        addTransaction(transferLaCard,
                                "Ai primit $" + Math.abs(bani) + " de la contul " + Card.getNume());
                        L13.saveCardData();
                        return transferLaCard;
                    }
                } else {

                    System.exit(0);
                    return transferLaCard;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Introduceti cardul prima data!");
                return transferLaCard;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Introduceti un card valid pentru transfer!");
            return transferLaCard;
        }
    }

    void afiseaza() {
        if (donePass) {
            String cardInfo = "Numele tau: " + Card.getNume() + "\nPin-ul tau: " +
                    "*".repeat(Card.pinLenght()) + "\nSuma de bani: " + Card.getBani();
            JOptionPane.showMessageDialog(null, cardInfo);
        } else {
            System.exit(0);
        }
    }

    JPanel getLoginUI() {

        JPanel panelToReturn = new JPanel();
        panelToReturn.setLayout(new GridLayout(0, 1));
        // Make the card Labels
        JPanel cardLabels = new JPanel(new GridLayout(1, 2));

        JLabel cardLabelText = new JLabel();
        cardLabelText.setText("Inserted card: ");
        JLabel cardLabel = new JLabel();
        cardLabel.setText(Card.getNume());

        cardLabels.add(cardLabelText);
        cardLabels.add(cardLabel);

        panelToReturn.add(cardLabels);

        // Adding a space
        panelToReturn.add(new JLabel(""));
        // Making the button to log in
        JButton logInButton = new JButton("Log In");

        logInButton.addActionListener(e -> {
            int password;
            do {
                try {
                    password = Integer.parseInt(JOptionPane.showInputDialog(panelToReturn,
                            "Insert password", null));
                } catch (Exception exc) {
                    password = -1;
                }

            } while (Card.checkPin(password) == false);
            if (Card.checkPin(password)) {
                donePass = true;
            }
        });
        panelToReturn.add(logInButton);

        // Create card button
        JButton createCard = new JButton("Creaza un nou card");
        createCard.addActionListener(e -> {
            try {
                L13.inventoryCards.add(new Card(JOptionPane.showInputDialog(null, "Numele noului card"),
                        Integer.parseInt(JOptionPane.showInputDialog(null, "Parola noului card")), 0));
                L13.saveCardData();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        });
        panelToReturn.add(createCard);
        return panelToReturn;
    }

    public JFrame getUI(String[] args) {
        // Create the main menu
        JFrame frame2 = new JFrame();
        JPanel transactionHistory = new JPanel();
        // Load Card
        JMenu menu = new JMenu("Optiuni Card");
        JMenuBar menuBar = new JMenuBar();
        // Setting up transaction history

        historyOfTransactionsSetUp(frame2, transactionHistory, Card);

        // Eject Card
        JMenuItem logIn = new JMenuItem("Log In To Other Card");
        logIn.addActionListener(e -> {
            frame2.remove(transactionHistory);
            HashMap<Integer, String> receieverCards = L13.getInventory();
            JPanel frame = new JPanel();
            for (HashMap.Entry<Integer, String> entry : receieverCards.entrySet()) {
                Integer id = entry.getKey();
                String name = entry.getValue();

                // Create a button with the String as its label
                JButton button = new JButton(name);

                // Add action listener to handle the button click
                button.addActionListener(e2 -> {

                    L13.cardLogIn = id;
                    new Thread(() -> L13.main(args)).start();
                    frame.remove(button);
                    frame2.dispose();
                });

                // Add the button to the frame
                frame.add(button);
            }
            frame2.add(frame);
            frame2.revalidate();
            frame2.repaint();
            historyOfTransactionsSetUp(frame2, transactionHistory, Card);
        });

        menu.add(logIn);
        JMenuItem ejectItem = new JMenuItem("Eject Card");
        ejectItem.addActionListener(e -> System.exit(0));
        menu.add(ejectItem);

        // Deposit
        JMenuItem depositItem = new JMenuItem("Deposit");
        depositItem.addActionListener(e -> {
            if (donePass) {
                float amount = Float.parseFloat(JOptionPane.showInputDialog(null, "Enter deposit amount", null));
                deposit(amount);
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first!");
            }
        });
        menu.add(depositItem);

        // Withdraw
        JMenuItem withdrawItem = new JMenuItem("Withdraw");
        withdrawItem.addActionListener(e -> {
            if (donePass) {
                int amount = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter withdrawal amount", null));
                withdraw(amount);
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first!");
            }
        });
        menu.add(withdrawItem);

        // Transfer
        JMenuItem transferItem = new JMenuItem("Transfer");
        transferItem.addActionListener(e -> {
            frame2.remove(transactionHistory);
            frame2.getContentPane().removeAll();
            frame2.repaint();

            if (donePass) {
                HashMap<Integer, String> receieverCards = L13.getInventory();
                JPanel frame = new JPanel();
                for (HashMap.Entry<Integer, String> entry : receieverCards.entrySet()) {
                    Integer id = entry.getKey();
                    String name = entry.getValue();

                    JButton button = new JButton(name);
                    button.addActionListener(e2 -> {
                        int amount = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter transfer amount", null));

                        // Use SwingWorker to handle the transfer
                        SwingWorker<Void, Void> worker = new SwingWorker<>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                transfer(L13.getCard(id), amount);
                                return null;
                            }

                            @Override
                            protected void done() {
                                // Update the UI after the transfer is complete
                                frame.setVisible(false);
                                frame.removeAll();
                                historyOfTransactionsSetUp(frame2, transactionHistory, Card);
                            }
                        };
                        worker.execute(); // Start the background task
                    });

                    frame.add(button);
                }
                frame2.add(frame);
                frame2.revalidate();
                frame2.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first!");
            }
        });

        menu.add(transferItem);

        // Show Card Info
        JMenuItem afiseazaItem = new JMenuItem("Show Card Info");
        afiseazaItem.addActionListener(e -> afiseaza());
        menu.add(afiseazaItem);

        // Add menu to the menu bar
        menuBar.add(menu);

        frame2.setSize(300, 300);
        frame2.setJMenuBar(menuBar);
        frame2.setVisible(true);
        frame2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame2;
    }

    private static void historyOfTransactionsSetUp(JFrame frame, JPanel panelToWorkWith, Card card) {
        panelToWorkWith.removeAll();
        panelToWorkWith.add(new JLabel("Money: $" + card.getBani()));
        panelToWorkWith.add(new JLabel("TRANSACTIONS:"));
        for (String transaction : card.getTransactionHistory()) {
            JLabel label = new JLabel(transaction);
            label.setForeground(Color.GREEN);
            panelToWorkWith.add(label);
        }
        frame.add(panelToWorkWith);
        frame.revalidate();
        frame.repaint();
    }

    private static void addTransaction(Card card, String message) {
        card.appendTransaction(message);
    }
}
