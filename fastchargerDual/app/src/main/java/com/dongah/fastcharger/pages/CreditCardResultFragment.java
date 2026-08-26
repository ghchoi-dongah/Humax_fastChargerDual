package com.dongah.fastcharger.pages;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.utils.SharedModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreditCardResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreditCardResultFragment extends Fragment implements View.OnClickListener {

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
    ImageView imgCreditCardSuccess;
    Handler countHandler;
    Runnable countRunnable;
    SharedModel sharedModel;
    String[] requestStrings = new String[1];

    public CreditCardResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreditCardResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreditCardResultFragment newInstance(String param1, String param2) {
        CreditCardResultFragment fragment = new CreditCardResultFragment();
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
        View view = inflater.inflate(R.layout.fragment_credit_card_result, container, false);
        imgCreditCardSuccess = view.findViewById(R.id.imgCreditCardSuccess);
        imgCreditCardSuccess.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //image check
        sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);

        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.mContext, R.raw.creditcardresult);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();

        /** 화면 유지 시간 60sec*/
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                countHandler = new Handler();
//                countRunnable = new Runnable() {
//                    @SuppressLint("DefaultLocale")
//                    @Override
//                    public void run() {
//                        cnt++;
//                        if (Objects.equals(cnt, 60)) {
//                            countHandler.removeCallbacks(countRunnable);
//                            countHandler.removeCallbacksAndMessages(null);
//                            countHandler.removeMessages(0);
//                            ((MainActivity) getActivity()).getClassUiProcess(mChannel).setUiSeq(UiSeq.INIT);
//                            ((MainActivity) getActivity()).getFragmentChange().onFragmentChange(mChannel, UiSeq.INIT,"INIT",null);
//                        } else {
//                            countHandler.postDelayed(countRunnable,1000);
//                        }
//                    }
//                };
//                countHandler.postDelayed(countRunnable, 1000);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (Objects.equals(id, R.id.imgCreditCardSuccess)) {
            ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.PLUG_CHECK);
            ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel, UiSeq.PLUG_CHECK, "PLUG_CHECK", null);
        }
    }
}