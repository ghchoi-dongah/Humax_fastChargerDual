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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.utils.SharedModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreditCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreditCardFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannel;

    int cnt = 0;
    ChargingCurrentData chargingCurrentData;
    Button btn500, btn1000, btn5000, btn10000, btn20000, btnClear, btn50000, btnPay;
    TextView txtInputAmt, unitPrice;
    DecimalFormat amountFormatter;
    Handler countHandler;
    Runnable countRunnable;

    public CreditCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreditCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreditCardFragment newInstance(String param1, String param2) {
        CreditCardFragment fragment = new CreditCardFragment();
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
        View view = inflater.inflate(R.layout.fragment_credit_card, container, false);
        chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
        amountFormatter = new DecimalFormat("###,##0");
        txtInputAmt = view.findViewById(R.id.txtInputAmt);
        unitPrice = view.findViewById(R.id.txtUnitPrice);
        String nonPrice = String.valueOf(chargingCurrentData.getPowerUnitPrice()).replace(".0", "");
        unitPrice.setText(getString(R.string.chargeUnitFormat, String.valueOf(nonPrice)));

        btn500 = view.findViewById(R.id.btn500);
        btn500.setOnClickListener(this);
        btn1000 = view.findViewById(R.id.btn1000);
        btn1000.setOnClickListener(this);
        btn5000 = view.findViewById(R.id.btn5000);
        btn5000.setOnClickListener(this);
        btn10000 = view.findViewById(R.id.btn10000);
        btn10000.setOnClickListener(this);
        btn20000 = view.findViewById(R.id.btn20000);
        btn20000.setOnClickListener(this);
        btn50000 = view.findViewById(R.id.btn50000);
        btn50000.setOnClickListener(this);
        btnClear = view.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        btnPay = view.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(this);

        return view;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.mContext, R.raw.creditcard);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();

            //화면 유지
            ((MainActivity) MainActivity.mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countHandler = new Handler();
                    countRunnable = new Runnable() {
                        @Override
                        public void run() {
                            cnt++;
                            if (Objects.equals(cnt, 60)) {
                                countHandler.removeCallbacks(countRunnable);
                                countHandler.removeCallbacksAndMessages(null);
                                countHandler.removeMessages(0);
                                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).onHome();
                            } else {
                                countHandler.postDelayed(countRunnable, 1000);
                            }
                        }
                    };
                    countHandler.postDelayed(countRunnable, 1000);
                }
            });

            chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
        } catch (Exception e) {
            logger.error("CreditCardFragment onViewCreated : {}", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            int getId = v.getId();
            if (Objects.equals(getId, R.id.btn500)) {
                txtInputAmt.setText(amountFormatter.format(setAmounts(txtInputAmt.getText().toString(), btn500.getTag().toString())));
            } else if (Objects.equals(getId, R.id.btn1000)) {
                txtInputAmt.setText(amountFormatter.format(setAmounts(txtInputAmt.getText().toString(), btn1000.getTag().toString())));
            } else if (Objects.equals(getId, R.id.btn20000)) {
                txtInputAmt.setText(amountFormatter.format(setAmounts(txtInputAmt.getText().toString(), btn20000.getTag().toString())));
            } else if (Objects.equals(getId, R.id.btn5000)) {
                txtInputAmt.setText(amountFormatter.format(setAmounts(txtInputAmt.getText().toString(), btn5000.getTag().toString())));
            } else if (Objects.equals(getId, R.id.btn10000)) {
                txtInputAmt.setText(amountFormatter.format(setAmounts(txtInputAmt.getText().toString(), btn10000.getTag().toString())));
            } else if (Objects.equals(getId, R.id.btn50000)) {
                txtInputAmt.setText(amountFormatter.format(setAmounts(txtInputAmt.getText().toString(), btn50000.getTag().toString())));
            } else if (Objects.equals(getId, R.id.btnClear)) {
                txtInputAmt.setText("0");
            } else if (Objects.equals(getId, R.id.btnPay)) {
                if (Objects.equals(txtInputAmt.getText().toString(), "0")) {
                    ((MainActivity) MainActivity.mContext).getToastPositionMake().onShowToast(mChannel, "결제 금액을 다시 입력하여 주세요");
                    txtInputAmt.setFocusableInTouchMode(true);
                    txtInputAmt.requestFocus();
                    return;
                }
                int prePayment = (int) setAmounts(txtInputAmt.getText().toString(), "0");
                chargingCurrentData.setPrePayment(prePayment);
                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.CREDIT_CARD_WAIT);
                ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel, UiSeq.CREDIT_CARD_WAIT, "CREDIT_CARD_WAIT", null);
            }
        } catch (Exception e) {
            logger.error("CreditCardFragment onClick : {}", e.getMessage());
        }
    }

    private double setAmounts(String amountSum, String amount) {
        return Integer.parseInt(amountSum.replaceAll("[^0-9]", "")) + Integer.parseInt(amount);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (countHandler != null) {
            countHandler.removeCallbacks(countRunnable);
            countHandler.removeCallbacksAndMessages(null);
            countHandler.removeMessages(0);
        }

        SharedModel sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);
        String[] requestStrings = new String[1];
        requestStrings[0] = "0";
        sharedModel.setMutableLiveData(requestStrings);
    }
}