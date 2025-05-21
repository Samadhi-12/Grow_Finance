# 🌱 Grow Finance

**Grow Finance** is a modern personal finance tracking Android application built using **Kotlin**. It allows users to manage their daily transactions, categorize income/expenses, set budgets, and visualize spending trends through dynamic charts and detailed reports.

---

## 📱 Features

* ✅ Add, view, update, and delete transactions
* 📊 Category-wise filtering and reporting
* 📈 Analytics with PieChart and LineChart using MPAndroidChart
* 💼 Monthly budget tracking
* 📀 Data persistence with SharedPreferences
* 📄 Export/Import data as JSON files
* 🔔 Notifications and alerts for budget limits (optional)

---

## 🧱 Tech Stack

* **Language:** Kotlin
* **UI Design:** XML with ViewBinding
* **Persistence:** SharedPreferences (with support for JSON import/export)
* **Charts & Graphs:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
* **Architecture:** MVVM (Model-View-ViewModel)
---

## ⚙️ Installation & Run

1. Clone the repository:

   ```bash
   git clone https://github.com/Samadhi-12/Grow_Finance
   ```
2. Open the project in **Android Studio**
3. Build & Run the app on an emulator or Android device (API 21+)

---

## 📄 Data Import & Export

* Export transactions to a JSON file via file picker
* Import transactions from a saved backup JSON file

> All file operations use Android’s SAF (Storage Access Framework) for secure URI-based file handling.

---

## 🗓️ Future Improvements

* Cloud sync (Firebase/Google Drive)
* Advanced budget alerts with notifications
* Multi-user support and login
* Dark mode

---

## 🧑‍💻 Contributors

* 👨‍💻 Lead Developer: Samadhi Gunasena
* 🎨 UI/UX Design: Samadhi Gunasena

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

