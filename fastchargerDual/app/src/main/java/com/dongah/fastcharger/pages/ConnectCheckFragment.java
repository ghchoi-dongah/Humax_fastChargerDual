package com.dongah.fastcharger.pages;

import android.graphics.drawable.AnimationDrawable;
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
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.utils.SharedModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConnectCheckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectCheckFragment extends Fragment implements View.OnClickListener {

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
    ImageView imgPlugBackground, imgPlugConnector, bg_check;
    TextView txtMessage;
    AnimationDrawable animationDrawableBackground;
    Handler uiCheckHandler;
    SharedModel sharedModel;
    String[] requestStrings = new String[1];

    public ConnectCheckFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlugCheckFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConnectCheckFragment newInstance(String param1, String param2) {
        ConnectCheckFragment fragment = new ConnectCheckFragment();
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
        View view = inflater.inflate(R.layout.fragment_connect_check, container, false);
        txtMessage = view.findViewById(R.id.txtMessage);
        imgPlugBackground = view.findViewById(R.id.imgPlugBackground);
        imgPlugConnector = view.findViewById(R.id.imgPlugConnector);
        imgPlugBackground.setBackgroundResource(R.drawable.plugbackground);
        animationDrawableBackground = (AnimationDrawable) imgPlugBackground.getBackground();
        animationDrawableBackground.start();
        imgPlugConnector.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //** 60sec */
        uiCheckHandler = new Handler();
        uiCheckHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).onHome();
            }
        }, 60000);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (Objects.equals(id, R.id.imgPlugConnector)) {
            ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.CHARGING);
            ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel, UiSeq.CHARGING, "CHARGING", null);
        }
    }

    public void onDetach() {
        super.onDetach();
        uiCheckHandler.removeCallbacksAndMessages(null);
        uiCheckHandler.removeMessages(0);
        if (animationDrawableBackground != null) {
            animationDrawableBackground.stop();
            animationDrawableBackground = null;
        }
    }

}