package com.alisavran.javainstagramclone.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alisavran.javainstagramclone.R;
import com.alisavran.javainstagramclone.adapter.PostAdapter;
import com.alisavran.javainstagramclone.databinding.ActivityFeedBinding;
import com.alisavran.javainstagramclone.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    PostAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        postArrayList = new ArrayList<>();
        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);
    }

    private void getData(){
        //orderby = tarih ayarı
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (value != null){
                    for (DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String, Object> data = snapshot.getData();
                        // casting -- çevirme
                        String userEmail = (String) data.get("useremail");  // alınan veri object döndürdüğü için (String) yaptik
                        String comment = (String) data.get("comment");
                        String downloadUrl = (String) data.get("downloadurl");

                        Post post = new Post(userEmail,comment,downloadUrl);
                        postArrayList.add(post);

                    }
                    postAdapter.notifyDataSetChanged(); // recyclerView a yeni veri geldiğinin haberinin verir.
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_post){
            //Upload Activity

            Intent i̇ntentToUpload = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(i̇ntentToUpload);

        } else if (item.getItemId() == R.id.signout) {
            //Signout

            auth.signOut();

            Intent intentToMain = new Intent(FeedActivity.this,MainActivity.class);
            startActivity(intentToMain);
            finish();
        }else if (item.getItemId() == R.id.edit_profile){
            Intent intentToEditProfile = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentToEditProfile);
        }else if (item.getItemId() == R.id.view_profile){
            Intent intentToViewProfile = new Intent(FeedActivity.this,ProfileActivity.class);
            startActivity(intentToViewProfile);
        }
        return super.onOptionsItemSelected(item);
    }
}