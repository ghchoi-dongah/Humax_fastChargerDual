package com.dongah.fastcharger.pages;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.ChargerPointType;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.utils.SharedModel;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;
import com.dongah.fastcharger.websocket.socket.SocketState;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InitFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(InitFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannel;

    Animation animBlink;
    View viewCircle;
    TextView textViewConnector, textViewInitMessage, txtMemberUnitInput;
    ImageView imageViewCar;
    SharedModel sharedModel;
    String[] requestStrings = new String[1];
    Handler qrHandler;

    MainActivity activity;
    ChargerConfiguration chargerConfiguration;
    ChargingCurrentData chargingCurrentData;

    public InitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InitFragment newInstance(String param1, String param2) {
        InitFragment fragment = new InitFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_init, container, false);
        activity = (MainActivity) MainActivity.mContext;
        chargerConfiguration = activity.getChargerConfiguration();
        chargingCurrentData = activity.getChargingCurrentData(mChannel);

        animBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        viewCircle = view.findViewById(R.id.viewCircle);
        viewCircle.setOnClickListener(this);
        textViewConnector = view.findViewById(R.id.textViewConnector);
        imageViewCar = view.findViewById(R.id.imageViewCar);
        textViewInitMessage = view.findViewById(R.id.textViewInitMessage);
        textViewInitMessage.startAnimation(animBlink);
        txtMemberUnitInput = view.findViewById(R.id.txtMemberUnitInput);

        try {
            if (mChannel == 0) {
                imageViewCar.setScaleX(1f);
                textViewConnector.setText(R.string.leftConnector);
            } else {
                imageViewCar.setScaleX(-1f);
                textViewConnector.setText(R.string.rightConnector);
            }
        } catch (Exception e) {
            logger.error("onCreateView error : {}", e.getMessage());
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            //사용 단가 갖고 오기
            Set<String> userTypes = new HashSet<>(Arrays.asList("A", "B"));
            Map<String, Integer> unitPrices = onFindUnitPrices(userTypes);
            int price = unitPrices.getOrDefault("A", 0);
            txtMemberUnitInput.setText(getString(R.string.chargeUnitFormat, String.valueOf(price)));
            chargingCurrentData.setPowerUnitPrice(Double.parseDouble(String.valueOf(price)));
        } catch (Exception e) {
            logger.error("onViewCreated error : {}", e.getMessage());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View v) {
        try {
            if (!Objects.equals(v.getId(), R.id.viewCircle)) return;

            // 초기 화면 으로 전환이 된 경우, current data clear
            chargingCurrentData.onCurrentDataClear();
            chargingCurrentData.setConnectorId(mChannel + 1);
            chargingCurrentData.setChargerPointType(ChargerPointType.COMBO);

            int id = v.getId();
            //* page change*/
            if (Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                if (!onUnitPrice()) {
                    Toast.makeText(getActivity(), "단가 정보가 없습니다. \n잠시 후, 충전하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    SocketState socketState = ((MainActivity) getActivity()).getSocketReceiveMessage().getSocket().getState();
                    if (Objects.equals(socketState, SocketState.OPEN)) {
                        ((MainActivity) getActivity()).getClassUiProcess(mChannel).setUiSeq(UiSeq.AUTH_SELECT);
                        ((MainActivity) getActivity()).getFragmentChange().onFragmentChange(mChannel, UiSeq.AUTH_SELECT, "AUTH_SELECT", null);
                    } else {
                        ((MainActivity) getActivity()).getToastPositionMake().onShowToast(mChannel, "서버 연결 DISCONNECT. \n충전을 할 수 없습니다.");
                    }
                } catch (Exception e){
                    ((MainActivity) getActivity()).getToastPositionMake().onShowToast(mChannel, "서버 연결 DISCONNECT. \n충전을 할 수 없습니다.");
                    logger.error(e.getMessage());
                }
            } else if (Objects.equals(((MainActivity) getActivity()).getChargerConfiguration().getAuthMode(), "4")) {
                ((MainActivity) getActivity()).getControlBoard().getTxData(mChannel).setStart(true);
                ((MainActivity) getActivity()).getControlBoard().getTxData(mChannel).setStop(false);
                ((MainActivity) getActivity()).getClassUiProcess(mChannel).setUiSeq(UiSeq.CONNECT_CHECK);
            } else {
                ((MainActivity) getActivity()).getClassUiProcess(mChannel).setUiSeq(UiSeq.PLUG_CHECK);
                double testPrice = Double.parseDouble(((MainActivity) getActivity()).getChargerConfiguration().getTestPrice());
                ((MainActivity) getActivity()).getChargingCurrentData(mChannel).setPowerUnitPrice(testPrice);
                ((MainActivity) getActivity()).getFragmentChange().onFragmentChange(mChannel, UiSeq.PLUG_CHECK, "PLUG_CHECK", null);
            }
        } catch (Exception e) {
            logger.error("onClick error : {}", e.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            animBlink.cancel();
            animBlink = null;

            if (qrHandler != null) {
                qrHandler.removeCallbacksAndMessages(null);
                qrHandler.removeMessages(0);
            }
            // back image
            requestStrings[0] = String.valueOf(mChannel);
            sharedModel.setMutableLiveData(requestStrings);
        } catch (Exception e) {
            logger.error("onDetach error : {}", e.getMessage());
        }
    }

    private boolean onUnitPrice() {
        boolean result = false;
        try {
            File file = new File(GlobalVariables.getRootPath() + File.separator + GlobalVariables.UNIT_FILE_NAME);
            result = file.exists() || !Objects.equals(chargerConfiguration.getAuthMode(), "0");
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Map<String, Integer> onFindUnitPrices(Set<String> userTypes) {
        Map<String, Integer> resultMap = new HashMap<>();

        try {
            File file = new File(GlobalVariables.getRootPath() + File.separator + GlobalVariables.UNIT_FILE_NAME);
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;

                ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();
                ZonedDateTime now = zonedDateTimeConvert.doGetCurrentTime();

                while ((line = bufferedReader.readLine()) != null) {
                    JSONObject unitObject = new JSONObject(line);
                    String userType = unitObject.getString("userType");

                    if (userTypes.contains(userType)) {
                        JSONArray jsonArrayUnit = unitObject.getJSONArray("tariff");

                        for (int k = 0; k < jsonArrayUnit.length(); k++) {
                            JSONObject obj = jsonArrayUnit.getJSONObject(k);
                            ZonedDateTime startAt = ZonedDateTime.parse(obj.getString("startAt"), DateTimeFormatter.ISO_DATE_TIME);
                            ZonedDateTime endAt = ZonedDateTime.parse(obj.getString("endAt"), DateTimeFormatter.ISO_DATE_TIME);

                            if ((now.isEqual(startAt) || now.isAfter(startAt)) && (now.isBefore(endAt) || now.isEqual(endAt))) {
                                resultMap.put(userType, obj.getInt("price"));
                                break; // 그 userType에 대해 단가 정보를 찾으면 다음 라인으로 넘어감
                            }
                        }

                        // 모든 userType을 찾으면 종료
                        if (resultMap.keySet().containsAll(userTypes)) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("onFindUnitPrices error : {}", e.getMessage());
        }

        return resultMap;
    }

}