package com.dongah.fastcharger.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.controlboard.ControlBoard;
import com.dongah.fastcharger.controlboard.ControlBoardListener;
import com.dongah.fastcharger.controlboard.ControlBoardUtil;
import com.dongah.fastcharger.controlboard.ListViewDspAdapter;
import com.dongah.fastcharger.controlboard.RxData;
import com.dongah.fastcharger.controlboard.TxData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControlDebugFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlDebugFragment extends Fragment implements View.OnClickListener, ControlBoardListener {

    private static final Logger logger = LoggerFactory.getLogger(ControlDebugFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DecimalFormat decimalFormat;
    Button btnClose;
    RadioGroup rgSelectChannel;
    ListView listRx, listTx;
    ListViewDspAdapter listViewRxAdapter, listViewTxAdapter;
    ControlBoard controlBoard;
    ControlBoardUtil controlBoardUtil;
    int currCh = 0;


    public ControlDebugFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ControlDebugFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControlDebugFragment newInstance(String param1, String param2) {
        ControlDebugFragment fragment = new ControlDebugFragment();
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
        View view = inflater.inflate(R.layout.fragment_control_debug, container, false);
        //** integer format */
        decimalFormat = new DecimalFormat("#,###,##0.0#");
        controlBoardUtil = new ControlBoardUtil();
        btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        rgSelectChannel = view.findViewById(R.id.rgSelectChannel);

        //** Rx data */
        listRx = (ListView) view.findViewById(R.id.listRx);
        listViewRxAdapter = new ListViewDspAdapter();
        listViewRxAdapter.notifyDataSetChanged();
        listRx.setAdapter(listViewRxAdapter);

        //* Tx data */
        listTx = (ListView) view.findViewById(R.id.listTx);
        listViewTxAdapter = new ListViewDspAdapter();
        listViewTxAdapter.notifyDataSetChanged();
        listTx.setAdapter(listViewTxAdapter);

        controlBoard = ((MainActivity) MainActivity.mContext).getControlBoard();
        controlBoard.setDspControlListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            rgSelectChannel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rbSelect = (RadioButton) view.findViewById(checkedId);
                    if (Objects.equals(rbSelect.getTag(), "0")) {
                        setCurrCh(0);
                    }
                    if (Objects.equals(rbSelect.getTag(), "1")) {
                        setCurrCh(1);
                    }
                }
            });
        } catch (Exception e) {
            logger.error(" ControlDebugFragment onViewCreated {}", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (Objects.equals(v.getId(), R.id.btnClose)) {
            //environment
            FragmentTransaction transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
            EnvironmentFragment environmentFragment = new EnvironmentFragment();
            transaction.replace(R.id.fullScreen, environmentFragment);
            transaction.commit();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        controlBoard.setControlBoardListenerStop();
    }

    @Override
    public void onControlBoardReceive(RxData[] rxData) {
        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listViewRxAdapter.clearItem();
                        listViewRxAdapter.addItem("RX", "csPilot", String.valueOf(rxData[getCurrCh()].isCsPilot()));
                        listViewRxAdapter.addItem("RX", "csStart", String.valueOf(rxData[getCurrCh()].isCsStart()));
                        listViewRxAdapter.addItem("RX", "csStop", String.valueOf(rxData[getCurrCh()].isCsStop()));
                        listViewRxAdapter.addItem("RX", "csFault", String.valueOf(rxData[getCurrCh()].isCsFault()));
                        listViewRxAdapter.addItem("RX", "cpVoltage", String.valueOf(rxData[getCurrCh()].getCpVoltage() * 0.1));
                        listViewRxAdapter.addItem("RX", "FW Ver", controlBoardUtil.parseVersion(rxData[getCurrCh()].getFirmWareVersion()));
                        listViewRxAdapter.addItem("RX", "RemainTime", controlBoardUtil.getRemainTime(rxData[getCurrCh()].getRemainTime()));
                        listViewRxAdapter.addItem("RX", "SOC", String.valueOf(rxData[getCurrCh()].getSoc()));
                        listViewRxAdapter.addItem("RX", "csMc1Fault", String.valueOf(rxData[getCurrCh()].isCsMc1Fault()));
                        listViewRxAdapter.addItem("RX", "csMc2Fault", String.valueOf(rxData[getCurrCh()].isCsMc2Fault()));
                        listViewRxAdapter.addItem("RX", "csRelay1", String.valueOf(rxData[getCurrCh()].isCsRelay1()));
                        listViewRxAdapter.addItem("RX", "csRelay2", String.valueOf(rxData[getCurrCh()].isCsRelay2()));
                        listViewRxAdapter.addItem("RX", "csRelay3", String.valueOf(rxData[getCurrCh()].isCsRelay3()));
                        listViewRxAdapter.addItem("RX", "csRelay4", String.valueOf(rxData[getCurrCh()].isCsRelay4()));
                        listViewRxAdapter.addItem("RX", "csRelay5", String.valueOf(rxData[getCurrCh()].isCsRelay5()));
                        listViewRxAdapter.addItem("RX", "csRelay6", String.valueOf(rxData[getCurrCh()].isCsRelay6()));
                        listViewRxAdapter.addItem("RX", "powerMeter", String.valueOf(rxData[getCurrCh()].getPowerMeter() * 0.01));
                        listViewRxAdapter.addItem("RX", "outVoltage", String.valueOf(rxData[getCurrCh()].getOutVoltage() * 0.1));
                        listViewRxAdapter.addItem("RX", "outCurrent", String.valueOf(rxData[getCurrCh()].getOutCurrent() * 0.1));

                        listViewRxAdapter.addItem("RX", "csEmergency", String.valueOf(rxData[getCurrCh()].isCsEmergency()));
                        listViewRxAdapter.addItem("RX", "csPLCComm", String.valueOf(rxData[getCurrCh()].isCsPLCComm()));
                        listViewRxAdapter.addItem("RX", "csPowerMeterComm", String.valueOf(rxData[getCurrCh()].isCsPowerMeterComm()));
                        listViewRxAdapter.addItem("RX", "csModule1Comm", String.valueOf(rxData[getCurrCh()].isCsModule1Comm()));
                        listViewRxAdapter.addItem("RX", "csModule2Comm", String.valueOf(rxData[getCurrCh()].isCsModule2Comm()));
                        listViewRxAdapter.addItem("RX", "csModule3Comm", String.valueOf(rxData[getCurrCh()].isCsModule3Comm()));
                        listViewRxAdapter.addItem("RX", "csModule4Comm", String.valueOf(rxData[getCurrCh()].isCsModule4Comm()));
                        listViewRxAdapter.addItem("RX", "충전기 누설", String.valueOf(rxData[getCurrCh()].isCsChargerLeak()));
                        listViewRxAdapter.addItem("RX", "차량 누설", String.valueOf(rxData[getCurrCh()].isCsCarLeak()));
                        listViewRxAdapter.addItem("RX", "OVR", String.valueOf(rxData[getCurrCh()].isCsOutOVR()));
                        listViewRxAdapter.addItem("RX", "OCR", String.valueOf(rxData[getCurrCh()].isCsOutOCR()));
                        listViewRxAdapter.addItem("RX", "커플러 온도 센서", String.valueOf(rxData[getCurrCh()].isCsCouplerTempSensor()));
                        listViewRxAdapter.addItem("RX", "커플러 과온도", String.valueOf(rxData[getCurrCh()].isCsCouplerOVT()));
                        listViewRxAdapter.addItem("RX", "csModule1Error", String.valueOf(rxData[getCurrCh()].isCsModule1Error()));
                        listViewRxAdapter.addItem("RX", "csModule2Error", String.valueOf(rxData[getCurrCh()].isCsModule2Error()));
                        listViewRxAdapter.addItem("RX", "csModule3Error", String.valueOf(rxData[getCurrCh()].isCsModule3Error()));
                        listViewRxAdapter.addItem("RX", "csModule4Error", String.valueOf(rxData[getCurrCh()].isCsModule4Error()));
                        listViewRxAdapter.addItem("RX", "커플러 온도", String.valueOf(rxData[getCurrCh()].getCouplerTemp()));
                        ///나중에 PLC 모뎀
                        listViewRxAdapter.notifyDataSetChanged();

                    }
                });
            }
        } catch (Exception e) {
            listViewRxAdapter.clearItem();
            logger.error(" onControlBoardReceive error :  {}", e.getMessage());
        }
    }

    @Override
    public void onControlBoardSend(TxData[] txData) {
        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listViewTxAdapter.clearItem();
                        listViewTxAdapter.addItem("TX", "IsStart", String.valueOf(txData[currCh].IsStart));
                        listViewTxAdapter.addItem("TX", "IsStop", String.valueOf(txData[currCh].IsStop));
                        listViewTxAdapter.addItem("TX", "IsReset", String.valueOf(txData[currCh].IsReset));
                        listViewTxAdapter.addItem("TX", "uiSequence", txData[currCh].uiSequence == 0 ? "대기" : txData[currCh].uiSequence == 1 ? "충전" : "종료");
                        listViewTxAdapter.addItem("TX", "chargerPointMode", txData[currCh].chargerPointMode == 0 ? "운영" : txData[currCh].uiSequence == 1 ? "부하" : "I/O");
                        listViewTxAdapter.addItem("TX", "testDualSingle", txData[currCh].testDualSingle == 0 ? "듀얼Test" : "싱글Test");
                        listViewTxAdapter.addItem("TX", "testDrVoltage", String.valueOf(txData[currCh].testDrVoltage));
                        listViewTxAdapter.addItem("TX", "testDrCurrent", String.valueOf(txData[currCh].testDrCurrent));
                        listViewTxAdapter.addItem("TX", "IsRelay1", txData[currCh].IsRelay1 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsRelay2", txData[currCh].IsRelay2 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsRelay3", txData[currCh].IsRelay3 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsRelay4", txData[currCh].IsRelay4 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsRelay5", txData[currCh].IsRelay5 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsRelay6", txData[currCh].IsRelay6 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsMC1", txData[currCh].IsMC1 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsMC2", txData[currCh].IsMC2 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsFan1", txData[currCh].IsFan1 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsOut1", txData[currCh].IsOut1 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsOut2", txData[currCh].IsOut2 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsOut1", txData[currCh].IsOut1 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsOut2", txData[currCh].IsOut2 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsOut3", txData[currCh].IsOut3 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsOut4", txData[currCh].IsOut4 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsOut5", txData[currCh].IsOut5 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsOut6", txData[currCh].IsOut6 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "IsOut7", txData[currCh].IsOut7 ? "ON" : "OFF");
                        listViewTxAdapter.addItem("TX", "outPowerLimit", String.valueOf(txData[currCh].outPowerLimit));
                        listViewTxAdapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (Exception e) {
            listViewRxAdapter.clearItem();
            logger.error(" onControlBoardSend error :  {}", e.getMessage());
        }
    }

    public int getCurrCh() {
        return currCh;
    }

    public void setCurrCh(int currCh) {
        this.currCh = currCh;
    }
}