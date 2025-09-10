# SmartExpenseTracker
Java OOP Project: Smart Expense Tracker with Budget Alerts (Swing GUI)

🚀 Smart Expense Tracker

A Java OOP Project with a simple Swing-based GUI that helps users manage their expenses, set category-wise budgets, and get alerts when limits are exceeded.

📌 Features

👤 User Management – Register users and set budgets per category.

💰 Expense Tracking – Add expenses under categories (Food, Travel, Shopping).

⚡ Budget Alerts – Get alerts when expenses exceed set limits.

🎭 OOP Concepts – Uses inheritance, polymorphism, and abstraction.

🛡 Exception Handling – Handles invalid inputs gracefully.

🖥 GUI – Built with Java Swing.

🏗 Project Structure
SmartExpenseTracker/
│── src/com/expenseTracker/
│   ├── MainApp.java                 # Entry point
│   ├── User.java                    # User details & budgets
│   ├── Expense.java                 # Abstract class
│   ├── FoodExpense.java             # Subclass
│   ├── TravelExpense.java           # Subclass
│   ├── ShoppingExpense.java         # Subclass
│   ├── Category.java                # Category details
│   ├── BudgetManager.java           # Budget logic & alerts
│   ├── InvalidAmountException.java  # Custom exception
│   ├── CategoryNotFoundException.java
│   └── UI/
│       └── MainMenu.java            # Swing GUI
│── README.md
│── .gitignore

👥 Team Members (B.Tech AIML - Group of 5)

Member 1 → User & Authentication

Member 2 → Expense & Category

Member 3 → Budget & Alerts

Member 4 → Exception Handling & Validation

Member 5 → UI & Integration (MainApp + Swing GUI)

⚙️ How to Run

Clone the repo:

git clone https://github.com/kasturirasekar/SmartExpenseTracker.git
cd SmartExpenseTracker


Open in IntelliJ IDEA / Eclipse / VS Code with Java extension.

Run the MainApp.java file.

Use the Swing GUI to add expenses, view summaries, and check budget alerts.

🎯 OOP Concepts Used

Encapsulation – Classes like User, Category encapsulate data.

Inheritance – FoodExpense, TravelExpense, ShoppingExpense inherit from Expense.

Polymorphism – getCategory() overridden in each expense type.

Abstraction – Expense is an abstract class.

Exception Handling – Custom exceptions for invalid inputs.
