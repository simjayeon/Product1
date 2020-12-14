package com.example.product1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.product1.RemoteService.BASE_URL;

public class MainActivity extends AppCompatActivity {

    Retrofit retrofit;
    RemoteService remoteService;
    List<ProductVO> arrayProduct = new ArrayList<>();
    ListView list;
    ProductAdapter productAdapter;
    String strOrder="code";
    String strQuery="";
    final static int ADD_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("상품관리");

        productAdapter = new ProductAdapter();

        list = findViewById(R.id.list);
        list.setAdapter(productAdapter);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        remoteService = retrofit.create(RemoteService.class);
        callData(strOrder, strQuery);

        FloatingActionButton floatingActionButton = findViewById(R.id.btnInsert);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, ADD_ACTIVITY);
            }
        });
    }

    public void callData(String order, String query){
        Call<List<ProductVO>> call = remoteService.listProduct(order, query);
        call.enqueue(new Callback<List<ProductVO>>(){
            @Override
            public void onResponse(Call<List<ProductVO>> call, Response<List<ProductVO>> response) {
                arrayProduct = response.body();
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ProductVO>> call, Throwable t) {
            }
        });
    }

    class ProductAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return arrayProduct.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.item, viewGroup, false);


            TextView txtCode = view.findViewById(R.id.code);
            TextView txtName = view.findViewById(R.id.pname);
            TextView txtPrice = view.findViewById(R.id.price);

            txtCode.setText(arrayProduct.get(i).getCode());
            txtName.setText(arrayProduct.get(i).getPname());
            txtPrice.setText(arrayProduct.get(i).getPrice()+"만원");

            ImageView image = view.findViewById(R.id.image);
            Picasso.with(MainActivity.this)
                    .load(BASE_URL+"image/"+arrayProduct.get(i).getImage())
                    .into(image);

            RelativeLayout item = view.findViewById(R.id.item);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                    intent.putExtra("code", arrayProduct.get(i).getCode());
                    startActivityForResult(intent,1 );
                }
            });
            return view;
        }
    }

    //메뉴 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem itemSearch = menu.findItem(R.id.itemSearch);
        SearchView searchView = (SearchView)itemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                strQuery = s;
                callData(strOrder, strQuery);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemCode:
                strOrder="code";
                break;
            case R.id.itemName:
                strOrder="pname";
                break;
            case R.id.itemDesc:
                strOrder="desc";
                break;
            case R.id.itemAsc:
                strOrder="asc";
                break;
        }
        callData(strOrder, strQuery);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==ADD_ACTIVITY && resultCode==RESULT_OK){
            Toast.makeText(MainActivity.this, "상품등록완료", Toast.LENGTH_SHORT).show();
            callData("code", "");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}