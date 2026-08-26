package com.dongah.fastcharger.pages;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.TECH3800.TLS3800;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreditCardWaitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreditCardWaitFragment extends Fragment {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardWaitFragment.class);

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
    TextView txtInputAmt;
    ImageView imgCreditCardTagging;
    AnimationDrawable animationDrawable;
    DecimalFormat amountFormatter;
    Handler countHandler, paymentHandler;
    Runnable countRunnable;

    TLS3800 tls3800;

    ChargerConfiguration chargerConfiguration;
    ChargingCurrentData chargingCurrentData;

    public CreditCardWaitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreditCardWaitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreditCardWaitFragment newInstance(String param1, String param2) {
        CreditCardWaitFragment fragment = new CreditCardWaitFragment();
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit_card_wait, container, false);
        chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
        tls3800 = ((MainActivity) MainActivity.mContext).getTls3800();

        amountFormatter = new DecimalFormat("###,##0");
        txtInputAmt = view.findViewById(R.id.txtInputAmt);

        imgCreditCardTagging = view.findViewById(R.id.imgCreditCardTagging);
        imgCreditCardTagging.setBackgroundResource(R.drawable.creditcardtagging);
        animationDrawable = (AnimationDrawable) imgCreditCardTagging.getBackground();
        animationDrawable.start();
        txtInputAmt = view.findViewById(R.id.txtInputAmt);
        txtInputAmt.setText(amountFormatter.format(chargingCurrentData.getPrePayment()));
        return view;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.mContext, R.raw.membercardwait);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();

            chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();

            ((MainActivity) MainActivity.mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countHandler = new Handler();
                    countRunnable = new Runnable() {
                        @Override
                        public void run() {
                            cnt++;
                            if (Objects.equals(cnt, 45)) {
                                countHandler.removeCallbacks(countRunnable);
                                countHandler.removeCallbacksAndMessages(null);
                                countHandler.removeMessages(0);
                                if (chargingCurrentData.isPrePaymentResult()) {
                                    //선 결제에 의한 무카드 취소 (4:무카드 취소)(5:부분 취소)
                                    chargingCurrentData.setPartialCancelPayment(chargingCurrentData.getPrePayment());
                                    tls3800.onTLS3800Request(mChannel, TLS3800.CMD_TX_PAYCANCEL, 4);
                                }

                                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).onHome();
                            } else {
                                countHandler.postDelayed(countRunnable, 1000);
                            }
                        }
                    };
                    countHandler.postDelayed(countRunnable, 1000);
                }
            });
            // 신용 카드 결제
            onTls3800Payment();
        } catch (Exception e) {
            logger.error(" CreditCardWaitFragment error : {}", e.getMessage());
        }
    }

    private void onTls3800Payment() {
        try {
            paymentHandler = new Handler();
            paymentHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int surTax, rate = 10;
                    surTax = (chargingCurrentData.getPrePayment() * rate) / (100 + rate);
                    chargingCurrentData.setSurtax(surTax);
                    chargingCurrentData.setTip(0);
                    tls3800.onTLS3800Request(mChannel, TLS3800.CMD_TX_PAY_G, 0);
                }
            }, 500);
        } catch (Exception e) {
            logger.error(" onTls3800Payment error : {}", e.getMessage());
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (countHandler != null) {
                countHandler.removeCallbacks(countRunnable);
                countHandler.removeCallbacksAndMessages(null);
                countHandler.removeMessages(0);
                countHandler = null;
            }
            if (paymentHandler != null) {
                paymentHandler.removeCallbacksAndMessages(null);
                paymentHandler = null;
            }
            // animation stop
            if (animationDrawable != null) {
                animationDrawable.stop();
                animationDrawable = null;
            }

            //결제 단말기 초기화
            tls3800.onTLS3800Request(mChannel, TLS3800.CMD_TX_RETURN, 0);

        } catch (Exception e) {
            logger.error("MemberCardWaitFragment onDetach : {} ", e.getMessage());
        }
    }
}