package pt.up.fe.mobile.ui.studentarea;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;


/**
 * Park Occupation Activity
 * 
 * @author Ângela Igreja
 */
public class ParkOccupationActivity extends BaseSinglePaneActivity 
{
	
	@Override
    protected Fragment onCreatePane() 
	{
		ParkOccupationFragment parkOcccupation = new ParkOccupationFragment();
	
        return parkOcccupation;
    }
	

}

