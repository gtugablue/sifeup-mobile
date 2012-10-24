package pt.up.beta.mobile.ui.subjects;

import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

/**
 * Subjects Activity
 * 
 * @author Ângela Igreja
 *
 */
public class TeachingServiceActivity extends BaseSinglePaneActivity {
    
	@Override
	protected Fragment onCreatePane() {
		return new TeachingServiceFragment();
	}

}
