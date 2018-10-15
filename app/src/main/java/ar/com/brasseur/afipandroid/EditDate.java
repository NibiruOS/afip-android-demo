package ar.com.brasseur.afipandroid;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class EditDate extends AppCompatEditText {
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final Calendar calendar = Calendar.getInstance();
    private boolean isNull;

    public EditDate(Context context) {
        super(context, null, android.R.attr.editTextStyle);
        init();
    }

    public EditDate(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
        init();
    }

    public EditDate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showPicker();
            }
        });
        setOnClickListener(v -> {
            showPicker();
        });
    }

    @Nullable
    public Date getDate() {
        return isNull
                ? null
                : calendar.getTime();
    }

    public void setDate(@Nullable Date date) {
        isNull = date == null;
        if (!isNull) {
            calendar.setTime(date);
        }
        updateLabel();
    }

    private void showPicker() {
        new DatePickerDialog(getContext(),
                (view1, year, monthOfYear, dayOfMonth) -> {
                    isNull = false;
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateLabel() {
        setText(isNull
                ? ""
                : DATE_FORMAT.format(calendar.getTime()));
    }
}
