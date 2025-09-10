package com.expenseTracker.UI;

import javax.swing.*;

public class MainMenu {
    public MainMenu() {
        JFrame frame = new JFrame("Smart Expense Tracker");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton addExpenseBtn = new JButton("Add Expense");
        JButton viewSummaryBtn = new JButton("View Summary");
        JButton budgetAlertsBtn = new JButton("View Alerts");
        JButton exitBtn = new JButton("Exit");

        JPanel panel = new JPanel();
        panel.add(addExpenseBtn);
        panel.add(viewSummaryBtn);
        panel.add(budgetAlertsBtn);
        panel.add(exitBtn);

        frame.add(panel);
        frame.setVisible(true);
    }
}
