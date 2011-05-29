package pt.up.fe.mobile.ui.tuition;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class TuitionHistoryActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
    	return new TuitionHistoryFragment();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }  

}
