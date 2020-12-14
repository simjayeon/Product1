package com.example.product1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.product1.RemoteService.BASE_URL;

public class AddActivity extends AppCompatActivity {
    String strFile;
    ImageView btnImage, image;
    Retrofit retrofit;
    RemoteService remoteService;
    EditText code, pname, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        getSupportActionBar().setTitle("상품등록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        permissionCheck();

        code=(EditText)findViewById(R.id.code);
        pname=(EditText)findViewById(R.id.pname);
        price=(EditText)findViewById(R.id.price);
        image=(ImageView)findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent ,1);
            }
        });
/*
        btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent ,1);
            }
        });
 */


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        remoteService = retrofit.create(RemoteService.class);

        Button button = findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file=new File(strFile);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
                RequestBody strCode=RequestBody.create(MediaType.parse("multipart/form-data"), code.getText().toString());
                RequestBody strPname= RequestBody.create(MediaType.parse("multipart/form-data"),pname.getText().toString());
                RequestBody strPrice=RequestBody.create(MediaType.parse("multipart/form-data"), price.getText().toString());
                RemoteService rs = retrofit.create(RemoteService.class);

                Call<ResponseBody> call = rs.uploadProduct(strCode, strPname, strPrice, fileToUpload);
                call.enqueue(new Callback<ResponseBody>() {
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(AddActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1){
            try{
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
                cursor.moveToFirst();
                strFile = cursor.getString(cursor.getColumnIndex(projection[0]));
                cursor.close();
                image.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
            }catch (Exception e){}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void permissionCheck(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

}