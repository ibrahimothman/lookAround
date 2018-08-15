package com.example.ibrakarim.lookaround.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ibrakarim.lookaround.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceDetailActivity extends AppCompatActivity {

    @BindView(R.id.place_image)
    ImageView mPlaceImage;
    @BindView(R.id.place_name)
    TextView mPlaceName;
    @BindView(R.id.place_address)
    TextView mPlaceAddress
    @BindView(R.id.opining_hours_txt)
    TextView mPlaceOpiningHours;
    @BindView(R.id.rating_bar)
    RatingBar mRatingBar;
    @BindView(R.id.view_on_map_btn)
    Button mViewOnMapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        // setup views
        ButterKnife.bind(this);
    }
}
