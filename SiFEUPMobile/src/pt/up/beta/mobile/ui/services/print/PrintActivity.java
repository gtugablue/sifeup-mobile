
package pt.up.beta.mobile.ui.services.print;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class PrintActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new PrintFragment();
    }

}
