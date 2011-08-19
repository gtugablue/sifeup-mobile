package pt.up.fe.mobile.ui.profile;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Profile Activity
 * 
 * @author Ângela Igreja
 */
public class ProfileActivity extends BaseSinglePaneActivity {
	
	public final static String PROFILE_TYPE = "pt.up.fe.mobile.ui.profile.PROFILE_TYPE"; 
	public final static String PROFILE_CODE = "pt.up.fe.mobile.ui.profile.PROFILE_CODE"; 

	public final static String PROFILE_TEACHER = "pt.up.fe.mobile.ui.profile.TEACHER";
	public final static String PROFILE_STUDENT = "pt.up.fe.mobile.ui.profile.STUDENT";
	public final static String PROFILE_EMPLOYEE = "pt.up.fe.mobile.ui.profile.EMPLOYEE";
	
	@Override
	protected Fragment onCreatePane() 
    {
    	Intent i = getIntent();
    	String type = i.getStringExtra(PROFILE_TYPE);
    	
    	if ( type == null || type.equals(PROFILE_STUDENT))
    		return new StudentProfileFragment();
    	else if ( type.equals(PROFILE_TEACHER))
    		return new TeacherProfileFragment();
      		else if ( type.equals(PROFILE_EMPLOYEE))
      			return new EmployeeProfileFragment();
    	
		return new StudentProfileFragment();
    	
    }
    
    /** Called when the activity is first created. */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }

}

