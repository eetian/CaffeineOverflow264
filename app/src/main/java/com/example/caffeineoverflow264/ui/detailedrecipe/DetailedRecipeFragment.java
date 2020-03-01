package com.example.caffeineoverflow264.ui.detailedrecipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caffeineoverflow264.R;
import com.example.caffeineoverflow264.model.DetailedRecipe;
import com.example.caffeineoverflow264.model.Ingredient;
import com.example.caffeineoverflow264.model.Result;
import com.example.caffeineoverflow264.repository.service.api.DetailedRecipeApiService;
import com.example.caffeineoverflow264.ui.SharedViewModel;
import com.example.caffeineoverflow264.util.IngridentListAdapter;
import com.example.caffeineoverflow264.util.OnIngredientClickListener;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailedRecipeFragment extends Fragment {
    static final String TAG = DetailedRecipeFragment.class.getSimpleName();
    static final String BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/";

    private DetailedRecipe detailedRecipe;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private IngridentListAdapter ingridentListAdapter;
    private Retrofit retrofit;

    private SharedViewModel sharedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("MIA       DetailedRecipe Fragment -> onCreateView()");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recipe Detail");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#008577")));

        return inflater.inflate(R.layout.fragment_detailed_recipe, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        // Dump data into recycler view
        RecyclerView recyclerView = getView().findViewById(R.id.ingridentList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingridentListAdapter = new IngridentListAdapter(ingredients, new OnIngredientClickListener() {
            @Override
            public void onIngredientClick(Ingredient ingredient) {
                Log.d(TAG, "ingredient clicked: " + ingredient.getName());
                //TODO: direct to external app Amazon
            }
        });
        recyclerView.setAdapter(ingridentListAdapter);

        // Get selected recipe <Result> and get a detailed recipe of it
        sharedViewModel.getSelectedResult().observe(getViewLifecycleOwner(), new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                getDetailRecipe(sharedViewModel.getSelectedResult().getValue().getId());
            }
        });
    }

    private void getDetailRecipe(String id) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        DetailedRecipeApiService recipeDetailApiService = retrofit.create(DetailedRecipeApiService.class);
        Call<DetailedRecipe> call = recipeDetailApiService.getDetailedRecipe(id);
        Log.d(TAG, "MIA      call.request(): " + call.request().toString());

        call.enqueue(new Callback<DetailedRecipe>() {
            @Override
            public void onResponse(Call<DetailedRecipe> call, Response<DetailedRecipe> response) {
                detailedRecipe = response.body();
                ingredients.clear();
                ingredients.addAll(detailedRecipe.getExtendedIngredients());

                TextView recipeNameTv = getView().findViewById(R.id.recipeTitle);
                recipeNameTv.setText(detailedRecipe.getTitle());
                TextView recipeInstructionTv = getView().findViewById(R.id.recipeInstruction);
                recipeInstructionTv.setText(detailedRecipe.getInstructions());

                // Set ingredients
                ingridentListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<DetailedRecipe> call, Throwable t) {
                Log.e(TAG, "DetailedRecipe API call fails");
            }
        });
    }

}
