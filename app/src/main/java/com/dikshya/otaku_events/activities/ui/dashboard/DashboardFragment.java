package com.dikshya.otaku_events.activities.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dikshya.otaku_events.R;
import com.dikshya.otaku_events.adapters.RecyclerViewAdapter;
import com.dikshya.otaku_events.api_util.Base_url;
import com.dikshya.otaku_events.interfaces.UsersAPI;
import com.dikshya.otaku_events.model.EventsDto;
import com.dikshya.otaku_events.model.User;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;


public class DashboardFragment extends Fragment {
    TextInputLayout tetSearchAvailable;
    private View root;
    private String token;
    private List<EventsDto> allEventsList = new ArrayList<>();
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        this.root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // GET TOKEN FROM SHARED PREFERENCE -----
        SharedPreferences sharedPreferences = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("AuthSP", MODE_PRIVATE);
        }
        this.token = sharedPreferences.getString("token", "");

        this.getAllEventListAndPopulate();

        return root;
    }

    private void getAllEventListAndPopulate() {
        tetSearchAvailable = root.findViewById(R.id.tetSearchAvailable);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_url.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersAPI usersAPI = retrofit.create(UsersAPI.class);
        final Call<User> getAllEvents = usersAPI.getActiveUser("Bearer " + token);

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Response<User> response = getAllEvents.execute();

            if (response.isSuccessful()) {
                allEventsList = response.body().getBookmarkedEvents();

                // Configure Recycler View ---
                RecyclerView myRv = root.findViewById(R.id.recyclerViewId);
                final RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(getContext(), allEventsList, true);
                myRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
                myRv.setAdapter(myAdapter);

                // Search edit text onChange listener ---
                tetSearchAvailable.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        myAdapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

            } else {
                Toast.makeText(getContext(), "Failed to load events!", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("", "", e);
        }
    }
}