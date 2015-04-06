package com.haedrian.haedrian.HomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.ProjectsList;
import com.haedrian.haedrian.R;

import java.util.Arrays;
import java.util.List;


public class InvestActivity extends ActionBarActivity {

    private Button trendingButton;
    private ListView categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trendingButton = (Button) findViewById(R.id.trending_button);
        categoryList = (ListView) findViewById(R.id.category_list);

        List<String> values = Arrays.asList(getResources().getStringArray(R.array.categories_array));
        ArrayAdapter<String> categories = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);

        categoryList.setAdapter(categories);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.trending_button:
                Intent intent = new Intent(this, ProjectsList.class);
                startActivity(intent);
                break;
            default:
                break;
        }
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
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
        FlurryAgent.logEvent(this.getClass().getSimpleName() + " opened");
    }
    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
