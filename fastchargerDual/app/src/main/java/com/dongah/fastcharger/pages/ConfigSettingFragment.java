package com.dongah.fastcharger.pages;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.utils.SharedModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigSettingFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(ConfigSettingFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ChargerConfiguration chargerConfiguration;
    SharedModel sharedModel;
    String[] requestStrings = new String[1];
    InputMethodManager imm;
    Spinner spChargerType, spChargerModel, spPayMode, spMode;
    int spPosition = 0, spChargerModelCode = 0, spPayModePos = 0, spModePos = 0;
    EditText editChargerId, editHttpURL;
    EditText editServerUrl, editPort;
    EditText editControlPort, editRfPort, editCreditPort;
    EditText editTestPrice, editDR, editVendorCode;
    EditText editPriceVendor, editSerial, editFirmware;
    EditText editIccId, editImsi, editGpsX, editGpsY;
    EditText editStatusNotificationDelay, editTargetSoc, editTargetChargingTime;
    Button btnKeyBoard, btnSave, btnExit, btnPowerOff;
    TextView txtTestPrice;
    CheckBox chkStopConfirm, chkSigned;

    public ConfigSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigSettingFragment newInstance(String param1, String param2) {
        ConfigSettingFragment fragment = new ConfigSettingFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_setting, container, false);
        try {
            //* charger configuration */
            chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
            // keyboard hidden*/
            imm = (InputMethodManager) (MainActivity.mContext).getSystemService(INPUT_METHOD_SERVICE);
            btnKeyBoard = view.findViewById(R.id.btnKeyBoard);
            btnKeyBoard.setOnClickListener(this);
            btnSave = view.findViewById(R.id.btnConfigSave);
            btnSave.setOnClickListener(this);
            btnExit = view.findViewById(R.id.btnConfigExit);
            btnExit.setOnClickListener(this);
            btnPowerOff = view.findViewById(R.id.btnPowerOff);
            btnPowerOff.setOnClickListener(this);
            editHttpURL = view.findViewById(R.id.editHttpURL);
            editHttpURL.setText(chargerConfiguration.getServerHttpString());

            // chargerType
            spChargerType = view.findViewById(R.id.spinnerChargerType);
            ArrayAdapter<CharSequence> chargerTypeAdapter = ArrayAdapter.createFromResource(MainActivity.mContext, R.array.chargerType, R.layout.spinner_item);
            chargerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spChargerType.setAdapter(chargerTypeAdapter);
            spChargerType.setSelection(chargerConfiguration.getChargerType() - 1);
            spChargerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spPosition = position + 1;
                    chargerConfiguration.setChargerType(position + 1);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            //** spChargerModel  ==> 0: DEVS100D12 */
            spChargerModel = view.findViewById(R.id.spinnerChargerModel);
            ArrayAdapter<CharSequence> mcuTypeAdapter = ArrayAdapter.createFromResource(MainActivity.mContext, R.array.chargerModel, R.layout.spinner_item);
            mcuTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spChargerModel.setAdapter(mcuTypeAdapter);
            spChargerModel.setSelection(chargerConfiguration.getChargerPointModelCode());
            spChargerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chargerConfiguration.setChargerPointModelCode(position);
                    Resources resources = getResources();
                    String[] chargerModel = resources.getStringArray(R.array.chargerModel);
                    chargerConfiguration.setChargerPointModel(chargerModel[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // 결제모드
            spPayMode = view.findViewById(R.id.spinnerPayment);
            ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(MainActivity.mContext, R.array.payMode, R.layout.spinner_item);
            paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spPayMode.setAdapter(paymentAdapter);
            spPayMode.setSelection(Integer.parseInt(chargerConfiguration.getSelectPayment()));
            spPayMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spPayModePos = position;
                    chargerConfiguration.setSelectPayment(String.valueOf(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // 인증모드
            spMode = view.findViewById(R.id.spinnerMode);
            ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(MainActivity.mContext, R.array.mode, R.layout.spinner_item);
            modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spMode.setAdapter(modeAdapter);
            spMode.setSelection(Integer.parseInt(chargerConfiguration.getAuthMode()));
            spMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spModePos = position;
                    chargerConfiguration.setAuthMode(String.valueOf(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            InitializationComponents(view);

            // focus : EditText focus 있어 초기화면에 키보드가 먼저 보임 */
            btnExit.setFocusableInTouchMode(true);
            btnExit.requestFocus();
        } catch (Exception e) {
            logger.error("onCreateView error : {}", e.getMessage());
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);
    }


    private void InitializationComponents(View v) {
        try {
            spPosition = chargerConfiguration.getChargerType();
            spChargerModelCode = chargerConfiguration.getChargerPointModelCode();
            spPayModePos = Integer.parseInt(chargerConfiguration.getSelectPayment());
            spModePos = Integer.parseInt(chargerConfiguration.getAuthMode());

            editChargerId = v.findViewById(R.id.editChargerId);
            editChargerId.setText(chargerConfiguration.getChargerId());
            editHttpURL = v.findViewById(R.id.editHttpURL);
            editHttpURL.setText(chargerConfiguration.getServerHttpString());
            editServerUrl = v.findViewById(R.id.editServerUrl);
            editServerUrl.setText(chargerConfiguration.getServerConnectingString());
            editPort = v.findViewById(R.id.editServerPort);
            editPort.setText(String.valueOf(chargerConfiguration.getServerPort()));

            editControlPort = v.findViewById(R.id.editControlPort);
            editControlPort.setText(chargerConfiguration.getControlCom());
            editRfPort = v.findViewById(R.id.editRfPort);
            editRfPort.setText(chargerConfiguration.getRfCom());
            editCreditPort = v.findViewById(R.id.editCreditPort);
            editCreditPort.setText(chargerConfiguration.getCreditCom());

            editDR = v.findViewById(R.id.editDR);
            editDR.setText(String.valueOf(chargerConfiguration.getDr()));
            txtTestPrice = v.findViewById(R.id.txtTestPrice);
            editTestPrice = v.findViewById(R.id.editTestPrice);
            editTestPrice.setText(chargerConfiguration.getTestPrice());

            editVendorCode = v.findViewById(R.id.editVendorCode);
            editVendorCode.setText(chargerConfiguration.getChargerPointVendor());
            editPriceVendor = v.findViewById(R.id.editPriceVendor);
            editPriceVendor.setText(chargerConfiguration.getUnitPriceVendorCode());
            editSerial = v.findViewById(R.id.editSerial);
            editSerial.setText(chargerConfiguration.getChargerPointSerialNumber());
            editFirmware = v.findViewById(R.id.editFirmware);
            editFirmware.setText(chargerConfiguration.getFirmwareVersion());
            editIccId = v.findViewById(R.id.editIccId);
            editIccId.setText(chargerConfiguration.getIccid());
            editImsi = v.findViewById(R.id.editImsi);
            editImsi.setText(chargerConfiguration.getImsi());

            editGpsX = v.findViewById(R.id.editGpsX);
            editGpsX.setText(chargerConfiguration.getGpsX());
            editGpsY = v.findViewById(R.id.editGpsY);
            editGpsY.setText(chargerConfiguration.getGpsY());

            editStatusNotificationDelay = v.findViewById(R.id.editStatusNotificationDelay);
            editStatusNotificationDelay.setText(String.valueOf(chargerConfiguration.getStatusNotificationDelay()));
            editTargetSoc = v.findViewById(R.id.editTargetSoc);
            editTargetSoc.setText(String.valueOf(chargerConfiguration.getTargetSoc()));
            editTargetChargingTime = v.findViewById(R.id.editTargetChargingTime);
            editTargetChargingTime.setText(String.valueOf(chargerConfiguration.getTargetChargingTime()));       //분 단위 데이터

            try {
                //stop confirm
                chkStopConfirm = v.findViewById(R.id.chkStopConfirm);
                chkStopConfirm.setChecked(chargerConfiguration.isStopConfirm());
                chkSigned = v.findViewById(R.id.chkSigned);
                chkSigned.setChecked(chargerConfiguration.isSigned());
            } catch (Exception e) {
                logger.error("InitializationComponents fail : {}", e.getMessage());
            }
        } catch (Exception e) {
            logger.error("InitializationComponents error : {}", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (Objects.equals(v.getId(), R.id.btnKeyBoard)) {
            try {
                View view = ((MainActivity) MainActivity.mContext).getCurrentFocus();
                if (view instanceof EditText) {
                    EditText editText = (EditText) ((MainActivity) MainActivity.mContext).getCurrentFocus();
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            } catch (Exception e) {
                logger.error(" config - onClick {}", e.getMessage());
            }
        } else if (Objects.equals(v.getId(), R.id.btnConfigSave)) {
            if (TextUtils.isEmpty(editChargerId.getText().toString())) {
                editChargerId.setFocusableInTouchMode(true);
                editChargerId.requestFocus();
                Toast.makeText(((MainActivity) getActivity()), R.string.chargerIdSaveFail, Toast.LENGTH_LONG).show();
                return;
            }


            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.configSaveTitle)
                    .setMessage(R.string.configSaveYesNo)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onSaveConfiguration();
                            // 전류 제한 설정
                            for (int i = 0; i < GlobalVariables.maxChannel; i++) {
                                ((MainActivity) MainActivity.mContext).getControlBoard().getTxData(i).setOutPowerLimit((short) Integer.parseInt(editDR.getText().toString()));
                            }
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.configSaveSuccess, Toast.LENGTH_SHORT).show();
                                }
                            }, 50);
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            InitializationComponents(getView());
                            Toast.makeText(((MainActivity) getActivity()), R.string.configSaveCancel, Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        } else if (Objects.equals(v.getId(), R.id.btnConfigExit)) {
            try {
                //** fragment change */
                FragmentTransaction transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
                EnvironmentFragment environmentFragment = new EnvironmentFragment();
                transaction.replace(R.id.fullScreen, environmentFragment);
                transaction.commit();
            } catch (Exception e) {
                logger.error("fragment change fail : {}", e.getMessage());
            }
        } else if (Objects.equals(v.getId(), R.id.btnPowerOff)) {
            ((MainActivity) MainActivity.mContext).onRebooting("Hard");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        // header title message change
        SharedModel sharedModel = new ViewModelProvider(requireActivity()).get(SharedModel.class);
        String[] requestStrings = new String[1];
        requestStrings[0] = "0";
        sharedModel.setMutableLiveData(requestStrings);
    }

    private void onTestModeSetting(int modeType) {
        try {
//            for (int i = 0; i <GlobalVariables.maxChannel ; i++) {
//                ((MainActivity) MainActivity.mContext).getControlBoard().getTxData(i).setTestMode((short)modeType);
//            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void onSaveConfiguration() {
        try {
            onConfigurationUpdate();
            chargerConfiguration.onSaveConfiguration();
            chargerConfiguration.onLoadConfiguration();
        } catch (Exception e) {
            Log.e("ConfigSettingFragment", e.toString());
        }
    }

    private void onConfigurationUpdate() {
        try {
            chargerConfiguration.setChargerType(spPosition);
            chargerConfiguration.setChargerPointModelCode(spChargerModelCode);
            chargerConfiguration.setChargerId(editChargerId.getText().toString());

            chargerConfiguration.setServerHttpString(editHttpURL.getText().toString());
            chargerConfiguration.setServerConnectingString(editServerUrl.getText().toString());
            chargerConfiguration.setServerPort(Integer.parseInt(editPort.getText().toString()));

            chargerConfiguration.setControlCom(editControlPort.getText().toString());
            chargerConfiguration.setRfCom(editRfPort.getText().toString());
            chargerConfiguration.setCreditCom(editCreditPort.getText().toString());

            chargerConfiguration.setChargerPointVendor(editVendorCode.getText().toString());

            chargerConfiguration.setUnitPriceVendorCode(editPriceVendor.getText().toString());
            chargerConfiguration.setChargerPointSerialNumber(editSerial.getText().toString());
            chargerConfiguration.setFirmwareVersion(editFirmware.getText().toString());
            chargerConfiguration.setIccid(editIccId.getText().toString());
            chargerConfiguration.setImsi(editImsi.getText().toString());
            chargerConfiguration.setTestPrice(editTestPrice.getText().toString());
            chargerConfiguration.setDr(Integer.parseInt(editDR.getText().toString()));
            chargerConfiguration.setGpsX(editGpsX.getText().toString());
            chargerConfiguration.setGpsY(editGpsY.getText().toString());
            chargerConfiguration.setStatusNotificationDelay(Integer.parseInt(editStatusNotificationDelay.getText().toString()));
            chargerConfiguration.setTargetSoc(Integer.parseInt(editTargetSoc.getText().toString()));
            chargerConfiguration.setTargetChargingTime(Integer.parseInt(editTargetChargingTime.getText().toString()));

            chargerConfiguration.setSelectPayment(String.valueOf(spPayModePos));
            chargerConfiguration.setAuthMode(String.valueOf(spModePos));

            chargerConfiguration.setStopConfirm(chkStopConfirm.isChecked());
            chargerConfiguration.setSigned(chkSigned.isChecked());
        } catch (Exception e) {
            logger.error("onConfigurationUpdate error : {}", e.getMessage());
        }
    }
}