package edu.umkc.mobile.cargasmileageestimator;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.umkc.mobile.cargasmileageestimator.data.MileageCollection;
import edu.umkc.mobile.cargasmileageestimator.model.MileageModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Spinner spinnerValue;
    String label;
    Double labelCount;
    List<MileageCollection> mileageCollections = null;
    final Random random = new Random();
    MileageModel model = new MileageModel();
    String GET_API_URL = "http://10.205.0.55:8080/api/getAllMileageCollections/";
    Map<String,Double> distanceMap =null;
    Map<String,Double> fuelMap = null;
    Map<String,Double> mileageMap = null;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
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
        final View view =  inflater.inflate(R.layout.fragment_stats, container, false);

        new StatsFragment.HttpAsyncTask().execute(GET_API_URL);

        Spinner spinner=(Spinner)view.findViewById(R.id.spinner1);

        List<String> dropdownlist = new ArrayList<>();
        dropdownlist.add("--Select--");
        dropdownlist.add("Distance");
        dropdownlist.add("Fuel");
        dropdownlist.add("Mileage");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, dropdownlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);

        final PieChart p = new PieChart(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(650,650);
        p.setLayoutParams(lp);
        //p.setBackgroundColor(0xffffffff);
        p.setOnSliceClickListener(new PieChart.OnSliceClickListener() {
            @Override
            public void onSliceClicked(PieChart pieChart, int sliceNumber) {
                spinnerValue = (Spinner) view.findViewById(R.id.spinner1);

                if(spinnerValue.getSelectedItem().toString().equalsIgnoreCase("Distance")){

                    List<String> keys = new ArrayList<String>(distanceMap.keySet());
                    List<Double> values = new ArrayList<Double>(distanceMap.values());
                    label = keys.get(sliceNumber);
                    labelCount = values.get(sliceNumber);
                    Toast.makeText(getContext(), label +": "+labelCount+" miles", Toast.LENGTH_SHORT).show();
                }else if(spinnerValue.getSelectedItem().toString().equalsIgnoreCase("Fuel")){

                    List<String> keys = new ArrayList<String>(fuelMap.keySet());
                    List<Double> values = new ArrayList<Double>(fuelMap.values());
                    label = keys.get(sliceNumber);
                    labelCount = values.get(sliceNumber);
                    Toast.makeText(getContext(), label +": "+labelCount+" gallons", Toast.LENGTH_SHORT).show();
                }else if(spinnerValue.getSelectedItem().toString().equalsIgnoreCase("Mileage")){
                    List<String> keys = new ArrayList<String>(mileageMap.keySet());
                    List<Double> values = new ArrayList<Double>(mileageMap.values());
                    label = keys.get(sliceNumber);
                    labelCount = values.get(sliceNumber);
                    Toast.makeText(getContext(), label +": "+labelCount+" mpg", Toast.LENGTH_SHORT).show();
                }else{
                    label = spinnerValue.getSelectedItem().toString() + sliceNumber;
                    labelCount = 1.0;
                }

            }
        });
        layout.addView(p);

        spinnerValue = (Spinner) view.findViewById(R.id.spinner1);


        spinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String  mselection=spinnerValue.getSelectedItem().toString();

                if("Distance".equalsIgnoreCase(mselection)){
                    float[] data = new float[distanceMap.size()];
                    Iterator it = distanceMap.entrySet().iterator();
                    int count=0;
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        data[count] = Float.parseFloat(pair.getValue().toString());
                        count++;
                    }

                    p.setSlices(data);
                    p.anima();
                }else if("Fuel".equalsIgnoreCase(mselection)){
                    float[] data = new float[fuelMap.size()];
                    Iterator it = fuelMap.entrySet().iterator();
                    int count=0;
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        data[count] = Float.parseFloat(pair.getValue().toString());
                        count++;
                    }

                    p.setSlices(data);
                    p.anima();
                }else if("Mileage".equalsIgnoreCase(mselection)){
                    float[] data = new float[mileageMap.size()];
                    Iterator it = mileageMap.entrySet().iterator();
                    int count=0;
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        data[count] = Float.parseFloat(pair.getValue().toString());
                        count++;
                    }

                    p.setSlices(data);
                    p.anima();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                //
            }
        });

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
            //Toast.makeText(context, "Stats Fragment Attached", Toast.LENGTH_SHORT).show();
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

            try {
                mileageCollections = model.GETALLCollection(GET_API_URL);
                distanceMap = new LinkedHashMap<String, Double>();
                fuelMap = new LinkedHashMap<String, Double>();
                mileageMap = new LinkedHashMap<String, Double>();
                for (MileageCollection collection : mileageCollections) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyy");
                    Date tempDate = simpleDateFormat.parse(collection.getDate());
                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                    distanceMap.put(outputDateFormat.format(tempDate), Double.parseDouble(collection.getDistance()));
                    fuelMap.put(outputDateFormat.format(tempDate), Double.parseDouble(collection.getGasRemaining()));
                    mileageMap.put(outputDateFormat.format(tempDate), Double.parseDouble(collection.getMileage()));
                }

            }catch (Exception e){
                Log.d("a",e.getMessage());
            }
            return "SUCCESS";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }
}
