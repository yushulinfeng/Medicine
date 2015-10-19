package org.outing.medicine.illness_manage;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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
public class BloodPressure extends Activity implements View.OnClickListener {
    private LineChart mLineChart;
    private EditText editIn;
    private Button buttonUpWrite;
    ArrayList<Coordinates> coordinatesArrayList = new ArrayList<Coordinates>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        init();
//        //有关图表
//        LineData mLineData = getLineData(24, 10);
//        ShowChart.showChart(mLineChart, mLineData, Color.rgb(114, 188, 223));
        Connect.POST(this, ServerURL.Get_Body_Message, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("type", "1");
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
                        String time=temp.getString("time");
                        Coordinates coordinates=new Coordinates(time,data);
                        coordinatesArrayList.add(coordinates);
                        Log.d("test",data+"   "+time);
                    }
                    //有关图表
                    LineData mLineData = getLineData(coordinatesArrayList);
                    ShowChart.showChart(mLineChart, mLineData, Color.rgb(114, 188, 223));

                } catch (Exception e) {
                }
                Toast.makeText(BloodPressure.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(BloodPressure.this, "标题", "内容", true);
                return dialog;
            }
        });
    }



    private void init() {
        mLineChart = (LineChart) findViewById(R.id.line_chart);
        editIn= (EditText) findViewById(R.id.edit_blood_pressure);
        buttonUpWrite= (Button) findViewById(R.id.btn_write);
        buttonUpWrite.setOnClickListener(this);
    }


    /**
     * 生成一个数据
     * @return
     */
    private LineData getLineData(ArrayList<Coordinates> messageList) {
        ArrayList<String> xValues = new ArrayList<String>();
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        for (int i = 0; i < messageList.size(); i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            Coordinates getCoordinates=messageList.get(i);
            String xstr = getCoordinates.getX();
            xstr=xstr.substring(xstr.length() - 8, xstr.length());
            xValues.add(xstr);
            // y轴的数据
            yValues.add(new Entry(Float.parseFloat(getCoordinates.getY()), i));
        }
        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "血压折线图（日）" /*显示在比例图上*/);
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
        switch (v.getId()){
            case R.id.btn_write:
                final String pressIn=editIn.getText().toString();
                try {
                    //报错则说明不是整数
                    int i=Integer.parseInt(pressIn);
                    if (i<=0){
                        Integer.parseInt("产生错误，转到catch");
                    }if (i<=30||i>=200){
                        Toast.makeText(BloodPressure.this, "请输入血压正确范围值",
                                Toast.LENGTH_SHORT).show();
                    }else {

                        Connect.POST(this, ServerURL.Post_Body_Message, new ConnectListener() {
                            @Override
                            public ConnectList setParam(ConnectList list) {
                                list.put("type", "1");
                                list.put("data",pressIn);
                                return list;
                            }
                            @Override
                            public void onResponse(String response) {
                                Log.d("TAG", response);
                                JSONArray arr = null;
                                try {
                                    Log.d("TAG", response);
                                    if (response.equals("0")){
                                        Toast.makeText(BloodPressure.this, "上传成功",
                                                Toast.LENGTH_SHORT).show();
                                        //成功后退出界面
                                        finish();
                                    }else {
                                        Toast.makeText(BloodPressure.this, "系统错误",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {

                                }
                                Toast.makeText(BloodPressure.this, response, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public ConnectDialog showDialog(ConnectDialog dialog) {
                                dialog.config(BloodPressure.this, "标题", "内容", true);
                                return dialog;
                            }
                        });
                    }

                }catch (Exception e){
                    Toast.makeText(BloodPressure.this, "请输入正确数据，系统只支持正整数",
                            Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }
}

