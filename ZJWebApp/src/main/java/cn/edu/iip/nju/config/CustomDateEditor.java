package cn.edu.iip.nju.config;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CustomDateEditor extends PropertyEditorSupport {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public void setAsText(String text) throws IllegalArgumentException {

        try {
            setValue(simpleDateFormat.parse(text));
        } catch (ParseException e) {
        }
    }

    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return simpleDateFormat.format(value);
    }

}
