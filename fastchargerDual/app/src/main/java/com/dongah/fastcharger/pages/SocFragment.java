package com.dongah.fastcharger.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.basefunction.PaymentType;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannel;


    TextView txtSocInput;
    SeekBar seekBar;
    Button btnConfirm;
    Button imgPlus, imgMinus;
    int socValue = 80 ;


    public SocFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocFragment newInstance(String param1, String param2) {
        SocFragment fragment = new SocFragment();
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

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soc, container, false);
        seekBar = view.findViewById(R.id.seekBar);
        txtSocInput = view.findViewById(R.id.txtSocInput);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);

        imgPlus = view.findViewById(R.id.imgPlus);
        imgMinus = view.findViewById(R.id.imgMinus);
        imgPlus.setOnClickListener(this);
        imgMinus.setOnClickListener(this);

        socValue = 80;
        txtSocInput.setText(String.format("%d%%", socValue));
        seekBar.setProgress(3);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                socValue = 50 + seekBar.getProgress() * 10;
                txtSocInput.setText(String.format("%d%%", socValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        int current = 50 + seekBar.getProgress() * 10 ;
        int getId = v.getId();
        ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
        chargingCurrentData.setTargetSoc(socValue);
        PaymentType paymentType = chargingCurrentData.getPaymentType();
        if (Objects.equals(getId, R.id.btnConfirm)) {
            if (Objects.equals(paymentType, PaymentType.MEMBER)) {
                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.MEMBER_CARD);
                ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel,UiSeq.MEMBER_CARD, "MEMBER_CARD", null);
            } else if (Objects.equals(paymentType, PaymentType.CREDIT)) {
                SocketReceiveMessage socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.CREDIT_CARD);
                ((MainActivity) MainActivity.mContext).getProcessHandler()
                        .sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE,
                                chargingCurrentData.getConnectorId(),
                                0,
                                "",
                                null,
                                GlobalVariables.getHumaxUserType(),
                                false));
                ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel,UiSeq.CREDIT_CARD, "CREDIT_CARD", null);
            }
        } else if (Objects.equals(getId, R.id.imgPlus)) {
            seekBar.setProgress(current + 1);
            txtSocInput.setText(String.format("%d%%", 50 + seekBar.getProgress() * 10));
        } else if (Objects.equals(getId, R.id.imgMinus)) {
            seekBar.setProgress(current - 1);
            txtSocInput.setText(String.format("%d%%", 100 - seekBar.getProgress() * 10));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}