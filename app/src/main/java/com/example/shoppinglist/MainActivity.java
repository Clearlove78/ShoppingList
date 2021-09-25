package com.example.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CartAdapter.RefreshPriceInterface ,View.OnClickListener, SendEmail.NoticeDialogListener {

    private ListView listView;
    private CheckBox cb_check_all;
    private TextView tv_total_price;
    private CartAdapter adapter;
    public EditText editText1;
    public EditText editText2;

    private double totalPrice = 0.00;
    private int totalCount = 0;
    private List<HashMap<String, String>> goodsList;
    private final String email = "jxia27@binghamton.edu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two);
        Button btn = (Button) findViewById(R.id.button10);
        Button email_btn = findViewById(R.id.email_btn);
        editText1 = (EditText) findViewById(R.id.edit_text1);
        editText2 = (EditText) findViewById(R.id.edit_text2);
        Builder dialogBuidler = new Builder(MainActivity.this);
        listView = (ListView) findViewById(R.id.listview);
        goodsList = new ArrayList();
        editText2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    btn.callOnClick();
                    editText2.setText("");
                    return true;
                }
                return false;
            }
        });
        adapter = new CartAdapter(this, goodsList);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Press Number"+(position+1)+" Product", Toast.LENGTH_SHORT).show();
                final int deleteId=position;
                Builder dialogBuidler = new Builder(MainActivity.this);
                dialogBuidler.setMessage("delete the message");
                dialogBuidler.setPositiveButton("confirm", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        deleteData(deleteId);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Delete Confirm", Toast.LENGTH_LONG).show();

                    }
                });
                dialogBuidler.setNegativeButton("cancel", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                dialogBuidler.create().show();
                return true;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = editText1.getText().toString();
                String inputPrice = editText2.getText().toString();
                if (TextUtils.isEmpty(inputText)){
                    Toast.makeText(MainActivity.this, "Input Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(inputPrice)) {
                    Toast.makeText(MainActivity.this, "Input Price", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> map = new HashMap<>();
                map.put("id", (new Random().nextInt(10000) % (10000 - 2900 + 2900) + 2900) + "");
                map.put("name", inputText);
                map.put("price", inputPrice);
                map.put("count", "1");
                goodsList.add(map);
                initView();
            }
        });
        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendEmail sendToEmailDialogFragment = new SendEmail();
                sendToEmailDialogFragment.show(getSupportFragmentManager(), "emailConfirm");
            }
        });

    }

    private void priceControl(Map<String, Integer> pitchOnMap) {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < goodsList.size(); i++) {
            if (pitchOnMap.get(goodsList.get(i).get("id")) == 1) {
                totalCount = totalCount + Integer.valueOf(goodsList.get(i).get("count"));
                double goodsPrice = Integer.valueOf(goodsList.get(i).get("count")) * Double.valueOf(goodsList.get(i).get("price"));
                totalPrice = totalPrice + goodsPrice;
            }
        }
        tv_total_price.setText("$ " + totalPrice);
    }

    private void deleteData(int position){
        ArrayList<HashMap<String, String>> newList=new ArrayList<>();
        goodsList.remove(position);
        newList.addAll(goodsList);
        goodsList.clear();
        goodsList.addAll(newList);

    }

    @Override
    public void refreshPrice(Map<String, Integer> pitchOnMap) {
        priceControl(pitchOnMap);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all_chekbox:
                AllTheSelected();
                break;
        }
    }

    private void AllTheSelected() {
        Map<String, Integer> map = adapter.getPitchOnMap();
        boolean isCheck = false;
        boolean isUnCheck = false;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if (Integer.valueOf(entry.getValue().toString()) == 1) isCheck = true;
            else isUnCheck = true;
        }
        if (isCheck == true && isUnCheck == false) {
            for (int i = 0; i < goodsList.size(); i++) {
                map.put(goodsList.get(i).get("id"), 0);
            }
            cb_check_all.setChecked(false);
        } else if (isCheck == true && isUnCheck == true) {
            for (int i = 0; i < goodsList.size(); i++) {
                map.put(goodsList.get(i).get("id"), 1);
            }
            cb_check_all.setChecked(true);
        } else if (isCheck == false && isUnCheck == true) {
            for (int i = 0; i < goodsList.size(); i++) {
                map.put(goodsList.get(i).get("id"), 1);
            }
            cb_check_all.setChecked(true);
        }
        priceControl(map);
        adapter.setPitchOnMap(map);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview);
        cb_check_all = (CheckBox) findViewById(R.id.all_chekbox);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        cb_check_all.setOnClickListener(this);
        adapter = new CartAdapter(this, goodsList);
        adapter.setRefreshPriceInterface(this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, email);
        i.putExtra(Intent.EXTRA_SUBJECT, "goodList");
        String list_str = "";
        for (int j = 0; j < goodsList.size(); j++) {
            list_str += "Name:";
            list_str += goodsList.get(j).get("name").toString();
            list_str += "       ";
            list_str += "Price:";
            list_str += goodsList.get(j).get("price").toString();
            list_str += "       ";
            list_str += "Count:";
            list_str += goodsList.get(j).get("count").toString()+"\n";
        }
        i.putExtra(Intent.EXTRA_TEXT, list_str);
        try {
            startActivity(Intent.createChooser(i, "Send email"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "No applications.", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), "Confirm", Toast.LENGTH_LONG).show();
    }

    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();
    }
}
