package com.haedrian.haedrian.HomeScreen.Wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.haedrian.haedrian.Adapters.TransactionListAdapter;
import com.haedrian.haedrian.R;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView transactionList;
    private List<String> transactions;
    private ArrayList<String> arrayList = new ArrayList<>();
    private TransactionListAdapter adapter;
    private Context context;
    private TextView noTransactions;

    public TransactionFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TransactionFragment newInstance(int sectionNumber) {
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction, container, false);

        transactionList = (ListView) rootView.findViewById(R.id.transaction_list);
        noTransactions = (TextView) rootView.findViewById(R.id.no_transaction_textview);

        transactions = new ArrayList<String>();

        this.context = rootView.getContext();

        initializeTransactions();

        return rootView;
    }

    private void initializeTransactions() {
        noTransactions.setVisibility(View.GONE);

        for (int i = 0; i < 7; i++) {
            arrayList.add("5.00");
        }

        adapter = new TransactionListAdapter(context, R.layout.row_transaction, arrayList);
        transactionList.setAdapter(adapter);
        transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, TransactionDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser)
//        {
//
//            noTransactions.setVisibility(View.GONE);
//
//            for (int i = 0; i < 3; i++) {
//                arrayList.add( i + ".00");
//            }
//
//            adapter = new TransactionListAdapter(context, R.layout.row_transaction, arrayList);
//            transactionList.setAdapter(adapter);
//            transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(context, TransactionDetailsActivity.class);
//                    startActivity(intent);
//                }
//            });
//
//
//
//                arrayList.clear();
//
//                // Get all transactions here
//                ParseQuery<ParseObject> transactionQuery = new ParseQuery("Transaction");
//                transactionQuery.whereEqualTo("senderId", parseId);
//                transactionQuery.findInBackground(new FindCallback<ParseObject>() {
//                    @Override
//                    public void done(List<ParseObject> parseObjects, ParseException e) {
//                        if (e == null) {
//                            // If it exists
//                            if (parseObjects.size() > 0) {
//
//                                noTransactions.setVisibility(View.GONE);
//
//                                for (int i = 0; i < parseObjects.size(); i++) {
//                                    arrayList.add(parseObjects.get(i));
//                                }
//
//                                adapter = new TransactionListAdapter(context, R.layout.row_transaction, arrayList);
//                                transactionList.setAdapter(adapter);
//                                transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        Intent intent = new Intent(context, TransactionDetailsActivity.class);
//                                        startActivity(intent);
//                                    }
//                                });
//                            } else {
//
//                            }
//
//                        } else {
//                            Toast.makeText(context, getResources().getString(R.string.msg_error) + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//
//        }
//    }
}
