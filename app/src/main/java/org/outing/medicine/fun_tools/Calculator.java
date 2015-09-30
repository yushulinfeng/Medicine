package org.outing.medicine.fun_tools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.outing.medicine.R;

import java.util.Stack;

public class Calculator extends Activity {
    private static final String ERROR = "出错";
    private TextView userInputText;

    private Stack<String> mInputStack;
    private Stack<String> mOperationStack;

    private boolean resetInput = false;
    private boolean hasFinalResult = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fun_tools_calculator);

        mInputStack = new Stack<String>();
        mOperationStack = new Stack<String>();
        userInputText = (TextView) findViewById(R.id.tools_calculator_tv_show);
        userInputText.setText("0");
    }

    private void ProcessKeypadInput(String keypadButton) {
        if (userInputText.getText().toString().equals(ERROR)) {
            userInputText.setText("0");
            clearStacks();
        }

        String text = keypadButton;
        String currentInput = userInputText.getText().toString();
        int currentInputLen = currentInput.length();
        String evalResult = null;
        switch (keypadButton) {
            case CalculatorText.BACKSPACE:
                if (resetInput)
                    return;
                int endIndex = currentInputLen - 1;
                if (endIndex < 1) {
                    userInputText.setText("0");
                } else {
                    userInputText.setText(currentInput.subSequence(0, endIndex));
                }
                break;
            case CalculatorText.SIGN: // 正负
                switch (currentInput) {
                    case CalculatorText.DIV:
                        break;
                    case CalculatorText.PLUS:
                        break;
                    case CalculatorText.MINUS:
                        break;
                    case CalculatorText.MULTIPLY:
                        break;
                    default:
                        if (currentInputLen > 0 && !currentInput.equals("0")) {
                            if (currentInput.charAt(0) == '-') {
                                userInputText.setText(currentInput.subSequence(1,
                                        currentInputLen));
                            } else {
                                userInputText.setText("-" + currentInput.toString());
                            }
                        }
                }
                break;
            case CalculatorText.OFF:
                finish();
                break;
            case CalculatorText.AC:
                userInputText.setText("0");
                clearStacks();
                break;
            case CalculatorText.DECIMAL_SEP: // 小数点
                if (hasFinalResult || resetInput) {
                    userInputText.setText("0.");
                    hasFinalResult = false;
                    resetInput = false;
                } else if (currentInput.contains("."))
                    return;
                else
                    userInputText.append(".");
                break;
            case CalculatorText.DIV:
            case CalculatorText.PLUS:
            case CalculatorText.MINUS:
            case CalculatorText.MULTIPLY:
                if (resetInput) {
                    mInputStack.pop();
                    mOperationStack.pop();
                } else {
                    if (currentInput.charAt(0) == '-') {
                        mInputStack.add("(" + currentInput + ")");
                    } else {
                        mInputStack.add(currentInput);
                    }
                    mOperationStack.add(currentInput);
                }

                mInputStack.add(text);
                mOperationStack.add(text);

                evalResult = evaluateResult(false);
                if (evalResult != null)
                    userInputText.setText(evalResult);
                resetInput = true;

                userInputText.setText(text);
                break;
            case CalculatorText.CALCULATE:// 等号
                if (mOperationStack.size() == 0)
                    break;
                switch (currentInput) {
                    case CalculatorText.DIV:
                    case CalculatorText.PLUS:
                    case CalculatorText.MINUS:
                    case CalculatorText.MULTIPLY:
                        mInputStack.pop();
                        mOperationStack.pop();
                        mInputStack.add(CalculatorText.PLUS);
                        mOperationStack.add(CalculatorText.PLUS);
                        mOperationStack.add(0 + "");// +0显示，防止运算符结尾
                        break;
                    default:
                        mOperationStack.add(currentInput);
                }
                evalResult = evaluateResult(true);
                if (evalResult != null) {
                    clearStacks();
                    userInputText.setText(evalResult);
                    resetInput = false;
                    hasFinalResult = true;
                }
                break;
            default:
                if (Character.isDigit(text.charAt(0))) {
                    if (currentInput.equals("0") || resetInput || hasFinalResult) {
                        userInputText.setText(text);
                        resetInput = false;
                        hasFinalResult = false;
                    } else {
                        userInputText.append(text);
                        resetInput = false;
                    }

                }
                break;
        }

    }

    private void clearStacks() {
        mInputStack.clear();
        mOperationStack.clear();
    }

    private String evaluateResult(boolean requestedByUser) {
        if ((!requestedByUser && mOperationStack.size() != 4)
                || (requestedByUser && mOperationStack.size() != 3))
            return null;

        String left = mOperationStack.get(0);
        String operator = mOperationStack.get(1);
        String right = mOperationStack.get(2);
        String tmp = null;
        if (!requestedByUser)
            tmp = mOperationStack.get(3);

        double leftVal = Double.parseDouble(left.toString());
        double rightVal = Double.parseDouble(right.toString());
        double result = Double.NaN;

        if (operator.equals(CalculatorText.DIV)) {
            result = leftVal / rightVal;
        } else if (operator.equals(CalculatorText.MULTIPLY)) {
            result = leftVal * rightVal;
        } else if (operator.equals(CalculatorText.PLUS)) {
            result = leftVal + rightVal;
        } else if (operator.equals(CalculatorText.MINUS)) {
            result = leftVal - rightVal;
        }

        String resultStr = doubleToString(result);
        if (resultStr == null)
            return null;

        mOperationStack.clear();
        if (!requestedByUser) {
            mOperationStack.add(resultStr);
            mOperationStack.add(tmp);
        }

        return resultStr;
    }

    private String doubleToString(double value) {
        if (Double.isNaN(value))
            return null;
        long longVal = (long) value;
        if (longVal == value) {
            if (longVal < 10000000000L && longVal > -10000000000L)
                return Long.toString(longVal);
            else
                return ERROR;
        } else
            return dealDouble(value);
    }

    // 总位数不超过10位（不含点），越界输出ERROR，需要if判断
    private String dealDouble(double value) {
        String result = "";
        if (value > 0)
            value += 0.0000000005D;
        else
            value -= 0.0000000005D;
        String value_str = Double.toString(value);
        if (value_str.contains("E"))// 科学计数法
            return ERROR;
        int dot_index = value_str.indexOf('.');
        if (dot_index <= 0 || dot_index > 10)
            return ERROR;
        if (dot_index == 10)// 第11位(索引10)是小数点，就只取十位
            return value_str.substring(0, 10);
        if (value_str.length() >= 11)
            result = value_str.substring(0, 11);
        else
            result = value_str;
        while (result.charAt(result.length() - 1) == '0') {// 末尾去0
            result = result.substring(0, result.length() - 1);
        }
        if (result.endsWith("."))
            result += "0";
        return result;
    }

    public void onButtonClick(View btn) {
        String keypadButton = (String) ((Button) btn).getText().toString();
        ProcessKeypadInput(keypadButton);
    }
}
