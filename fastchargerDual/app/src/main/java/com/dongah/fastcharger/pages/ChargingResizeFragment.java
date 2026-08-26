package com.dongah.fastcharger.pages;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.ClassUiProcess;
import com.dongah.fastcharger.basefunction.PaymentType;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.utils.SharedModel;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChargingResizeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChargingResizeFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(ChargingResizeFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannel;

    TextView txtChargePay, txtChargeTime, txtAmountOfCharge, textViewInputPrePayment, textViewRequestCurrentValue;
    TextView txtRemainTime, txtSoc, textViewInputUnit, textViewLimitSoc;
    TextView textViewPrePayment;
    CircularProgressIndicator progressCircular;
    Button  btnChargingStop;
    TextView txtOutVoltage, txtOutCurrent, txtOutPower;
    Handler uiUpdateHandler;
    double powerUnitPrice = 0f;
    SharedModel sharedModel;
    String[] requestStrings = new String[1];
    ClassUiProcess classUiProcess;
    ChargingCurrentData chargingCurrentData;
    Date startTime = null, useTime = null;
    DecimalFormat payFormatter = new DecimalFormat("#,###,##0");
    DecimalFormat powerFormatter = new DecimalFormat("#,###,##0.00");
    DecimalFormat voltageFormatter = new DecimalFormat("#,###,##0.0");
    ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();

    Handler displayHandler;


    public ChargingResizeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChargingResizeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChargingResizeFragment newInstance(String param1, String param2) {
        ChargingResizeFragment fragment = new ChargingResizeFragment();
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
            mChannel = getArguments().getInt(CHANNEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charging_resize, container, false);
        txtChargePay = view.findViewById(R.id.txtChargePay);
        txtChargeTime = view.findViewById(R.id.txtChargeTime);
        txtAmountOfCharge = view.findViewById(R.id.txtAmountOfCharge);
        txtOutCurrent = view.findViewById(R.id.textViewInputAmount);
        txtOutPower = view.findViewById(R.id.textViewInputPower);
        txtOutVoltage = view.findViewById(R.id.textViewInputVoltage);
        txtRemainTime = view.findViewById(R.id.txtRemainTime);
        btnChargingStop = view.findViewById(R.id.btnChargingStop);
        btnChargingStop.setOnClickListener(this);
        txtSoc = view.findViewById(R.id.txtSoc);
        textViewInputUnit = view.findViewById(R.id.textViewInputUnit);
        textViewPrePayment = view.findViewById(R.id.textViewPrePayment);
        textViewInputPrePayment = view.findViewById(R.id.textViewInputPrePayment);
        textViewRequestCurrentValue = view.findViewById(R.id.textViewRequestCurrentValue);
        textViewLimitSoc = view.findViewById(R.id.textViewLimitSoc);
        progressCircular = view.findViewById(R.id.progressCircular);
        chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
        return view;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.mContext, R.raw.charging);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();

            progressCircular.isIndeterminate();
            sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);
            requestStrings[0] = String.valueOf(mChannel);
            sharedModel.setMutableLiveData(requestStrings);

            if (Objects.equals(chargingCurrentData.getPaymentType(), PaymentType.CREDIT)) {
                textViewPrePayment.setVisibility(View.VISIBLE);
                textViewInputPrePayment.setVisibility(View.VISIBLE);
            } else {
                textViewPrePayment.setVisibility(View.INVISIBLE);
                textViewInputPrePayment.setVisibility(View.INVISIBLE);
            }

            try {
                classUiProcess = ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel);
                startTime = zonedDateTimeConvert.doStringDateToDate(chargingCurrentData.getChargingStartTime());
                powerUnitPrice = chargingCurrentData.getPowerUnitPrice();
                textViewInputUnit.setText(String.valueOf(powerUnitPrice) + " 원");
                textViewInputPrePayment.setText(chargingCurrentData.getPrePayment() + " 원");
                textViewLimitSoc.setText(getString(R.string.limitSoc) + ((MainActivity) MainActivity.mContext).getChargerConfiguration().getTargetSoc() + "%");
                progressCircular.setProgress(((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel).getSoc(), true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            onCharging();
        } catch (Exception e) {
            logger.error("ChargingFragment onViewCreated : {}", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (Objects.equals(getId, R.id.btnChargingStop)) {
            try {
                ChargerConfiguration chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
                if (!Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                    ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel, UiSeq.CHARGING_STOP_MESSAGE, "CHARGING_STOP_MESSAGE", null);
                } else {
                    //서버 인증 모드인 경우
                    ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
                    PaymentType paymentType = chargingCurrentData.getPaymentType();
                    if (Objects.equals(paymentType, PaymentType.MEMBER) && chargerConfiguration.isStopConfirm()) {
                        //StopTransactionOnInvalidId
                        ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel, UiSeq.MEMBER_CARD, "MEMBER_CARD", null);
                    } else {
                        ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel, UiSeq.CHARGING_STOP_MESSAGE, "CHARGING_STOP_MESSAGE", null);
                    }
                }

            } catch (Exception e) {
                logger.error("Charging onClick error : {}", e.getMessage());
            }
        }
    }

