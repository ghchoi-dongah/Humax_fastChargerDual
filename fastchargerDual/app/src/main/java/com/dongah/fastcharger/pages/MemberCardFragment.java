package com.dongah.fastcharger.pages;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.TECH3800.TLS3800;
import com.dongah.fastcharger.utils.SharedModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemberCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberCardFragment extends Fragment {

    private static final Logger logger = LoggerFactory.getLogger(MemberCardFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannel;

    int MAX_TIME = 20;
    int cnt = 0;
    ImageView imgMemberCardTagging;
    TextView textViewTagTimer;
    AnimationDrawable animationDrawable;
    Handler countHandler;
    Runnable countRunnable;

    public MemberCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemberCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberCardFragment newInstance(String param1, String param2) {
        MemberCardFragment fragment = new MemberCardFragment();
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
        View view = inflater.inflate(R.layout.fragment_member_card, container, false);
        textViewTagTimer = view.findViewById(R.id.textViewTagTimer);
        imgMemberCardTagging = view.findViewById(R.id.imgMemberCardTagging);
        imgMemberCardTagging.setBackgroundResource(R.drawable.membercardtagging);
        animationDrawable = (AnimationDrawable) imgMemberCardTagging.getBackground();
        String[] requestStrings = new String[1];
        SharedModel sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);
        requestStrings[0] = String.valueOf(mChannel);
        sharedModel.setMutableLiveData(requestStrings);

        //TL3800
        ((MainActivity) MainActivity.mContext).getTls3800().onTLS3800Request(mChannel, TLS3800.CMD_TX_RF_READ, 0);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.mContext, R.raw.membercard);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();

            animationDrawable.start();
            textViewTagTimer.setText(MAX_TIME + "초");
            
            //count
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countHandler = new Handler();
                    countRunnable = new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            cnt++;
                            if (Objects.equals(cnt, MAX_TIME)) {
                                ((MainActivity) getActivity()).getClassUiProcess(mChannel).onHome();
                            } else {
                                textViewTagTimer.setText((MAX_TIME - cnt) + "초");
                                countHandler.postDelayed(countRunnable, 1000);
                            }
                        }
                    };
                    countHandler.postDelayed(countRunnable, 1000);
                }
            });

        } catch (Exception e) {
            logger.error("MemberCardFragment onViewCreated error : {} ", e.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            animationDrawable.stop();
            ((AnimationDrawable) imgMemberCardTagging.getBackground()).stop();
            imgMemberCardTagging.setBackground(null);
            if (countHandler != null) {
                countHandler.removeCallbacks(countRunnable);
                countHandler.removeCallbacksAndMessages(null);
                countHandler.removeMessages(0);
            }
        } catch (Exception e) {
            logger.error("MemberCardFragment onDetach error : {} ", e.getMessage());
        }
    }
}