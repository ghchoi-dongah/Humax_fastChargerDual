package com.dongah.fastcharger.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.basefunction.UiSeq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductTestBottomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductTestBottomFragment extends Fragment implements View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(ProductTestBottomFragment.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button btnExit, btnIO, btnLoad, btnTotalLoad;
    FragmentTransaction transaction;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductTestBottomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductTestBottomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductTestBottomFragment newInstance(String param1, String param2) {
        ProductTestBottomFragment fragment = new ProductTestBottomFragment();
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
        View view = inflater.inflate(R.layout.fragment_product_test_bottom, container, false);

        btnExit = view.findViewById(R.id.btnExit);
        btnIO = view.findViewById(R.id.btnIO);
        btnIO.setEnabled(false);
        btnLoad = view.findViewById(R.id.btnLoad);
        btnTotalLoad = view.findViewById(R.id.btnTotalLoad);
        btnExit.setOnClickListener(this);
        btnIO.setOnClickListener(this);
        btnLoad.setOnClickListener(this);
        btnTotalLoad.setOnClickListener(this);
        btnIO.performClick();

        return view;
    }

    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (Objects.equals(getId, R.id.btnExit)) {
            transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
            EnvironmentFragment environmentFragment = new EnvironmentFragment();
            transaction.replace(R.id.fullScreen, environmentFragment);
            transaction.commit();
        } else if (Objects.equals(getId, R.id.btnIO)) {
            try {
                btnIO.setEnabled(false);
                btnLoad.setEnabled(true);
                btnTotalLoad.setEnabled(true);
                onDisplayChange(UiSeq.LOAD_TEST_IO, "LOAD_TEST_IO", null);
            } catch (Exception e) {
                logger.error("btnIO error : " + e.getMessage());
            }
        } else if (Objects.equals(getId, R.id.btnLoad)) {
            try {
                btnIO.setEnabled(true);
                btnLoad.setEnabled(false);
                btnTotalLoad.setEnabled(true);
                onDisplayChange(UiSeq.LOAD_TEST, "LOAD_TEST", null);
            } catch (Exception e) {
                logger.error("btnLoad error : " + e.getMessage());
            }
        } else if (Objects.equals(getId, R.id.btnTotalLoad)) {
            try {
                btnIO.setEnabled(true);
                btnLoad.setEnabled(true);
                btnTotalLoad.setEnabled(false);
                onDisplayChange(UiSeq.LOAD_TEST_TOTAL, "LOAD_TEST_TOTAL", null);
            } catch (Exception e) {
                logger.error("btnLoadTest error : " + e.getMessage());
            }
        }
    }

    private void onDisplayChange(UiSeq uiSeq, String sendText, String type) {
        Bundle bundle = new Bundle();
        transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
        switch (uiSeq) {
            case LOAD_TEST_IO:
//                try {
//                    ProductTestIoFragment productTestIoFragment = new ProductTestIoFragment();
//                    transaction.replace(R.id.operationDisplay, productTestIoFragment, sendText);
//                    transaction.commit();
//                } catch (Exception e) {
//                    logger.error("LOAD_TEST_IO error : " + e.getMessage());
//                }
                break;
            case LOAD_TEST:
                try {
                    ProductTestLoadFragment productTestLoadFragment = new ProductTestLoadFragment();
                    transaction.replace(R.id.operationDisplay, productTestLoadFragment, sendText);
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("LOAD_TEST error : " + e.getMessage());
                }
                break;
            case LOAD_TEST_TOTAL:
                try {
                    ProductTestTotalLoadFragment productTestTotalLoadFragment = new ProductTestTotalLoadFragment();
                    transaction.replace(R.id.operationDisplay, productTestTotalLoadFragment, sendText);
                    transaction.commit();
                } catch (Exception e) {
                    logger.error("LOAD_TEST_TOTAL error : " + e.getMessage());
                }
                break;
        }
    }
}