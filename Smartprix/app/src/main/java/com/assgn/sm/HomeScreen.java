package com.assgn.sm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import parser.Categories;
import parser.JsonResultParser;
import services.APIClass;
import services.MyResultReceiver;
import services.Queries;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener, MyResultReceiver.Receiver{
    private Toolbar toolbar;
    private Activity activity;
    private ListView list_category;
    private ListAdapter listAdapter;
    private ArrayList<String> productCategory;
    private ArrayList<String> productCategoryMatched;
    private EditText search_text;
    private TextView search_btn;
    private String searchedText = "";
    private Handler handler;
    private MyResultReceiver Receiver;
    private Categories categories =null;
    private LinearLayout error_loading_screen;
    private ProgressBar progressBar;
    private TextView tryAgain;
    public static JsonResultParser resultParser = new JsonResultParser();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
        activity = this;
        getActionBarToolbar();

        error_loading_screen = (LinearLayout) findViewById(R.id.error_loading_screen);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tryAgain=  (TextView) findViewById(R.id.tryAgain);
        list_category = (ListView) findViewById(R.id.list_category);
        search_text = (EditText) findViewById(R.id.search_text);
        search_btn = (TextView) findViewById(R.id.search_btn);

        productCategory = new ArrayList<>();
        productCategoryMatched = new ArrayList<>();
        handler = new Handler();
        Receiver = new MyResultReceiver(handler);
        Receiver.setReceiver(HomeScreen.this);

        listAdapter = new ListAdapter();
        error_loading_screen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);;
        callApi(Queries.QueriesUrl("categories",new String[]{}), 1);
        search_btn.setOnClickListener(this);

        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!searchedText.equalsIgnoreCase(s.toString())) {
                    searchedText = "";
                    productCategoryMatched.clear();
                    listAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void callApi(final String url, final int type) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                new APIClass(type, Receiver).execute(url);
            }
        });
    }
    public Toolbar getActionBarToolbar() {
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.main_toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
        return toolbar;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                searchedText = search_text.getText().toString();
                String CorrrectSearchTerm = "";
                productCategoryMatched.clear();

                if (searchedText.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Please enter exact search text or click on list item! ", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < productCategory.size(); i++) {
                        if (productCategory.get(i).toUpperCase().equalsIgnoreCase(searchedText.toUpperCase()))
                            CorrrectSearchTerm = productCategory.get(i);
                        if (productCategory.get(i).toLowerCase().contains(searchedText.toLowerCase())) {
                            productCategoryMatched.add(productCategory.get(i));
                        }
                    }
                    if (productCategoryMatched.size()!=0 )
                        listAdapter.notifyDataSetChanged();
                }
                if (!CorrrectSearchTerm.equalsIgnoreCase("") ) {
                    Intent i  = new Intent(activity,ProductListing.class);
                    i.putExtra("category",CorrrectSearchTerm);
                    startActivity(i);

                } else {
                    Toast.makeText(activity, "Please enter exact search text or click on list item!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String data = resultData.getString("apiresult");

        switch (resultCode)
        {
            case 1:
                try{
                  categories= (Categories) resultParser.getJsonMap(data,1);

                  if(categories.request_status.equalsIgnoreCase("SUCCESS"))
                  {
                      productCategory = categories.request_result;
                      error_loading_screen.setVisibility(View.GONE);
                      progressBar.setVisibility(View.GONE);
                      progressBar.setVisibility(View.GONE);
                      list_category.setAdapter(listAdapter);
                  }
                    else {
                      error_loading_screen.setVisibility(View.VISIBLE);
                      tryAgain.setVisibility(View.VISIBLE);
                      progressBar.setVisibility(View.GONE);
                  }


            }
          catch (Exception e)
          {
              e.printStackTrace();
              error_loading_screen.setVisibility(View.GONE);
          }


                break;

            default:
                break;
        }

    }

    public class ListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public ViewHolder holder;

        public ListAdapter() {
            this.mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (searchedText.equalsIgnoreCase(""))
                return productCategory.size();
            else
                return productCategoryMatched.size();

        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            try {
                holder = new ViewHolder();
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.listview_item, null);
                    holder.category_name = (TextView) convertView.findViewById(R.id.category_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (searchedText.equalsIgnoreCase(""))
                    holder.category_name.setText(productCategory.get(position));
                else
                    holder.category_name.setText(productCategoryMatched.get(position));

                convertView.setOnClickListener(new setItemClick(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }


    class ViewHolder {
        TextView category_name;
    }

    class setItemClick implements View.OnClickListener {
        final int position;

        public setItemClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

                    Intent i  = new Intent(activity,ProductListing.class);
                    if (searchedText.equalsIgnoreCase(""))
                        i.putExtra("category",productCategory.get(position));
                    else
                        i.putExtra("category",productCategoryMatched.get(position));
                    startActivity(i);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
