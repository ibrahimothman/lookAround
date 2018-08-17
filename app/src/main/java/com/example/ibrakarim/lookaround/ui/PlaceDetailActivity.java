package com.example.ibrakarim.lookaround.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.squareup.picasso.Picasso;

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
    private String imageRef;
    private String mLocationUri;

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

        mViewOnMapBtn.setEnabled(false);
        mViewOnMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(mLocationUri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(mapIntent);
            }
        });
    }

    private void getPlaceDetails(String placeId) {
        String url = getPlaceDetailUrl(placeId);
        Call<PlaceDetail> call = ApiClient.getApiClient().create(ApiInterface.class).getPlaceDetail(url);
        call.enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                if(response.isSuccessful()){
                    mViewOnMapBtn.setEnabled(true);
                    Log.d(TAG,"status is "+response.body().getStatus());
                    String status = response.body().getStatus();
                    if(status.equals("OK")) {
                        Result results = response.body().getResult();
                        String placeName = results.getName();
                        String placeAddress = results.getFormatted_address();
                        String placeRating = results.getRating();
                        mLocationUri = results.getUrl();
                        Log.d(TAG, "name is " + placeName + " address is " + placeAddress + " rating is " + placeRating
                        +" uri is "+mLocationUri);
                        updateUI(placeName,placeAddress,placeRating);
                        imageRef = results.getReference();
                        if(imageRef != null) {
                            getPlaceImage(imageRef);
                        }else
                            Toast.makeText(PlaceDetailActivity.this, "No Image Available yet", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(PlaceDetailActivity.this, "try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlaceDetail> call, Throwable t) {

            }
        });
    }

    private void getPlaceImage(String imageRef) {
        String url = ApiClient.PLACE_IMAGE_BASE_URL+"maxwidth=100&photoreference="+
                imageRef+"&key="+getString(R.string.place_api_key);
        Log.d(TAG,"imgae is "+url);
        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(mPlaceImage);
    }

    private void updateUI(String placeName, String placeAddress, String placeRating) {
        mPlaceName.setText(placeName);
        mPlaceAddress.setText(placeAddress);
        mPlaceName.setText(placeName);
        if(placeRating != null) {
            mRatingBar.setRating(Float.parseFloat(placeRating));
        }else mRatingBar.setVisibility(View.INVISIBLE);
    }

    private String getPlaceDetailUrl(String placeId) {
        StringBuilder builder = new StringBuilder(ApiClient.PLACE_DETAIL_BASE_URL);
        builder.append("placeid="+placeId+"&fields=name,rating,formatted_address,url&key="+
        getString(R.string.place_api_key));

        Log.d(TAG,"url is "+builder.toString());
        return builder.toString();
    }
}
