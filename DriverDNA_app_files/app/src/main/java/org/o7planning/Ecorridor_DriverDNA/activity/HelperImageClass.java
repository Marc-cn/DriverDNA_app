package org.o7planning.Ecorridor_DriverDNA.activity;

import org.o7planning.Ecorridor_DriverDNA.R;

public class HelperImageClass {

    static int resIDOfImageToLoad =  0;

    public static void SetImageToLoad(Double value)
    {
        if (value >= 0.0 && value < 10.0)
        {
            resIDOfImageToLoad = R.drawable.red;
        }
        else if(value >= 10.0 && value < 30.0)
        {
            resIDOfImageToLoad = R.drawable.grey;
        }

        else if(value >= 30.0 && value < 50.0)
        {
            resIDOfImageToLoad = R.drawable.green;
        }

        else if(value >= 50.0 && value < 100.0)
        {
            resIDOfImageToLoad = R.drawable.yellow;
        }
    }

    public static int GetResourceIDOfImageToLoad()
    {
        return resIDOfImageToLoad;
    }
}
