package com.dongah.fastcharger.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.basefunction.UiSeq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SmsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmsFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(SmsFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannel;

    TextView txtInputAmt;
    Button btnNum1, btnNum2, btnNum3, btnNum4, btnNum5, btnNum6, btnNum7;
    Button btnNum8, btnNum9, btnNum0, btnNum010, btnCancel, btnConfirm, btnDel;
    String smsTelNo = "";

    public SmsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SmsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SmsFragment newInstance(String param1, String param2) {
        SmsFragment fragment = new SmsFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sms, container, false);
        txtInputAmt = view.findViewById(R.id.txtInputAmt);
        btnNum0 = view.findViewById(R.id.btnNum0);
        btnNum1 = view.findViewById(R.id.btnNum1);
        btnNum2 = view.findViewById(R.id.btnNum2);
        btnNum3 = view.findViewById(R.id.btnNum3);
        btnNum4 = view.findViewById(R.id.btnNum4);
        btnNum5 = view.findViewById(R.id.btnNum5);
        btnNum6 = view.findViewById(R.id.btnNum6);
        btnNum7 = view.findViewById(R.id.btnNum7);
        btnNum8 = view.findViewById(R.id.btnNum8);
        btnNum9 = view.findViewById(R.id.btnNum9);
        btnNum010 = view.findViewById(R.id.btnNum010);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnDel = view.findViewById(R.id.btnDel);
        btnNum0.setOnClickListener(this);
        btnNum1.setOnClickListener(this);
        btnNum2.setOnClickListener(this);
        btnNum3.setOnClickListener(this);
        btnNum4.setOnClickListener(this);
        btnNum5.setOnClickListener(this);
        btnNum6.setOnClickListener(this);
        btnNum7.setOnClickListener(this);
        btnNum8.setOnClickListener(this);
        btnNum9.setOnClickListener(this);
        btnNum010.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (Objects.equals(v.getId(), R.id.btnCancel) | Objects.equals(v.getId(), R.id.btnConfirm)) {
            if (Objects.equals(smsTelNo.length(), 11)) {
                ((MainActivity) MainActivity.mContext).getProcessHandler().sendMessage(
                        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().onMakeHandlerMessage(
                                GlobalVariables.MESSAGE_HANDLER_SMS_MESSAGE,
                                ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel).getConnectorId(),
                                0,
                                smsTelNo,
                                null,
                                null,
                                false
                        )
                );
                ((MainActivity) MainActivity.mContext).getToastPositionMake().onShowToast(mChannel, "충전 완료 정보를 알람톡으로 전송합니다 ");
            }
            ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel, UiSeq.CHARGING, "CHARGING", null);
        } else if (Objects.equals(v.getId(), R.id.btnDel)) {
            backSpace();
//        } else if (Objects.equals(v.getId(), R.id.btnClear)) {
//            txtInputAmt.setText("");
//            smsTelNo = "";
        }
        else if (Objects.equals(v.getId(), R.id.btnNum0)) {
            addNumber("0");
        } else if (Objects.equals(v.getId(), R.id.btnNum1)) {
            addNumber("1");
        } else if (Objects.equals(v.getId(), R.id.btnNum2)) {
            addNumber("2");
        } else if (Objects.equals(v.getId(), R.id.btnNum3)) {
            addNumber("3");
        } else if (Objects.equals(v.getId(), R.id.btnNum4)) {
            addNumber("4");
        } else if (Objects.equals(v.getId(), R.id.btnNum5)) {
            addNumber("5");
        } else if (Objects.equals(v.getId(), R.id.btnNum6)) {
            addNumber("6");
        } else if (Objects.equals(v.getId(), R.id.btnNum7)) {
            addNumber("7");
        } else if (Objects.equals(v.getId(), R.id.btnNum8)) {
            addNumber("8");
        } else if (Objects.equals(v.getId(), R.id.btnNum9)) {
            addNumber("9");
        } else if (Objects.equals(v.getId(), R.id.btnNum010)) {
            addNumber("010");
        }
    }


    private void addNumber(String num) {
        if (smsTelNo.length() < 11) {
            smsTelNo += num;
            LabelView(smsTelNo);
        }
    }

    private void backSpace() {
        try {
            smsTelNo = !smsTelNo.isEmpty() ? smsTelNo.substring(0, smsTelNo.length() - 1) : "";
            LabelView(smsTelNo);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void LabelView(String telNo) {
        try {
            StringBuilder tempViewStr = new StringBuilder(telNo);
            if ((telNo.length() > 6) && (telNo.length() < 11)) {
                tempViewStr.insert(6, "-");
                tempViewStr.insert(3, "-");
            } else if ((telNo.length() > 3) && (telNo.length() <= 6)) {
                tempViewStr.insert(3, "-");
            } else if (telNo.length() == 11) {
                tempViewStr.insert(7, "-");
                tempViewStr.insert(3, "-");
            }
            txtInputAmt.setText(tempViewStr.toString());
        } catch (Exception e) {
            logger.error("LabelView : {}", e.getMessage());
        }
    }

}