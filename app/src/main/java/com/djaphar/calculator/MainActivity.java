package com.djaphar.calculator;

import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    TextView inputTV;
    String  leftValue = "", rightValue = "", err = "", pattern = "#.########";
    Operation operation;
    boolean flEqual = false;

    Plus plus = new Plus();
    Minus minus = new Minus();
    Divide divide = new Divide();
    Multiply Multiply = new Multiply();

    abstract class Operation {
        abstract String getTxt();
        abstract void exec();
    }

    class Plus extends Operation {
        @Override
        String getTxt() {
            return "+";
        }
        @Override
        void exec() {
            if (errCheck(leftValue, rightValue)) {
                reset();
            } else {
                leftValue = new DecimalFormat(pattern).format(Double.parseDouble(leftValue) + Double.parseDouble(rightValue));
                rightValue = "";
            }
        }
    }

    class Minus extends Operation {
        @Override
        String getTxt() {
            return "-";
        }
        @Override
        void exec() {
            if (errCheck(leftValue, rightValue)) {
                reset();
            } else {
                leftValue = new DecimalFormat(pattern).format(Double.parseDouble(leftValue) - Double.parseDouble(rightValue));
                rightValue = "";
            }
        }
    }

    class Divide extends Operation {
        @Override
        String getTxt() {
            return "/";
        }
        @Override
        void exec() {
            if (errCheck(leftValue, rightValue)) {
                reset();
            } else {
                if (Double.parseDouble(rightValue) != 0) {
                    leftValue = new DecimalFormat(pattern).format(Double.parseDouble(leftValue) / Double.parseDouble(rightValue));
                    rightValue = "";
                } else {
                    err = "Деление на ноль!";
                    reset();
                }
            }
        }
    }

    class Multiply extends Operation {
        @Override
        String getTxt() {
            return "*";
        }
        @Override
        void exec() {
            if (errCheck(leftValue, rightValue)) {
                reset();
            } else  {
                leftValue = new DecimalFormat(pattern).format(Double.parseDouble(leftValue) * Double.parseDouble(rightValue));
                rightValue = "";
            }
        }
    }

    public void execVibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(40);
        }
    }

    public String setDot(String newValue) {
        if (newValue.contains(",")) {
            newValue = newValue.replace(',', '.');
        }
        return newValue;
    }

    public String setComma(String newValue) {
        if (newValue.contains(".")) {
            newValue = newValue.replace('.', ',');
        }
        return newValue;
    }

    public void reset() {
        leftValue = "";
        rightValue = "";
        operation = null;
        flEqual = false;
    }

    public Boolean errCheck(String leftValue, String rightValue) {
        if (leftValue.equals("-") || leftValue.equals("0.") || rightValue.equals("0.")) {
            err = "Ошибка!";
            return true;
        } else {
            return false;
        }
    }

    public void display() {
        String s;
        if (!err.equals("")) {
            inputTV.setTextSize(40);
            s = err;
            err = "";
        } else {
            inputTV.setTextSize(50);
            s = leftValue;
            if (operation != null) {
                s += operation.getTxt() + rightValue;
            }
        }
        s = setComma(s);
        inputTV.setText(s);
        if (flEqual)
            inputTV.setBackgroundColor(Color.GRAY);
        else
            inputTV.setBackgroundColor(Color.TRANSPARENT);
    }

    public void execOperation(Operation op) {
        execVibrate();
        if (operation != null && !rightValue.equals("")) {
            leftValue = setDot(leftValue);
            rightValue = setDot(rightValue);
            operation.exec();
        }
        if (leftValue.equals("")) {
            operation = null;
        } else {
            operation = op;
        }
        flEqual = (op == null);
        display();
    }

    public void onDigitBtnClick(View view) {
        execVibrate();
        if (flEqual)
            reset();
        if (operation == null) {
            leftValue += ((Button) view).getText().toString();
        } else {
            rightValue += ((Button) view).getText().toString();
        }
        display();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputTV = findViewById(R.id.inputTV);

        findViewById(R.id.buttonPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execOperation(plus);
            }
        });

        findViewById(R.id.buttonMinus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execOperation(minus);
            }
        });

        findViewById(R.id.buttonDivide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execOperation(divide);
            }
        });

        findViewById(R.id.buttonMultiply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execOperation(Multiply);
            }
        });

        findViewById(R.id.buttonClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execVibrate();
                reset();
                display();
            }
        });

        findViewById(R.id.buttonDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execVibrate();
                if (flEqual)
                    reset();
                if (operation == null) {
                    if (!leftValue.contains(",")) {
                        if (leftValue.equals("")) {
                            leftValue += "0";
                        }
                        leftValue += ",";
                    }
                } else {
                    if (!rightValue.contains(",")) {
                        if (rightValue.equals("")) {
                            rightValue += "0";
                        }
                        rightValue += ",";
                    }
                }
                display();
            }
        });

        findViewById(R.id.buttonReverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execVibrate();
                flEqual = false;
                if (operation == null) {
                    if (!leftValue.contains("-")) {
                        leftValue = "-" + leftValue;
                    } else {
                        leftValue = leftValue.replace("-", "");
                    }
                    display();
                }
            }
        });

        findViewById(R.id.buttonPercent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execVibrate();
                flEqual = false;
                if (errCheck(setDot(leftValue), setDot(rightValue))) {
                    reset();
                } else {
                    if ((operation == null) && (!leftValue.equals(""))) {
                        leftValue = new DecimalFormat(pattern).format(Double.parseDouble(setDot(leftValue)) / 100);
                    } else if (!rightValue.equals("")){
                        rightValue = new DecimalFormat(pattern).format(Double.parseDouble(setDot(rightValue)) / 100);
                    }
                }
                display();
            }
        });

        findViewById(R.id.buttonDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execVibrate();
                flEqual = false;
                if (!rightValue.equals("")) {
                    rightValue = rightValue.substring(0, rightValue.length() - 1);
                } else {
                    if (operation != null) {
                        operation = null;
                    } else {
                        if (!leftValue.equals("")) {
                            leftValue = leftValue.substring(0, leftValue.length() - 1);
                        }
                    }
                }
                display();
            }
        });

        findViewById(R.id.buttonEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execOperation(null);
            }
        });
    }
}
