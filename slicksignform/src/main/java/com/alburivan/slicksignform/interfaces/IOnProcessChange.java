package com.alburivan.slicksignform.interfaces;

import com.alburivan.slicksignform.FormField;
import java.util.List;

/**
 * Created by Ivan on 5/19/2016
 */
public interface IOnProcessChange {

    /**
     * This method handles the work to be done in the background and to be implemented by the developer
     * @param param The collection of fields used in this form
     */
    boolean workInBackground(List<FormField> param);

    /**
     *
     */
    void workFinished();

}
