package com.assgn.sm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.util.ArrayList;

import parser.JsonResultParser;
import parser.SearchResultParser;
import parser.SingleProduct;
import services.APIClass;
import services.MyResultReceiver;
import services.Queries;

public class ProductListing extends AppCompatActivity implements MyResultReceiver.Receiver {
    private Toolbar toolbar;
    private Activity activity;
    private String category="";
    private LinearLayout error_loading_screen;
    private ProgressBar progressBar,load_more_progreesBar;
    private TextView tryAgain,load_more;
    private Handler handler;
    private MyResultReceiver Receiver;
    private SearchResultParser searchResultParser =null;
    private ListAdapter listAdapter;
    private ListView listView;
    private boolean flag_loading =false;
    private int countProducts=0;
    private LinearLayout footerView;
    private LayoutInflater layoutInflator;
    public static JsonResultParser resultParser = new JsonResultParser();
    private ArrayList<SearchResultParser.Results> searchResults = new ArrayList<>();
    private ArrayList<SearchResultParser.Results> searchedResults = new ArrayList<>();
    private EditText search_text;
    private TextView category_text;
    private String searchText="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing);
        activity = this;
        getActionBarToolbar();

        category = getIntent().getStringExtra("category");
        listView = (ListView) findViewById(R.id.products_category);
        error_loading_screen = (LinearLayout) findViewById(R.id.error_loading_screen);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tryAgain=  (TextView) findViewById(R.id.tryAgain);
        layoutInflator = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        footerView = (LinearLayout) layoutInflator.inflate(
                R.layout.load_more_footer, null);
        layoutInflator = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View header = layoutInflator.inflate(R.layout.product_listing_header, null);
        category_text = (TextView) header.findViewById(R.id.category);
        load_more= (TextView) footerView
                .findViewById(R.id.load_more);
        load_more.setOnClickListener(null);

        load_more_progreesBar = (ProgressBar) footerView
                .findViewById(R.id.load_more_progressBar);
        load_more_progreesBar.setVisibility(View.GONE);
        footerView.setLongClickable(false);
        footerView.setClickable(false);

        handler = new Handler();
        Receiver= new MyResultReceiver(handler);
        Receiver.setReceiver(ProductListing.this);
        listAdapter = new ListAdapter();
        category_text.setText(category);
        listView.addHeaderView(header,null,false);
        listView.addFooterView(footerView);
        search_text = (EditText) findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchedResults.clear();
                searchText = s.toString();
                if(!s.toString().equalsIgnoreCase(""))
                    flag_loading = true;
                else
                    flag_loading = false;
                for(int i=0;i<searchResults.size();i++)
                {
                    if(searchResults.get(i).name.toUpperCase().contains(s.toString().toUpperCase()))
                    {
                     searchedResults.add(searchResults.get(i));
                    }
                }
                if(!searchText.equalsIgnoreCase(""))
                    listAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(flag_loading == false)
                    {
                        flag_loading = true;
                        countProducts+=10;
                        load_more_progreesBar.setVisibility(View.VISIBLE);
                        load_more.setVisibility(View.VISIBLE);
                        load_more.setText("LOADING...");
                        callApi(Queries.QueriesUrl("search",new String[]{category,Integer.toString(countProducts)}), 4);
                    }
                }
            }
        });
        error_loading_screen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        callApi(Queries.QueriesUrl("search",new String[]{category,Integer.toString(countProducts)}), 4);


    }
    private void callApi(final String url, final int type) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                new APIClass(type, Receiver).execute(url);
            }
        });
    }
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String data = resultData.getString("apiresult");
        switch (resultCode)
        {
            case 4:
                try{
                    searchResultParser= (SearchResultParser) resultParser.getJsonMap(data,4);
                    if(searchResultParser.request_status.equalsIgnoreCase("SUCCESS"))
                    {
                        for(int i=0;i<searchResultParser.request_result.results.size();i++)
                        {
                            searchResults.add(searchResultParser.request_result.results.get(i));
                        }
                        error_loading_screen.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        load_more_progreesBar.setVisibility(View.GONE);
                        load_more.setVisibility(View.GONE);
                        if(countProducts==0)
                            listView.setAdapter(listAdapter);
                        searchedResults.clear();
                        listAdapter.notifyDataSetChanged();
                        flag_loading =false;
                    }
                    else
                    {
                        error_loading_screen.setVisibility(View.VISIBLE);
                        tryAgain.setVisibility(View.VISIBLE);
                        tryAgain.setText("Server problem! Please try again later ! ");
                        progressBar.setVisibility(View.GONE);
                        load_more_progreesBar.setVisibility(View.GONE);
                        load_more.setVisibility(View.GONE);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    tryAgain.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    tryAgain.setText("No Response from server for the "+category+".\n Please try some other category! ");
                    error_loading_screen.setVisibility(View.VISIBLE);
                    load_more_progreesBar.setVisibility(View.GONE);
                    load_more.setVisibility(View.GONE);

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
            if(searchText.equalsIgnoreCase(""))
            return searchResults.size();
            else
                return searchedResults.size();

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
                    convertView = mInflater.inflate(R.layout.listitem_products_category, null);
                    holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                    holder.product_price = (TextView) convertView.findViewById(R.id.product_price);
                    holder.product_image = (ImageView) convertView.findViewById(R.id.product_image);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                convertView.setOnClickListener(new setItemClick(position));
                if(searchText.equalsIgnoreCase(""))
                {
                    holder.product_name.setText(searchResults.get(position).name);
                    UrlImageViewHelper.setUrlDrawable(holder.product_image,searchResults.get(position).img_url);
                    holder.product_price.setText("Rs."+Integer.toString(searchResults.get(position).price));
                }
                else
                {
                    holder.product_name.setText(searchedResults.get(position).name);
                    UrlImageViewHelper.setUrlDrawable(holder.product_image,searchedResults.get(position).img_url);
                    holder.product_price.setText("Rs. "+searchedResults.get(position).price);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }


    class ViewHolder {
        TextView product_name,product_price;
        ImageView product_image;
    }

    class setItemClick implements View.OnClickListener {
        final int position ;
        public setItemClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(activity, ProductDetail.class);
            if(searchText.equalsIgnoreCase(""))
            {
                i.putExtra("id", searchResults.get(position).id);
                i.putExtra("name",searchResults.get(position).name);
                i.putExtra("imgurl",searchResults.get(position).img_url);

            }
            else {
                i.putExtra("id", searchedResults.get(position).id);
                i.putExtra("name",searchedResults.get(position).name);
                i.putExtra("imgurl",searchedResults.get(position).img_url);
            }
            startActivity(i);
        }
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
}
