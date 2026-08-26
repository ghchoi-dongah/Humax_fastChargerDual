package com.dongah.fastcharger.pages;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.dongah.fastcharger.basefunction.PaymentType;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.utils.SharedModel;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;
import com.dongah.fastcharger.websocket.socket.Connector;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;
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
 * Use the {@link AuthSelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthSelectFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(AuthSelectFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL = "CHANNEL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannel;

    View viewMember, viewNoMember, viewQr;
    TextView textViewMemberUnitInput, textViewNoMemberUnitInput;
    ImageView imageViewQr;
    ChargerConfiguration chargerConfiguration;
    ChargingCurrentData chargingCurrentData;
    Handler uiCheckHandler;
    SocketReceiveMessage socketReceiveMessage;
    String[] requestStrings = new String[1];
    SharedModel sharedModel;

    double aUnitPrice, bUnitPrice;

    public AuthSelectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AuthSelectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AuthSelectFragment newInstance(String param1, String param2) {
        AuthSelectFragment fragment = new AuthSelectFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_select, container, false);
        chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
        chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(mChannel);
        socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
        sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);

        textViewMemberUnitInput = view.findViewById(R.id.textViewMemberUnitInput);
        textViewNoMemberUnitInput = view.findViewById(R.id.textViewNoMemberUnitInput);

        viewMember = view.findViewById(R.id.viewMember);
        viewMember.setOnClickListener(this);
        viewNoMember = view.findViewById(R.id.viewNoMember);
        viewNoMember.setOnClickListener(this);
        viewQr = view.findViewById(R.id.viewQr);
        viewQr.setOnClickListener(this);
        imageViewQr = view.findViewById(R.id.imageViewQr);

        //사용 단가 갖고 오기
        Set<String> userTypes = new HashSet<>(Arrays.asList("A", "B"));
        Map<String, Integer> unitPrices = onFindUnitPrices(userTypes);
        aUnitPrice = unitPrices.getOrDefault("A", 0);
        bUnitPrice = unitPrices.getOrDefault("B", 0);
        textViewMemberUnitInput.setText(getString(R.string.price, aUnitPrice));
        textViewNoMemberUnitInput.setText(getString(R.string.price, bUnitPrice));

        try {
            if (!TextUtils.isEmpty(chargerConfiguration.getChargerId())) {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Connector connector = ((MainActivity) MainActivity.mContext).getConnectorList().get(mChannel);
                String qrCodeUrl = connector.getQrUrl();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeUrl, BarcodeFormat.QR_CODE, 150, 150);
                imageViewQr.setImageBitmap(toGrayscale(bitmap));
            }
        } catch (Exception e) {
            logger.error("QrCode : {}", e.getMessage());
        }

        return view;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            requestStrings[0] = String.valueOf(mChannel);
            sharedModel.setMutableLiveData(requestStrings);

            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.mContext, R.raw.authselect);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();

            uiCheckHandler = new Handler();
            uiCheckHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).onHome();
                }
            }, 60000);

        }catch (Exception e) {
            logger.error("onViewCreated error : {}", e.getMessage());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View v) {
        try {
            int getId = v.getId();
            if (Objects.equals(getId, R.id.viewMember)) {
                chargingCurrentData.setPaymentType(PaymentType.MEMBER);
                chargingCurrentData.setPowerUnitPrice(aUnitPrice);
                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.MEMBER_CARD);
                ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel,UiSeq.MEMBER_CARD, "MEMBER_CARD", null);
            } else if (Objects.equals(getId, R.id.viewNoMember)) {
                GlobalVariables.setCustomUnitPriceReq(false);
                GlobalVariables.setHumaxUserType("B");
                chargingCurrentData.setPaymentType(PaymentType.CREDIT);
                chargingCurrentData.setPowerUnitPrice(bUnitPrice);
                ((MainActivity) MainActivity.mContext).getProcessHandler()
                        .sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE,
                                chargingCurrentData.getConnectorId(),
                                0,
                                "",
                                null,
                                GlobalVariables.getHumaxUserType(),
                                false));
                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.CREDIT_CARD);
                ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel,UiSeq.CREDIT_CARD, "CREDIT_CARD", null);
            } else if (Objects.equals(getId, R.id.viewQr)) {
                ((MainActivity) MainActivity.mContext).getClassUiProcess(mChannel).setUiSeq(UiSeq.QR_CODE);
                ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(mChannel,UiSeq.QR_CODE, "QR_CODE", null);
            }
        } catch (Exception e) {
            logger.error("onClick error : {}", e.getMessage());
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

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (uiCheckHandler != null) {
                uiCheckHandler.removeCallbacksAndMessages(null);
                uiCheckHandler.removeMessages(0);
                uiCheckHandler = null;
            }
        } catch (Exception e) {
            logger.error("onDetach error : {}", e.getMessage());
        }
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