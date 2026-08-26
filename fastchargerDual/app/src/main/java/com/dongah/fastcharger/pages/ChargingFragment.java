package com.dongah.fastcharger.pages;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
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
 * Use the {@link ChargingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChargingFragment extends Fragment {

    private static final Logger logger = LoggerFactory.getLogger(ChargingFragment.class);

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
    TextView txtRemainTime, txtSoc, textViewTimer, textViewInputUnit, textViewLimitSoc;
    TextView txtOutVoltage, txtOutCurrent, txtOutPower;
    CircularProgressIndicator progressCircular;
    CardView cardViewPayment;
    Handler uiUpdateHandler;
    double powerUnitPrice = 0f;
    SharedModel sharedModel;
    String[] requestStrings = new String[1];
    ClassUiProcess classUiProcess;
    Date startTime = null, useTime = null;
    DecimalFormat payFormatter = new DecimalFormat("#,###,##0");
    DecimalFormat powerFormatter = new DecimalFormat("#,###,##0.00");
    DecimalFormat voltageFormatter = new DecimalFormat("#,###,##0.0");
    ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();
    ChargingCurrentData chargingCurrentData;


    Handler displayHandler;
    Runnable displayRunnable;
    int cnt;


    public ChargingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChargingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChargingFragment newInstance(String param1, String param2) {
        ChargingFragment fragment = new ChargingFragment();
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charging, container, false);
        txtChargePay = view.findViewById(R.id.txtChargePay);
        txtChargeTime = view.findViewById(R.id.txtChargeTime);
        txtAmountOfCharge = view.findViewById(R.id.txtAmountOfCharge);
        txtOutCurrent = view.findViewById(R.id.textViewInputAmount);
        txtOutPower = view.findViewById(R.id.textViewInputPower);
        txtOutVoltage = view.findViewById(R.id.textViewInputVoltage);
        txtRemainTime = view.findViewById(R.id.txtRemainTime);
        txtSoc = view.findViewById(R.id.txtSoc);
        textViewInputUnit = view.findViewById(R.id.textViewInputUnit);
        textViewInputPrePayment = view.findViewById(R.id.textViewInputPrePayment);
        textViewTimer = view.findViewById(R.id.textViewTimer);
        textViewRequestCurrentValue = view.findViewById(R.id.textViewRequestCurrentValue);
        textViewLimitSoc = view.findViewById(R.id.textViewLimitSoc);
        progressCircular = view.findViewById(R.id.progressCircular);
        cardViewPayment = view.findViewById(R.id.cardView3);
        chargingCurrentData = ((MainActivity) getActivity()).getChargingCurrentData(mChannel);
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
                cardViewPayment.setVisibility(View.VISIBLE);
            } else {
                cardViewPayment.setVisibility(View.INVISIBLE);
            }


            //display
            ((MainActivity) MainActivity.mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayHandler = new Handler();
                    displayRunnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cnt++;
                                if (Objects.equals(cnt,10)){
                                    ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.CHARGING);
                                    ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel, UiSeq.CHARGING, "UiSeq.CHARGING", "small");
                                } else {
                                    textViewTimer.setText(String.valueOf(10 - cnt) + getString(R.string.screenChangeMessage));
                                    displayHandler.postDelayed(displayRunnable, 1000);
                                }
                            } catch (Exception e){
                                logger.error(e.getMessage());
                            }
                        }
                    };
                    displayHandler.postDelayed(displayRunnable, 1000);
                }
            });

            try {
                ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
                startTime = zonedDateTimeConvert.doStringDateToDate(chargingCurrentData.getChargingStartTime());
                powerUnitPrice = chargingCurrentData.getPowerUnitPrice();
                textViewInputUnit.setText(powerUnitPrice + " 원");
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
                            ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
                            if (((MainActivity) MainActivity.mContext).getChargerConfiguration().getTargetSoc() <= chargingCurrentData.getSoc()) {

                            }

                            useTime = zonedDateTimeConvert.doStringDateToDate(zonedDateTimeConvert.getStringCurrentTimeZone());
                            if (useTime != null) {
                                diffTime = (useTime.getTime() - startTime.getTime()) / 1000;
                                int hour = (int) diffTime / 3600;
                                int minute = (int) (diffTime % 3600) / 60;
                                int second = (int) diffTime % 60;
                                chargingCurrentData.setChargingTime((int) diffTime);
                                txtChargeTime.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second));
                                chargingCurrentData.setChargingUseTime(txtChargeTime.getText().toString());

                                txtChargePay.setText(payFormatter.format((long) chargingCurrentData.getPowerMeterUsePay()));
                                txtAmountOfCharge.setText(powerFormatter.format(chargingCurrentData.getPowerMeterUse() * 0.01));

                                int rHour = chargingCurrentData.getRemaintime() / 3600;
                                int rMinute = (chargingCurrentData.getRemaintime() % 3600) / 60;
                                int rSecond = chargingCurrentData.getRemaintime() % 60;

                                txtRemainTime.setText(String.format("%02d", rHour) + ":" + String.format("%02d", rMinute) + ":" + String.format("%02d", rSecond));

                                txtSoc.setText(chargingCurrentData.getSoc() + "%");
                                progressCircular.setProgress(chargingCurrentData.getSoc(), true);

                                txtOutVoltage.setText(voltageFormatter.format(chargingCurrentData.getOutPutVoltage() * 0.1) + " V");
                                txtOutCurrent.setText(powerFormatter.format(chargingCurrentData.getOutPutCurrent() * 0.1) + " A");
                                txtOutPower.setText(powerFormatter.format(chargingCurrentData.getOutPutVoltage() * chargingCurrentData.getOutPutCurrent() * 0.00001) + " kW");
                                textViewRequestCurrentValue.setText(powerFormatter.format(((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel).getTargetCurrent() * 0.1) + "A");
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
            if (uiUpdateHandler != null) uiUpdateHandler = null;
            //display handler
            displayHandler.removeCallbacksAndMessages(null);
            displayHandler.removeMessages(0);
            if (displayHandler != null) displayHandler = null;
        } catch (Exception e) {
            logger.error("ChargingFragment onDetach : {}", e.getMessage());
        }
    }
}