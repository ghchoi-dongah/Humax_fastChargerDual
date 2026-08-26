package com.dongah.fastcharger.pages;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.utils.SharedModel;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;
import com.dongah.fastcharger.websocket.socket.Connector;
import com.dongah.fastcharger.websocket.socket.SocketState;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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
 * Use the {@link RightInitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RightInitFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(InitFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FrameLayout RightConnector;
    ImageView btnQr;
    TextView textViewMemberUnitInput;
    ChargerConfiguration chargerConfiguration;
    ChargingCurrentData chargingCurrentData;
    private int mChannel;

    public RightInitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RightInitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RightInitFragment newInstance(String param1, String param2) {
        RightInitFragment fragment = new RightInitFragment();
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
        View view = inflater.inflate(R.layout.fragment_right_init, container, false);
        textViewMemberUnitInput = view.findViewById(R.id.textViewMemberUnitInput);
        RightConnector = view.findViewById(R.id.rightConnector);
        RightConnector.setOnClickListener(this);
        btnQr = view.findViewById(R.id.btnQr);
        btnQr.setOnClickListener(this);
        try {
            chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
            if (!TextUtils.isEmpty(chargerConfiguration.getChargerId())) {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Connector connector = ((MainActivity) MainActivity.mContext).getConnectorList().get(1);
                String qrCodeURL = connector.getQrUrl();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeURL, BarcodeFormat.QR_CODE, 600, 600);
                btnQr.setImageBitmap(toGrayscale(bitmap));
            }
            String[] requestStrings = new String[1];
            SharedModel sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);
            requestStrings[0] = String.valueOf(mChannel);
            sharedModel.setMutableLiveData(requestStrings);
        } catch (Exception e) {
            logger.error("QrCode : {}", e.getMessage());
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel).setConnectorId(mChannel+1);
        //사용 단가 갖도 오기
        Set<String> userTypes = new HashSet<>(Arrays.asList("A", "B"));
        Map<String, Integer> unitPrices = onFindUnitPrices(userTypes);
        chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
        textViewMemberUnitInput.setText(getString(R.string.memChargingUnit) +  String.format(" %s 원", unitPrices.getOrDefault("A", 0)));
        chargingCurrentData.setPowerUnitPrice(Double.parseDouble(String.valueOf(unitPrices.getOrDefault("A", 0))));
    }

    @Override
    public void onClick(View v) {
        try {

            int getId = v.getId();
            ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
            chargingCurrentData.onCurrentDataClear();
            chargingCurrentData.setConnectorId(mChannel+1);
            if (Objects.equals(getId, R.id.btnQr)) {
                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.QR_CODE);
                ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel,UiSeq.QR_CODE, "QR_CODE", null);
            } else {
                if (Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                    if (!onUnitPrice()) {
                        Toast.makeText(getActivity(), "단가 정보가 없습니다. \n잠시 후, 충전하세요!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SocketState socketState = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().getSocket().getState();
                    if (Objects.equals(socketState, SocketState.OPEN)) {
                        ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel).setConnectorId(mChannel+1);
                        ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.AUTH_SELECT);
                        ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel,UiSeq.AUTH_SELECT, "AUTH_SELECT", null);
                    } else {
                        Toast.makeText(getActivity(), "서버 연결 DISCONNECT. \n충전을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else if (Objects.equals(chargerConfiguration.getAuthMode(), "4")) {
                    // power meter test(간이 시료)
                    ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.CONNECT_CHECK);
                    ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel,UiSeq.CONNECT_CHECK, "AUTH_SELECT", null);
                } else {
                    double testPrice = Double.parseDouble(((MainActivity) MainActivity.mContext).getChargerConfiguration().getTestPrice());
                    ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel).setPowerUnitPrice(testPrice);
                    ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.PLUG_CHECK);
                    ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel,UiSeq.PLUG_CHECK, "PLUG_CHECK", null);
                }

            }
        } catch (Exception e) {
            logger.error(" init onClick error : {}", e.getMessage());
        }
    }
    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private boolean onUnitPrice() {
        boolean result = false;
        try {
            File file = new File(GlobalVariables.getRootPath() + File.separator + GlobalVariables.UNIT_FILE_NAME);
            result = file.exists();
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public void onDetach() {
        super.onDetach();

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
                                break; // 그 userType에 대해 단가 찾았으면 다음 라인으로 넘어감
                            }
                        }

                        // 모든 userType이 다 찾아졌으면 종료
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