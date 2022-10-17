package uz.example.less67_task3_retrofitrequestpasrsing_java;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.example.less67_task3_retrofitrequestpasrsing_java.activity.CreateActivity;
import uz.example.less67_task3_retrofitrequestpasrsing_java.adapter.PosterAdapter;
import uz.example.less67_task3_retrofitrequestpasrsing_java.model.Poster;
import uz.example.less67_task3_retrofitrequestpasrsing_java.model.PosterResp;
import uz.example.less67_task3_retrofitrequestpasrsing_java.network.RetrofitHttp;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Poster> posters = new ArrayList<>();
    ProgressBar pb_loading;
    FloatingActionButton floating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    void initViews(){
        pb_loading = findViewById(R.id.pb_loading);
        floating = findViewById(R.id.floating);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        apiPosterList();
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateActivity();
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras !=null){
            Log.d("###","extras not NULL - ");
            String edit_title = extras.getString("title");
            String edit_post = extras.getString("post");
            String edit_userId = extras.getString("id_user");
            String edit_id = extras.getString("id");
            Poster poster = new Poster(Integer.parseInt(edit_id),Integer.parseInt(edit_userId),edit_title,edit_post);
            Toast.makeText(MainActivity.this, "Post Prepared to Edit", Toast.LENGTH_LONG).show();

            apiPosterEdit(poster);

        }

    }

    void refreshAdapter(ArrayList<Poster> posters) {
        PosterAdapter adapter = new PosterAdapter(this, posters);
        recyclerView.setAdapter(adapter);
    }
    public void dialogPoster(Poster poster) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Poster")
                .setMessage("Are you sure you want to delete this poster?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        apiPosterDelete(poster);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == 78) {
                        Intent data = result.getData();

                        if (data !=null){
                            String new_title = data.getStringExtra("title");
                            String new_post = data.getStringExtra("post");
                            String new_userId = data.getStringExtra("id_user");
                            Poster poster = new Poster(Integer.parseInt(new_userId),new_title,new_post);
                            Toast.makeText(MainActivity.this, "Title modified", Toast.LENGTH_LONG).show();

                            apiPosterCreate(poster);
                        }
                        // your operation....
                    }else {
                        Toast.makeText(MainActivity.this, "Operation canceled", Toast.LENGTH_LONG).show();
                    }

                }
            });
    void openCreateActivity(){
        Intent intent = new Intent(MainActivity.this, CreateActivity.class);
        launchSomeActivity.launch(intent);
    }
    private void apiPosterList() {
        pb_loading.setVisibility(View.VISIBLE);
        RetrofitHttp.posterService.listPoster().enqueue(new Callback<ArrayList<PosterResp>>() {
            @Override
            public void onResponse(Call<ArrayList<PosterResp>> call, Response<ArrayList<PosterResp>> response) {
                pb_loading.setVisibility(View.GONE);
                //Log.d("@@@", response.body().toString());
                posters.clear();
                ArrayList<PosterResp> items = response.body();
                if (items!=null){
                    for (PosterResp item : items){
                        Poster poster = new Poster(item.getId(),item.getUser_id(),item.getTitle(),item.getBody());
                        posters.add(poster);
                    }
                }
                refreshAdapter(posters);
            }
            @Override
            public void onFailure(Call<ArrayList<PosterResp>> call , Throwable t ) {
                Log.e("@@@", t.getMessage().toString());
            }
        });
    }
    private void apiPosterCreate(Poster poster) {
        pb_loading.setVisibility(View.VISIBLE);
        RetrofitHttp.posterService.createPost(poster).enqueue(new Callback<PosterResp>() {
            @Override
            public void onResponse(Call<PosterResp> call, Response<PosterResp> response) {
                Log.d("@@@", response.body().toString());
                Toast.makeText(MainActivity.this, poster.getTitle()+" Created", Toast.LENGTH_LONG).show();
                apiPosterList();
            }

            @Override
            public void onFailure(Call<PosterResp> call, Throwable t) {
                Log.d("@@@", t.toString());
            }
        });
    }
    private void apiPosterEdit(Poster poster) {
        pb_loading.setVisibility(View.VISIBLE);
        RetrofitHttp.posterService.updatePost(poster.getId(),poster).enqueue(new Callback<PosterResp>() {
            @Override
            public void onResponse(@NonNull Call<PosterResp> call, @NonNull Response<PosterResp> response) {
             if (response.body() != null){
                 Log.d("@@@", response.body().toString());
                 Toast.makeText(MainActivity.this, poster.getTitle()+" Edited", Toast.LENGTH_LONG).show();
                 apiPosterList();
             }else{
                 Log.d("@@@", response.toString());
             }

            }

            @Override
            public void onFailure(@NonNull Call<PosterResp> call, @NonNull Throwable t) {
                Log.d("@@@", t.toString());
                Toast.makeText(MainActivity.this, poster.getTitle()+" Edited", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void apiPosterDelete(Poster poster) {
        pb_loading.setVisibility(View.VISIBLE);

            RetrofitHttp.posterService.deletePost(poster.getId()).enqueue(new Callback<PosterResp>() {
                @Override
                public void onResponse(Call<PosterResp> call, Response<PosterResp> response) {
                    Log.d("@@@", response.body().toString());
                    Toast.makeText(MainActivity.this, poster.getTitle()+" Deleted", Toast.LENGTH_LONG).show();
                    apiPosterList();
                }


                @Override
                public void onFailure(Call<PosterResp> call, Throwable t) {
                    Log.d("@@@", t.toString());
                }
            });

    }

}