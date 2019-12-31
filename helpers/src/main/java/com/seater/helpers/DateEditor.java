package com.seater.helpers;

import java.beans.PropertyEditorSupport;
import java.util.Date;

public class DateEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
            setValue(new Date(Long.parseLong(text)));
    }

}
