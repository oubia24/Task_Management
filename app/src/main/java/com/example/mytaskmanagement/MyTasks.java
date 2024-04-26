package com.example.mytaskmanagement;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MyTasks extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    FragmentManager fragmentManager;

    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    FloatingActionButton fab;
    FirebaseAuth mAuth;
    TextView emailUserTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.floatingActionButton);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        mAuth = FirebaseAuth.getInstance();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.navigation_Drawer_Open,R.string.navigation_Drawer_Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        View headerView = navigationView.getHeaderView(0);
        emailUserTextView = headerView.findViewById(R.id.email_user);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemed = item.getItemId();
                if (itemed == R.id.home_task){
                    Toast.makeText(MyTasks.this,"Home for bottom_nav",Toast.LENGTH_SHORT).show();
                    openFragment(new ListdesTasksFragment());
                    return true;
                } else if (itemed == R.id.myFavs) {
                    Toast.makeText(MyTasks.this,"Favorite for bottom_nav",Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemed == R.id.add) {
                    Toast.makeText(MyTasks.this,"Add for bottom_nav",Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemed == R.id.message) {
                    Toast.makeText(MyTasks.this,"Message for bottom_nav",Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        openFragment(new ListdesTasksFragment());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ajouter = new Intent(MyTasks.this, AddTask.class);
                startActivity(ajouter);
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemed = item.getItemId();
        if(itemed == R.id.nav_home){
            Toast.makeText(this,"Home for nav_header",Toast.LENGTH_SHORT).show();

        } else if (itemed == R.id.nav_setting) {
            Toast.makeText(this,"Setting for nav_header",Toast.LENGTH_SHORT).show();

        } else if (itemed == R.id.nav_share) {
            Toast.makeText(this,"Share for nav_header",Toast.LENGTH_SHORT).show();

        } else if (itemed == R.id.nav_about) {
            Toast.makeText(this,"About for nav_header",Toast.LENGTH_SHORT).show();

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth != null){
            emailUserTextView.setText(mAuth.getCurrentUser().getEmail());
        }

    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
    private void openFragment(Fragment fragment){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }
}