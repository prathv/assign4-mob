package com.example.android.weatherwithpreferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.weatherwithpreferences.data.WeatherPreferences;
import com.example.android.weatherwithpreferences.utils.NetworkUtils;
import com.example.android.weatherwithpreferences.utils.OpenWeatherMapUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ForecastAdapter.OnForecastItemClickListener, LoaderManager.LoaderCallbacks<String>,SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FORECAST_URL_KEY = "forecastUrl";
    private static final int FORECAST_LOADER_ID = 0;

    private TextView mForecastLocationTV;
    private RecyclerView mForecastItemsRV;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mLoadingErrorMessageTV;
    private ForecastAdapter mForecastAdapter;

    private String currentUnitKey = "";
    private String currentLocationKey = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        currentUnitKey = WeatherPreferences.getDefaultTemperatureUnits();
        currentLocationKey = WeatherPreferences.getDefaultForecastLocation();

        // Remove shadow under action bar.
        getSupportActionBar().setElevation(0);

        mForecastLocationTV = (TextView)findViewById(R.id.tv_forecast_location);
        mLoadingIndicatorPB = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = (TextView)findViewById(R.id.tv_loading_error_message);
        mForecastItemsRV = (RecyclerView)findViewById(R.id.rv_forecast_items);

        mForecastLocationTV.setText(WeatherPreferences.getDefaultForecastLocation());

        mForecastAdapter = new ForecastAdapter(this);
        mForecastItemsRV.setAdapter(mForecastAdapter);
        mForecastItemsRV.setLayoutManager(new LinearLayoutManager(this));
        mForecastItemsRV.setHasFixedSize(true);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String forecastURL = OpenWeatherMapUtils.buildForecastURL(
                WeatherPreferences.getDefaultForecastLocation(),
                WeatherPreferences.getDefaultTemperatureUnits()
        );
        Bundle argsBundle = new Bundle();
        argsBundle.putString(FORECAST_URL_KEY, forecastURL);

        getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, argsBundle, this);
    }

    @Override
    public void onForecastItemClick(OpenWeatherMapUtils.ForecastItem forecastItem) {
        Intent intent = new Intent(this, ForecastItemDetailActivity.class);
        intent.putExtra(OpenWeatherMapUtils.ForecastItem.EXTRA_FORECAST_ITEM, forecastItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                showForecastLocation();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showForecastLocation() {
        Uri geoUri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", WeatherPreferences.getDefaultForecastLocation())
                .build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mForecastJSON;

            @Override
            protected void onStartLoading() {
                if (mForecastJSON != null) {
                    Log.d(TAG, "AsyncTaskLoader delivering cached forecast" +mForecastJSON);
                    deliverResult(mForecastJSON);
                } else {
                    mLoadingIndicatorPB.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String forecastURL = args.getString(FORECAST_URL_KEY);
                Log.d(TAG, "AsyncTaskLoader  forecast url " +forecastURL);

                if (forecastURL == null || forecastURL.equals("")) {
                    return null;
                }
                Log.d(TAG, "AsyncTaskLoader loading forecast from url: " + forecastURL);

                String forecastJSON = null;
                try {
                    forecastJSON = NetworkUtils.doHTTPGet(forecastURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return forecastJSON;
            }

            @Override
            public void deliverResult(String forecastJSON) {
                mForecastJSON = forecastJSON;
                super.deliverResult(forecastJSON);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String forecastJSON) {
        Log.d(TAG, "AsyncTaskLoader load finished");
        mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
        if (forecastJSON != null) {
            mLoadingErrorMessageTV.setVisibility(View.INVISIBLE);
            mForecastItemsRV.setVisibility(View.VISIBLE);
            ArrayList<OpenWeatherMapUtils.ForecastItem> forecastItems = OpenWeatherMapUtils.parseForecastJSON(forecastJSON);
            mForecastAdapter.updateForecastItems(forecastItems);
        } else {
            mForecastItemsRV.setVisibility(View.INVISIBLE);
            mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing to do here...
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("Mainactivity","Key is "+key);

        Bundle argsBundle = new Bundle();

        if(key.compareTo("pref_weather_key")==0){

            currentLocationKey = sharedPreferences.getString("pref_weather_key","Corvallis,us");
            mForecastLocationTV.setText(currentLocationKey);
            Log.d("Mainactivity","Changed currentLocationkey"+currentLocationKey);

        }

        if(key.compareTo("sort")==0){

            currentUnitKey = sharedPreferences.getString(key,"imperial");
            Log.d("Mainactivity","Changed currentUnitKey"+currentUnitKey);


        }

        String forecastURL = OpenWeatherMapUtils.buildForecastURL(
                currentLocationKey,
                currentUnitKey
        );

        Log.d("Mainactivity","Changed forecastUrl"+forecastURL);

        argsBundle.putString(FORECAST_URL_KEY, forecastURL);
        getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID,argsBundle,this);
    }
}
