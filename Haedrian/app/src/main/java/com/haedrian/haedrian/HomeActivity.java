package com.haedrian.haedrian;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class HomeActivity extends ActionBarActivity {
    // Nav Drawer stuff
    private String[] mHomeButtons = {"Home", "Add", "Projects", "Invest"};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_nav_item, mHomeButtons));
        // Set the list's click listener
       // mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        Intent intent;

        switch(view.getId()) {
            case R.id.send_request:
                intent = new Intent(this, SendRequestActivity.class);
                startActivity(intent);
                return;
            case R.id.add:
                intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                return;
            case R.id.projects:
                intent = new Intent(this, ProjectsActivity.class);
                startActivity(intent);
                return;
            case R.id.invest:
                intent = new Intent(this, InvestActivity.class);
                startActivity(intent);
                return;
            default:
                return;
        }

    }
}
