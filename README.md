# 💰 Smart Finance Pro

Aplikasi pencatatan keuangan Android profesional dengan AI Financial Insight.

![Min SDK](https://img.shields.io/badge/Min_SDK-24-green)
![Language](https://img.shields.io/badge/Language-Java-orange)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 🚀 Cara Setup & Build

### Langkah 1 — Clone / Extract Project
```bash
# Jika dari GitHub
git clone https://github.com/USERNAME/SmartFinancePro.git
cd SmartFinancePro
```

### Langkah 2 — Download gradle-wrapper.jar (WAJIB)
```bash
# Linux / Mac
curl -L "https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar" \
  -o gradle/wrapper/gradle-wrapper.jar

# Atau buka dengan Android Studio — otomatis didownload
```

### Langkah 3 — Build dengan Android Studio
1. Buka Android Studio
2. File → Open → pilih folder `SmartFinancePro`
3. Tunggu Gradle sync selesai
4. Build → Build APK(s) → Build Debug APK

### Langkah 4 — Build via Command Line
```bash
chmod +x gradlew
./gradlew assembleDebug

# APK tersimpan di:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📦 Upload ke GitHub + Auto Build APK

### 1. Buat repository di GitHub
```bash
git init
git add .
git commit -m "Initial commit: Smart Finance Pro"
git branch -M main
git remote add origin https://github.com/USERNAME/SmartFinancePro.git
git push -u origin main
```

### 2. GitHub Actions akan otomatis:
- ✅ Build project
- ✅ Generate APK debug
- ✅ Upload APK sebagai artifact (bisa didownload)

### 3. Download APK dari GitHub:
- Buka tab **Actions** di repository
- Klik workflow run terbaru
- Scroll ke bawah → **Artifacts**
- Download `SmartFinancePro-Debug-APK`

---

## 📱 Fitur Aplikasi

| Fitur | Status |
|-------|--------|
| Dashboard dengan balance card | ✅ |
| Bar chart 6 bulan | ✅ |
| CRUD Transaksi | ✅ |
| Filter & Search transaksi | ✅ |
| Swipe to delete | ✅ |
| Budget per kategori | ✅ |
| Progress bar budget | ✅ |
| AI Financial Insight | ✅ |
| Laporan bulanan + Pie chart | ✅ |
| PIN Lock keamanan | ✅ |
| Room Database (offline) | ✅ |

---

## 🏗️ Arsitektur

```
app/
├── data/
│   ├── model/          ← Room Entities (Transaction, Budget)
│   ├── dao/            ← Data Access Objects
│   ├── database/       ← AppDatabase (Singleton)
│   └── repository/     ← TransactionRepository, BudgetRepository
├── viewmodel/          ← MVVM ViewModels
├── ui/
│   ├── dashboard/      ← DashboardFragment
│   ├── transaction/    ← TransactionFragment, AddTransactionActivity
│   ├── budget/         ← BudgetFragment, BudgetAdapter
│   ├── insight/        ← InsightFragment, InsightAdapter
│   ├── report/         ← ReportFragment
│   └── auth/           ← PinLockActivity
├── service/
│   └── AIInsightEngine ← Rule-based AI analysis
└── utils/
    ├── Categories      ← Category constants + colors
    └── FormatUtils     ← IDR formatting, date helpers
```

---

## 🤖 AI Insight Engine

Tanpa API eksternal — menggunakan rule-based analysis:
- **Deteksi lonjakan** pengeluaran per kategori (>20% dari bulan lalu)
- **Rasio income/expense** — warning jika >80%
- **Kategori dominan** — flagging jika satu kategori >40% total
- **Peluang hemat** — saran konkret dengan estimasi Rupiah
- **Reinforcement positif** — apresiasi ketika keuangan sehat

---

## 📊 Tech Stack

- **Bahasa**: Java (Native Android)
- **Arsitektur**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **UI**: Material Design 3
- **Charts**: MPAndroidChart v3.1.0
- **Navigation**: Jetpack Navigation Component
- **Lifecycle**: LiveData + ViewModel
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
