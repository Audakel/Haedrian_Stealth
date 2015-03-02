package com.haedrian.haedrian;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.haedrian.haedrian.Adapters.InvestListAdapter;


public class InvestActivity extends ActionBarActivity {
    RecyclerView mRecyclerView;
    InvestListAdapter mInvestListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);

        mInvestListAdapter = new InvestListAdapter(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.investListView);
        mRecyclerView.setAdapter(mInvestListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_invest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
