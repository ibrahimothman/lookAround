package com.example.ibrakarim.lookaround.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibrakarim.lookaround.R;
import com.example.ibrakarim.lookaround.model.PlaceDetail;
import com.example.ibrakarim.lookaround.model.Result;
import com.example.ibrakarim.lookaround.retrofit.ApiClient;
import com.example.ibrakarim.lookaround.retrofit.ApiInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceDetailActivity extends AppCompatActivity {

    private static final String TAG = PlaceDetailActivity.class.getSimpleName();
    @BindView(R.id.place_image)
    ImageView mPlaceImage;
    @BindView(R.id.place_name)
    TextView mPlaceName;
    @BindView(R.id.place_address)
    TextView mPlaceAddress;
    @BindView(R.id.opining_hours_txt)
    TextView mPlaceOpiningHours;
    @BindView(R.id.rating_bar)
    RatingBar mRatingBar;
    @BindView(R.id.view_on_map_btn)
    Button mViewOnMapBtn;

    private String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        // setup views
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null && intent.getStringExtra(MapsActivity.PLACE_ID_EXTRA) != null){
            placeId = intent.getStringExtra(MapsActivity.PLACE_ID_EXTRA);
            getPlaceDetails(placeId);
        }
    }

    private void getPlaceDetails(String placeId) {
        String url = getPlaceDetailUrl(placeId);
        Call<PlaceDetail> call = ApiClient.getApiClient().create(ApiInterface.class).getPlaceDetail(url);
        call.enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,"status is "+response.body().getStatus());
                    String status = response.body().getStatus();
                    if(status.equals("OK")) {
                        Result results = response.body().getResult();
                        String placeName = results.getName();
                        String placeAddress = results.getFormatted_address();
                        String placeRating = results.getRating();
                        Log.d(TAG, "name is " + placeName + " address is " + placeAddress + " rating is " + placeRating);
                        updateUI(placeName,placeAddress,placeRating);
                    }else
                        Toast.makeText(PlaceDetailActivity.this, "try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlaceDetail> call, Throwable t) {

            }
        });
    }

    private void updateUI(String placeName, String placeAddress, String placeRating) {
        mPlaceName.setText(placeName);
        mPlaceAddress.setText(placeAddress);
        mPlaceName.setText(placeName);
        mRatingBar.setRating(Float.parseFloat(placeRating));
    }

    private String getPlaceDetailUrl(String placeId) {
        StringBuilder builder = new StringBuilder(ApiClient.PLACE_DETAIL_BASE_URL);
        builder.append("placeid="+placeId+"&fields=name,rating,formatted_address&key="+
        getString(R.string.place_api_key));

        Log.d(TAG,"url is "+builder.toString());
        return builder.toString();
    }
}
