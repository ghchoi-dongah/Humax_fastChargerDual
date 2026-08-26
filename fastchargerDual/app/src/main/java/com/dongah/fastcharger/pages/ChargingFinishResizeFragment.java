package com.dongah.fastcharger.pages;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.TECH3800.TLS3800;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.ClassUiProcess;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.basefunction.PaymentType;
import com.dongah.fastcharger.controlboard.RxData;
import com.dongah.fastcharger.handler.ProcessHandler;
import com.dongah.fastcharger.websocket.ocpp.core.ChargePointStatus;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;
import com.dongah.fastcharger.websocket.socket.SocketState;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChargingFinishResizeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChargingFinishResizeFragment extends Fragment implements View.OnClickListener{
    private static final Logger logger = LoggerFactory.getLogger(ChargingFinishFragment.class);


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannel;

    Button btnStopConfirm;
    CircularProgressIndicator progressCircular;
    CardView cardViewPayment;
    TextView  txtSoc, txtAmountOfCharge, txtChargePay, txtChargeTime, textViewInputPrePayment, textViewInputCancelPayment;
    ClassUiProcess classUiProcess;
    ChargingCurrentData chargingCurrentData;
    DecimalFormat payFormatter = new DecimalFormat("#,###,##0");
    DecimalFormat unitPriceFormatter = new DecimalFormat("#,###,##0.0");
    DecimalFormat powerFormatter = new DecimalFormat("#,###,##0.00");
    Handler uiCheckHandler, paymentHandler;
    int realPay;


    public ChargingFinishResizeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChargingFinishResizeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChargingFinishResizeFragment newInstance(String param1, String param2) {
        ChargingFinishResizeFragment fragment = new ChargingFinishResizeFragment();
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charging_finish_resize, container, false);
        txtSoc = view.findViewById(R.id.txtSoc);
        txtAmountOfCharge = view.findViewById(R.id.txtAmountOfCharge);
        txtChargePay = view.findViewById(R.id.txtChargePay);
        txtChargeTime = view.findViewById(R.id.txtChargeTime);
        btnStopConfirm = view.findViewById(R.id.btnStopConfirm);
        btnStopConfirm.setOnClickListener(this);
        textViewInputPrePayment = view.findViewById(R.id.textViewInputPrePayment);
        textViewInputCancelPayment = view.findViewById(R.id.textViewInputCancelPayment);
        classUiProcess = ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel);
        chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
        textViewInputPrePayment.setText(String.valueOf(chargingCurrentData.getPrePayment()) + " 원");
        progressCircular = view.findViewById(R.id.progressCircular);
        cardViewPayment = view.findViewById(R.id.cardView2);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //charging finish info
        try {
            if (Objects.equals(chargingCurrentData.getPaymentType(), PaymentType.CREDIT)) {
                cardViewPayment.setVisibility(View.VISIBLE);
            } else {
                cardViewPayment.setVisibility(View.INVISIBLE);
            }
            progressCircular.isIndeterminate();

            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.mContext, R.raw.chargingfinsih);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();
            //unplug check 후 초기 화면
            uiCheckHandler = new Handler();
            uiCheckHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!((MainActivity) MainActivity.mContext).getControlBoard().getRxData(mChannel).isCsPilot()) {
                        btnStopConfirm.performClick();
                    }
                    uiCheckHandler.postDelayed(this, 60000);
                }
            }, 60000);

            //charging finish info
            ((MainActivity) MainActivity.mContext).runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    txtSoc.setText(chargingCurrentData.getSoc() == 0 ? "미지원" : chargingCurrentData.getSoc() + " %");
                    txtAmountOfCharge.setText(powerFormatter.format(chargingCurrentData.getPowerMeterUse() * 0.01) + " kWh");
                    realPay = (int) chargingCurrentData.getPowerMeterUsePay();
                    txtChargePay.setText(payFormatter.format(realPay) + " 원");
                    txtChargeTime.setText(chargingCurrentData.getChargingUseTime());
                    progressCircular.setProgress(chargingCurrentData.getSoc(), true);

                    try {
                        //result price
                        ChargerConfiguration chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
                        SocketReceiveMessage socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                        ProcessHandler processHandler = ((MainActivity) MainActivity.mContext).getProcessHandler();
                        SocketState state = socketReceiveMessage.getSocket().getState();
                        //// dataTransfer - Result price
//                        if (Objects.equals(state.getValue(), 7) && Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
//                            processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
//                                    GlobalVariables.MESSAGE_HANDLER_RESULT_PRICE,
//                                    chargingCurrentData.getConnectorId(),
//                                    0,
//                                    chargingCurrentData.getIdTag(),
//                                    null,
//                                    null,
//                                    false));
//                        }
                        // unPlug 종료시 statusNotification available change
                        RxData rxData = ((MainActivity) MainActivity.mContext).getControlBoard().getRxData(mChannel);
                        if (!rxData.isCsPilot() || (rxData.isCsPilot() && chargingCurrentData.getChargePointStatus() == ChargePointStatus.Finishing) ) {
                            chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                            if (Objects.equals(state.getValue(), 7) && Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                                processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                        GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                        chargingCurrentData.getConnectorId(),
                                        0,
                                        null,
                                        null,
                                        null,
                                        false));
                            }
                        }
                    } catch (Exception e) {
                        logger.error(" result price send fail : {}", e.getMessage());
                    }
                }
            });
            // 신용 카드 결제
            if (chargingCurrentData.isPrePaymentResult()) onTls3800Payment();

        } catch (Exception e) {
            logger.error("onViewCreated : {}", e.getMessage());
        }
    }


    private void onTls3800Payment() {
        try {
            paymentHandler = new Handler();
            paymentHandler.postDelayed(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    try {

                        int gapAmt = chargingCurrentData.getPrePayment() -  realPay;
                        //
                        //선 결제에 의한 무카드 취소 (4:무카드 취소)(5:부분 취소)
                        if (Objects.equals(chargingCurrentData.getPowerMeterUsePay(), 0)) {
                            chargingCurrentData.setPartialCancelPayment(chargingCurrentData.getPrePayment());
                            ((MainActivity) MainActivity.mContext).getTls3800().onTLS3800Request(mChannel, TLS3800.CMD_TX_PAYCANCEL, 4);
                        } else if (gapAmt > 0) {
                            int surTax = 0, rate = 10;
                            surTax = (gapAmt * rate) / (100 * rate);
                            chargingCurrentData.setPartialCancelPayment(gapAmt);
                            chargingCurrentData.setSurtax(surTax);
                            chargingCurrentData.setTip(0);
                            textViewInputCancelPayment.setText("-" + String.valueOf(gapAmt) + " 원");
                            SocketReceiveMessage socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                            ProcessHandler processHandler = ((MainActivity) MainActivity.mContext).getProcessHandler();
                            processHandler.sendMessage(
                                    socketReceiveMessage.onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_PARTIAL_CANCEL,
                                            chargingCurrentData.getConnectorId(),
                                            0,
                                            chargingCurrentData.getCreditCardNumber(),
                                            null,
                                            "HUMAX",
                                            true
                                    ));

//                            ((MainActivity) MainActivity.mContext).getTls3800().onTLS3800Request(mChannel, TLS3800.CMD_TX_PAYCANCEL, 5);
                        }

                    } catch (Exception e) {
                        logger.error("paymentHandler error : {}", e.getMessage());
                    }
                }
            }, 500);

        } catch (Exception e) {
            logger.error("onTls3800Payment error : {}", e.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (Objects.deepEquals(getId, R.id.btnStopConfirm)) {

            ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).onHome();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
//        requestStrings[0] = String.valueOf(mChannel);
//        sharedModel.setMutableLiveData(requestStrings);
        uiCheckHandler.removeCallbacksAndMessages(null);
        uiCheckHandler.removeMessages(0);
    }
}