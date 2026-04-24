package com.smartfinance.pro.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.smartfinance.pro.data.dao.BudgetDao;
import com.smartfinance.pro.data.dao.TransactionDao;
import com.smartfinance.pro.data.model.Budget;
import com.smartfinance.pro.data.model.Transaction;

/**
 * Main Room Database class for Smart Finance Pro.
 * Singleton pattern ensures only one instance exists.
 */
@Database(
    entities = {Transaction.class, Budget.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "smart_finance_pro.db";
    private static volatile AppDatabase INSTANCE;

    // ─── Abstract DAO accessors ────────────────────────────────────────
    public abstract TransactionDao transactionDao();
    public abstract BudgetDao budgetDao();

    // ─── Singleton getInstance ─────────────────────────────────────────
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    /** For testing purposes only */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
