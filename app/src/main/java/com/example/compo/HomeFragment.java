// HomeFragment.java
package com.example.compo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    CurrentDate currentDate;
    FloatingActionButton addButton;
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    ViewGroup rootView;
    AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        currentDate = new CurrentDate();
        TextView dateTextView = rootView.findViewById(R.id.date_text_view);
        String dateString = currentDate.getDate();
        dateTextView.setText(dateString);

        addButton = rootView.findViewById(R.id.add_button);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        adView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddComponentActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showFavorites(rootView);
    }

    public void showFavorites(ViewGroup rootView) {
        databaseHelper = new DatabaseHelper(getContext());
        database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.rawQuery("select * from component_favorites", null);
        LinearLayout componentList = rootView.findViewById(R.id.component_list);

        if (cursor != null) {
            int recordCount = cursor.getCount();
            for (int i = 0; i < recordCount; i++) {
                Button button = rootView.findViewById(i);
                if (button != null) {
                    componentList.removeView(button);
                }
            }
            for (int i = 0; i < recordCount; i++) {
                cursor.moveToNext();
                int favor_id = cursor.getInt(0);
                String compo_name = cursor.getString(1);
                int measured = cursor.getInt(2);
                String compo_color_dir = cursor.getString(3);
                String title = cursor.getString(4);

                Cursor iconCursor = database.rawQuery("select compo_icon_xml_dir from component_type where compo_name = '" + compo_name + "'", null);
                iconCursor.moveToNext();
                String icon_xml_dir = iconCursor.getString(0);

                Button button = new Button(getActivity());
                button.setText(title);
                button.setTextSize(20);
                button.setPadding(100, 5, 100, 20);
                button.setId(i);
                button.setBackgroundColor(Color.parseColor(compo_color_dir));

                if (icon_xml_dir.equals("study")) {
                    Drawable image = getResources().getDrawable(R.drawable.ic_study);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w, h);
                    button.setCompoundDrawables(image, null, null, null);
                } else if (icon_xml_dir.equals("exercise")) {
                    Drawable image = getResources().getDrawable(R.drawable.ic_exercise);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w, h);
                    button.setCompoundDrawables(image, null, null, null);
                } else if (icon_xml_dir.equals("game")) {
                    Drawable image = getResources().getDrawable(R.drawable.ic_game);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w, h);
                    button.setCompoundDrawables(image, null, null, null);
                } else if (icon_xml_dir.equals("read_book")) {
                    Drawable image = getResources().getDrawable(R.drawable.ic_read_book);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w, h);
                    button.setCompoundDrawables(image, null, null, null);
                }

                componentList.addView(button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("운동".equals(compo_name)) {
                            Intent intent = new Intent(getActivity(), PedometerActivity.class);
                            intent.putExtra("TITLE", title);
                            startActivity(intent);
                        } else if (measured == 0 && !compo_name.equals("운동")) {
                            Intent intent = new Intent(getActivity(), TimerActivity.class);
                            ArrayList<Object> arrayToHistory = new ArrayList<>();
                            arrayToHistory.add(favor_id);
                            intent.putExtra("arrayToHistory", arrayToHistory);
                            intent.putExtra("compoTitle", title);
                            startActivity(intent);
                        } else if (measured == 1 && !compo_name.equals("운동")) {
                            Intent intent = new Intent(getActivity(), StopwatchActivity.class);
                            ArrayList<Object> arrayToHistory = new ArrayList<>();
                            arrayToHistory.add(favor_id);
                            intent.putExtra("arrayToHistory", arrayToHistory);
                            intent.putExtra("compoTitle", title);
                            startActivity(intent);
                        }
                    }
                });

                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        button.setTextColor(Color.BLACK);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("삭제 확인");
                        builder.setMessage("이 활동을 삭제하시겠습니까?");

                        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.delete("component_favorites", "favor_id = ?", new String[]{String.valueOf(favor_id)});
                                componentList.removeView(button);
                            }
                        });

                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        // Customize the buttons
                        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                        positiveButton.setTextColor(Color.RED);
                        positiveButton.setTextSize(18);

                        negativeButton.setTextColor(Color.BLUE);
                        negativeButton.setTextSize(18);

                        return true;
                    }
                });
            }
        }
        cursor.close();
    }
}
