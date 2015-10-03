package pt.up.beta.mobile.ui.personalarea;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;


/**
 * Exam Description Activity
 * 
 * @author Ângela Igreja
 */
public class ExamDescriptionActivity extends BaseSinglePaneActivity 
{
	
	@Override
    protected Fragment onCreatePane() 
	{
        return new ExamDescriptionFragment();
    }
	
}
