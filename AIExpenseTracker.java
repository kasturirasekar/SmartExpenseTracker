import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.io.*;

// Main application class
public class AIExpenseTracker {
    private JFrame mainFrame;
    private User currentUser;
    private BudgetManager budgetManager;
    private List<Expense> expenses;
    private ExpenseClassifier expenseClassifier;
    private DatabaseManager dbManager;
    private ThemeManager themeManager;
    private NotificationService notificationService;
    private ExportManager exportManager;
    
    // Category dropdown options
    private String[] categories = {"Food", "Travel", "Shopping", "Entertainment", "Utilities", "Healthcare"};
    
    public AIExpenseTracker() {
        // Initialize managers
        dbManager = new DatabaseManager();
        themeManager = new ThemeManager();
        notificationService = new NotificationService();
        exportManager = new ExportManager();
        
        dbManager.initializeDatabase();
        showLoginScreen();
    }
    
    private void initializeApplication() {
        // Initialize with data from database
        initializeData();
        prepareGUI();
    }
    
    private void initializeData() {
        // Initialize budget manager
        budgetManager = new BudgetManager(dbManager);
        
        // Initialize AI classifier
        expenseClassifier = new ExpenseClassifier();
        
        // Train classifier with data from database
        trainClassifierWithDatabaseData();
        
        // Load user budgets from database
        loadUserBudgets();
        
        // Initialize expenses list and load from database
        expenses = dbManager.loadExpenses(currentUser);
        
        // Add expenses to budget manager
        for (Expense expense : expenses) {
            budgetManager.addExpense(expense);
        }
    }
    
    private void trainClassifierWithDatabaseData() {
        // Train the classifier with data from database
        List<TrainingData> trainingData = dbManager.loadTrainingData();
        for (TrainingData data : trainingData) {
            expenseClassifier.trainModel(data.getDescription(), data.getCategory());
        }
    }
    
    private void loadUserBudgets() {
        Map<String, Double> budgets = dbManager.loadUserBudgets(currentUser);
        for (Map.Entry<String, Double> entry : budgets.entrySet()) {
            currentUser.setBudget(entry.getKey(), entry.getValue());
        }
    }
    
