package com.dongah.fastcharger.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.TECH3800.TLS3800;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.utils.SharedModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FaultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaultFragment extends Fragment {

    private static final Logger logger = LoggerFactory.getLogger(FaultFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private static final String CHANNEL = "CHANNEL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private int mChannel;

    int rebootCount = 10;
    TextView txtFaultMessage, txtFaultTitle;
    Handler faultMessageDisplay, rebootHandler, paymentHandler;
    Runnable faultMessageDisplayRunnable, rebootRunnable;
    String[] requestStrings = new String[1];
    SharedModel sharedModel;
    ChargingCurrentData chargingCurrentData;
    String messageContext;


    public FaultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FaultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaultFragment newInstance(String param1, String param2) {
        FaultFragment fragment = new FaultFragment();
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
            mParam3 = getArguments().getString(ARG_PARAM3);
            mChannel = getArguments().getInt(CHANNEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fault, container, false);
        chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
        txtFaultMessage = view.findViewById(R.id.txtFaultMessage);
        return view;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Objects.equals(mParam2, "FAULT_MESSAGE")) {
            try {
                ((MainActivity) MainActivity.mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        faultMessageDisplay = new Handler();
                        faultMessageDisplayRunnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    txtFaultMessage.setText(chargingCurrentData.getFaultMessage());
                                    faultMessageDisplay.postDelayed(faultMessageDisplayRunnable, 1000);
                                } catch (Exception e) {
                                    txtFaultMessage.setText("");
                                    logger.error("onViewCreated error : {}", e.getMessage());
                                }
                            }
                        };
                        faultMessageDisplay.postDelayed(faultMessageDisplayRunnable, 0);
                    }
                });
            } catch (Exception e) {
                logger.error(" FAULT_MESSAGE error : {}", e.getMessage());
            }
        } else if (Objects.equals(mParam2, "REBOOTING")) {
            try {
                if (Objects.equals(mParam3, "Hard")) {
//                    txtFaultTitle.setText("시스템 리부팅");
                    messageContext = " 초 뒤 시스템이 리부팅 됩니다.";
                } else {
//                    txtFaultTitle.setText("program 종료");
                    messageContext = " 초 뒤 자동 종료 됩니다.";
                }
                ((MainActivity) MainActivity.mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rebootHandler = new Handler();
                        rebootRunnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    txtFaultMessage.setText("    " + String.valueOf(rebootCount) + messageContext);
                                    if (!Objects.equals(rebootCount, 0)) {
                                        rebootHandler.postDelayed(rebootRunnable, 1000);
                                    } else {
                                        rebootHandler.removeCallbacks(rebootRunnable);
                                        rebootHandler.removeCallbacksAndMessages(null);
                                        rebootHandler.removeMessages(0);
                                        ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel).setReBoot(true);
                                        ((MainActivity) MainActivity.mContext).onRebooting(mParam3);
                                    }
                                    rebootCount--;
                                } catch (Exception e) {
                                    logger.error("REBOOTING error : {}", e.getMessage());
                                }
                            }
                        };
                        rebootHandler.postDelayed(rebootRunnable, 1000);
                    }
                });
            } catch (Exception e) {
                logger.error(" REBOOTING error : {}", e.getMessage());
            }
        }
    }

    private void onTls3800PartialCancelPayment() {
        try {
            paymentHandler = new Handler();
            paymentHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int surTax = 0, rate = 10, cancelType;
                    int prePay = chargingCurrentData.getPrePayment();
                    int usePay = (int) chargingCurrentData.getPowerMeterUsePay();
                    if (Objects.equals(usePay, 0)) {
                        // 무카드
                        cancelType = 4;
                        surTax = (prePay * rate) / (100 + rate);
                    } else {
                        cancelType = 5;
                        surTax = ((prePay - usePay) * rate) / (100 * rate);
                        chargingCurrentData.setPartialCancelPayment(prePay - usePay);
                    }
                    chargingCurrentData.setSurtax(surTax);
                    chargingCurrentData.setTip(0);
                    ((MainActivity) MainActivity.mContext).getTls3800().onTLS3800Request(mChannel, TLS3800.CMD_TX_PAYCANCEL, cancelType);
                }
            }, 500);

        } catch (Exception e) {
            logger.error(" onTls3800Payment error : {}", e.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (faultMessageDisplay != null) {
            faultMessageDisplay.removeCallbacks(faultMessageDisplayRunnable);
            faultMessageDisplay.removeCallbacksAndMessages(null);
            faultMessageDisplay.removeMessages(0);
            faultMessageDisplay = null;
        }
        if (paymentHandler != null) {
            paymentHandler.removeCallbacksAndMessages(null);
            paymentHandler.removeMessages(0);
            paymentHandler = null;
        }
        // header title message change
        SharedModel sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);
        String[] requestStrings = new String[1];
        requestStrings[0] = "0";
        sharedModel.setMutableLiveData(requestStrings);
    }
}