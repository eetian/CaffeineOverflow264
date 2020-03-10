package com.example.caffeineoverflow264.ui.calculator;

import android.database.Cursor;
import android.os.Handler;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.caffeineoverflow264.repository.service.api.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalculatorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CalculatorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is calculator fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public double maxCaffeine(int age, double weight){
        double maxCaffeine;
        if(age <= 15)
            maxCaffeine = weight * 2.5;
        else if (age < 50)
            maxCaffeine = weight * 5;
        else
            maxCaffeine = weight * 3;

        return maxCaffeine;
    }

    //To calculate Caffeine left
    public double calculateCaffeine(){
        double caffeine, weight, maxCaffeine,caffeineIntake;
        int age;

        caffeineIntake = 0.0;

        //Calculate maximum Caffeine in mg per user;
        Cursor userCursor = DatabaseHelper.getUserDetails();
        userCursor.moveToFirst();
        weight = userCursor.getDouble(2);
        age = userCursor.getInt(3);
        maxCaffeine = maxCaffeine(age,weight);
        if (!userCursor.moveToFirst()) {
            System.out.println("No user recorded");
            return maxCaffeine;
        }

        Date currDateClicked = new Date();
        System.out.println(currDateClicked.toString());

        Cursor logCursor = DatabaseHelper.getLogDetailsOnOneDay(currDateClicked.toString());
        if (!logCursor.moveToFirst()) {
            System.out.println("No intake recorded");
            return maxCaffeine;
        }

        do {
            System.out.println("Intake recorded");
            int coffeeId = logCursor.getInt(2);
            // Get caffeine amount per oz for this coffeeId
            int caffineAmount = DatabaseHelper.getCaffeineAmount(coffeeId);
            int oz = logCursor.getInt(3);
            caffeineIntake += oz * caffineAmount;
        } while (logCursor.moveToNext());

        caffeine = maxCaffeine - caffeineIntake;

        return caffeine;
    }
}