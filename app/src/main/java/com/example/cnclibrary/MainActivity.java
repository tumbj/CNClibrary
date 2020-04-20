package com.example.cnclibrary;//package com.example.cnclibrary;

import android.content.Intent;
import android.os.Bundle;

import com.example.cnclibrary.admin.AddBookActivity;
import com.example.cnclibrary.admin.ListBorrowedActivity;
import com.example.cnclibrary.ui.scanner.BorrowActivity;
import com.example.cnclibrary.ui.scanner.ReturnActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);



    }

    public void borrowClk(android.view.View view) {
        Intent intent = new Intent(this, BorrowActivity.class);
        startActivity(intent);
    }

    public void returnClk(android.view.View view) {
        Intent intent = new Intent(this, ReturnActivity.class);
        startActivity(intent);
    }

    public void addBookClk(android.view.View view) {
        Intent intent = new Intent(this, AddBookActivity.class);
        startActivity(intent);
    }

    public void  onClkListBorrowed(android.view.View view) {
        Intent intent = new Intent(this, ListBorrowedActivity.class);
        startActivity(intent);
    }
}
