package com.assgn.sm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.util.ArrayList;

import parser.JsonResultParser;
import parser.ProductDetails;
import parser.SearchResultParser;
import services.APIClass;
import services.MyResultReceiver;
import services.Queries;

public class ProductDetail extends AppCompatActivity implements View.OnClickListener, MyResultReceiver.Receiver {
    private Toolbar toolbar;
    private ListView listView;
    private Activity activity;
    private ImageView product_image;
    private TextView product_name,product_price,num_product_stores;
    private ProductDetails productDetails;
    private int productId;
    private String productName,productImage;
    private LinearLayout error_loading_screen;
    private ProgressBar progressBar;
    private TextView tryAgain;
    private Handler handler;
    private MyResultReceiver Receiver;
    private LayoutInflater layoutInflator;
    private ListAdapter listAdapter;
    public static JsonResultParser resultParser = new JsonResultParser();
    private ArrayList<ProductDetails.Prices> allStores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        listView  = (ListView) findViewById(R.id.products_stores);

        error_loading_screen = (LinearLayout) findViewById(R.id.error_loading_screen);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tryAgain=  (TextView) findViewById(R.id.tryAgain);
        activity = this;
        layoutInflator = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View header = layoutInflator.inflate(R.layout.product_details_header, null);
        product_name = (TextView) header.findViewById(R.id.product_name);
        product_image=  (ImageView) header.findViewById(R.id.product_image);
        product_price = (TextView) header.findViewById(R.id.product_bestprice);
        num_product_stores = (TextView) header.findViewById(R.id.num_product_stores);
        handler = new Handler();
        Receiver= new MyResultReceiver(handler);
        Receiver.setReceiver(ProductDetail.this);
        listAdapter = new ListAdapter();
        getActionBarToolbar();
      //  productId = getIntent().getIntExtra("id",-1);
        productId = -1;
        productName = getIntent().getStringExtra("name");
        productImage=  getIntent().getStringExtra("imgurl");
        UrlImageViewHelper.setUrlDrawable(product_image,productImage);
        product_name.setText(productName);
        listView.addHeaderView(header,null,false);
        if(productId==-1)
            productId = 2179;
        error_loading_screen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        callApi(Queries.QueriesUrl("product_full",new String[]{Integer.toString(productId)}), 3);
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
    public void onClick(View v) {

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String data = resultData.getString("apiresult");
        switch (resultCode)
        {
            case 3:
                try{

                    productDetails= (ProductDetails) resultParser.getJsonMap(data,3);
                    if(productDetails.request_status.equalsIgnoreCase("SUCCESS"))
                    {
                        allStores = productDetails.request_result.prices;
                        error_loading_screen.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        int bestPrice=Integer.MAX_VALUE;
                        for(int i=0;i<allStores.size();i++)
                        {
                            if(Integer.parseInt(allStores.get(i).price)<bestPrice)
                                bestPrice = Integer.parseInt(allStores.get(i).price);
                        }
                        product_price.setText("Best price: Rs. "+ Integer.toString(bestPrice));
                        num_product_stores.setText("Available at "+allStores.size()+" Stores");
                        listView.setAdapter(listAdapter);
                        listAdapter.notifyDataSetChanged();
                    }
                    else
                    {
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
            return allStores.size();

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
                    convertView = mInflater.inflate(R.layout.single_store_details, null);
                    holder.store_logo = (ImageView) convertView.findViewById(R.id.store_logo);
                    holder.buy = (TextView) convertView.findViewById(R.id.buy);
                    holder.price = (TextView) convertView.findViewById(R.id.price);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                UrlImageViewHelper.setUrlDrawable(holder.store_logo,allStores.get(position).logo);
                holder.price.setText("Rs. "+allStores.get(position).price);
                holder.buy.setOnClickListener(new setItemClick(position, holder.buy));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }


    class ViewHolder {
        ImageView store_logo;
        TextView price, buy;
    }

    class setItemClick implements View.OnClickListener {
        final TextView buy;
        final int position;

        public setItemClick(int position, TextView buy) {
            this.position = position;
            this.buy = buy;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.buy:
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(allStores.get(position).link));
                    startActivity(i);
                    break;
                default:
                    break;
            }
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
