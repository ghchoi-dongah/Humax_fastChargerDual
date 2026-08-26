package com.dongah.fastcharger.pages;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.websocket.socket.SocketMessageListener;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebSocketDebugFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebSocketDebugFragment extends Fragment implements View.OnClickListener, SocketMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketDebugFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button btnWebClose, btnWebClear;
    TextView txtDebugMessage;

    public WebSocketDebugFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebSocketDebugFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebSocketDebugFragment newInstance(String param1, String param2) {
        WebSocketDebugFragment fragment = new WebSocketDebugFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_socket_debug, container, false);
        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().setSocketMessageDebugListener(this);
        btnWebClose = view.findViewById(R.id.btn_web_close);
        btnWebClose.setOnClickListener(this);
        btnWebClear = view.findViewById(R.id.btn_web_clear);
        btnWebClear.setOnClickListener(this);
        txtDebugMessage = view.findViewById(R.id.txt_Debug_Message);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (Objects.equals(v.getId(), R.id.btn_web_close)) {
            //** fragment change */
            FragmentTransaction transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
            EnvironmentFragment environmentFragment = new EnvironmentFragment();
            transaction.replace(R.id.fullScreen, environmentFragment);
            transaction.commit();
        } else if (Objects.equals(v.getId(), R.id.btn_web_clear)) {
            txtDebugMessage.setText(null);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().setSocketMessageDebugListenerStop();
    }

    @Override
    public void onMessageReceiveEvent(JSONObject jsonObject) {

    }

    @Override
    public void onMessageReceiveDebugEvent(JSONObject jsonObject, String actionName) {

    }

    @Override
    public void onMessageReceiveDebugEvent(int type, String text, String actionName) {
        ((MainActivity) MainActivity.mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (txtDebugMessage.getLineCount() > 120) txtDebugMessage.setText("");
                    String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    String sMessageHeader = type == 1 ? "<< receive data : " : "<< send data : ";
                    sMessageHeader += sDate + " : " + actionName + ">>\n";
                    txtDebugMessage.append(sMessageHeader + text + "\n");
                    txtDebugMessage.setMovementMethod(new ScrollingMovementMethod());
                } catch (Exception e) {
                    logger.error(" onMessageReceiveDebugEvent error :  {}", e.getMessage());
                }
            }
        });
    }
}