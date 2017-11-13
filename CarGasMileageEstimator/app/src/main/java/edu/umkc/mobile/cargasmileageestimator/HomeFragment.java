package edu.umkc.mobile.cargasmileageestimator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.umkc.mobile.cargasmileageestimator.data.CarDetailsCollection;
import edu.umkc.mobile.cargasmileageestimator.data.MileageCollection;
import edu.umkc.mobile.cargasmileageestimator.data.MileageData;
import edu.umkc.mobile.cargasmileageestimator.distance.DistanceService;
import edu.umkc.mobile.cargasmileageestimator.distance.DistanceServiceNativeImpl;
import edu.umkc.mobile.cargasmileageestimator.enums.UnitEnum;
import edu.umkc.mobile.cargasmileageestimator.model.MileageModel;
import edu.umkc.mobile.cargasmileageestimator.model.MileageRecord;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // widgets used for this activity
    protected Button startTracking;
    protected Button stopTracking;
    //protected Spinner unitValue;
    protected TextView distanceText;
    protected TextView rangeText;
    protected TextView gasText;
    protected TextView mileageText;
    protected TextView text_total_distance;
    protected TextView text_total_gas;
    protected TextView text_total_gas_cost;


    String INSERT_API_URL = "http://192.168.1.8:8080/api/insertMileageCollection/";
    String GET_API_URL = "http://192.168.1.8:8080/api/getMileageCollection/";
    String GET_CAR_API_URL = "http://192.168.1.8:8080/api/getCarDetails/";
    String INSERT_CAR_API_URL = "http://192.168.1.8:8080/api/insertCarDetails/";

    MileageCollection mileageCollection = new MileageCollection();

    MileageModel model = new MileageModel();
    CarDetailsCollection carDetails = new CarDetailsCollection();

    //protected ArrayAdapter<String> unitDataAdapter;

    // data handles
    protected MileageData mileageData;
    protected MileageRecord currentMileageRecord;
    public static  String restoredEmailId ="";
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // state
    protected boolean tracking = false;

    // location handles
    protected LocationManager mlocManager;
    protected LocationListener mlocListener;

    protected DistanceService distanceService = new DistanceServiceNativeImpl();


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        // initialize the UI
        initializeUI(view);

        // update the current state
        updateUIState();

        // data to keep track of
        mileageData = new MileageData(getContext());

        // location
        mlocManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MileageLocationListener();

        return view;
    }

    /**
     * Called when a configuration change is made (ex: change in orientation)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initializeUI(View view) {

        // get the distance text
        distanceText = (TextView) view.findViewById(R.id.text_distance);
        gasText = (TextView) view.findViewById(R.id.text_gas);
        rangeText = (TextView) view.findViewById(R.id.text_range);
        mileageText = (TextView) view.findViewById(R.id.text_mileage);
        text_total_distance= (TextView) view.findViewById(R.id.text_total_distance);
        text_total_gas= (TextView) view.findViewById(R.id.text_total_gas);
        text_total_gas_cost= (TextView) view.findViewById(R.id.text_total_gas_cost);

        DateFormat df = new SimpleDateFormat("MMddyyyy");

// Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
// Using DateFormat format method we can create a string
// representation of a date with the defined format.
        String reportDate = df.format(today);
        new HttpGetAsyncTask().execute(GET_API_URL+reportDate);
        restoredEmailId = user.getEmail().toString().replaceAll("\\.", "dot");
        new HttpGetCarDetailsAsyncTask().execute(GET_CAR_API_URL+restoredEmailId);


        // get the buttons
        startTracking = (Button) view.findViewById(R.id.button_startTracking);
        stopTracking = (Button) view.findViewById(R.id.button_stopTracking);

        // populate unit spinner
       /* List<String> labels = new ArrayList<String>(UnitEnum.values().length);
        for (UnitEnum unit : UnitEnum.values()) {
            labels.add(unit.getLabel());
        }
        unitDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);
        unitDataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitValue.setAdapter(unitDataAdapter);
        unitDataAdapter.notifyDataSetChanged();*/

        // add a listeners on the buttons
        startTracking.setOnClickListener(new View.OnClickListener() {

            // clicked on start/resume/pause
            public void onClick(View v) {
                // verify if it's a new track, pause, or resume
                if (currentMileageRecord != null) {
                    if (tracking) {
                        // pause was pressed
                        pauseTracking();
                    } else {
                        // resume was pressed
                        resumeTracking();
                    }
                } else {
                    // start was pressed
                    startTracking();
                }

            }
        });
        stopTracking.setOnClickListener(new View.OnClickListener() {

            // click on stop
            public void onClick(View v) {
                // stop was pressed
                stopTracking();

            }
        });



    }

    private void updateUIState() {
        if (tracking) {
            // tracking - enable pause
            enablePauseButton();

            // enable stop
            enableStopButton();

        } else {
            if (currentMileageRecord != null) {
                // enable resume
                enableResumeButton();

                // enable stop
                enableStopButton();
            } else {
                // enable start
                enableStartButton();

                // disable stop
                disableStopButton();
            }
        }

        // update the distance display
        if(currentMileageRecord != null)
            updateMileage(currentMileageRecord);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //Toast.makeText(context, "Settings Fragment Attached", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // enable clicking on start
    private void enableStartButton() {
        startTracking.setEnabled(true);
        startTracking.setText(R.string.start_label);
    }

    // enable clicking on pause
    private void enablePauseButton() {
        startTracking.setEnabled(true);
        startTracking.setText("Pause");
    }

    // enable clicking on stop
    private void enableStopButton() {
        stopTracking.setEnabled(true);
    }

    // enable clicking on resume
    private void enableResumeButton() {
        startTracking.setEnabled(true);
        startTracking.setText("Resume");
    }

    // disable clicking on the stop button
    private void disableStopButton() {
        stopTracking.setEnabled(false);
    }

    // enable location updates from the gps
    public void enableLocationUpdates() {
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0,
                mlocListener); // 90 seconds, 1km
    }

    // disable location updates from the gpc
    public void disableLocationUpdates() {
        mlocManager.removeUpdates(mlocListener);
    }

    /**
     * Start tracking the mileage
     */
    public void startTracking() {
        tracking = true;

        // disable to not allow more than one click
        startTracking.setEnabled(false);

        // don't allow changing the unit
       // unitValue.setEnabled(false);

        // create a new record
        currentMileageRecord = new MileageRecord();
        currentMileageRecord.setDate(new Date(System.currentTimeMillis()));
        currentMileageRecord.setDistance(0);
        currentMileageRecord.setUnit("MILES");
        currentMileageRecord.setGas(Double.parseDouble(carDetails.getFuel()));
        currentMileageRecord.setMileage(Double.parseDouble(carDetails.getMileage()));
        currentMileageRecord.setTotalDistance(Double.parseDouble(carDetails.getOdometer()));
        currentMileageRecord.setTotalGasUtilized(Double.parseDouble(carDetails.getTotalGas()));
        double range = currentMileageRecord.getMileage() * currentMileageRecord.getGas();
        currentMileageRecord.setRange(range);

        // enable gps
        enableLocationUpdates();

        // change the start to a pause
        enablePauseButton();

        // allow to stop tracking
        enableStopButton();
    }

    /**
     * Pause tracking - any travels while paused should NOT be included in the total distance travelled
     */
    public void pauseTracking() {
        // change to resume
        enableResumeButton();

        // no longer tracking
        tracking = false;

        // get and add the current location
        updateWithCurrentLocation();

        // turn off the gps
        disableLocationUpdates();

    }

    /**
     * Stop tracking and persist the current record
     */
    public void stopTracking() {
        disableStopButton();

        // capture current location
        updateWithCurrentLocation();


        // TODO note -- the rest of this MUST execute only after a fix on the current location has been made. Therefore, this needs to be
        // refactored with some type of callback

        // disable gps
        disableLocationUpdates();

        // persist the distance
 //       mileageData.insert(currentMileageRecord);

        // reset the current record
        currentMileageRecord = null;

        // allow new tracking
        enableStartButton();

        // allow changing the unit
        //unitValue.setEnabled(true);

    }

    /**
     * Resume tracking from the current location
     */
    public void resumeTracking() {
        // allow pause
        enablePauseButton();

        tracking = true;

        // add a location of 0 to essentially reset the starting point
        currentMileageRecord.addCordinate(0, 0);

        // track current location
        updateWithCurrentLocation();

        // turn gps back on
        enableLocationUpdates();
    }

    /**
     * Update the value of the distance driven on the UI
     *
     * @param mileageRecord
     */
    public void updateMileage(MileageRecord mileageRecord) {

        // determine how the distance should be displayed
        String selectedUnit = "MILES";

        double adjustedDistance = 0;
        int count =0;
        for (UnitEnum unit : UnitEnum.values()) {
            if (count==1) {
                adjustedDistance = unit.convertFromMeters(mileageRecord.getDistance());
            }
            count++;
        }


        double adjustedRange = 0;
        adjustedRange = mileageRecord.getRange() - adjustedDistance;
        String range = Double.toString((double)Math.round(adjustedRange*100.0)/100.0);

        rangeText.setText(range+" miles");
        mileageCollection.setRange(range);

        double mileage = mileageRecord.getMileage();
        double adjustedGas = 0;
        adjustedGas = mileageRecord.getGas() - (adjustedDistance/mileage);
        String gas = Double.toString((double)Math.round(adjustedGas*100.0)/100.0);

        gasText.setText(gas+" gallons");
        mileageCollection.setGasRemaining(gas);
        carDetails.setFuel(gas);

        String totalDistance = Double.toString((double)Math.round((mileageRecord.getTotalDistance()+adjustedDistance)*100.0)/100.0);
        text_total_distance.setText(totalDistance+" miles");
        mileageCollection.setTotalDistance(totalDistance);
        carDetails.setOdometer(totalDistance);

        String totalGas = Double.toString((double)Math.round((mileageRecord.getTotalGasUtilized())*100.0)/100.0);
        text_total_gas.setText(totalGas+" gallons");
        mileageCollection.setTotalGas(totalGas);
        carDetails.setTotalGas(totalGas);

        String totalGasCost = Double.toString((double)Math.round((mileageRecord.getTotalGasUtilized() * 2.56)*100.0)/100.0);
        text_total_gas_cost.setText("$"+totalGasCost);
        mileageCollection.setTotalGasCost(totalGasCost);
        carDetails.setTotalGasCost(totalGasCost);
        mileage = mileageRecord.getMileage();
        String mileageString = Double.toString((double)Math.round(mileage*100.0)/100.0);
        mileageText.setText(mileageString+" mpg");
        mileageCollection.setMileage(mileageString);
        carDetails.setMileage(mileageString);

        // distance in correct unit
        String distance = Double.toString((double)Math.round(adjustedDistance*100.0)/100.0);

        distanceText.setText(distance+" miles");

        //mileageCollection = new MileageCollection();
        //if(Double.parseDouble(mileageCollection.getDistance()==null?"0":mileageCollection.getDistance())<=0.0)
            mileageCollection.setDistance(distance);
        DateFormat df = new SimpleDateFormat("MMddyyyy");

// Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
// Using DateFormat format method we can create a string
// representation of a date with the defined format.
        String reportDate = df.format(today);
        mileageCollection.setDate(reportDate);

        mileageCollection.setCar_id("1234");
         mileageCollection.setId(Math.random()+reportDate);

        mileageCollection.getCar_id();

        new HttpAsyncTask().execute(INSERT_API_URL);
        new HttpAsyncCarTask().execute(INSERT_CAR_API_URL);

    }


    /**
     * Get and update the current location
     */
    public void updateWithCurrentLocation() {

		/*
		 *
		 * figure out how to include either a timer or callback of some sort before enabling this
		 *
		// add a one-time listener to get the location quickly
		OneTimeLocationListener onetimeListener = new OneTimeLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onetimeListener);
		mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, onetimeListener);
		*/

        // get the current location from the last known -- this will just be used as a backup source
        Location currentLocation = mlocManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(currentLocation != null) {
            updateLocation(currentLocation);
        }
    }




    /**
     * Update the with the given location
     *
     * @param loc
     */
    public void updateLocation(Location loc) {
        if(loc != null) {
            double latitude = loc.getLatitude();
            double longitude = loc.getLongitude();

            // calculate the distance travelled
            double distanceTravelled = 0;

            // check if distance needs to be calculated
            if (currentMileageRecord.getPreviousLatitude() != 0
                    && currentMileageRecord.getPreviousLongitude() != 0) {
                // call the service
                distanceTravelled = distanceService.getDistanceTravelled(
                        currentMileageRecord.getPreviousLatitude(),
                        currentMileageRecord.getPreviousLongitude(), latitude,
                        longitude);
            }

            // TODO look into if a UI thread is needed for the ui updates like in RCP


            // TODO make thread safe
            currentMileageRecord.addCordinate(latitude, longitude);

            // update the current distance that has been travelled
            currentMileageRecord.setDistance(currentMileageRecord.getDistance()
                    + distanceTravelled);

            // update the UI
            updateMileage(currentMileageRecord);
        }
    }

    /**
     * Listener for location updates
     *
     * @author mike
     *
     */
    public class MileageLocationListener implements LocationListener {

        /**
         * Handle a change in location
         */
        @Override
        public void onLocationChanged(Location loc) {

            // handle the change in location
            updateLocation(loc);

        }

        @Override
        public void onProviderDisabled(String arg0) {

        }

        @Override
        public void onProviderEnabled(String arg0) {

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

        }

    }


    /**
     * Used to get a location fix once
     *
     * @author mike
     *
     */
    public class OneTimeLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // update the location
            updateLocation(location);

            // remove this listener
            mlocManager.removeUpdates(this);

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d("a","aaa");

            return model.POST(urls[0],mileageCollection);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private class HttpAsyncCarTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            return model.POST_CarDetails(urls[0],carDetails);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private class HttpGetAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return model.GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                if(result!=null && !"".equalsIgnoreCase(result)){
                    JSONObject json = new JSONObject(result);
                    distanceText.setText(json.getString("distance")+" miles");
                    if(json.getString("mileage")!=null && json.getString("mileage")!="0") {
                        mileageText.setText(json.getString("mileage") + " mpg");
                        mileageCollection.setMileage(json.getString("mileage"));
                    }
                   /* gasText.setText(json.getString("gasRemaining")+" gallons");
                    rangeText.setText(json.getString("range")+ "miles");
                    text_total_distance.setText(json.getString("totalDistance")+" miles");
                    text_total_gas.setText(json.getString("totalGas")+" gallons");
                    text_total_gas_cost.setText("$"+json.getString("totalGasCost"));*/

                    if(Double.parseDouble(mileageCollection.getDistance()==null?"0":mileageCollection.getDistance())<=0.0)
                    mileageCollection.setDistance(json.getString("distance"));

                }

            }catch (Exception e){

            }

        }
    }
    private class HttpGetCarDetailsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return model.GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                if(result!=null && !"".equalsIgnoreCase(result)){
                    JSONObject json = new JSONObject(result);
                    if(mileageText.getText().toString().contains("0") || mileageText.getText().toString().contains("null"))
                        mileageText.setText(json.getString("mileage")+" mpg");
                    gasText.setText(json.getString("fuel")+" gallons");
                    double adjustedRange = 0;
                    double mil = Double.parseDouble(json.getString("mileage").toString());
                    double ga = Double.parseDouble(json.getString("fuel"));
                    adjustedRange = mil * ga;
                    String range = Double.toString((double)Math.round(adjustedRange*100.0)/100.0)+" miles";
                    rangeText.setText(range+ "miles");
                    text_total_distance.setText(json.getString("odometer")+" miles");
                    text_total_gas.setText(json.getString("totalGas")+" gallons");
                    text_total_gas_cost.setText("$"+json.getString("totalGasCost"));

                    carDetails.setId(json.getString("id"));
                    carDetails.setEmailId(json.getString("emailId"));
                    carDetails.setMake(json.getString("make"));
                    carDetails.setModel(json.getString("model"));
                    carDetails.setYear(json.getString("year"));
                    carDetails.setOdometer(json.getString("odometer"));
                    carDetails.setTankCapacity(json.getString("tankCapacity"));
                    carDetails.setMileage(json.getString("mileage"));
                    carDetails.setFuel(json.getString("fuel"));
                    carDetails.setTotalGas(json.getString("totalGas"));
                    carDetails.setTotalGasCost(json.getString("totalGasCost"));

                    mileageCollection.setGasRemaining(json.getString("fuel"));
                    mileageCollection.setMileage(json.getString("mileage"));
                    mileageCollection.setTotalGas(json.getString("totalGas"));
                    mileageCollection.setTotalGasCost(json.getString("totalGasCost"));
                    mileageCollection.setTotalDistance(json.getString("odometer"));

                }

            }catch (Exception e){

            }

        }
    }
}
