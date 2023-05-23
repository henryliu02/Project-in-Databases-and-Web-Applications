package edu.uci.ics.fabflixmobile.ui.mainpage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainpageBinding;

public class MainPageActivity extends AppCompatActivity {
    private EditText movie_title;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainpageBinding binding = ActivityMainpageBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());
        movie_title = binding.movietitle;
        final Button searchButton = binding.search;
        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());
    }

    private void search() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;




    }
}
