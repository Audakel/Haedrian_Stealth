package com.haedrian.haedrian.HomeScreen.Contacts;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;


import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.BuildConfig;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.util.Utils;


/**
 * FragmentActivity to hold the main {@link ContactsListFragment}. On larger screen devices which
 * can fit two panes also load {@link ContactDetailFragment}.
 */
public class GetContacts extends Activity implements
        ContactsListFragment.OnContactsInteractionListener {

    // Defines a tag for identifying log entries
    private static final String TAG = "ContactsListActivity";

    private ContactDetailFragment mContactDetailFragment;

    // If true, this is a larger screen device which fits two panes
    private boolean isTwoPaneLayout;

    // True if this activity instance is a search result view (used on pre-HC devices that load
    // search results in a separate instance of the activity rather than loading results in-line
    // as the query is typed.
    private boolean isSearchResultView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);

        // Set main content view. On smaller screen devices this is a single pane view with one
        // fragment. One larger screen devices this is a two pane view with two fragments.
        setContentView(R.layout.activity_get_contacts);

        // Check if two pane bool is set based on resource directories
        isTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);

        // Check if this activity instance has been triggered as a result of a search query. This
        // will only happen on pre-HC OS versions as from HC onward search is carried out using
        // an ActionBar SearchView which carries out the search in-line without loading a new
        // Activity.
//        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
//
//            // Fetch query from intent and notify the fragment that it should display search
//            // results instead of all contacts.
//            String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
//            ContactsListFragment mContactsListFragment = (ContactsListFragment)
//                    getFragmentManager().findFragmentById(R.id.contact_list);
//
//            // This flag notes that the Activity is doing a search, and so the result will be
//            // search results rather than all contacts. This prevents the Activity and Fragment
//            // from trying to a search on search results.
//            isSearchResultView = true;
//            mContactsListFragment.setSearchQuery(searchQuery);
//
//            // Set special title for search results
//            String title = getString(R.string.contacts_list_search_results_title, searchQuery);
//            setTitle(title);
//        }
//
//        if (isTwoPaneLayout) {
//            // If two pane layout, locate the contact detail fragment
//            mContactDetailFragment = (ContactDetailFragment)
//                    getFragmentManager().findFragmentById(R.id.contact_detail);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
        FlurryAgent.logEvent(this.getClass().getName() + " opened.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param cursor The contact Uri to the selected contact.
     */
    @Override
    public void onContactSelected(Cursor cursor, int position) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        startActivity(intent);
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact is no longer selected.
     */
    @Override
    public void onSelectionCleared() {
        if (isTwoPaneLayout && mContactDetailFragment != null) {
            mContactDetailFragment.setContact(null);
        }
    }

    @Override
    public boolean onSearchRequested() {
        // Don't allow another search if this activity instance is already showing
        // search results. Only used pre-HC.
        return !isSearchResultView && super.onSearchRequested();
    }
}