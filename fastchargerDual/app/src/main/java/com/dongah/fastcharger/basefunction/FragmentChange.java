package com.dongah.fastcharger.basefunction;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.pages.AdminPasswordFragment;
import com.dongah.fastcharger.pages.AuthSelectFragment;
import com.dongah.fastcharger.pages.ChargingFinishResizeFragment;
import com.dongah.fastcharger.pages.ChargingFragment;
import com.dongah.fastcharger.pages.ChargingResizeFragment;
import com.dongah.fastcharger.pages.ConfigSettingFragment;
import com.dongah.fastcharger.pages.ControlDebugFragment;
import com.dongah.fastcharger.pages.CreditCardFragment;
import com.dongah.fastcharger.pages.CreditCardWaitFragment;
import com.dongah.fastcharger.pages.EnvironmentFragment;
import com.dongah.fastcharger.pages.FaultFragment;
import com.dongah.fastcharger.pages.HeaderFragment;
import com.dongah.fastcharger.pages.InitFragment;
import com.dongah.fastcharger.pages.MemberCardFragment;
import com.dongah.fastcharger.pages.MemberCardWaitFragment;
import com.dongah.fastcharger.pages.MessageYesNoFragment;
import com.dongah.fastcharger.pages.PlugWaitFragment;
import com.dongah.fastcharger.pages.ProductTestFragment;
import com.dongah.fastcharger.pages.QrFragment;
import com.dongah.fastcharger.pages.SocFragment;
import com.dongah.fastcharger.pages.WebSocketDebugFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class FragmentChange {

    public static final Logger logger = LoggerFactory.getLogger(FragmentChange.class);


    FragmentCurrent fragmentCurrent;

    public FragmentChange() {
    }

    public void onFragmentChange(int channel, UiSeq uiSeq, String sendText, String type) {
        Bundle bundle = new Bundle();
        bundle.putInt("CHANNEL", channel);
        ((MainActivity) MainActivity.mContext).setFragmentSeq(channel, uiSeq);
        int frameLayoutId = channel == 0 ? R.id.ch0 : R.id.ch1;
        // full = 1024x696,  small = 512x696
        FragmentTransaction transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
        onAdminLayoutChange(uiSeq);
        switch (uiSeq) {
            case INIT:
                try {
                    onFrameLayoutChange(false);
                    bundle.putInt("CHANNEL", channel == 0 ? 0 : 1);
                    InitFragment initFragment =  new InitFragment();
                    transaction.replace(frameLayoutId, initFragment, sendText);
                    initFragment.setArguments(bundle);
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : INIT {}", e.getMessage());
                }
                break;
            case AUTH_SELECT:
                try {
                    onFrameLayoutChange(false);
                    AuthSelectFragment authSelectFragment = new AuthSelectFragment();
                    authSelectFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, authSelectFragment, "AUTH_SELECT");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : AUTH_SELECT {}", e.getMessage());
                }
                break;
            case SOC:
                try {
                    onFrameLayoutChange(false);
                    SocFragment socFragment = new SocFragment();
                    socFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, socFragment, "SOC");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange-socFragment error :  {}", e.getMessage());
                }
                break;
            case MEMBER_CARD:
                try {
                    onFrameLayoutChange(false);
                    MemberCardFragment memberCardFragment = new MemberCardFragment();
                    memberCardFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, memberCardFragment, "MEMBER_CARD");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : MEMBER_CARD {} ", e.getMessage());
                }
                break;
            case MEMBER_CARD_WAIT:
                try {
                    onFrameLayoutChange(false);
                    MemberCardWaitFragment memberCardWaitFragment = new MemberCardWaitFragment();
                    memberCardWaitFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, memberCardWaitFragment, "MEMBER_CARD_WAIT");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : MEMBER_CARD_WAIT {}", e.getMessage());
                }
                break;
            case CREDIT_CARD:
                try {
                    onFrameLayoutChange(false);
                    CreditCardFragment creditCardFragment = new CreditCardFragment();
                    creditCardFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, creditCardFragment, "CREDIT_CARD");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : CREDIT_CARD {} ", e.getMessage());
                }
                break;
            case CREDIT_CARD_WAIT:
                try {
                    onFrameLayoutChange(false);
                    CreditCardWaitFragment creditCardWaitFragment = new CreditCardWaitFragment();
                    creditCardWaitFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, creditCardWaitFragment, "CREDIT_CARD_WAIT");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : CREDIT_CARD_WAIT {}", e.getMessage());
                }
                break;
            case PLUG_CHECK:
            case CONNECT_CHECK:
                try {
                    onFrameLayoutChange(false);
                    PlugWaitFragment plugWaitFragment = new PlugWaitFragment();
                    plugWaitFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, plugWaitFragment, "PLUG_CHECK");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : PLUG_CHECK {}", e.getMessage());
                }
                break;
            case CHARGING:
                try {
                    onFrameLayoutChange(Objects.equals(type, "full"));
                    if (Objects.equals(type, "full")) {
                        ChargingFragment chargingFragment = new ChargingFragment();
                        chargingFragment.setArguments(bundle);
                        transaction.replace(R.id.fullScreen, chargingFragment, "CHARGING");
                    } else if (Objects.equals(type, "small")) {
//                        onRemoveFragment(channel, sendText);
                        ChargingResizeFragment chargingResizeFragment = new ChargingResizeFragment();
                        chargingResizeFragment.setArguments(bundle);
                        transaction.replace(frameLayoutId, chargingResizeFragment, "CHARGING");
                    }
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : CHARGING {} ", e.getMessage());
                }
                break;
            case CHARGING_STOP_MESSAGE:
                try {
                    onFrameLayoutChange(false);
                    MessageYesNoFragment messageYesNoFragment = new MessageYesNoFragment();
                    messageYesNoFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, messageYesNoFragment, "CHARGING_STOP_MESSAGE");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : CHARGING_STOP_MESSAGE {} ", e.getMessage());
                }
                break;
            case FINISH:
                try {
                    onFrameLayoutChange(false);
                    ChargingFinishResizeFragment chargingFinishResizeFragment = new ChargingFinishResizeFragment();
                    chargingFinishResizeFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, chargingFinishResizeFragment, "FINISH");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : FINISH {}", e.getMessage());
                }
                break;
            case QR_CODE:
                try {
                    onFrameLayoutChange(false);
                    QrFragment qrFragment = new QrFragment();
                    qrFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, qrFragment, "QR_CODE");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : QR_CODE {}", e.getMessage());
                }
                break;
            case FAULT:
                try {
                    FaultFragment faultFragment = new FaultFragment();
                    bundle.putString("param2", "FAULT_MESSAGE");
                    faultFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, faultFragment, "FAULT");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : FAULT {}", e.getMessage());
                }
                break;
            case REBOOTING:
                try {
                    FaultFragment faultFragment = new FaultFragment();
                    bundle.putString("param2", "REBOOTING");
                    bundle.putString("param3", type);
                    faultFragment.setArguments(bundle);
                    transaction.replace(frameLayoutId, faultFragment, "REBOOTING");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : REBOOTING {}", e.getMessage());
                }
                break;
            case ADMIN_PASS:
                try{
                    onFrameLayoutChange(true);
                    AdminPasswordFragment adminPasswordFragment = new AdminPasswordFragment();
                    adminPasswordFragment.setArguments(bundle);
                    transaction.replace(R.id.fullScreen, adminPasswordFragment, "ADMIN_PASS");
                    transaction.commit();

                } catch (Exception e){
                    logger.error("onFragmentChange error : ADMIN_PASS {}", e.getMessage());
                }
                break;
            case ENVIRONMENT:
                try{
                    onFrameLayoutChange(true);
                    EnvironmentFragment environmentFragment = new EnvironmentFragment();
                    environmentFragment.setArguments(bundle);
                    transaction.replace(R.id.fullScreen, environmentFragment, "ADMIN_PASS");
                    transaction.commit();

                } catch (Exception e){
                    logger.error("onFragmentChange error : ENVIRONMENT {}", e.getMessage());
                }
                break;
            case CONFIG_SETTING:
                try{
                    onFrameLayoutChange(true);
                    ConfigSettingFragment configSettingFragment = new ConfigSettingFragment();
                    configSettingFragment.setArguments(bundle);
                    transaction.replace(R.id.fullScreen, configSettingFragment, "CONFIG_SETTING");
                    transaction.commit();

                } catch (Exception e){
                    logger.error("onFragmentChange error : CONFIG_SETTING {}", e.getMessage());
                }
                break;
            case WEB_SOCKET:
                try {
                    onFrameLayoutChange(true);
                    WebSocketDebugFragment webSocketDebugFragment = new WebSocketDebugFragment();
                    webSocketDebugFragment.setArguments(bundle);
                    transaction.replace(R.id.fullScreen, webSocketDebugFragment, "WEBSOCKET");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : webSocketDebugFragment {}", e.getMessage());
                }
                break;
            case CONTROL_BOARD_DEBUGGING:
                try {
                    onFrameLayoutChange(true);
                    ControlDebugFragment controlDebugFragment = new ControlDebugFragment();
                    controlDebugFragment.setArguments(bundle);
                    transaction.replace(R.id.fullScreen, controlDebugFragment, "CONTROL");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : controlDebugFragment {}", e.getMessage());
                }
                break;
            case LOAD_TEST:
                try {
                    onFrameLayoutChange(true);
                    ProductTestFragment productTestFragment = new ProductTestFragment();
                    productTestFragment.setArguments(bundle);
                    transaction.replace(R.id.fullScreen, productTestFragment, "LOAD_TEST");
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("onFragmentChange error : productTestFragment {}", e.getMessage());
                }
                break;
        }
    }


    public void onFrameLayoutChange(boolean hidden) {
        //main activity layout fullScreen change
        try {
            FrameLayout frameLayout0 = ((MainActivity) MainActivity.mContext).findViewById(R.id.ch0);
            FrameLayout frameLayout1 = ((MainActivity) MainActivity.mContext).findViewById(R.id.ch1);
            FrameLayout fullScreen = ((MainActivity) MainActivity.mContext).findViewById(R.id.fullScreen);

            if (hidden) {
                fullScreen.setVisibility(View.VISIBLE);
                frameLayout0.setVisibility(View.INVISIBLE);
                frameLayout1.setVisibility(View.INVISIBLE);
            } else {
                onFrameLayoutRemove();
                fullScreen.setVisibility(View.INVISIBLE);
                frameLayout0.setVisibility(View.VISIBLE);
                frameLayout1.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            logger.error("onFrameLayoutChange error : {}", e.getMessage());
        }
    }

    public void onAdminLayoutChange(UiSeq uiSeq) {
        try {
            FrameLayout frameHeader = ((MainActivity) MainActivity.mContext).findViewById(R.id.header);
            FrameLayout frameFooter = ((MainActivity) MainActivity.mContext).findViewById(R.id.frameFooter);

            switch (uiSeq) {
                case ADMIN_PASS:
                case ENVIRONMENT:
                case CONFIG_SETTING:
                case WEB_SOCKET:
                case CONTROL_BOARD_DEBUGGING:
                    frameHeader.setVisibility(View.INVISIBLE);
                    frameFooter.setVisibility(View.INVISIBLE);
                    break;
                default:
                    frameHeader.setVisibility(View.VISIBLE);
                    frameFooter.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception e) {
            logger.error("onAdminLayoutChange error : {}", e.getMessage());
        }
    }


    public void onFrameLayoutRemove(){
        try {
            fragmentCurrent = new FragmentCurrent();
            FragmentTransaction transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
            Fragment fragment = fragmentCurrent.getCurrentFragment();
            if (fragment != null) {
                transaction.remove(fragment); // 제거
                transaction.commit(); // UI 반영
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void onFrameLayoutRemove(Fragment currentFragment) {
        try {
            fragmentCurrent = new FragmentCurrent();
            FragmentTransaction transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
            Fragment fragment = fragmentCurrent.getCurrentFragment();
            if (fragment instanceof FaultFragment) {
                FrameLayout fullScreen = ((MainActivity) MainActivity.mContext).findViewById(R.id.fullScreen);
                fullScreen.setVisibility(View.INVISIBLE);
                transaction.remove(fragment); // 제거
                transaction.commit(); // UI 반영
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    public void onFragmentHeaderChange(int channel, String sendText) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("CHANNEL", channel);
            int frameLayoutId = R.id.header;
            FragmentTransaction transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
            HeaderFragment headerFragment = new HeaderFragment();
            transaction.replace(frameLayoutId, headerFragment, sendText);
            headerFragment.setArguments(bundle);
            transaction.commit();
        } catch (Exception e) {
            logger.error("onFragmentHeaderChange error : {}", e.getMessage());
        }
    }

    public void onRemoveFragment(int channel, String tag) {
        try {
            int frameLayoutId = channel == 0 ? R.id.ch0 : R.id.ch1;
            FragmentManager fragmentManager = ((MainActivity) MainActivity.mContext).getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
//            if (fragment != null) fragmentManager.beginTransaction().remove(fragment).commit();
        } catch (Exception e) {
            logger.error("onRemoveFragment error : {}", e.getMessage());
        }
    }

}