//    FileManagement fileManagement = new FileManagement();
//    int saveCnt = 0 ;
    @SuppressWarnings("ConstantConditions")
    private void onCharging() {
        uiUpdateHandler = new Handler();
        uiUpdateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @SuppressLint({"DefaultLocale", "SetTextI18n"})
                    @Override
                    public void run() {
                        try {
                            long diffTime = 0;
                            useTime = zonedDateTimeConvert.doStringDateToDate(zonedDateTimeConvert.getStringCurrentTimeZone());
                            if (useTime != null) {
                                diffTime = (useTime.getTime() - startTime.getTime()) / 1000;
                                int hour = (int) diffTime / 3600;
                                int minute = (int) (diffTime % 3600) / 60;
                                int second = (int) diffTime % 60;
                                chargingCurrentData.setChargingTime((int) diffTime);
                                txtChargeTime.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second));
                                chargingCurrentData.setChargingUseTime(txtChargeTime.getText().toString());

                                txtChargePay.setText(payFormatter.format((long) chargingCurrentData.getPowerMeterUsePay()) + " 원");
                                txtAmountOfCharge.setText(powerFormatter.format(chargingCurrentData.getPowerMeterUse() * 0.01) + " kWh");

                                int rHour = chargingCurrentData.getRemaintime() / 3600;
                                int rMinute = (chargingCurrentData.getRemaintime() % 3600) / 60;
                                int rSecond = chargingCurrentData.getRemaintime() % 60;

                                txtRemainTime.setText(String.format("%02d", rHour) + ":" + String.format("%02d", rMinute) + ":" + String.format("%02d", rSecond));
                                chargingCurrentData.setRemainTimeStr(String.format("%02d", rHour)+String.format("%02d", rMinute)+String.format("%02d", rSecond));

                                txtSoc.setText(chargingCurrentData.getSoc() + "%");
                                progressCircular.setProgress(chargingCurrentData.getSoc(), true);

                                txtOutVoltage.setText(voltageFormatter.format(chargingCurrentData.getOutPutVoltage() * 0.1) + " V");
                                txtOutCurrent.setText(powerFormatter.format(chargingCurrentData.getOutPutCurrent() * 0.1) + " A");
                                txtOutPower.setText(powerFormatter.format(chargingCurrentData.getOutPutVoltage() * chargingCurrentData.getOutPutCurrent() * 0.00001) + " kW");
                                textViewRequestCurrentValue.setText(powerFormatter.format(((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel).getTargetCurrent() * 0.1) + "A");

//
//                                //log data
//                                if (Objects.equals(saveCnt, 4)) {
//                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//                                    String logTime = zonedDateTimeConvert.doGetCurrentTime().format(formatter);
//
//                                    String saveDate = logTime + "|" + txtOutVoltage.getText().toString() + "|" + txtOutCurrent.getText().toString() + "|"
//                                            + txtOutPower.getText().toString() + "|" + txtSoc.getText().toString() ;
//
//                                    fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "chargingLog", saveDate, true);
//                                    saveCnt = 0;
//                                } else {
//                                    saveCnt++;
//                                }


                            }
                        } catch (Exception e) {
                            logger.error("ChargingFragment onCharging : {}", e.getMessage());
                        }
                    }
                });
                uiUpdateHandler.postDelayed(this, 1000);
            }
        }, 50);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            requestStrings[0] = String.valueOf(mChannel);
            sharedModel.setMutableLiveData(requestStrings);
            uiUpdateHandler.removeCallbacksAndMessages(null);
            uiUpdateHandler.removeMessages(0);
            //display handler
            displayHandler.removeCallbacksAndMessages(null);
            displayHandler.removeMessages(0);
            if (uiUpdateHandler != null) uiUpdateHandler = null;
        } catch (Exception e) {
            logger.error("ChargingFragment onDetach : {}", e.getMessage());
        }
    }
}