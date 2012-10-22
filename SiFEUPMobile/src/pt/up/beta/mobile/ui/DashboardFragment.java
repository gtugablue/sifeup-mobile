
package pt.up.beta.mobile.ui;


import pt.up.beta.mobile.tracker.AnalyticsUtils;
import pt.up.beta.mobile.ui.facilities.FeupFacilitiesActivity;
import pt.up.beta.mobile.ui.friends.FriendsActivity;
import pt.up.beta.mobile.ui.news.NewsActivity;
import pt.up.beta.mobile.ui.notifications.NotificationsActivity;
import pt.up.beta.mobile.ui.personalarea.PersonalAreaActivity;
import pt.up.beta.mobile.ui.services.ServicesActivity;
import pt.up.beta.mobile.R;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Launch the activity's available from the main menu.
 * 
 * @author Ângela Igreja 
 *
 */
public class DashboardFragment extends Fragment {

    public void fireTrackerEvent(String label) {
        AnalyticsUtils.getInstance(getActivity()).trackEvent(
                "Home Screen Dashboard", "Click", label, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container);

        // Attach event handlers
        root.findViewById(R.id.home_btn_student_area).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Student Area");
                startActivity(new Intent(getActivity(), PersonalAreaActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
            
        });
        
        root.findViewById(R.id.home_btn_student_services).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Student Services");
                startActivity(new Intent(getActivity(), ServicesActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                    
            }
            
        });
     
        root.findViewById(R.id.home_btn_friends).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Profile");
                startActivity(new Intent(getActivity(), FriendsActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                    
            }
            
        });

        root.findViewById(R.id.home_btn_news).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("News");
                startActivity(new Intent(getActivity(),NewsActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        
        
        root.findViewById(R.id.home_btn_map).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Map");
                startActivity(new Intent(getActivity(),FeupFacilitiesActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        
       root.findViewById(R.id.home_btn_notifications).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Notifications");
                startActivity(new Intent(getActivity(),NotificationsActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        
        return root;
    }
}