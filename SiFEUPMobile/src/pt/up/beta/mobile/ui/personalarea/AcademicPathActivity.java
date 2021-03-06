package pt.up.beta.mobile.ui.personalarea;

import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

/**
 * Academic Path Activity and launch the object {@link AcademicPathFragment}.
 * 
 * @author Ângela Igreja
 *
 */
public class AcademicPathActivity extends BaseSinglePaneActivity {

	@Override
	protected Fragment onCreatePane() {
		return new AcademicPathFragment();
	}

}
