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
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.controlboard.ControlBoard;
import com.dongah.fastcharger.controlboard.RxData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductTestLoadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductTestLoadFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(ProductTestLoadFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    InputMethodManager imm;
    Button btnKeyBoard;
    Button btnStart1, btnStop1, btnStart2, btnStop2;
    EditText editDrV1, editDrA1, editDrV2, editDrA2;
    EditText editOutV1, editOutA1, editOutV2, editOutA2;
    DecimalFormat voltageFormatter;

    ToggleButton btnMainMC1, btnMainMC2;
    ControlBoard controlBoard;
    Handler statusHandler;
    Runnable statusRunnable;

    public ProductTestLoadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductTestLoadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductTestLoadFragment newInstance(String param1, String param2) {
        ProductTestLoadFragment fragment = new ProductTestLoadFragment();
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
        View view = inflater.inflate(R.layout.fragment_product_test_load, container, false);
        voltageFormatter = new DecimalFormat("#,###,##0.0");
        imm = (InputMethodManager) ((MainActivity) MainActivity.mContext).getSystemService(INPUT_METHOD_SERVICE);
        controlBoard = ((MainActivity) MainActivity.mContext).getControlBoard();
        for (int i = 0; i < GlobalVariables.maxChannel; i++) {
            controlBoard.getTxData(i).setChargerPointMode((short) 1);
            controlBoard.getTxData(i).setTestDualSingle((short) 2);

        }
        editDrV1 = view.findViewById(R.id.editDrV1);
        editDrA1 = view.findViewById(R.id.editDrA1);
        editDrV2 = view.findViewById(R.id.editDrV2);
        editDrA2 = view.findViewById(R.id.editDrA2);
        editOutV1 = view.findViewById(R.id.editOutV1);
        editOutA1 = view.findViewById(R.id.editOutA1);
        editOutV2 = view.findViewById(R.id.editOutV2);
        editOutA2 = view.findViewById(R.id.editOutA2);

        btnKeyBoard = view.findViewById(R.id.btnKeyBoard);
        btnKeyBoard.setOnClickListener(this);

        btnStart1 = view.findViewById(R.id.btnStart1);
        btnStart2 = view.findViewById(R.id.btnStart2);
        btnStop1 = view.findViewById(R.id.btnStop1);
        btnStop2 = view.findViewById(R.id.btnStop2);
        btnStart1.setOnClickListener(this);
        btnStart2.setOnClickListener(this);
        btnStop1.setOnClickListener(this);
        btnStop2.setOnClickListener(this);
        btnMainMC1 = view.findViewById(R.id.btnMainMC1);
        btnMainMC2 = view.findViewById(R.id.btnMainMC2);
        btnMainMC1.setOnClickListener(this);
        btnMainMC2.setOnClickListener(this);
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
                            for (int i = 0; i < GlobalVariables.maxChannel; i++) {
                                RxData rxData = controlBoard.getRxData(i);
                                switch (i) {
                                    case 0:
                                        editOutV1.setText(voltageFormatter.format(rxData.getOutVoltage() * 0.1));
                                        editOutA1.setText(voltageFormatter.format(rxData.getOutCurrent() * 0.1));
                                        controlBoard.getTxData(0).setTestDrVoltage((short) (Double.parseDouble(editDrV1.getText().toString())));
                                        controlBoard.getTxData(0).setTestDrCurrent((short) (Double.parseDouble(editDrA1.getText().toString())));
                                        break;
                                    case 1:
                                        editOutV2.setText(voltageFormatter.format(rxData.getOutVoltage() * 0.1));
                                        editOutA2.setText(voltageFormatter.format(rxData.getOutCurrent() * 0.1));
                                        controlBoard.getTxData(1).setTestDrVoltage((short) (Double.parseDouble(editDrV2.getText().toString())));
                                        controlBoard.getTxData(1).setTestDrCurrent((short) (Double.parseDouble(editDrA2.getText().toString())));
                                        break;
                                }
                            }
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
        } else if (Objects.equals(getId, R.id.btnStart1)) {
            try {
                btnStart1.setEnabled(false);
                btnStop1.setEnabled(true);
                controlBoard.getTxData(0).setTestDrVoltage((short) (Double.parseDouble(editDrV1.getText().toString())));
                controlBoard.getTxData(0).setTestDrCurrent((short) (Double.parseDouble(editDrA1.getText().toString())));
                controlBoard.getTxData(0).setStart(true);
                controlBoard.getTxData(0).setStop(false);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else if (Objects.equals(getId, R.id.btnStop1)) {
            btnStart1.setEnabled(true);
            btnStop1.setEnabled(false);
            controlBoard.getTxData(0).setTestDrVoltage((short) 0);
            controlBoard.getTxData(0).setTestDrCurrent((short) 0);
            controlBoard.getTxData(0).setStart(false);
            controlBoard.getTxData(0).setStop(true);
        } else if (Objects.equals(getId, R.id.btnStart2)) {
            try {
                btnStart2.setEnabled(false);
                btnStop2.setEnabled(true);
                controlBoard.getTxData(1).setTestDrVoltage((short) (Double.parseDouble(editDrV2.getText().toString())));
                controlBoard.getTxData(1).setTestDrCurrent((short) (Double.parseDouble(editDrA2.getText().toString())));
                controlBoard.getTxData(1).setStart(true);
                controlBoard.getTxData(1).setStop(false);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else if (Objects.equals(getId, R.id.btnStop2)) {
            btnStart2.setEnabled(true);
            btnStop2.setEnabled(false);
            controlBoard.getTxData(1).setTestDrVoltage((short) 0);
            controlBoard.getTxData(1).setTestDrCurrent((short) 0);
            controlBoard.getTxData(1).setStart(false);
            controlBoard.getTxData(1).setStop(true);
        } else if (Objects.equals(getId, R.id.btnMainMC1)) {
            controlBoard.getTxData(0).setMC1(btnMainMC1.isChecked());
        } else if (Objects.equals(getId, R.id.btnMainMC2)) {
            controlBoard.getTxData(1).setMC1(btnMainMC2.isChecked());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        statusHandler.removeCallbacks(statusRunnable);
        statusHandler.removeCallbacksAndMessages(null);
        statusHandler.removeMessages(0);
    }
}