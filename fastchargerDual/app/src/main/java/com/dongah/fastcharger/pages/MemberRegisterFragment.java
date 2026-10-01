package com.dongah.fastcharger.pages;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.TECH3800.TLS3800;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.utils.FileManagement;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemberRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberRegisterFragment extends Fragment {

    private static final Logger logger = LoggerFactory.getLogger(MemberRegisterFragment.class);
    private static final String MEMBER_CARD_FILE = "localMemberCardList";

    private int mChannel;
    private boolean deleteMode = false;
    private List<String> cardList = new ArrayList<>();

    private LinearLayout llCardList;
    private TextView tvEmpty;
    private Button btnRegister, btnDelete, btnDeleteConfirm, btnExit;

    private FileManagement fileManagement = new FileManagement();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MemberRegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemberRegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberRegisterFragment newInstance(String param1, String param2) {
        MemberRegisterFragment fragment = new MemberRegisterFragment();
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
            mChannel = getArguments().getInt("CHANNEL", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member_register, container, false);

        llCardList = view.findViewById(R.id.llCardList);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        btnRegister = view.findViewById(R.id.btnMemberRegisterAdd);
        btnDelete = view.findViewById(R.id.btnMemberDeleteMode);
        btnDeleteConfirm = view.findViewById(R.id.btnMemberDeleteConfirm);
        btnExit = view.findViewById(R.id.btnMemberRegisterExit);

        btnDeleteConfirm.setVisibility(View.GONE);

        loadCardList();
        refreshCardListView();

        btnRegister.setOnClickListener(v -> startCardRegister());

        btnDelete.setOnClickListener(v -> {
            if (!deleteMode) {
                deleteMode = true;
                btnDeleteConfirm.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(false);
                refreshCardListView();
            }
        });

        btnDeleteConfirm.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("삭제 확인")
                    .setMessage(getString(R.string.memberDeleteConfirm))
                    .setPositiveButton("YES", (dialog, which) -> deleteSelectedCards())
                    .setNegativeButton("NO", null)
                    .show();
        });

        btnExit.setOnClickListener(v -> exitFragment());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalVariables.memberRegisterMode = false;
        GlobalVariables.memberCardRegisterCallback = null;
    }

    private void startCardRegister() {
        GlobalVariables.memberRegisterMode = true;
        GlobalVariables.memberCardRegisterCallback = cardId -> {
            GlobalVariables.memberRegisterMode = false;
            GlobalVariables.memberCardRegisterCallback = null;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (cardList.contains(cardId)) {
                    Toast.makeText(getActivity(), R.string.memberAlreadyExists, Toast.LENGTH_SHORT).show();
                } else {
                    cardList.add(cardId);
                    saveCardList();
                    refreshCardListView();
                    Toast.makeText(getActivity(), R.string.memberRegisterSuccess, Toast.LENGTH_SHORT).show();
                }
            });
        };
        ((MainActivity) MainActivity.mContext).getTls3800().onTLS3800Request(mChannel, TLS3800.CMD_TX_RF_READ, 0);
        Toast.makeText(getActivity(), R.string.memberCardTaggingRegister, Toast.LENGTH_LONG).show();
    }

    private void deleteSelectedCards() {
        List<String> toRemove = new ArrayList<>();
        for (int i = 0; i < llCardList.getChildCount(); i++) {
            View row = llCardList.getChildAt(i);
            CheckBox cb = row.findViewById(R.id.cbCard);
            if (cb != null && cb.isChecked()) {
                TextView tv = row.findViewById(R.id.tvCardNumber);
                if (tv != null) toRemove.add(tv.getText().toString());
            }
        }
        cardList.removeAll(toRemove);
        saveCardList();
        deleteMode = false;
        btnDeleteConfirm.setVisibility(View.GONE);
        btnRegister.setEnabled(true);
        refreshCardListView();
    }

    private void refreshCardListView() {
        llCardList.removeAllViews();
        if (cardList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            for (String cardId : cardList) {
                View row = LayoutInflater.from(getActivity()).inflate(R.layout.item_member_card, llCardList, false);
                TextView tvCardNumber = row.findViewById(R.id.tvCardNumber);
                CheckBox cbCard = row.findViewById(R.id.cbCard);
                tvCardNumber.setText(cardId);
                cbCard.setVisibility(deleteMode ? View.VISIBLE : View.GONE);
                cbCard.setChecked(false);
                llCardList.addView(row);
            }
        }
    }

    private void loadCardList() {
        cardList.clear();
        try {
            String filePath = GlobalVariables.getRootPath() + File.separator + MEMBER_CARD_FILE;
            String content = fileManagement.getStringFromFile(filePath);
            if (content != null && !content.isEmpty()) {
                JSONArray arr = new JSONArray(content);
                for (int i = 0; i < arr.length(); i++) {
                    cardList.add(arr.getString(i));
                }
            }
        } catch (Exception e) {
            logger.error("loadCardList error: {}", e.getMessage());
        }
    }

    private void saveCardList() {
        try {
            JSONArray arr = new JSONArray();
            for (String card : cardList) arr.put(card);
            fileManagement.stringToFileSave(GlobalVariables.getRootPath(), MEMBER_CARD_FILE, arr.toString(), false);
        } catch (Exception e) {
            logger.error("saveCardList error: {}", e.getMessage());
        }
    }

    private void exitFragment() {
        try {
            FragmentTransaction transaction = ((MainActivity) MainActivity.mContext).getSupportFragmentManager().beginTransaction();
            EnvironmentFragment environmentFragment = new EnvironmentFragment();
            transaction.replace(R.id.fullScreen, environmentFragment);
            transaction.commit();
        } catch (Exception e) {
            logger.error("exitFragment error: {}", e.getMessage());
        }
    }
}