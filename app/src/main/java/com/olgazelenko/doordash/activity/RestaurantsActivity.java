package com.olgazelenko.doordash.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.olgazelenko.doordash.R;
import com.olgazelenko.doordash.adapter.RestaurantAdaptor;
import com.olgazelenko.doordash.model.Restaurant;
import com.olgazelenko.doordash.network.GetDataService;
import com.olgazelenko.doordash.network.RetrofitClientInstance;
import com.olgazelenko.doordash.utils.DividerItemDecoration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantsActivity extends AppCompatActivity {
    private final String URL_REQUEST = "URL_REQUEST";
    private List<Restaurant> restaurantListResponseData;
    private RecyclerView restaurantRecyclerView;
    private TextView emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants_activity);
        InitViewById();
        getRestaurantsListData();
    }

    private void InitViewById() {
        this.setTitle(getResources().getString(R.string.activity_label_disover));
        restaurantRecyclerView = findViewById(R.id.list);
        emptyView = findViewById(R.id.empty_view);
        restaurantRecyclerView.addItemDecoration(new DividerItemDecoration(
                getApplicationContext()
        ));
    }

    private void getRestaurantsListData() {
        /*display a progress dialog*/
        final ProgressDialog progressDialog = new ProgressDialog(RestaurantsActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage(getString(R.string.msg_wait)); // set message
        progressDialog.show(); // show progress dialog

        Map<String, Object> map = getQueryParamsMap();

        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance.getClient().create(GetDataService.class);
        Call<List<Restaurant>> call = service.getRestaurantList(map);
        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (!response.isSuccessful()) {
                    Log.d(URL_REQUEST, response.raw().request().url().toString());
                    emptyView.setText(R.string.msg_no_restaurants);
                }
                restaurantListResponseData = response.body();
                if (restaurantListResponseData == null) {
                    emptyView.setText(R.string.msg_error);
                } else {
                    setDataInRecyclerView();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                if (t instanceof IOException)
                    emptyView.setText(R.string.no_internet_connection);
                else
                    emptyView.setText(R.string.msg_error);
                progressDialog.dismiss();
            }
        });
    }

    private Map<String, Object> getQueryParamsMap() {
        double lat = 37.422740;
        double lng = -122.139956;
        int offset = 0;
        int limit = 20;

        Map<String, Object> map = new HashMap<>();
        map.put("lat", lat);
        map.put("lng", lng);
        map.put("offset", offset);
        map.put("limit", limit);
        return map;
    }

    private void setDataInRecyclerView() {
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RestaurantsActivity.this);
        restaurantRecyclerView.setLayoutManager(linearLayoutManager);
        // call the constructor of UsersAdapter to send the reference and data to Adapter
        RestaurantAdaptor adapter = new RestaurantAdaptor(RestaurantsActivity.this, restaurantListResponseData);
        restaurantRecyclerView.setAdapter(adapter); // set the Adapter to RecyclerView
    }
}




