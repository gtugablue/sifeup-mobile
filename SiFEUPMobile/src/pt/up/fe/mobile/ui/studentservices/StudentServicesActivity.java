package pt.up.fe.mobile.ui.studentservices;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import pt.up.fe.mobile.ui.studentarea.StudentAreaFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class StudentServicesActivity extends BaseSinglePaneActivity {

	@Override
    protected Fragment onCreatePane() {
        return new StudentServicesFragment();
    }
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    } 

}