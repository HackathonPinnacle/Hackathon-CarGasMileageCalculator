package edu.umkc.mobile.cargasmileageestimator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.umkc.mobile.cargasmileageestimator.data.MileageData;
import edu.umkc.mobile.cargasmileageestimator.distance.DistanceService;
import edu.umkc.mobile.cargasmileageestimator.distance.DistanceServiceNativeImpl;
import edu.umkc.mobile.cargasmileageestimator.enums.UnitEnum;
import edu.umkc.mobile.cargasmileageestimator.model.MileageRecord;


/**
 * A simple {@link Fragment} subclass.
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
    protected Spinner unitValue;
    protected TextView distanceText;
    protected ArrayAdapter<String> unitDataAdapter;

    // data handles
    protected MileageData mileageData;
    protected MileageRecord currentMileageRecord;

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // get the distance text
        distanceText = (TextView) view.findViewById(R.id.text_distance);

        // get the buttons
        startTracking = (Button) view.findViewById(R.id.button_startTracking);
        stopTracking = (Button) view.findViewById(R.id.button_stopTracking);
        unitValue = (Spinner) view.findViewById(R.id.spinner_Unit);

        // populate unit spinner
        List<String> labels = new ArrayList<String>(UnitEnum.values().length);
        for (UnitEnum unit : UnitEnum.values()) {
            labels.add(unit.getLabel());
        }
        unitDataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, labels);
        unitDataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitValue.setAdapter(unitDataAdapter);
        unitDataAdapter.notifyDataSetChanged();

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


        //update UI state

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
        if (currentMileageRecord != null)
            updateDistanceDriven(currentMileageRecord.getDistance());

        // data to keep track of
        mileageData = new MileageData(getContext());

        // location
        mlocManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MileageLocationListener();

        return view;
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
            Toast.makeText(context, "Home Fragment Attached", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,
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
        unitValue.setEnabled(false);

        // create a new record
        currentMileageRecord = new MileageRecord();
        currentMileageRecord.setDate(new Date(System.currentTimeMillis()));
        currentMileageRecord.setDistance(0);
        currentMileageRecord.setUnit((String) unitValue.getSelectedItem());

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
        mileageData.insert(currentMileageRecord);

        // reset the current record
        currentMileageRecord = null;

        // allow new tracking
        enableStartButton();

        // allow changing the unit
        unitValue.setEnabled(true);
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
     * @param meterDistance
     */
    public void updateDistanceDriven(double meterDistance) {

        // determine how the distance should be displayed
        String selectedUnit = unitValue.getSelectedItem().toString();

        double adjustedDistance = 0;

        for (UnitEnum unit : UnitEnum.values()) {
            if (unit.getLabel().equals(selectedUnit)) {
                adjustedDistance = unit.convertFromMeters(meterDistance);
            }
        }

        // distance in correct unit
        String distance = Integer.toString((int) Math.round(adjustedDistance));

        distanceText.setText(distance);
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
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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
            updateDistanceDriven(currentMileageRecord.getDistance());
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

}