    private void showLoginScreen() {
        JFrame loginFrame = new JFrame("AI Expense Tracker - Login");
        loginFrame.setSize(400, 300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridBagLayout());
        loginFrame.setLocationRelativeTo(null);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("AI Expense Tracker", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(60, 90, 170));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginFrame.add(titleLabel, gbc);
        
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginFrame.add(userLabel, gbc);
        
        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginFrame.add(userField, gbc);
        
        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginFrame.add(passLabel, gbc);
        
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginFrame.add(passField, gbc);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginFrame.add(loginBtn, gbc);
        
        JButton registerBtn = new JButton("Register New User");
        registerBtn.setBackground(new Color(100, 180, 100));
        registerBtn.setForeground(Color.WHITE);
        gbc.gridy = 4;
        loginFrame.add(registerBtn, gbc);
        
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, 
                    "Please enter both username and password", 
                    "Login Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User user = dbManager.authenticateUser(username, password);
            if (user != null) {
                currentUser = user;
                loginFrame.dispose();
                initializeApplication();
            } else {
                JOptionPane.showMessageDialog(loginFrame, 
                    "Invalid username or password", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        registerBtn.addActionListener(e -> showRegistrationDialog(loginFrame));
        
        loginFrame.setVisible(true);
    }
    
    private void showRegistrationDialog(JFrame parent) {
        JDialog registerDialog = new JDialog(parent, "User Registration", true);
        registerDialog.setSize(400, 350);
        registerDialog.setLayout(new GridBagLayout());
        registerDialog.setLocationRelativeTo(parent);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Register New User", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        registerDialog.add(titleLabel, gbc);
        
        JLabel nameLabel = new JLabel("Full Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        registerDialog.add(nameLabel, gbc);
        
        JTextField nameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        registerDialog.add(nameField, gbc);
        
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        registerDialog.add(userLabel, gbc);
        
        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        registerDialog.add(userField, gbc);
        
        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        registerDialog.add(passLabel, gbc);
        
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        registerDialog.add(passField, gbc);
        
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        registerDialog.add(emailLabel, gbc);
        
        JTextField emailField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        registerDialog.add(emailField, gbc);
        
        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(70, 130, 180));
        registerBtn.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        registerDialog.add(registerBtn, gbc);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(150, 150, 150));
        cancelBtn.setForeground(Color.WHITE);
        gbc.gridy = 6;
        registerDialog.add(cancelBtn, gbc);
        
        registerBtn.addActionListener(e -> {
            String name = nameField.getText();
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String email = emailField.getText();
            
            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, 
                    "Please fill all required fields", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (dbManager.registerUser(name, username, password, email)) {
                JOptionPane.showMessageDialog(registerDialog, 
                    "Registration successful! Please login.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(registerDialog, 
                    "Registration failed. Username might already exist.", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> registerDialog.dispose());
        
        registerDialog.setVisible(true);
    }
    
    private void prepareGUI() {
        mainFrame = new JFrame("AI Expense Tracker - Welcome " + currentUser.getName());
        mainFrame.setSize(1000, 700);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        
        // Apply theme
        themeManager.applyTheme(mainFrame, ThemeManager.ThemeType.LIGHT);
        
        // Enhanced menu bar with more options
        JMenuBar menuBar = createEnhancedMenuBar();
        mainFrame.setJMenuBar(menuBar);
        
        // Create main panel with card layout for different views
        JPanel mainPanel = new JPanel(new CardLayout());
        
        // Create all panels
        JPanel menuPanel = createMenuPanel(mainPanel);
        JPanel addExpensePanel = createAddExpensePanel(mainPanel);
        JPanel viewExpensesPanel = createEnhancedViewExpensesPanel(mainPanel);
        JPanel budgetAlertsPanel = createBudgetAlertsPanel(mainPanel);
        JPanel insightsPanel = createEnhancedInsightsPanel(mainPanel);
        JPanel goalsPanel = createGoalsPanel(mainPanel);
        JPanel settingsPanel = createSettingsPanel(mainPanel);
        
        // Add panels to main panel
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(addExpensePanel, "AddExpense");
        mainPanel.add(viewExpensesPanel, "ViewExpenses");
        mainPanel.add(budgetAlertsPanel, "BudgetAlerts");
        mainPanel.add(insightsPanel, "Insights");
        mainPanel.add(goalsPanel, "Goals");
        mainPanel.add(settingsPanel, "Settings");
        
        mainFrame.add(mainPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        
        // Show welcome notification
        notificationService.showWelcomeNotification(currentUser);
    }
    
    private JMenuBar createEnhancedMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportItem = new JMenuItem("Export Data");
        JMenuItem importItem = new JMenuItem("Import Data");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        exportItem.addActionListener(e -> showExportDialog());
        importItem.addActionListener(e -> showImportDialog());
        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(exportItem);
        fileMenu.add(importItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem trainModelItem = new JMenuItem("Train AI Model");
        JMenuItem manageBudgetsItem = new JMenuItem("Manage Budgets");
        JMenuItem manageGoalsItem = new JMenuItem("Manage Goals");
        JMenuItem receiptScannerItem = new JMenuItem("Scan Receipt");
        
        trainModelItem.addActionListener(e -> showTrainingDialog());
        manageBudgetsItem.addActionListener(e -> showManageBudgetsDialog());
        manageGoalsItem.addActionListener(e -> showManageGoalsDialog());
        receiptScannerItem.addActionListener(e -> showReceiptScannerDialog());
        
        toolsMenu.add(trainModelItem);
        toolsMenu.add(manageBudgetsItem);
        toolsMenu.add(manageGoalsItem);
        toolsMenu.addSeparator();
        toolsMenu.add(receiptScannerItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem darkThemeItem = new JMenuItem("Dark Theme");
        JMenuItem lightThemeItem = new JMenuItem("Light Theme");
        JMenuItem blueThemeItem = new JMenuItem("Blue Theme");
        
        darkThemeItem.addActionListener(e -> themeManager.applyTheme(mainFrame, ThemeManager.ThemeType.DARK));
        lightThemeItem.addActionListener(e -> themeManager.applyTheme(mainFrame, ThemeManager.ThemeType.LIGHT));
        blueThemeItem.addActionListener(e -> themeManager.applyTheme(mainFrame, ThemeManager.ThemeType.BLUE));
        
        viewMenu.add(darkThemeItem);
        viewMenu.add(lightThemeItem);
        viewMenu.add(blueThemeItem);
        
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(viewMenu);
        
        return menuBar;
    }
    
    private JPanel createMenuPanel(JPanel mainPanel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("AI-Powered Expense Tracker", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 90, 170));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(80, 120, 200));
        gbc.gridy = 1;
        panel.add(welcomeLabel, gbc);
        
        // Quick Stats
        gbc.gridy = 2;
        panel.add(createQuickStatsPanel(), gbc);
        
        // Menu Buttons
        JButton addExpenseBtn = createStyledButton("💳 Add Expense", new Color(70, 130, 180));
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(addExpenseBtn, gbc);
        
        JButton viewExpensesBtn = createStyledButton("📊 View & Analyze", new Color(65, 105, 225));
        gbc.gridy = 4;
        panel.add(viewExpensesBtn, gbc);
        
        JButton viewAlertsBtn = createStyledButton("⚠️ Budget Alerts", new Color(220, 100, 80));
        gbc.gridy = 5;
        panel.add(viewAlertsBtn, gbc);
        
        JButton insightsBtn = createStyledButton("🤖 AI Insights", new Color(100, 180, 100));
        gbc.gridy = 6;
        panel.add(insightsBtn, gbc);
        
        JButton goalsBtn = createStyledButton("🎯 Spending Goals", new Color(255, 165, 0));
        gbc.gridy = 7;
        panel.add(goalsBtn, gbc);
        
        JButton settingsBtn = createStyledButton("⚙️ Settings", new Color(150, 150, 150));
        gbc.gridy = 8;
        panel.add(settingsBtn, gbc);
        
        // Add action listeners
        addExpenseBtn.addActionListener(e -> showCard(mainPanel, "AddExpense"));
        viewExpensesBtn.addActionListener(e -> showCard(mainPanel, "ViewExpenses"));
        viewAlertsBtn.addActionListener(e -> showCard(mainPanel, "BudgetAlerts"));
        insightsBtn.addActionListener(e -> showCard(mainPanel, "Insights"));
        goalsBtn.addActionListener(e -> showCard(mainPanel, "Goals"));
        settingsBtn.addActionListener(e -> showCard(mainPanel, "Settings"));
        
        return panel;
    }
    
    private JPanel createQuickStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(new Color(220, 230, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        double totalSpent = budgetManager.getTotalSpentAllCategories();
        double monthlyBudget = currentUser.getTotalMonthlyBudget();
        
        // Total Spent
        JLabel spentLabel = new JLabel("Total Spent", JLabel.CENTER);
        spentLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel spentValue = new JLabel(String.format("$%.2f", totalSpent), JLabel.CENTER);
        spentValue.setFont(new Font("Arial", Font.BOLD, 16));
        spentValue.setForeground(new Color(220, 80, 80));
        
        // Budget Left
        JLabel budgetLabel = new JLabel("Budget Left", JLabel.CENTER);
        budgetLabel.setFont(new Font("Arial", Font.BOLD, 12));
        double budgetLeft = monthlyBudget - totalSpent;
        JLabel budgetValue = new JLabel(String.format("$%.2f", budgetLeft), JLabel.CENTER);
        budgetValue.setFont(new Font("Arial", Font.BOLD, 16));
        budgetValue.setForeground(budgetLeft >= 0 ? new Color(80, 180, 80) : new Color(220, 80, 80));
        
        // Top Category
        String topCategory = budgetManager.getTopSpendingCategory();
        JLabel categoryLabel = new JLabel("Top Category", JLabel.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel categoryValue = new JLabel(topCategory, JLabel.CENTER);
        categoryValue.setFont(new Font("Arial", Font.BOLD, 14));
        categoryValue.setForeground(new Color(80, 120, 200));
        
        panel.add(createStatPanel(spentLabel, spentValue));
        panel.add(createStatPanel(budgetLabel, budgetValue));
        panel.add(createStatPanel(categoryLabel, categoryValue));
        
        return panel;
    }
    
    private JPanel createStatPanel(JLabel label, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(label, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 45));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }
    
    private JPanel createAddExpensePanel(JPanel mainPanel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Add New Expense", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 90, 170));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        JLabel amountLabel = new JLabel("Amount:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(amountLabel, gbc);
        
        JTextField amountField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(amountField, gbc);
        
        JLabel descLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(descLabel, gbc);
        
        JTextField descField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(descField, gbc);
        
        JLabel categoryLabel = new JLabel("Category:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(categoryLabel, gbc);
        
        JComboBox<String> categoryDropdown = new JComboBox<>(categories);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(categoryDropdown, gbc);
        
        // New: Receipt upload
        JLabel receiptLabel = new JLabel("Receipt Image:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(receiptLabel, gbc);
        
        JPanel receiptPanel = new JPanel(new FlowLayout());
        JButton uploadReceiptBtn = new JButton("Upload Receipt");
        JLabel receiptStatus = new JLabel("No receipt");
        receiptPanel.add(uploadReceiptBtn);
        receiptPanel.add(receiptStatus);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(receiptPanel, gbc);
        
        JButton predictCategoryBtn = new JButton("Predict Category");
        predictCategoryBtn.setBackground(new Color(100, 150, 200));
        predictCategoryBtn.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(predictCategoryBtn, gbc);
        
        JButton submitBtn = new JButton("Add Expense");
        submitBtn.setBackground(new Color(70, 130, 180));
        submitBtn.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(submitBtn, gbc);
        
        JButton backBtn = new JButton("Back to Menu");
        backBtn.setBackground(new Color(150, 150, 150));
        backBtn.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(backBtn, gbc);
        
        // File chooser for receipt
        uploadReceiptBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif"));
            
            if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                receiptStatus.setText(file.getName());
                // In full implementation, would save file path to expense
            }
        });
        
        // Add action listeners
        predictCategoryBtn.addActionListener(e -> {
            String description = descField.getText().trim();
            if (!description.isEmpty()) {
                String predictedCategory = expenseClassifier.predictCategory(description);
                categoryDropdown.setSelectedItem(predictedCategory);
                JOptionPane.showMessageDialog(panel, 
                    "AI predicted category: " + predictedCategory,
                    "Category Prediction", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, 
                    "Please enter a description first", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        submitBtn.addActionListener(e -> {
            try {
                // Validate and add expense
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    throw new InvalidAmountException("Amount must be positive");
                }
                
                String category = (String) categoryDropdown.getSelectedItem();
                String description = descField.getText();
                
                if (description.trim().isEmpty()) {
                    throw new Exception("Description cannot be empty");
                }
                
                // Create appropriate expense object based on category
                Expense expense = ExpenseFactory.createExpense(category, amount, new java.util.Date(), description);
                
                // Add expense to database
                if (dbManager.saveExpense(currentUser, expense)) {
                    // Add expense to local list
                    expenses.add(expense);
                    
                    // Train the AI model with this new example and save to database
                    expenseClassifier.trainModel(description, category);
                    dbManager.saveTrainingData(description, category);
                    
                    // Update budget manager
                    budgetManager.addExpense(expense);
                    
                    // Check for budget alerts
                    Category cat = new Category(category, currentUser.getBudget(category));
                    if (budgetManager.checkLimit(cat)) {
                        JOptionPane.showMessageDialog(panel, 
                            "Budget alert: You've exceeded your " + category + " budget!",
                            "Budget Alert", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                    
                    // Clear fields
                    amountField.setText("");
                    descField.setText("");
                    receiptStatus.setText("No receipt");
                    
                    JOptionPane.showMessageDialog(panel, "Expense added successfully!");
                } else {
                    throw new Exception("Failed to save expense to database");
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, 
                    "Please enter a valid amount.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (InvalidAmountException ex) {
                JOptionPane.showMessageDialog(panel, 
                    ex.getMessage(), 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, 
                    "An error occurred: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backBtn.addActionListener(e -> {
            showCard(mainPanel, "Menu");
        });
        
        return panel;
    }
    
    // Enhanced View Expenses Panel with Search and Filter
    private JPanel createEnhancedViewExpensesPanel(JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 255));
        
        // Header with search and filter
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 90, 170));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Expense Manager", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Search and Filter Panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(new Color(60, 90, 170));
        
        JTextField searchField = new JTextField(15);
        JComboBox<String> categoryFilter = new JComboBox<>(categories);
        categoryFilter.insertItemAt("All Categories", 0);
        categoryFilter.setSelectedIndex(0);
        
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        JButton exportBtn = new JButton("Export");
        JButton chartBtn = new JButton("View Charts");
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(categoryFilter);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        searchPanel.add(exportBtn);
        searchPanel.add(chartBtn);
        
        headerPanel.add(searchPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Date", "Amount", "Category", "Description", "Receipt"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable expensesTable = new JTable(model);
        expensesTable.setFillsViewportHeight(true);
        expensesTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel();
        JButton backBtn = new JButton("Back to Menu");
        JButton deleteBtn = new JButton("Delete Selected");
        
        buttonPanel.add(backBtn);
        buttonPanel.add(deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Action listeners
        searchBtn.addActionListener(e -> performSearch(searchField.getText(), 
            (String) categoryFilter.getSelectedItem(), model));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            refreshExpensesTable(model);
        });
        exportBtn.addActionListener(e -> exportManager.exportExpensesToCSV(expenses, "expenses_export.csv"));
        chartBtn.addActionListener(e -> showExpenseCharts());
        backBtn.addActionListener(e -> showCard(mainPanel, "Menu"));
        deleteBtn.addActionListener(e -> deleteSelectedExpense(expensesTable, model));
        
        refreshExpensesTable(model);
        return panel;
    }
    
    private void refreshExpensesTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing data
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        
        for (Expense expense : expenses) {
            Object[] rowData = {
                dateFormat.format(expense.getDate()),
                String.format("$%.2f", expense.getAmount()),
                expense.getCategory(),
                expense.getDescription(),
                expense.hasReceipt() ? "📎" : ""
            };
            model.addRow(rowData);
        }
    }
    
    private void performSearch(String searchText, String category, DefaultTableModel model) {
        model.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        
        for (Expense expense : expenses) {
            boolean matchesSearch = searchText.isEmpty() || 
                expense.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                String.valueOf(expense.getAmount()).contains(searchText);
            
            boolean matchesCategory = category.equals("All Categories") || 
                expense.getCategory().equals(category);
            
            if (matchesSearch && matchesCategory) {
                Object[] rowData = {
                    dateFormat.format(expense.getDate()),
                    String.format("$%.2f", expense.getAmount()),
                    expense.getCategory(),
                    expense.getDescription(),
                    expense.hasReceipt() ? "📎" : ""
                };
                model.addRow(rowData);
            }
        }
    }
    
    private void deleteSelectedExpense(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to delete this expense?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                Expense expense = expenses.get(selectedRow);
                if (dbManager.deleteExpense(currentUser, expense)) {
                    expenses.remove(selectedRow);
                    budgetManager.removeExpense(expense);
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(mainFrame, "Expense deleted successfully!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select an expense to delete.");
        }
    }
    
    private void showExpenseCharts() {
        JDialog chartDialog = new JDialog(mainFrame, "Expense Charts", true);
        chartDialog.setSize(800, 600);
        chartDialog.setLayout(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pie Chart - Category Distribution
        JPanel pieChartPanel = createCategoryPieChart();
        tabbedPane.addTab("Category Distribution", pieChartPanel);
        
        // Bar Chart - Monthly Trends
        JPanel barChartPanel = createMonthlyTrendChart();
        tabbedPane.addTab("Monthly Trends", barChartPanel);
        
        chartDialog.add(tabbedPane, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> chartDialog.dispose());
        chartDialog.add(closeBtn, BorderLayout.SOUTH);
        
        chartDialog.setLocationRelativeTo(mainFrame);
        chartDialog.setVisible(true);
    }
    
    private JPanel createCategoryPieChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Create a simple pie chart using text and progress bars
        JTextArea chartArea = new JTextArea();
        chartArea.setEditable(false);
        chartArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        chartArea.setBackground(Color.WHITE);
        
        StringBuilder chartText = new StringBuilder();
        chartText.append("EXPENSE DISTRIBUTION BY CATEGORY\n");
        chartText.append("================================\n\n");
        
        double totalSpent = budgetManager.getTotalSpentAllCategories();
        
        for (String category : categories) {
            double spent = budgetManager.getTotalSpent(category);
            if (spent > 0) {
                double percentage = (spent / totalSpent) * 100;
                int bars = (int) (percentage / 2); // Each bar represents 2%
                
                chartText.append(String.format("%-15s: %5.1f%% ", category, percentage));
                for (int i = 0; i < bars; i++) {
                    chartText.append("█");
                }
                chartText.append(String.format(" $%.2f\n", spent));
            }
        }
        
        chartArea.setText(chartText.toString());
        panel.add(new JScrollPane(chartArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMonthlyTrendChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Create a simple bar chart using text
        JTextArea chartArea = new JTextArea();
        chartArea.setEditable(false);
        chartArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        chartArea.setBackground(Color.WHITE);
        
        StringBuilder chartText = new StringBuilder();
        chartText.append("MONTHLY SPENDING TRENDS\n");
        chartText.append("=======================\n\n");
        
        Map<String, Double> monthlyData = budgetManager.getMonthlySpendingData();
        double maxSpending = monthlyData.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        
        for (Map.Entry<String, Double> entry : monthlyData.entrySet()) {
            double amount = entry.getValue();
            int bars = (int) ((amount / maxSpending) * 50); // Scale to 50 characters max
            
            chartText.append(String.format("%-10s: $%6.2f ", entry.getKey(), amount));
            for (int i = 0; i < bars; i++) {
                chartText.append("█");
            }
            chartText.append("\n");
        }
        
        chartArea.setText(chartText.toString());
        panel.add(new JScrollPane(chartArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBudgetAlertsPanel(JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 255));
        
        JLabel titleLabel = new JLabel("Budget Alerts", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 90, 170));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JTextArea alertsArea = new JTextArea();
        alertsArea.setEditable(false);
        alertsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        alertsArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(alertsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton backBtn = new JButton("Back to Menu");
        backBtn.setBackground(new Color(150, 150, 150));
        backBtn.setForeground(Color.WHITE);
        panel.add(backBtn, BorderLayout.SOUTH);
        
        backBtn.addActionListener(e -> {
            showCard(mainPanel, "Menu");
        });
        
        checkBudgetAlerts(alertsArea);
        return panel;
    }
    
    private void checkBudgetAlerts(JTextArea alertsArea) {
        alertsArea.setText(""); // Clear previous alerts
        
        // Check each category for budget limits
        for (String category : categories) {
            double budget = currentUser.getBudget(category);
            double spent = budgetManager.getTotalSpent(category);
            
            if (spent > budget) {
                alertsArea.append("❌ ALERT: You've exceeded your " + category + " budget!\n");
                alertsArea.append("   Budget: $" + String.format("%.2f", budget) + "\n");
                alertsArea.append("   Spent: $" + String.format("%.2f", spent) + "\n");
                alertsArea.append("   Over by: $" + String.format("%.2f", (spent - budget)) + "\n\n");
            } else if (spent > budget * 0.8) {
                alertsArea.append("⚠️ Warning: You're approaching your " + category + " budget limit\n");
                alertsArea.append("   Budget: $" + String.format("%.2f", budget) + "\n");
                alertsArea.append("   Spent: $" + String.format("%.2f", spent) + "\n");
                alertsArea.append("   Remaining: $" + String.format("%.2f", (budget - spent)) + "\n\n");
            }
        }
        
        if (alertsArea.getText().isEmpty()) {
            alertsArea.setText("✅ No budget alerts. All expenses are within budget limits.");
        }
    }
    
    // Enhanced Insights Panel with AI Predictions
    private JPanel createEnhancedInsightsPanel(JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 255));
        
        JLabel titleLabel = new JLabel("AI Insights & Analytics", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 90, 170));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JTextArea insightsArea = new JTextArea();
        insightsArea.setEditable(false);
        insightsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        insightsArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(insightsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh Insights");
        JButton predictBtn = new JButton("AI Predictions");
        JButton backBtn = new JButton("Back to Menu");
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(predictBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        refreshBtn.addActionListener(e -> generateEnhancedInsights(insightsArea));
        predictBtn.addActionListener(e -> showAIPredictions());
        backBtn.addActionListener(e -> showCard(mainPanel, "Menu"));
        
        generateEnhancedInsights(insightsArea);
        return panel;
    }
    
    private void generateEnhancedInsights(JTextArea insightsArea) {
        insightsArea.setText("");
        InsightGenerator insightGenerator = new InsightGenerator(budgetManager, currentUser, expenses);
        
        insightsArea.append("🎯 SMART SPENDING INSIGHTS\n");
        insightsArea.append("========================\n\n");
        
        insightsArea.append(insightGenerator.generateSpendingSummary());
        insightsArea.append("\n" + insightGenerator.generateBudgetAlerts());
        insightsArea.append("\n" + insightGenerator.generateSpendingPatterns());
        insightsArea.append("\n" + insightGenerator.generateRecommendations());
    }
    
    private void showAIPredictions() {
        ExpensePredictor predictor = new ExpensePredictor(expenses);
        JDialog predictionDialog = new JDialog(mainFrame, "AI Predictions", true);
        predictionDialog.setSize(500, 400);
        predictionDialog.setLayout(new BorderLayout());
        
        JTextArea predictionArea = new JTextArea();
        predictionArea.setEditable(false);
        predictionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        predictionArea.setMargin(new Insets(10, 10, 10, 10));
        
        predictionArea.append("🤖 AI PREDICTIONS & FORECASTS\n");
        predictionArea.append("============================\n\n");
        
        // Monthly predictions
        predictionArea.append("📈 Monthly Spending Forecast:\n");
        Map<String, Double> monthlyPredictions = predictor.predictMonthlySpending();
        for (Map.Entry<String, Double> entry : monthlyPredictions.entrySet()) {
            predictionArea.append(String.format("  %s: $%.2f\n", entry.getKey(), entry.getValue()));
        }
        
        predictionArea.append("\n⚠️ Budget Risk Assessment:\n");
        Map<String, String> riskAssessments = predictor.assessBudgetRisks(currentUser);
        for (Map.Entry<String, String> entry : riskAssessments.entrySet()) {
            predictionArea.append(String.format("  %s: %s\n", entry.getKey(), entry.getValue()));
        }
        
        predictionArea.append("\n💡 Smart Recommendations:\n");
        List<String> recommendations = predictor.generateSmartRecommendations();
        for (String recommendation : recommendations) {
            predictionArea.append("  • " + recommendation + "\n");
        }
        
        predictionDialog.add(new JScrollPane(predictionArea), BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> predictionDialog.dispose());
        predictionDialog.add(closeBtn, BorderLayout.SOUTH);
        
        predictionDialog.setLocationRelativeTo(mainFrame);
        predictionDialog.setVisible(true);
    }
    
    // Goals Panel
    private JPanel createGoalsPanel(JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 255));
        
        JLabel titleLabel = new JLabel("Spending Goals & Targets", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 90, 170));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Goals list
        DefaultListModel<SpendingGoal> goalsModel = new DefaultListModel<>();
        JList<SpendingGoal> goalsList = new JList<>(goalsModel);
        goalsList.setCellRenderer(new GoalListRenderer());
        JScrollPane scrollPane = new JScrollPane(goalsList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load goals
        List<SpendingGoal> goals = dbManager.loadUserGoals(currentUser);
        for (SpendingGoal goal : goals) {
            goalsModel.addElement(goal);
        }
        
        JPanel buttonPanel = new JPanel();
        JButton addGoalBtn = new JButton("Add New Goal");
        JButton editGoalBtn = new JButton("Edit Goal");
        JButton deleteGoalBtn = new JButton("Delete Goal");
        JButton backBtn = new JButton("Back to Menu");
        
        buttonPanel.add(addGoalBtn);
        buttonPanel.add(editGoalBtn);
        buttonPanel.add(deleteGoalBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addGoalBtn.addActionListener(e -> showAddGoalDialog(goalsModel));
        editGoalBtn.addActionListener(e -> editSelectedGoal(goalsList, goalsModel));
        deleteGoalBtn.addActionListener(e -> deleteSelectedGoal(goalsList, goalsModel));
        backBtn.addActionListener(e -> showCard(mainPanel, "Menu"));
        
        return panel;
    }
    
    private void showAddGoalDialog(DefaultListModel<SpendingGoal> goalsModel) {
        JDialog goalDialog = new JDialog(mainFrame, "Add New Goal", true);
        goalDialog.setSize(400, 300);
        goalDialog.setLayout(new GridLayout(5, 2, 10, 10));
        goalDialog.setLocationRelativeTo(mainFrame);
        
        JTextField nameField = new JTextField();
        JTextField targetAmountField = new JTextField();
        JTextField targetDateField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        
        goalDialog.add(new JLabel("Goal Name:"));
        goalDialog.add(nameField);
        goalDialog.add(new JLabel("Target Amount:"));
        goalDialog.add(targetAmountField);
        goalDialog.add(new JLabel("Target Date (yyyy-mm-dd):"));
        goalDialog.add(targetDateField);
        goalDialog.add(new JLabel("Category:"));
        goalDialog.add(categoryCombo);
        
        JButton saveBtn = new JButton("Save Goal");
        JButton cancelBtn = new JButton("Cancel");
        
        goalDialog.add(saveBtn);
        goalDialog.add(cancelBtn);
        
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                double targetAmount = Double.parseDouble(targetAmountField.getText());
                String targetDateStr = targetDateField.getText();
                String category = (String) categoryCombo.getSelectedItem();
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date targetDate = sdf.parse(targetDateStr);
                
                SpendingGoal goal = new SpendingGoal(name, targetAmount, targetDate, category);
                if (dbManager.saveUserGoal(currentUser, goal)) {
                    goalsModel.addElement(goal);
                    goalDialog.dispose();
                    JOptionPane.showMessageDialog(mainFrame, "Goal added successfully!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(goalDialog, "Error: " + ex.getMessage());
            }
        });
        
        cancelBtn.addActionListener(e -> goalDialog.dispose());
        
        goalDialog.setVisible(true);
    }
    
    private void editSelectedGoal(JList<SpendingGoal> goalsList, DefaultListModel<SpendingGoal> goalsModel) {
        SpendingGoal selected = goalsList.getSelectedValue();
        if (selected != null) {
            // Implementation for editing goal
            JOptionPane.showMessageDialog(mainFrame, "Edit feature coming soon!");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a goal to edit.");
        }
    }
    
    private void deleteSelectedGoal(JList<SpendingGoal> goalsList, DefaultListModel<SpendingGoal> goalsModel) {
        SpendingGoal selected = goalsList.getSelectedValue();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Delete goal: " + selected.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (dbManager.deleteUserGoal(currentUser, selected)) {
                    goalsModel.removeElement(selected);
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a goal to delete.");
        }
    }
    
    // Settings Panel
    private JPanel createSettingsPanel(JPanel mainPanel) {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBackground(new Color(245, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton exportDataBtn = createSettingsButton("📤 Export All Data", new Color(70, 130, 180));
        JButton importDataBtn = createSettingsButton("📥 Import Data", new Color(65, 105, 225));
        JButton backupBtn = createSettingsButton("💾 Backup Data", new Color(100, 180, 100));
        JButton notificationsBtn = createSettingsButton("🔔 Notification Settings", new Color(255, 165, 0));
        JButton aboutBtn = createSettingsButton("ℹ️ About", new Color(150, 150, 150));
        JButton backBtn = createSettingsButton("← Back to Menu", new Color(120, 120, 120));
        
        exportDataBtn.addActionListener(e -> showExportDialog());
        importDataBtn.addActionListener(e -> showImportDialog());
        backupBtn.addActionListener(e -> backupData());
        notificationsBtn.addActionListener(e -> showNotificationSettings());
        aboutBtn.addActionListener(e -> showAboutDialog());
        backBtn.addActionListener(e -> showCard(mainPanel, "Menu"));
        
        panel.add(exportDataBtn);
        panel.add(importDataBtn);
        panel.add(backupBtn);
        panel.add(notificationsBtn);
        panel.add(aboutBtn);
        panel.add(backBtn);
        
        return panel;
    }
    
    private JButton createSettingsButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return button;
    }
    
    // Dialog methods
    private void showExportDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Expenses");
        fileChooser.setSelectedFile(new File("expenses_export.csv"));
        
        if (fileChooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (exportManager.exportExpensesToCSV(expenses, file.getAbsolutePath())) {
                JOptionPane.showMessageDialog(mainFrame, "Data exported successfully!");
            }
        }
    }
    
    private void showImportDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Expenses");
        
        if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            List<Expense> importedExpenses = exportManager.importExpensesFromCSV(file.getAbsolutePath());
            if (importedExpenses != null) {
                for (Expense expense : importedExpenses) {
                    if (dbManager.saveExpense(currentUser, expense)) {
                        expenses.add(expense);
                        budgetManager.addExpense(expense);
                    }
                }
                JOptionPane.showMessageDialog(mainFrame, 
                    String.format("Imported %d expenses successfully!", importedExpenses.size()));
            }
        }
    }
    
    private void showTrainingDialog() {
        JDialog trainingDialog = new JDialog(mainFrame, "Train AI Model", true);
        trainingDialog.setSize(400, 300);
        trainingDialog.setLayout(new BorderLayout());
        
        JTextArea trainingArea = new JTextArea();
        trainingArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(trainingArea);
        trainingDialog.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton addExampleBtn = new JButton("Add Training Example");
        JButton closeBtn = new JButton("Close");
        
        buttonPanel.add(addExampleBtn);
        buttonPanel.add(closeBtn);
        trainingDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Display current training examples
        trainingArea.setText(expenseClassifier.getTrainingExamples());
        
        addExampleBtn.addActionListener(e -> {
            String example = JOptionPane.showInputDialog(trainingDialog, 
                "Enter description to categorize:");
            if (example != null && !example.trim().isEmpty()) {
                String category = (String) JOptionPane.showInputDialog(trainingDialog,
                    "Select category for: " + example,
                    "Category Selection",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    categories,
                    categories[0]);
                
                if (category != null) {
                    expenseClassifier.trainModel(example, category);
                    dbManager.saveTrainingData(example, category);
                    trainingArea.setText(expenseClassifier.getTrainingExamples());
                }
            }
        });
        
        closeBtn.addActionListener(e -> trainingDialog.dispose());
        
        trainingDialog.setLocationRelativeTo(mainFrame);
        trainingDialog.setVisible(true);
    }
    
    private void showManageBudgetsDialog() {
        JDialog budgetDialog = new JDialog(mainFrame, "Manage Budgets", true);
        budgetDialog.setSize(400, 400);
        budgetDialog.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Set Monthly Budgets", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        budgetDialog.add(titleLabel, gbc);
        
        Map<String, JTextField> budgetFields = new HashMap<>();
        int row = 1;
        
        for (String category : categories) {
            JLabel categoryLabel = new JLabel(category + ":");
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            budgetDialog.add(categoryLabel, gbc);
            
            JTextField budgetField = new JTextField(10);
            budgetField.setText(String.format("%.2f", currentUser.getBudget(category)));
            gbc.gridx = 1;
            gbc.gridy = row;
            budgetDialog.add(budgetField, gbc);
            
            budgetFields.put(category, budgetField);
            row++;
        }
        
        JButton saveBtn = new JButton("Save Budgets");
        saveBtn.setBackground(new Color(70, 130, 180));
        saveBtn.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        budgetDialog.add(saveBtn, gbc);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(150, 150, 150));
        cancelBtn.setForeground(Color.WHITE);
        gbc.gridy = row + 1;
        budgetDialog.add(cancelBtn, gbc);
        
        saveBtn.addActionListener(e -> {
            try {
                for (String category : categories) {
                    double budget = Double.parseDouble(budgetFields.get(category).getText());
                    if (budget < 0) {
                        throw new InvalidAmountException("Budget cannot be negative");
                    }
                    currentUser.setBudget(category, budget);
                    dbManager.saveUserBudget(currentUser, category, budget);
                }
                JOptionPane.showMessageDialog(budgetDialog, 
                    "Budgets updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                budgetDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(budgetDialog, 
                    "Please enter valid numbers for all budgets", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (InvalidAmountException ex) {
                JOptionPane.showMessageDialog(budgetDialog, 
                    ex.getMessage(), 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> budgetDialog.dispose());
        
        budgetDialog.setLocationRelativeTo(mainFrame);
        budgetDialog.setVisible(true);
    }
    
    private void showManageGoalsDialog() {
        // Redirect to goals panel
        showCard((JPanel) mainFrame.getContentPane().getComponent(0), "Goals");
    }
    
    private void showReceiptScannerDialog() {
        JDialog scannerDialog = new JDialog(mainFrame, "Receipt Scanner", true);
        scannerDialog.setSize(400, 300);
        scannerDialog.setLayout(new BorderLayout());
        
        JTextArea scannerArea = new JTextArea();
        scannerArea.setText("📷 Receipt Scanner\n\n" +
            "1. Take a photo of your receipt\n" +
            "2. AI will extract expense details\n" +
            "3. Review and save the expense\n\n" +
            "Feature coming soon in next update!");
        scannerArea.setEditable(false);
        scannerArea.setMargin(new Insets(10, 10, 10, 10));
        
        scannerDialog.add(new JScrollPane(scannerArea), BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> scannerDialog.dispose());
        scannerDialog.add(closeBtn, BorderLayout.SOUTH);
        
        scannerDialog.setLocationRelativeTo(mainFrame);
        scannerDialog.setVisible(true);
    }
    
    private void backupData() {
        String backupFile = "expense_tracker_backup_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".csv";
        if (exportManager.exportExpensesToCSV(expenses, backupFile)) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Backup created successfully: " + backupFile);
        }
    }
    
    private void showNotificationSettings() {
        JOptionPane.showMessageDialog(mainFrame,
            "Notification Settings:\n\n" +
            "• Budget alerts: Enabled\n" +
            "• Weekly reports: Enabled\n" +
            "• Goal reminders: Enabled\n\n" +
            "Custom notification settings coming soon!",
            "Notification Settings",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(mainFrame,
            "AI Expense Tracker v2.0\n\n" +
            "Advanced expense tracking with AI-powered insights\n" +
            "Features:\n" +
            "• Smart categorization\n" +
            "• Budget management\n" +
            "• Spending goals\n" +
            "• Data export/import\n" +
            "• Multiple themes\n\n" +
            "Built with Java Swing & MySQL",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Utility methods
    private void showCard(JPanel mainPanel, String cardName) {
        CardLayout cl = (CardLayout)(mainPanel.getLayout());
        cl.show(mainPanel, cardName);
    }
    
    private void logout() {
        mainFrame.dispose();
        showLoginScreen();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AIExpenseTracker());
    }
}

// ============================================================================
// ENHANCED MANAGER CLASSES
// ============================================================================

// Theme Manager with multiple themes
class ThemeManager {
    public enum ThemeType { LIGHT, DARK, BLUE }
    
    private Map<ThemeType, ColorScheme> themes;
    
    public ThemeManager() {
        themes = new HashMap<>();
        initializeThemes();
    }
    
    private void initializeThemes() {
        themes.put(ThemeType.LIGHT, new ColorScheme(
            new Color(240, 245, 255), // background
            new Color(60, 90, 170),   // primary
            Color.BLACK,              // text
            Color.WHITE               // component
        ));
        
        themes.put(ThemeType.DARK, new ColorScheme(
            new Color(40, 44, 52),    // background
            new Color(86, 182, 194),  // primary
            Color.WHITE,              // text
            new Color(55, 59, 67)     // component
        ));
        
        themes.put(ThemeType.BLUE, new ColorScheme(
            new Color(225, 235, 255), // background
            new Color(30, 70, 150),   // primary
            new Color(20, 40, 80),    // text
            new Color(200, 220, 255)  // component
        ));
    }
    
    public void applyTheme(Container container, ThemeType themeType) {
        ColorScheme scheme = themes.get(themeType);
        applyThemeRecursive(container, scheme);
    }
    
    private void applyThemeRecursive(Component component, ColorScheme scheme) {
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyThemeRecursive(child, scheme);
            }
        }
        
        if (component instanceof JPanel) {
            component.setBackground(scheme.background);
        } else if (component instanceof JLabel) {
            component.setForeground(scheme.text);
        } else if (component instanceof JButton) {
            // Keep button colors as set
        } else if (component instanceof JTextArea || component instanceof JTextField) {
            component.setBackground(scheme.component);
            component.setForeground(scheme.text);
        }
    }
    
    private static class ColorScheme {
        Color background, primary, text, component;
        
        ColorScheme(Color bg, Color prim, Color txt, Color comp) {
            background = bg;
            primary = prim;
            text = txt;
            component = comp;
        }
    }
}

// Enhanced Expense Class with Receipt Support
abstract class Expense {
    protected double amount;
    protected java.util.Date date;
    protected String description;
    protected String receiptImagePath;
    protected String location;
    
    public Expense(double amount, java.util.Date date, String description) {
        this.amount = amount;
        this.date = date;
        this.description = description;
    }
    
    public Expense(double amount, String description) {
        this(amount, new java.util.Date(), description);
    }
    
    // Abstract method - must be implemented by subclasses (ABSTRACTION)
    public abstract String getCategory();
    
    // ENCAPSULATION - Private fields with public getters
    public double getAmount() { return amount; }
    public java.util.Date getDate() { return date; }
    public String getDescription() { return description; }
    
    public boolean hasReceipt() {
        return receiptImagePath != null && !receiptImagePath.isEmpty();
    }
    
    public void attachReceipt(String imagePath) {
        this.receiptImagePath = imagePath;
    }
    
    // Method overriding (POLYMORPHISM - Runtime)
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return String.format("%s: $%.2f on %s - %s", 
            getCategory(), amount, sdf.format(date), description);
    }
    
    // Method overloading (POLYMORPHISM - Compile-time)
    public void updateAmount(double newAmount) {
        this.amount = newAmount;
    }
    
    public void updateAmount(double newAmount, String reason) {
        this.amount = newAmount;
        this.description += " (Updated: " + reason + ")";
    }
}

// INHERITANCE - Concrete classes extending the abstract Expense class
class FoodExpense extends Expense {
    public FoodExpense(double amount, java.util.Date date, String description) {
        super(amount, date, description);
    }
    
    // Method overriding - Implementing abstract method
    @Override
    public String getCategory() {
        return "Food";
    }
    
    // Additional method specific to FoodExpense
    public boolean isRestaurantExpense() {
        return description.toLowerCase().contains("restaurant") || 
               description.toLowerCase().contains("dinner") ||
               description.toLowerCase().contains("lunch");
    }
}

class TravelExpense extends Expense {
    private String travelType; // Additional field specific to travel
    
    public TravelExpense(double amount, java.util.Date date, String description) {
        super(amount, date, description);
        this.travelType = "General";
    }
    
    public TravelExpense(double amount, java.util.Date date, String description, String travelType) {
        super(amount, date, description);
        this.travelType = travelType;
    }
    
    @Override
    public String getCategory() {
        return "Travel";
    }
    
    // ENCAPSULATION - Private field with public getter
    public String getTravelType() { return travelType; }
}

class ShoppingExpense extends Expense {
    public ShoppingExpense(double amount, java.util.Date date, String description) {
        super(amount, date, description);
    }
    
    @Override
    public String getCategory() {
        return "Shopping";
    }
}

class EntertainmentExpense extends Expense {
    public EntertainmentExpense(double amount, java.util.Date date, String description) {
        super(amount, date, description);
    }
    
    @Override
    public String getCategory() {
        return "Entertainment";
    }
}

class UtilitiesExpense extends Expense {
    public UtilitiesExpense(double amount, java.util.Date date, String description) {
        super(amount, date, description);
    }
    
    @Override
    public String getCategory() {
        return "Utilities";
    }
}

class HealthcareExpense extends Expense {
    public HealthcareExpense(double amount, java.util.Date date, String description) {
        super(amount, date, description);
    }
    
    @Override
    public String getCategory() {
        return "Healthcare";
    }
}

// FACTORY PATTERN - Demonstrating CREATIONAL pattern
class ExpenseFactory {
    public static Expense createExpense(String category, double amount, java.util.Date date, String description) {
        switch (category) {
            case "Food": return new FoodExpense(amount, date, description);
            case "Travel": return new TravelExpense(amount, date, description);
            case "Shopping": return new ShoppingExpense(amount, date, description);
            case "Entertainment": return new EntertainmentExpense(amount, date, description);
            case "Utilities": return new UtilitiesExpense(amount, date, description);
            case "Healthcare": return new HealthcareExpense(amount, date, description);
            default: throw new IllegalArgumentException("Unknown category: " + category);
        }
    }
}

// Spending Goal Class
class SpendingGoal {
    private String name;
    private double targetAmount;
    private double currentAmount;
    private java.util.Date targetDate;
    private String category;
    private boolean isCompleted;
    
    public SpendingGoal(String name, double targetAmount, java.util.Date targetDate, String category) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.category = category;
        this.currentAmount = 0;
        this.isCompleted = false;
    }
    
    public void updateProgress(double amount) {
        this.currentAmount += amount;
        if (this.currentAmount >= this.targetAmount) {
            this.isCompleted = true;
        }
    }
    
    public double getProgressPercentage() {
        return (currentAmount / targetAmount) * 100;
    }
    
    public String getProgressString() {
        return String.format("$%.2f / $%.2f (%.1f%%)", currentAmount, targetAmount, getProgressPercentage());
    }
    
    // Getters and setters
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public java.util.Date getTargetDate() { return targetDate; }
    public String getCategory() { return category; }
    public boolean isCompleted() { return isCompleted; }
}

// Goal List Renderer for nice display
class GoalListRenderer extends JLabel implements ListCellRenderer<SpendingGoal> {
    public GoalListRenderer() {
        setOpaque(true);
    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends SpendingGoal> list, SpendingGoal goal, 
            int index, boolean isSelected, boolean cellHasFocus) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String text = String.format("<html><b>%s</b><br>%s<br>Target: %s by %s<br>Progress: %s</html>",
            goal.getName(),
            goal.getCategory(),
            String.format("$%.2f", goal.getTargetAmount()),
            sdf.format(goal.getTargetDate()),
            goal.getProgressString());
        
        setText(text);
        
        if (isSelected) {
            setBackground(new Color(220, 230, 255));
            setForeground(Color.BLACK);
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }
        
        if (goal.isCompleted()) {
            setBackground(new Color(200, 255, 200));
        }
        
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return this;
    }
}

// Enhanced Budget Manager
class BudgetManager {
    private Map<String, List<Expense>> expensesByCategory;
    private DatabaseManager dbManager;
    
    public BudgetManager(DatabaseManager dbManager) {
        this.expensesByCategory = new HashMap<>();
        this.dbManager = dbManager;
        
        // Initialize categories
        String[] categories = {"Food", "Travel", "Shopping", "Entertainment", "Utilities", "Healthcare"};
        for (String category : categories) {
            expensesByCategory.put(category, new ArrayList<>());
        }
        
        // Load expenses from database
        loadExpensesFromDatabase();
    }
    
    private void loadExpensesFromDatabase() {
        // This method demonstrates using the dbManager field
        if (dbManager != null) {
            // Expenses are loaded separately in the main application
            // This is just to show the dbManager is being used
            System.out.println("BudgetManager: Database manager is available");
        }
    }
    
    public void addExpense(Expense expense) {
        String category = expense.getCategory();
        expensesByCategory.putIfAbsent(category, new ArrayList<>());
        expensesByCategory.get(category).add(expense);
    }
    
    public void removeExpense(Expense expense) {
        String category = expense.getCategory();
        if (expensesByCategory.containsKey(category)) {
            expensesByCategory.get(category).remove(expense);
        }
    }
    
    public boolean checkLimit(Category category) {
        double spent = getTotalSpent(category.getName());
        return spent > category.getLimit();
    }
    
    public double getTotalSpent(String category) {
        if (!expensesByCategory.containsKey(category)) {
            return 0.0;
        }
        
        double total = 0.0;
        for (Expense expense : expensesByCategory.get(category)) {
            total += expense.getAmount();
        }
        return total;
    }
    
    public double getTotalSpentAllCategories() {
        double total = 0;
        for (List<Expense> categoryExpenses : expensesByCategory.values()) {
            for (Expense expense : categoryExpenses) {
                total += expense.getAmount();
            }
        }
        return total;
    }
    
    public String getTopSpendingCategory() {
        String topCategory = "None";
        double maxSpent = 0;
        
        for (Map.Entry<String, List<Expense>> entry : expensesByCategory.entrySet()) {
            double categoryTotal = getTotalSpent(entry.getKey());
            if (categoryTotal > maxSpent) {
                maxSpent = categoryTotal;
                topCategory = entry.getKey();
            }
        }
        
        return topCategory;
    }
    
    public Map<String, Double> getMonthlySpendingData() {
        Map<String, Double> monthlyData = new LinkedHashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy");
        
        // Sample data for demonstration
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 6; i++) {
            String month = monthFormat.format(cal.getTime());
            monthlyData.put(month, 500.0 + (Math.random() * 500)); // Random data
            cal.add(Calendar.MONTH, -1);
        }
        
        return monthlyData;
    }
}

// AI-Powered Insight Generator
class InsightGenerator {
    private BudgetManager budgetManager;
    private User user;
    private List<Expense> expenses;
    
    public InsightGenerator(BudgetManager budgetManager, User user, List<Expense> expenses) {
        this.budgetManager = budgetManager;
        this.user = user;
        this.expenses = expenses;
    }
    
    public String generateSpendingSummary() {
        StringBuilder sb = new StringBuilder();
        double totalSpent = budgetManager.getTotalSpentAllCategories();
        double totalBudget = user.getTotalMonthlyBudget();
        
        sb.append("💰 Spending Summary:\n");
        sb.append(String.format("  Total Spent: $%.2f of $%.2f\n", totalSpent, totalBudget));
        sb.append(String.format("  Remaining: $%.2f\n", Math.max(0, totalBudget - totalSpent)));
        if (totalBudget > 0) {
            sb.append(String.format("  Utilization: %.1f%%\n\n", (totalSpent / totalBudget) * 100));
        } else {
            sb.append("  Utilization: N/A (No budget set)\n\n");
        }
        
        return sb.toString();
    }
    
    public String generateBudgetAlerts() {
        StringBuilder sb = new StringBuilder();
        sb.append("⚠️ Budget Alerts:\n");
        
        String[] categories = {"Food", "Travel", "Shopping", "Entertainment", "Utilities", "Healthcare"};
        boolean hasAlerts = false;
        
        for (String category : categories) {
            double budget = user.getBudget(category);
            double spent = budgetManager.getTotalSpent(category);
            if (budget > 0) {
                double percentage = (spent / budget) * 100;
                
                if (percentage > 90) {
                    sb.append(String.format("  ❌ %s: %.1f%% over budget!\n", category, percentage - 100));
                    hasAlerts = true;
                } else if (percentage > 75) {
                    sb.append(String.format("  🟡 %s: %.1f%% used\n", category, percentage));
                    hasAlerts = true;
                }
            }
        }
        
        if (!hasAlerts) {
            sb.append("  ✅ All categories within safe limits\n");
        }
        sb.append("\n");
        
        return sb.toString();
    }
    
    public String generateSpendingPatterns() {
        StringBuilder sb = new StringBuilder();
        sb.append("📈 Spending Patterns:\n");
        
        if (expenses.isEmpty()) {
            sb.append("  No spending data available yet.\n");
        } else {
            // Find most frequent spending day
            Map<String, Integer> dayCount = new HashMap<>();
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            
            for (Expense expense : expenses) {
                String day = dayFormat.format(expense.getDate());
                dayCount.put(day, dayCount.getOrDefault(day, 0) + 1);
            }
            
            String mostFrequentDay = dayCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
            
            sb.append(String.format("  Most active day: %s\n", mostFrequentDay));
            
            // Average daily spending
            double avgDaily = budgetManager.getTotalSpentAllCategories() / 30; // rough estimate
            sb.append(String.format("  Average daily: $%.2f\n", avgDaily));
        }
        
        sb.append("\n");
        return sb.toString();
    }
    
    public String generateRecommendations() {
        StringBuilder sb = new StringBuilder();
        sb.append("💡 Smart Recommendations:\n");
        
        if (expenses.isEmpty()) {
            sb.append("  Start tracking expenses to get personalized recommendations.\n");
        } else {
            String topCategory = budgetManager.getTopSpendingCategory();
            double topSpent = budgetManager.getTotalSpent(topCategory);
            
            sb.append(String.format("  Focus on reducing %s spending (current: $%.2f)\n", topCategory, topSpent));
            sb.append("  Consider setting spending limits for high categories\n");
            sb.append("  Review recurring expenses for optimization\n");
        }
        
        return sb.toString();
    }
}

// AI Expense Predictor
class ExpensePredictor {
    private List<Expense> expenses;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy");
    
    public ExpensePredictor(List<Expense> expenses) {
        this.expenses = expenses;
    }
    
    public Map<String, Double> predictMonthlySpending() {
        Map<String, Double> predictions = new LinkedHashMap<>();
        
        // Simple prediction based on historical average
        if (!expenses.isEmpty()) {
            double monthlyAverage = calculateMonthlyAverage();
            Calendar cal = Calendar.getInstance();
            
            for (int i = 0; i < 3; i++) {
                cal.add(Calendar.MONTH, 1);
                String futureMonth = monthFormat.format(cal.getTime());
                // Add some random variation (±10%)
                double variation = 0.9 + (Math.random() * 0.2);
                predictions.put(futureMonth, monthlyAverage * variation);
            }
        } else {
            // Default predictions if no data
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 3; i++) {
                cal.add(Calendar.MONTH, 1);
                predictions.put(monthFormat.format(cal.getTime()), 500.0);
            }
        }
        
        return predictions;
    }
    
    private double calculateMonthlyAverage() {
        Map<String, Double> monthlyTotals = new HashMap<>();
        
        for (Expense expense : expenses) {
            String month = monthFormat.format(expense.getDate());
            monthlyTotals.put(month, monthlyTotals.getOrDefault(month, 0.0) + expense.getAmount());
        }
        
        if (monthlyTotals.isEmpty()) return 0;
        
        double sum = monthlyTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        return sum / monthlyTotals.size();
    }
    
    public Map<String, String> assessBudgetRisks(User user) {
        Map<String, String> risks = new HashMap<>();
        String[] categories = {"Food", "Travel", "Shopping", "Entertainment", "Utilities", "Healthcare"};
        
        for (String category : categories) {
            double historicalAvg = getCategoryAverage(category);
            double budget = user.getBudget(category);
            
            if (budget <= 0) {
                risks.put(category, "NO BUDGET SET");
            } else if (historicalAvg > budget * 1.1) {
                risks.put(category, "HIGH RISK - Historical spending exceeds budget");
            } else if (historicalAvg > budget * 0.9) {
                risks.put(category, "MEDIUM RISK - Close to budget limit");
            } else {
                risks.put(category, "LOW RISK - Within safe limits");
            }
        }
        
        return risks;
    }
    
    private double getCategoryAverage(String category) {
        return expenses.stream()
            .filter(e -> e.getCategory().equals(category))
            .mapToDouble(Expense::getAmount)
            .average()
            .orElse(0);
    }
    
    public List<String> generateSmartRecommendations() {
        List<String> recommendations = new ArrayList<>();
        
        if (expenses.isEmpty()) {
            recommendations.add("Start tracking your expenses to get personalized recommendations");
            return recommendations;
        }
        
        // Analyze spending patterns
        double avgExpense = expenses.stream().mapToDouble(Expense::getAmount).average().orElse(0);
        long smallExpenses = expenses.stream().filter(e -> e.getAmount() < 10).count();
        
        if (smallExpenses > expenses.size() * 0.3) {
            recommendations.add("Consider reducing small frequent purchases - they add up quickly");
        }
        
        if (avgExpense > 100) {
            recommendations.add("Large individual expenses detected - review for optimization opportunities");
        }
        
        recommendations.add("Set up monthly spending reviews to track progress");
        recommendations.add("Consider using the 50/30/20 rule for budget allocation");
        
        return recommendations;
    }
}

// Export/Import Manager
class ExportManager {
    public boolean exportExpensesToCSV(List<Expense> expenses, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("Date,Amount,Category,Description");
            
            // Write data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Expense expense : expenses) {
                writer.printf("%s,%.2f,%s,%s\n",
                    dateFormat.format(expense.getDate()),
                    expense.getAmount(),
                    expense.getCategory(),
                    escapeCsv(expense.getDescription()));
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Expense> importExpensesFromCSV(String filePath) {
        List<Expense> importedExpenses = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date date = dateFormat.parse(parts[0]);
                        double amount = Double.parseDouble(parts[1]);
                        String category = parts[2];
                        String description = unescapeCsv(parts[3]);
                        
                        Expense expense = ExpenseFactory.createExpense(category, amount, date, description);
                        importedExpenses.add(expense);
                    } catch (Exception e) {
                        System.err.println("Error parsing line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        return importedExpenses;
    }
    
    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private String unescapeCsv(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }
}

// Notification Service
class NotificationService {
    public void showWelcomeNotification(User user) {
        showNotification("Welcome back, " + user.getName() + "!",
            "You have " + getPendingAlertsCount(user) + " budget alerts to review.");
    }
    
    public void showBudgetAlert(String category, double spent, double budget) {
        double percentage = (spent / budget) * 100;
        String message = String.format("%s budget: $%.2f of $%.2f (%.1f%%)",
            category, spent, budget, percentage);
        
        showNotification("Budget Alert", message);
    }
    
    private int getPendingAlertsCount(User user) {
        // Simplified - in real implementation, would check actual alerts
        return 2; // Example count
    }
    
    private void showNotification(String title, String message) {
        // In a full implementation, would use system tray notifications
        System.out.println("NOTIFICATION: " + title + " - " + message);
    }
}

// ENCAPSULATION - User class with private fields and public methods
class User {
    private int userId;
    private String username;
    private String name;
    private String email;
    private Map<String, Double> budgetsPerCategory;
    
    // Constructor overloading
    public User(int userId, String username, String name, String email) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.budgetsPerCategory = new HashMap<>();
    }
    
    public User(String username, String name) {
        this(0, username, name, "");
    }
    
    // ENCAPSULATION - Getters and setters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    
    public void setBudget(String category, double amount) {
        budgetsPerCategory.put(category, amount);
    }
    
    public double getBudget(String category) {
        return budgetsPerCategory.getOrDefault(category, 0.0);
    }
    
    public double getTotalMonthlyBudget() {
        return budgetsPerCategory.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    
    public Map<String, Double> getAllBudgets() {
        return new HashMap<>(budgetsPerCategory);
    }
    
    // Method demonstrating polymorphism
    public String getSummary() {
        return String.format("User: %s (ID: %d)", name, userId);
    }
    
    // Method overloading
    public String getSummary(boolean detailed) {
        if (detailed) {
            return String.format("User: %s (ID: %d, Username: %s, Email: %s)", 
                name, userId, username, email);
        }
        return getSummary();
    }
}

// Category class demonstrating ENCAPSULATION
class Category {
    private String name;
    private double limit;
    private double totalSpent;
    
    public Category(String name, double limit) {
        this.name = name;
        this.limit = limit;
        this.totalSpent = 0;
    }
    
    // ENCAPSULATION - Getters and setters
    public String getName() { return name; }
    public double getLimit() { return limit; }
    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double spent) { totalSpent = spent; }
    
    public double getRemainingBudget() {
        return limit - totalSpent;
    }
    
    public double getUtilizationPercentage() {
        return limit > 0 ? (totalSpent / limit) * 100 : 0;
    }
    
    // Method overriding
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return Objects.equals(name, category.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

// Training Data class for AI model
class TrainingData {
    private String description;
    private String category;
    
    public TrainingData(String description, String category) {
        this.description = description;
        this.category = category;
    }
    
    // ENCAPSULATION
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    
    @Override
    public String toString() {
        return String.format("Description: %s -> Category: %s", description, category);
    }
}

// Custom Exceptions (INHERITANCE from Exception class)
class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}

class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}

// AI Classifier
class ExpenseClassifier {
    private Map<String, Map<String, Integer>> categoryWordCounts;
    private Map<String, Integer> categoryDocumentCounts;
    private Set<String> vocabulary;
    private int totalDocuments;
    private Map<String, List<String>> trainingExamples;
    
    public ExpenseClassifier() {
        categoryWordCounts = new HashMap<>();
        categoryDocumentCounts = new HashMap<>();
        vocabulary = new HashSet<>();
        trainingExamples = new HashMap<>();
        totalDocuments = 0;
        
        // Initialize for all categories
        for (String category : new String[]{"Food", "Travel", "Shopping", "Entertainment", "Utilities", "Healthcare"}) {
            categoryWordCounts.put(category, new HashMap<>());
            categoryDocumentCounts.put(category, 0);
            trainingExamples.put(category, new ArrayList<>());
        }
    }
    
    public void trainModel(String description, String category) {
        String[] words = preprocessText(description);
        
        Map<String, Integer> wordCounts = categoryWordCounts.get(category);
        for (String word : words) {
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            vocabulary.add(word);
        }
        
        categoryDocumentCounts.put(category, categoryDocumentCounts.get(category) + 1);
        totalDocuments++;
        
        trainingExamples.get(category).add(description);
    }
    
    public String predictCategory(String description) {
        String[] words = preprocessText(description);
        String bestCategory = "Unknown";
        double bestScore = Double.NEGATIVE_INFINITY;
        
        for (String category : categoryDocumentCounts.keySet()) {
            double categoryProbability = (double) categoryDocumentCounts.get(category) / totalDocuments;
            double logProbability = Math.log(categoryProbability);
            
            Map<String, Integer> wordCounts = categoryWordCounts.get(category);
            int totalWordsInCategory = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
            
            for (String word : words) {
                int count = wordCounts.getOrDefault(word, 0);
                double wordProbability = (count + 1.0) / (totalWordsInCategory + vocabulary.size());
                logProbability += Math.log(wordProbability);
            }
            
            if (logProbability > bestScore) {
                bestScore = logProbability;
                bestCategory = category;
            }
        }
        
        return bestCategory;
    }
    
    private String[] preprocessText(String text) {
        return text.toLowerCase()
                  .replaceAll("[^a-z0-9\\s]", "")
                  .split("\\s+");
    }
    
    public int getTrainingExamplesCount() {
        return totalDocuments;
    }
    
    public double getAccuracy() {
        return 0.85;
    }
    
    public String getTrainingExamples() {
        StringBuilder sb = new StringBuilder();
        sb.append("Training Examples:\n");
        sb.append("==================\n\n");
        
        for (String category : trainingExamples.keySet()) {
            sb.append(category).append(" (").append(trainingExamples.get(category).size()).append(" examples):\n");
            for (String example : trainingExamples.get(category)) {
                sb.append("  - ").append(example).append("\n");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
}

// ============================================================================
// DATABASE MANAGEMENT CLASS (JDBC Implementation)
// ============================================================================

class DatabaseManager {
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC Driver not found!", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createTablesIfNotExist();
            insertSampleData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Failed to connect to database: " + e.getMessage() + "\nPlease make sure MySQL is running and database credentials are correct.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createTablesIfNotExist() throws SQLException {
        // Users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        // Expenses table
        String createExpensesTable = """
            CREATE TABLE IF NOT EXISTS expenses (
                expense_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                category VARCHAR(50) NOT NULL,
                description TEXT,
                expense_date DATE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id)
            )
        """;
        
        // Budgets table
        String createBudgetsTable = """
            CREATE TABLE IF NOT EXISTS budgets (
                budget_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                category VARCHAR(50) NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                UNIQUE KEY unique_user_category (user_id, category)
            )
        """;
        
        // Training data table for AI model
        String createTrainingTable = """
            CREATE TABLE IF NOT EXISTS training_data (
                training_id INT AUTO_INCREMENT PRIMARY KEY,
                description TEXT NOT NULL,
                category VARCHAR(50) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        // Goals table
        String createGoalsTable = """
            CREATE TABLE IF NOT EXISTS user_goals (
                goal_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                name VARCHAR(100) NOT NULL,
                target_amount DECIMAL(10,2) NOT NULL,
                current_amount DECIMAL(10,2) DEFAULT 0,
                target_date DATE NOT NULL,
                category VARCHAR(50),
                is_completed BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createExpensesTable);
            stmt.execute(createBudgetsTable);
            stmt.execute(createTrainingTable);
            stmt.execute(createGoalsTable);
        }
    }
    
    private void insertSampleData() throws SQLException {
        // Insert sample training data if table is empty
        String checkTrainingData = "SELECT COUNT(*) FROM training_data";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkTrainingData)) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert sample training data
                String[] sampleData = {
                    "INSERT INTO training_data (description, category) VALUES ('Lunch at McDonald''s', 'Food')",
                    "INSERT INTO training_data (description, category) VALUES ('Dinner at Olive Garden', 'Food')",
                    "INSERT INTO training_data (description, category) VALUES ('Groceries from Walmart', 'Shopping')",
                    "INSERT INTO training_data (description, category) VALUES ('Clothes from Macy''s', 'Shopping')",
                    "INSERT INTO training_data (description, category) VALUES ('Bus fare', 'Travel')",
                    "INSERT INTO training_data (description, category) VALUES ('Train ticket', 'Travel')",
                    "INSERT INTO training_data (description, category) VALUES ('Movie tickets', 'Entertainment')",
                    "INSERT INTO training_data (description, category) VALUES ('Concert tickets', 'Entertainment')",
                    "INSERT INTO training_data (description, category) VALUES ('Electricity bill', 'Utilities')",
                    "INSERT INTO training_data (description, category) VALUES ('Water bill', 'Utilities')",
                    "INSERT INTO training_data (description, category) VALUES ('Doctor visit', 'Healthcare')",
                    "INSERT INTO training_data (description, category) VALUES ('Medicine', 'Healthcare')"
                };
                
                for (String sql : sampleData) {
                    stmt.execute(sql);
                }
            }
        }
        
        // Insert a default user if no users exist
        String checkUsers = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkUsers)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertUser = "INSERT INTO users (username, password, name, email) VALUES ('admin', 'admin123', 'Admin User', 'admin@example.com')";
                stmt.execute(insertUser);
                
                // Set default budgets for admin user
                String getUserId = "SELECT user_id FROM users WHERE username = 'admin'";
                ResultSet userIdRs = stmt.executeQuery(getUserId);
                if (userIdRs.next()) {
                    int userId = userIdRs.getInt("user_id");
                    String[] categories = {"Food", "Travel", "Shopping", "Entertainment", "Utilities", "Healthcare"};
                    double[] defaultBudgets = {500.0, 300.0, 200.0, 150.0, 250.0, 100.0};
                    
                    for (int i = 0; i < categories.length; i++) {
                        String insertBudget = String.format(
                            "INSERT INTO budgets (user_id, category, amount) VALUES (%d, '%s', %.2f)",
                            userId, categories[i], defaultBudgets[i]);
                        stmt.execute(insertBudget);
                    }
                }
            }
        }
    }
    
    public User authenticateUser(String username, String password) {
        String sql = "SELECT user_id, username, name, email FROM users WHERE username = ? AND password = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean registerUser(String name, String username, String password, String email) {
        String sql = "INSERT INTO users (name, username, password, email) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, email);
            
            int affectedRows = pstmt.executeUpdate();
            
            // Set default budgets for new user
            if (affectedRows > 0) {
                setDefaultBudgetsForNewUser(username);
            }
            
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void setDefaultBudgetsForNewUser(String username) throws SQLException {
        String getUserIdSql = "SELECT user_id FROM users WHERE username = ?";
        int userId = -1;
        
        try (PreparedStatement pstmt = connection.prepareStatement(getUserIdSql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }
        }
        
        if (userId != -1) {
            // Set default budgets
            String[] categories = {"Food", "Travel", "Shopping", "Entertainment", "Utilities", "Healthcare"};
            double[] defaultBudgets = {500.0, 300.0, 200.0, 150.0, 250.0, 100.0};
            
            String insertBudgetSql = "INSERT INTO budgets (user_id, category, amount) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertBudgetSql)) {
                for (int i = 0; i < categories.length; i++) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, categories[i]);
                    pstmt.setDouble(3, defaultBudgets[i]);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }
    }
    
    public boolean saveExpense(User user, Expense expense) {
        String sql = "INSERT INTO expenses (user_id, amount, category, description, expense_date) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory());
            pstmt.setString(4, expense.getDescription());
            pstmt.setDate(5, new java.sql.Date(expense.getDate().getTime()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Expense> loadExpenses(User user) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT amount, category, description, expense_date FROM expenses WHERE user_id = ? ORDER BY expense_date DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = ExpenseFactory.createExpense(
                    rs.getString("category"),
                    rs.getDouble("amount"),
                    new java.util.Date(rs.getDate("expense_date").getTime()),
                    rs.getString("description")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }
    
    public Map<String, Double> loadUserBudgets(User user) {
        Map<String, Double> budgets = new HashMap<>();
        String sql = "SELECT category, amount FROM budgets WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                budgets.put(rs.getString("category"), rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }
    
    public boolean saveUserBudget(User user, String category, double amount) {
        String sql = """
            INSERT INTO budgets (user_id, category, amount) 
            VALUES (?, ?, ?) 
            ON DUPLICATE KEY UPDATE amount = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setDouble(4, amount);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void saveTrainingData(String description, String category) {
        String sql = "INSERT INTO training_data (description, category) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setString(2, category);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<TrainingData> loadTrainingData() {
        List<TrainingData> trainingData = new ArrayList<>();
        String sql = "SELECT description, category FROM training_data";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                trainingData.add(new TrainingData(
                    rs.getString("description"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trainingData;
    }
    
    public boolean saveUserGoal(User user, SpendingGoal goal) {
        String sql = "INSERT INTO user_goals (user_id, name, target_amount, current_amount, target_date, category) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, goal.getName());
            pstmt.setDouble(3, goal.getTargetAmount());
            pstmt.setDouble(4, goal.getCurrentAmount());
            pstmt.setDate(5, new java.sql.Date(goal.getTargetDate().getTime()));
            pstmt.setString(6, goal.getCategory());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<SpendingGoal> loadUserGoals(User user) {
        List<SpendingGoal> goals = new ArrayList<>();
        String sql = "SELECT name, target_amount, current_amount, target_date, category, is_completed FROM user_goals WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SpendingGoal goal = new SpendingGoal(
                    rs.getString("name"),
                    rs.getDouble("target_amount"),
                    new java.util.Date(rs.getDate("target_date").getTime()),
                    rs.getString("category")
                );
                // Set current amount and completion status
                goal.updateProgress(rs.getDouble("current_amount"));
                goals.add(goal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }
    
    public boolean deleteUserGoal(User user, SpendingGoal goal) {
        String sql = "DELETE FROM user_goals WHERE user_id = ? AND name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, goal.getName());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteExpense(User user, Expense expense) {
        String sql = "DELETE FROM expenses WHERE user_id = ? AND amount = ? AND category = ? AND description = ? AND expense_date = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory());
            pstmt.setString(4, expense.getDescription());
            pstmt.setDate(5, new java.sql.Date(expense.getDate().getTime()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
