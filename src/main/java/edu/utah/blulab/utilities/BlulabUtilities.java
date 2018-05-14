package edu.utah.blulab.utilities;

import java.util.*;

public class BlulabUtilities {

    public static boolean isNullOrEmpty(String feature) {
        return (null == feature) || (feature.isEmpty());
    }

    public static boolean isNullOrEmpty(Collection<?> value) {
        return value == null || value.isEmpty();
    }
    

}


