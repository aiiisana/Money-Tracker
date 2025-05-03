# **MoneyTracker**
**Personal finance tracking app with analytics, categories, and account management**

---

## **Features**

### **User Flow**
- **Welcome Screen**: Sign up or log in
- **Account Creation**: Name, email, password
- **Authentication**: Access the main dashboard
- **Premium Features**:
  - Advanced analytics
  - Data export
  - Custom themes
  - Multi-account sync

---

### **1. Budgets**
- View budgets for selected period (month/week)
- Track spending limits

### **2. Category Analytics**
- Visual breakdown of expenses by category
- Pie charts and trend graphs

### **3. Transaction History**
- Full list of records (expenses, income, transfers)
- Filter by date, category, type, amount

### **4. Add Transactions and Transfers**
- Add:
  - Income or expense
  - Internal transfer between accounts
  - Amount, category/subcategory, note, date, payment method
- Seamless navigation between transaction and transfer screens

### **5. Custom Categories and Subcategories**
- Create new categories with icons
- Add subcategories with icon selection
- Categories are used in transaction records and analytics

### **6. Card Management**
- Add, view, edit, and delete accounts (cash, bank, etc.)
- Track card limits and balances

### **7. User Profile**
- Edit name, email, password

### **8. Password Recovery**
- Forgot password flow via Firebase reset email

---

## **Tech Stack**

| Technology | Purpose |
|------------|---------|
| **Kotlin + Jetpack (MVVM)** | Main language and architecture |
| **Room** | Local storage for transactions and transfers |
| **Firebase Auth** | User authentication |
| **Firebase DB (Realtime/Firestore)** | Cloud storage for account data |
| **SharedPreferences** | Store login state and settings |
| **StateFlow / ViewModel** | Reactive UI state management |

---

## **Installation**

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/MoneyTracker.git
   ```
2.	Open in Android Studio
3.	Add google-services.json for Firebase integration
4.	Run on emulator or device
