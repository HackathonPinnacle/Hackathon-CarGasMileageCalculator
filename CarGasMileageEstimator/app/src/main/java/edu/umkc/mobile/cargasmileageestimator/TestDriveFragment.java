package edu.umkc.mobile.cargasmileageestimator;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import edu.umkc.mobile.cargasmileageestimator.data.CarDetailsCollection;
import edu.umkc.mobile.cargasmileageestimator.data.EmailDetails;
import edu.umkc.mobile.cargasmileageestimator.model.MileageModel;
import android.support.v4.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TestDriveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TestDriveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestDriveFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match

    public static  String restoredEmailId ="";
    public static String editedRestoredEmailId = "";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnTestDrive;

    TextView textViewCarDetails;
    TextView textMake;
    EditText editTextMake;
    TextView textModel;
    EditText editTextModel;
    TextView textYear;
    EditText editTextYear;
    TextView textOdometer;
    EditText editTextOdometer;
    TextView textTankCapacity;
    EditText editTextTankCapacity;
    Button btnCarDetails;
    Button btnRunTestDrive;
    TextView textView;

    TextView textEndOdometerReadings;
    EditText editTextEndOdometerReadings;

    TextView textFuelConsumed;
    EditText editTextFuelConsumed;

    TextView textMileage;

    TextView textTermsAndConditions;

    Button btnProccedToTestDrive;

    Button btnCalculateMileage;

    static int InitialOdometerReading = 0;
    static int TankCapacity =0;

    public Boolean carDetails = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    String INSERT_API_URL = "http://10.205.0.55:8080/api/insertCarDetails/";
    String GET_API_URL = "http://10.205.0.55:8080/api/getCarDetails/";
    CarDetailsCollection carDetailsCollection;
    MileageModel model = new MileageModel();

    private FirebaseAuth auth;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public TestDriveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestDriveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestDriveFragment newInstance(String param1, String param2) {
        TestDriveFragment fragment = new TestDriveFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences(LoginActivity.MyPREFERENCES, 0);
        //restoredEmailId = prefs.getString("email", "");
        restoredEmailId = user.getEmail().toString();

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.email = restoredEmailId;


        //Initialising content
        final View view = inflater.inflate(R.layout.fragment_test_drive, container, false);
        textViewCarDetails = (TextView) view.findViewById(R.id.textViewCarDetails);
        textMake = (TextView) view.findViewById(R.id.textMake);
        editTextMake = (EditText) view.findViewById(R.id.editTextMake);
        textModel = (TextView) view.findViewById(R.id.textModel);
        editTextModel = (EditText) view.findViewById(R.id.editTextModel);
        textYear = (TextView) view.findViewById(R.id.textYear);
        editTextYear = (EditText) view.findViewById(R.id.editTextYear);
        textOdometer = (TextView) view.findViewById(R.id.textOdometer);
        editTextOdometer = (EditText) view.findViewById(R.id.editTextOdometer);
        textTankCapacity = (TextView) view.findViewById(R.id.textFuelCap);
        editTextTankCapacity = (EditText) view.findViewById(R.id.editTextFuelCap);
        btnCarDetails = (Button) view.findViewById(R.id.btnCarDetails);
        btnProccedToTestDrive = (Button) view.findViewById(R.id.btnProccedToTestDrive);
        textEndOdometerReadings = (TextView) view.findViewById(R.id.textEndOdometerReadings);
        editTextEndOdometerReadings = (EditText) view.findViewById(R.id.editTextEndOdometerReadings);
        textFuelConsumed = (TextView) view.findViewById(R.id.textFuelConsumed);
        editTextFuelConsumed = (EditText) view.findViewById(R.id.editTextFuelConsumed);
        btnCalculateMileage = (Button) view.findViewById(R.id.btnCalculateMileage);
        textMileage = (TextView) view.findViewById(R.id.textMileage);
        btnTestDrive = (Button) view.findViewById(R.id.testDrive);
        textTermsAndConditions = (TextView) view.findViewById(R.id.textTermsAndConditions);
        textView = (TextView) view.findViewById(R.id.textView);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        btnRunTestDrive = (Button) view.findViewById(R.id.btnRunTestDrive);


        ///check if the db has car details. if so pull the details from db and display. and display mileage if already calculated.
        //checking db has car details and proceeding further
        editedRestoredEmailId = restoredEmailId.replaceAll("\\.", "dot");
        new TestDriveFragment.HttpGetAsyncTask().execute(GET_API_URL+editedRestoredEmailId);



        //If car details already exist displaying them
        if (editTextMake.getText().toString().trim().length()>0)
        {
           //textViewCarDetails.setText("Here are your car details!");
//            editTextMake.setText("TOYOTA");;
//            editTextModel.setText("CAMRY LE");
//            editTextYear.setText("2009");
//            editTextOdometer.setText("1000");
//            editTextTankCapacity.setText("15");
            textMileage.setVisibility(View.VISIBLE);
            btnCarDetails.setVisibility(View.GONE);
        }
        //letting the user fill the details
        else
        {
            btnCarDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextMake.getText().toString().trim().length()>0 && editTextModel.getText().toString().trim().length()>0 && editTextYear.getText().toString().trim().length()>0
                            && editTextOdometer.getText().toString().trim().length()>0 && editTextTankCapacity.getText().toString().trim().length()>0) {
                        //Save car details
                        SaveCarDetails();

                        //on selection of Test drive
                        btnTestDrive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TestDrive();
                                // On start of test drive
                                btnRunTestDrive.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        radioButton = (RadioButton) view.findViewById(radioGroup.getCheckedRadioButtonId());
                                        String data = radioButton.getText().toString();
                                        if (data.equals("Full Tank")) {
                                            RunTestDrive();
                                            btnProccedToTestDrive.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ProceedToTestDrive();
                                                    btnCalculateMileage.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            //Calculating mileage and updating the details
                                                            CalculateMileage();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Please enter valid details", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        return view;
    }

    //Save car details
    public void SaveCarDetails()
    {

        InitialOdometerReading = Integer.parseInt(editTextOdometer.getText().toString());
        TankCapacity = Integer.parseInt(editTextTankCapacity.getText().toString());

        ///http post to push car details along with user emailId.
        carDetailsCollection = new CarDetailsCollection();
        carDetailsCollection.setEmailId(editedRestoredEmailId);
        carDetailsCollection.setMake(editTextMake.getText().toString());
        carDetailsCollection.setModel(editTextModel.getText().toString());
        carDetailsCollection.setYear(editTextYear.getText().toString());
        carDetailsCollection.setOdometer(editTextOdometer.getText().toString());
        carDetailsCollection.setTankCapacity(editTextTankCapacity.getText().toString());
        carDetailsCollection.setId(String.valueOf(Math.random()+1234));

        new TestDriveFragment.HttpAsyncTask().execute(INSERT_API_URL);

        btnTestDrive.setVisibility(View.VISIBLE);

    }
    //on selection of Test drive
    public void TestDrive()
    {
        textViewCarDetails.setVisibility(View.GONE);
        textMake.setVisibility(View.GONE);
        textModel.setVisibility(View.GONE);
        textYear.setVisibility(View.GONE);
        textOdometer.setVisibility(View.GONE);
        textTankCapacity.setVisibility(View.GONE);
        editTextMake.setVisibility(View.GONE);
        editTextModel.setVisibility(View.GONE);
        editTextYear.setVisibility(View.GONE);
        editTextOdometer.setVisibility(View.GONE);
        editTextTankCapacity.setVisibility(View.GONE);
        btnCarDetails.setVisibility(View.GONE);
        btnTestDrive.setVisibility(View.GONE);

        textView.setVisibility(View.VISIBLE);
        radioGroup.setVisibility(View.VISIBLE);
        btnRunTestDrive.setVisibility(View.VISIBLE);
        textTermsAndConditions.setVisibility(View.VISIBLE);
    }
    // On start of test drive
    public void RunTestDrive()
    {
        textView.setVisibility(View.GONE);
        radioGroup.setVisibility(View.GONE);
        btnRunTestDrive.setVisibility(View.GONE);
        textTermsAndConditions.setVisibility(View.GONE);

        textOdometer.setVisibility(View.VISIBLE);
        editTextOdometer.setVisibility(View.VISIBLE);
        textTankCapacity.setVisibility(View.VISIBLE);
        editTextTankCapacity.setVisibility(View.VISIBLE);

        textOdometer.setText("Initial Odometer Reading: ");
        editTextOdometer.setText(String.valueOf(InitialOdometerReading));
        editTextTankCapacity.setText(String.valueOf(TankCapacity));
        btnProccedToTestDrive.setVisibility(View.VISIBLE);
    }
    public void ProceedToTestDrive()
    {
        btnProccedToTestDrive.setVisibility(View.GONE);
        textTermsAndConditions.setVisibility(View.GONE);

        textEndOdometerReadings.setVisibility(View.VISIBLE);
        editTextEndOdometerReadings.setVisibility(View.VISIBLE);
        textFuelConsumed.setVisibility(View.VISIBLE);
        editTextFuelConsumed.setVisibility(View.VISIBLE);

        btnCalculateMileage.setVisibility(View.VISIBLE);
    }
    public void CalculateMileage()
    {
        int distanceTravelled = Integer.parseInt(editTextEndOdometerReadings.getText().toString()) -InitialOdometerReading;

        Double Mileage = Math.round(distanceTravelled/Double.parseDouble(editTextFuelConsumed.getText().toString())*100.0)/100.0;

        Double gasCost = 2.29;
        Double totalGas = Math.round((Integer.parseInt(editTextEndOdometerReadings.getText().toString()) / Mileage)*100.0)/100.0;
        Double totalGasCost = Math.round(totalGas * gasCost*100.0)/100.0;

        btnCalculateMileage.setVisibility(View.GONE);
        textOdometer.setVisibility(View.GONE);
        editTextOdometer.setVisibility(View.GONE);
        textTankCapacity.setVisibility(View.GONE);
        editTextTankCapacity.setVisibility(View.GONE);
        textEndOdometerReadings.setVisibility(View.GONE);
        editTextEndOdometerReadings.setVisibility(View.GONE);
        textFuelConsumed.setVisibility(View.GONE);
        editTextFuelConsumed.setVisibility(View.GONE);
        textTermsAndConditions.setVisibility(View.GONE);

        textMileage.setText(Double.toString(Mileage));
        textMileage.setVisibility(View.VISIBLE);

        carDetailsCollection = new CarDetailsCollection();
        carDetailsCollection.setEmailId(editedRestoredEmailId);
        carDetailsCollection.setMake(editTextMake.getText().toString());
        carDetailsCollection.setModel(editTextModel.getText().toString());
        carDetailsCollection.setYear(editTextYear.getText().toString());
        //carDetailsCollection.setOdometer(editTextOdometer.getText().toString());
        carDetailsCollection.setTankCapacity(editTextTankCapacity.getText().toString());
        carDetailsCollection.setOdometer(editTextEndOdometerReadings.getText().toString());
        carDetailsCollection.setMileage(Mileage.toString());
        carDetailsCollection.setFuel(editTextTankCapacity.getText().toString());
        carDetailsCollection.setId(String.valueOf(Math.random()+1234));
        carDetailsCollection.setTotalGas(String.valueOf(totalGas));
        carDetailsCollection.setTotalGasCost(String.valueOf(totalGasCost));
        new TestDriveFragment.HttpAsyncTask().execute(INSERT_API_URL);
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
            //Toast.makeText(context, restoredText.toString(), Toast.LENGTH_SHORT).show();
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


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return model.POST_CarDetails(urls[0],carDetailsCollection);

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
            Toast.makeText(getContext(), "Received!", Toast.LENGTH_LONG).show();
            try {
                if(result!=null && !"".equalsIgnoreCase(result)){
                    JSONObject json = new JSONObject(result);
                    editTextMake.setText(json.getString("make"));
                    editTextModel.setText(json.getString("model"));
                    editTextYear.setText(json.getString("year"));
                    editTextOdometer.setText(json.getString("odometer"));
                    editTextTankCapacity.setText(json.getString("tankCapacity"));
                    textMileage.setText("CAR MILEAGE: "+json.getString("mileage"));
                }

            }catch (Exception e){

            }

        }
    }
}

