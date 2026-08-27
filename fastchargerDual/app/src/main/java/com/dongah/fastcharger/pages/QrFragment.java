package com.dongah.fastcharger.pages;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.utils.SharedModel;
import com.dongah.fastcharger.websocket.socket.Connector;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QrFragment extends Fragment {

    private static final Logger logger = LoggerFactory.getLogger(QrFragment.class);
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannel;

    Handler uiCheckHandler;
    ImageView imgQrCode, imageCheck;
    SharedModel sharedModel;
    String[] requestStrings = new String[1];
    ChargingCurrentData chargingCurrentData;


    public QrFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QrFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QrFragment newInstance(String param1, String param2) {
        QrFragment fragment = new QrFragment();
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
        View view = inflater.inflate(R.layout.fragment_qr, container, false);
        chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);

        imgQrCode = view.findViewById(R.id.imgQrCode);
        imageCheck = view.findViewById(R.id.bgCheck);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
//            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.mContext, R.raw.qr);
//            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
//            mediaPlayer.start();

            uiCheckHandler = new Handler();
            uiCheckHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).onHome();
                }
            }, 30000);


            // img check
            sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);
            sharedModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<String[]>() {
                @Override
                public void onChanged(String[] strings) {
                    // UiSeq = MEMBER_CARD(4), MEMBER_CARD_WAIT(5), CREDIT_CARD(6), CREDIT_CARD_WAIT(7) 일떄
                    try {
                        int otherChannel = Integer.parseInt(strings[0]);
                        UiSeq otherUiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(otherChannel).getUiSeq();
                        switch (otherUiSeq) {
                            case MEMBER_CARD:
                            case MEMBER_CARD_WAIT:
                            case CREDIT_CARD:
                            case CREDIT_CARD_WAIT:
                                imageCheck.setVisibility(View.VISIBLE);
                                break;
                            default:
                                imageCheck.setVisibility(View.INVISIBLE);
                                break;
                        }
                    } catch (Exception e) {
                        logger.error("img check error : {}", e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            logger.error("QR_CODE onViewCreated : {}", e.getMessage());
        }

        try {
            Connector connector = ((MainActivity) MainActivity.mContext).getConnectorList().get(mChannel);
            String qrCodeURL = connector.getQrUrl();

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeURL, BarcodeFormat.QR_CODE, 600, 600);
            imgQrCode.setImageBitmap(bitmap);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (uiCheckHandler != null) {
                uiCheckHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            logger.error("onDestroyView error : {}", e.getMessage(), e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            // back image
            requestStrings[0] = String.valueOf(mChannel);
            sharedModel.setMutableLiveData(requestStrings);
        } catch (Exception e) {
            logger.error("onDetach error : {}", e.getMessage(), e);
        }
    }
}