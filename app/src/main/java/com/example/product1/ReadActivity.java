package com.example.product1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.product1.RemoteService.BASE_URL;

public class ReadActivity extends AppCompatActivity {
    Retrofit retrofit;
    RemoteService remoteService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        getSupportActionBar().setTitle("상품정보");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        remoteService = retrofit.create(RemoteService.class);
        Call<ProductVO> call = remoteService.readProduct(intent.getStringExtra("code"));
        call.enqueue(new Callback<ProductVO>() {
            @Override
            public void onResponse(Call<ProductVO> call, Response<ProductVO> response) {
                ProductVO vo = response.body();
                TextView txtCode = findViewById(R.id.txtCode);
                TextView txtName = findViewById(R.id.txtName);
                TextView txtPrice = findViewById(R.id.txtPrice);

                txtCode.setText(vo.getCode());
                txtName.setText(vo.getPname());
                txtPrice.setText(vo.getPrice()+"만원");

                ImageView image = findViewById(R.id.image);
                Picasso.with(ReadActivity.this)
                        .load(BASE_URL+"image/"+vo.getImage())
                        .into(image);
            }

            @Override
            public void onFailure(Call<ProductVO> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}