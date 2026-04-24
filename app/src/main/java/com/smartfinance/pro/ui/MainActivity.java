package com.smartfinance.pro.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartfinance.pro.R;
import com.smartfinance.pro.databinding.ActivityMainBinding;
import com.smartfinance.pro.ui.transaction.AddTransactionActivity;

/**
 * Main host activity containing Bottom Navigation and NavHostFragment.
 * Houses: Dashboard, Transactions, Budget, Insight, Report fragments.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check PIN lock
        SharedPreferences prefs = getSharedPreferences("smart_finance_prefs", MODE_PRIVATE);
        boolean pinEnabled = prefs.getBoolean("pin_enabled", false);
        if (pinEnabled && savedInstanceState == null) {
            startActivity(new Intent(this, com.smartfinance.pro.ui.auth.PinLockActivity.class));
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment)
            getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }

        // FAB → Add Transaction
        binding.fabAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            startActivity(intent);
        });

        // Hide/show FAB on destination change
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (id == R.id.dashboardFragment || id == R.id.transactionFragment) {
                binding.fabAddTransaction.show();
            } else {
                binding.fabAddTransaction.hide();
            }
        });
    }
}
