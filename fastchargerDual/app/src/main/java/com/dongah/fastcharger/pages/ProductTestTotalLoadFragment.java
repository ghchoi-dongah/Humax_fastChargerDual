package com.dongah.fastcharger.pages;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.controlboard.ControlBoard;
import com.dongah.fastcharger.controlboard.RxData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductTestTotalLoadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductTestTotalLoadFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(ProductTestTotalLoadFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    InputMethodManager imm;

    Button btnKeyBoard;
    Button btnCH1, btnCH2;
    Button btnTotalStart, btnTotalStop, btnReset;
    ToggleButton btnMainMC1;
    EditText editOutV1, editOutA1;
    EditText editDrV1, editDrA1;
    DecimalFormat voltageFormatter;

    ControlBoard controlBoard;
    Handler statusHandler;
    Runnable statusRunnable;
    int selectChannel = 0;


    public ProductTestTotalLoadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductTestTotalLoadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductTestTotalLoadFragment newInstance(String param1, String param2) {
        ProductTestTotalLoadFragment fragment = new ProductTestTotalLoadFragment();
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
        View view = inflater.inflate(R.layout.fragment_product_test_total_load, container, false);
        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        btnKeyBoard = view.findViewById(R.id.btnKeyBoard);
        btnKeyBoard.setOnClickListener(this);

        voltageFormatter = new DecimalFormat("#,###,##0.0");
        /** test mode  */
        controlBoard = ((MainActivity) MainActivity.mContext).getControlBoard();
        controlBoard.getTxData(0).setChargerPointMode((short) 1);
        btnCH1 = view.findViewById(R.id.btnCH1);
        btnCH2 = view.findViewById(R.id.btnCH2);
        btnCH1.setOnClickListener(this);
        btnCH2.setOnClickListener(this);
        btnTotalStart = view.findViewById(R.id.btnTotalStart);
        btnTotalStop = view.findViewById(R.id.btnTotalStop);
        btnTotalStart.setOnClickListener(this);
        btnTotalStop.setOnClickListener(this);
        btnReset = view.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);
        btnMainMC1 = view.findViewById(R.id.btnMainMC1);
        btnMainMC1.setOnClickListener(this);

        editOutV1 = view.findViewById(R.id.editOutV1);
        editOutA1 = view.findViewById(R.id.editOutA1);
        editDrV1 = view.findViewById(R.id.editDrV1);
        editDrA1 = view.findViewById(R.id.editDrA1);
        return view;
    }


    private void onDspControlStatus() {
        ((MainActivity) MainActivity.mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusHandler = new Handler();
                statusRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            RxData rxData = controlBoard.getRxData(selectChannel);
                            editOutV1.setText(voltageFormatter.format(rxData.getOutVoltage() * 0.1));
                            editOutA1.setText(voltageFormatter.format(rxData.getOutCurrent() * 0.1));
                            controlBoard.getTxData(selectChannel).setTestDrVoltage((short) (Double.parseDouble(editDrV1.getText().toString())));
                            controlBoard.getTxData(selectChannel).setTestDrCurrent((short) (Double.parseDouble(editDrA1.getText().toString())));
                        } catch (Exception e) {
                            logger.error("onDspControlStatus error : {}", e.getMessage());
                        }
                        statusHandler.postDelayed(statusRunnable, 400);
                    }
                };
                statusHandler.postDelayed(statusRunnable, 0);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        onDspControlStatus();
    }


    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (Objects.equals(getId, R.id.btnKeyBoard)) {
            try {
                View view = ((MainActivity) MainActivity.mContext).getCurrentFocus();
                if (view instanceof EditText) {
                    EditText editText = (EditText) (((MainActivity) MainActivity.mContext).getCurrentFocus());
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            } catch (Exception e) {
                logger.error("onClick error : {}", e.getMessage());
            }
        } else if (Objects.equals(getId, R.id.btnCH1)) {
            selectChannel = 0;
            controlBoard.getTxData(0).setTestDualSingle((short) 0);
        } else if (Objects.equals(getId, R.id.btnCH2)) {
            selectChannel = 1;
            controlBoard.getTxData(0).setTestDualSingle((short) 1);
        } else if (Objects.equals(getId, R.id.btnTotalStart)) {
            btnCH1.setEnabled(false);
            btnCH2.setEnabled(false);
            btnTotalStart.setEnabled(false);
            btnTotalStop.setEnabled(true);
            controlBoard.getTxData(selectChannel).setTestDrVoltage((short) (Double.parseDouble(editDrV1.getText().toString())));
            controlBoard.getTxData(selectChannel).setTestDrCurrent((short) (Double.parseDouble(editDrA1.getText().toString())));
            controlBoard.getTxData(selectChannel).setStart(true);
            controlBoard.getTxData(selectChannel).setStop(false);
        } else if (Objects.equals(getId, R.id.btnTotalStop)) {
            btnCH1.setEnabled(true);
            btnCH2.setEnabled(true);
            btnTotalStart.setEnabled(true);
            btnTotalStop.setEnabled(false);
            controlBoard.getTxData(selectChannel).setTestDrVoltage((short) 0);
            controlBoard.getTxData(selectChannel).setTestDrCurrent((short) 0);
            controlBoard.getTxData(selectChannel).setStart(false);
            controlBoard.getTxData(selectChannel).setStop(true);
        } else if (Objects.equals(getId, R.id.btnMainMC1)) {
            controlBoard.getTxData(0).setMC1(btnMainMC1.isChecked());
        } else if (Objects.equals(getId, R.id.btnReset)) {
            controlBoard.getTxData(0).setReset(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        btnTotalStop.performClick();
        controlBoard.getTxData(0).setTestDualSingle((short) 2);
        statusHandler.removeCallbacks(statusRunnable);
        statusHandler.removeCallbacksAndMessages(null);
        statusHandler.removeMessages(0);
    }
}