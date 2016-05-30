package com.alburivan.slicksignform.interfaces;

import com.alburivan.slicksignform.FormField;

import java.util.List;

/**
 * Created by Ivan on 5/19/2016
 */
public interface IOnProcessChange {




    /** Work to be done in the background
     * @param param*/
    void onAsyncStart(List<FormField> param);

    void onAsyncFinished();

}
