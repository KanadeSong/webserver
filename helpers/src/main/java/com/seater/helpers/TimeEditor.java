package com.seater.helpers;

import java.beans.PropertyEditorSupport;
import java.sql.Time;

public class TimeEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(new Time(Long.parseLong(text)));
    }
}
