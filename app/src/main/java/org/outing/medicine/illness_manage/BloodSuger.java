package org.outing.medicine.illness_manage;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.chat.Coordinates;
import org.outing.medicine.tools.chat.ShowChart;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

import java.util.ArrayList;

/**
 * Created by apple on 15/10/10.
 */
public class BloodSuger extends TActivity implements View.OnClickListener {
    private LineChart mLineChart;
    private EditText editIn;
    private Button buttonUpWrite, btnDay, btnTime;
    private int dataType = 1;
    ArrayList<Coordinates> coordinatesArrayList = new ArrayList<Coordinates>();

    @Override
    public void onCreate() {
        setContentView(R.layout.activity_blood_suger);
        setTitle("血糖");
        setTitleBackColor(R.color.btn_2_normal);
        showBackButton();
        init();
//        //有关图表
//        LineData mLineData = getLineData(24, 10);
//        ShowChart.showChart(mLineChart, mLineData, Color.rgb(114, 188, 223));
        Connect.POST(this, ServerURL.Get_Body_Message, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("type", "3");
                return list;
            }

            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                JSONArray arr = null;
                try {
                    arr = new JSONArray(response);
                    Log.d("test", "arr" + arr);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject temp = (JSONObject) arr.getJSONObject(i);
                        String data = temp.getString("data");
                        String time = temp.getString("time");
                        Coordinates coordinates = new Coordinates(time, data);
                        coordinatesArrayList.add(coordinates);
                        Log.d("test", data + "   " + time);
                    }
                    //有关图表
                    LineData mLineData = getLineData(coordinatesArrayList);
                    ShowChart.showChart(mLineChart, mLineData, Color.rgb(114, 188, 223));

                } catch (Exception e) {

                }
                Toast.makeText(BloodSuger.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(BloodSuger.this, "标题", "内容", true);
                return dialog;
            }
        });
    }


    private void init() {
        mLineChart = (LineChart) findViewById(R.id.line_chart);
        editIn = (EditText) findViewById(R.id.edit_blood_suger);
        buttonUpWrite = (Button) findViewById(R.id.btn_write);
        buttonUpWrite.setOnClickListener(this);
        btnDay = (Button) findViewById(R.id.time_button);
        btnDay.setOnClickListener(this);
        btnTime = (Button) findViewById(R.id.day_button);
        btnTime.setOnClickListener(this);
    }


    /**
     * 生成一个数据
     *
     * @return
     */
    private LineData getLineData(ArrayList<Coordinates> messageList) {
        ArrayList<String> xValues = new ArrayList<String>();
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        String lastxstr = null;
        int county = 0, count = 0;
        Float y = 0.0f;
        for (int i = 0; i < messageList.size(); i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            Coordinates getCoordinates = messageList.get(i);
            if (dataType == 1) {
                String xstr = getCoordinates.getX();
                xstr = xstr.substring(xstr.length() - 8, xstr.length());
                xValues.add(xstr);
                // y轴的数据
                yValues.add(new Entry(Float.parseFloat(getCoordinates.getY()), i));
            }
            if (dataType == 2) {
                String xstr = getCoordinates.getX();
                xstr = xstr.substring(0, 10);
                if (lastxstr == null) {
                    lastxstr = xstr;
                }
                if (!lastxstr.equals(xstr)) {
                    xValues.add(lastxstr);
                    yValues.add(new Entry(y / county, count));
                    lastxstr = xstr;
                    y = 0.0f;
                    county = 0;
                    count++;
                }
                if (lastxstr.equals(xstr)) {
                    y = y + Float.parseFloat(getCoordinates.getY());
                    county++;
                }
                if (i == messageList.size() - 1) {
                    xValues.add(lastxstr);
                    yValues.add(new Entry(y / county, count));
                }

            }
        }
        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "血糖" /*显示在比例图上*/);
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setCircleSize(3f);// 显示的圆形大小
        lineDataSet.setColor(Color.WHITE);// 显示颜色
        lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
        lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(xValues, lineDataSets);

        return lineData;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_write:
                final String sugerIn = editIn.getText().toString();
                try {
                    //报错则说明不是整数
                    Double i = Double.parseDouble(sugerIn);
                    if (i <= 0) {
                        Integer.parseInt("产生错误，转到catch");
                    }
                    if (i <= 10 || i >= 500) {
                        Toast.makeText(BloodSuger.this, "请输入血糖正确范围值，注意，血糖单位是mg/dl",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        Connect.POST(this, ServerURL.Post_Body_Message, new ConnectListener() {
                            @Override
                            public ConnectList setParam(ConnectList list) {
                                list.put("type", "3");
                                list.put("data", sugerIn);
                                return list;
                            }

                            @Override
                            public void onResponse(String response) {
                                Log.d("TAG", response);
                                JSONArray arr = null;
                                try {
                                    Log.d("TAG", response);
                                    if (response.equals("0")) {
                                        Toast.makeText(BloodSuger.this, "上传成功",
                                                Toast.LENGTH_SHORT).show();
                                        //成功后退出界面
                                        finish();
                                    } else {
                                        Toast.makeText(BloodSuger.this, "系统错误",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {

                                }
                                Toast.makeText(BloodSuger.this, response, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public ConnectDialog showDialog(ConnectDialog dialog) {
                                dialog.config(BloodSuger.this, "标题", "内容", true);
                                return dialog;
                            }
                        });
                    }

                } catch (Exception e) {
                    Toast.makeText(BloodSuger.this, "请输入正确数据，系统只支持正整数",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.day_button:
                dataType = 2;
                LineData mLineData1 = getLineData(coordinatesArrayList);
                ShowChart.showChart(mLineChart, mLineData1, Color.rgb(114, 188, 223));
                break;
            case R.id.time_button:
                dataType = 1;
                LineData mLineData2 = getLineData(coordinatesArrayList);
                ShowChart.showChart(mLineChart, mLineData2, Color.rgb(114, 188, 223));
                break;


        }
    }

    @Override
    public void showContextMenu() {
    }

}
