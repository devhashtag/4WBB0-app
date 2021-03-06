package com.example.pocketalert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketalert.configuration.Command;
import com.example.pocketalert.connect.ConnectedActivity;
import com.example.pocketalert.connect.Message;
import com.example.pocketalert.data.Device;
import com.example.pocketalert.data.DeviceViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class MainActivity extends ConnectedActivity {
    public static final String TAG = "MainActivity";

    public static boolean vibrationEnabled = true;
    private DeviceViewModel deviceViewModel;

    // Request codes
    public static final int REGISTER_ACTIVITY_REQUEST_CODE = 69;
    public static final int VIEW_DETAILS_ACTIVITY_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);

        // created the recycler view to show the list of all connected users.
        RecyclerView recyclerView = findViewById(R.id.usersRecyclerView);
        final DeviceListAdapter adapter = new DeviceListAdapter(this, this::viewDevice, this::removeDevice);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Update the cached copy of the devices in the adapter.
        deviceViewModel.getAll().observe(this, adapter::setDevices);

        // Always try to start the service (if it is already on, the start signal is ignored)
        startService();
        load_Setting();

        updateDevices();
    }

    private void updateDevices() {
        sendRequest(Command.Request.GET_DEVICES_INFORMATION, null, (Message response) -> {
            if (! Command.Response.DEVICES_INFORMATION.toString().equals(response.command))  {
                Log.e(TAG, "UpdateDevices: Could not retrieve device information: " + response.command + "\n" + response.argument);
                return;
            }

            Gson gson = new Gson();
            Type type = new TypeToken<List<Device>>(){}.getType();

            List<Device> devices = gson.fromJson(response.argument, type);

            for (Device device : devices) {
                if (deviceViewModel.exists(device.Id)) {
                    deviceViewModel.update(device);
                } else {
                    deviceViewModel.insert(device);
                }
            }

            String serialized = gson.toJson(deviceViewModel.getAll().getValue());
            Log.d(TAG, "Serialized devices: " + serialized);
        });
    }

    /**
     * When another Activity returns a result, this is where that result gets processed.
     *
     * @param requestCode The request code that was used when the activity was started.
     * @param resultCode  The result code return by the activity.
     * @param data        The Intent data the activity return.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            // Adds a new device to the database
//            String id = data.getStringExtra("id");
//            if (id != null) {
//                User user = new User(id);
//                userViewModel.insert(user);
//            }
        } else if (requestCode == VIEW_DETAILS_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Updates the users data
//            Log.d("hierzo","test2");
//
//            String id = data.getStringExtra("id");
//            User user = new User(Objects.requireNonNull(id));
//            user.setName(Objects.requireNonNull(data.getStringExtra("name")));
//            user.setAddress(Objects.requireNonNull(data.getStringExtra("address")));
//            user.setPhone(Objects.requireNonNull(data.getStringExtra("phone")));
//            user.setEmail(Objects.requireNonNull(data.getStringExtra("email")));
//            user.setBirthday(Objects.requireNonNull(data.getStringExtra("birthday")));
//            userViewModel.update(user);
//
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            viewUser(id);
        }
    }

    /**
     * Starts the service and identifies to the server
     */
    public void startService() {
        // Start service
        sendRequest(Command.Control.START_SERVICE, null, null);

        // Check if we have stored a user ID
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("userID", null);

        // Identify, or request an ID
        if (userID == null) {
            // Request ID
            sendRequest(Command.Request.REQUEST_ID, null, (Message response) -> {
                preferences
                        .edit()
                        .putString("userID", response.argument)
                        .apply();

                Log.d(TAG, "Id: " + response.argument);
            });
        } else {
            // Identify
            sendRequest(Command.Request.ID, userID, null);
        }
    }

    public void stopService() {
        sendRequest(Command.Control.STOP_SERVICE, null, null);
    }

    /**
     * When the FAB with the plus is clicked, go to the RegisterActivity.
     */
    public void addDevice(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REGISTER_ACTIVITY_REQUEST_CODE);
    }

    /**
     * When the name of one of the users is pressed, go to the DetailActivity with the details of that user.
     */
    public void viewUser(View view) {
        Button viewButton = (Button) view;
        Log.d(TAG, "viewUser: " + viewButton.getText().toString());
//        viewUser(viewButton.getText().toString());
    }

    public void viewUser(String id) {
        Log.d("hierzo","test3");

        Intent intent = new Intent(this, DetailActivity.class);
        putExtrasDetails(intent, id);
        startActivityForResult(intent, VIEW_DETAILS_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Puts the details of the user with the specified ID into the specified Intent.
     *
     * @param intent The intent to add the user's details to in the extras.
     * @param id     The ID of the user who's details should be put in the intent's extras.
     */
    private void putExtrasDetails(@NonNull Intent intent, String id) {
//        Log.d("hierzo","test1");
//        User user = userViewModel.getUser(id);
//        intent.putExtra("id", id);
//        intent.putExtra("name", user.getName());
//        intent.putExtra("address", user.getAddress());
//        intent.putExtra("phone", user.getPhone());
//        intent.putExtra("email", user.getEmail());
//        intent.putExtra("birthday", user.getBirthday());
    }

    public void removeDevice(String id) {
        sendRequest(Command.Request.REMOVE_DEVICE, id, (Message response) -> {
            if (! Command.Response.OK.toString().equals(response.command)) {
                Log.e(TAG, "deleteUser: cannot delete device" + response.argument);
                return;
            }

            Device device = deviceViewModel.getDevice(id);

            if (device == null) {
                Log.e(TAG, "Device is null: " + id);
                return;
            }

            deviceViewModel.delete(device);
        });
    }

    public void viewDevice(String id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    /**
     * When the delete button next to a user/device is pressed, that device gets removed from the database.
     */
    public void deleteUser(View view) {
        Button deleteButton = (Button) view;
        String deviceId = deleteButton.getText().toString();

        sendRequest(Command.Request.REMOVE_DEVICE, deviceId, (Message response) -> {
            if (! Command.Response.OK.toString().equals(response.command)) {
                Log.e(TAG, "deleteUser: cannot delete device" + response.argument);
                return;
            }

            Device device = deviceViewModel.getDevice(deviceId);

            if (device == null) {
                Log.e(TAG, "Device is null: " + deviceId);
                return;
            }

            deviceViewModel.delete(device);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dropdown_menu, menu);
        return true;
    }

    public void closeApp(View view) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.SettingsActivity:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.HistorySettings:
                Intent historyIntent = new Intent(this,HistoryActivity.class);
                startActivity(historyIntent);
                break;
            case R.id.calendarSettings:
                Intent reminderIntent = new Intent(this,ReminderActivity.class);
                startActivity(reminderIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void setDarkMode(boolean darkModeSetting) {
        if (darkModeSetting) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void load_Setting() {
        // Get the shared preferences
        SharedPreferences appSettingPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the dark mode according to the set mode
        setDarkMode(appSettingPrefs.getBoolean("darkmode", false));

        setOrientationMode(Objects.requireNonNull(appSettingPrefs.getString("Orientation", "2")));
    }

    public void setOrientationMode(String Orientation) {
        // Sets the current Orientation mode under the index of Rotation
        switch (Orientation) {
            case "1":
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
            case "2":
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case "3":
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        load_Setting();
        super.onResume();
    }
}