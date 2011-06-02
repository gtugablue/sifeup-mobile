package pt.up.fe.mobile.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import external.com.google.android.apps.iosched.util.UIUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SubjectsFragment extends ListFragment implements OnItemClickListener {
	
	/** Contains all subscribed subjects */
	ArrayList<Subject> subjects = new ArrayList<Subject>();
	
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
        

        new SubjectsTask().execute();

    }

    /** Classe privada para a busca de dados ao servidor */
    private class SubjectsTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
			if ( getActivity() == null )
				 return;
        	if ( !result.equals("") )
        	{
				Log.e("Subjects","success");
				
				 try {
					 String[] from = new String[] {"chair", "time", "room"};
			         int[] to = new int[] { R.id.exam_chair, R.id.exam_time, R.id.exam_room};
				     // prepare the list of all records
			         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
			         for(Subject s : subjects){
			             HashMap<String, String> map = new HashMap<String, String>();
			             map.put("chair", s.namePt);
			             map.put("time", s.acronym + " (" + s.nameEn + ")");
			             map.put("room", getString(R.string.subjects_year,s.year, s.semester));
			             fillMaps.add(map);
			         }
			         // fill in the grid_item layout
			         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_exam, from, to);
			         setListAdapter(adapter);
			         getListView().setOnItemClickListener(SubjectsFragment.this);
			         Log.e("JSON", "subjects visual list loaded");
				 }
				 catch (Exception ex){
					 ex.printStackTrace();
					 if ( getActivity() != null )
							Toast.makeText(getActivity(), "F*** Fragments", Toast.LENGTH_LONG).show();

				 }
    		}
			else{	
				Log.e("Login","error");
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
        	if ( getActivity() != null ) 
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        }

		@Override
		protected String doInBackground(Void ... theVoid) {
			String page = "";
		  	try {
	    			page = SifeupAPI.getSubjectsReply(
								SessionManager.getInstance().getLoginCode(),
								"2010");
	    		if(	SifeupAPI.JSONError(page))
	    		{
		    		 return "";
	    		}
	    		
	    		JSONSubjects(page);
	    		
				return "Success";
				
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
    
    
    /**
     * 
     * Represents a subject.
     * Holds all data about it.
     *
     */
    private class Subject{
		public String acronym; // "EICXXXX"
		public int year; // 3
		public String namePt; // Sistemas Distribuidos
		public String nameEn; // Distributed Systems
		public String semester; // 2S
    }
    
    /** 
	 * Subject Parser
	 * Stores Subjects in SubjectFragment.subjects
	 * Returns true in case of correct parsing.
	 * 
	 * @param page
	 * @return boolean
	 * @throws JSONException
	 */
    public boolean JSONSubjects(String page) throws JSONException{
    	JSONObject jObject = new JSONObject(page);
    	
    	// clear old schedule
    	this.subjects.clear();
    	
    	if(jObject.has("inscricoes")){
    		Log.e("JSON", "founded disciplines");
    		JSONArray jArray = jObject.getJSONArray("inscricoes");
    		
    		// if year number is wrong, returns false
    		if(jArray.length()==0)
    			return false;
    		
    		// iterate over jArray
    		for(int i = 0; i < jArray.length(); i++){
    			// new JSONObject
    			JSONObject jSubject = jArray.getJSONObject(i);
    			// new Block
    			Subject subject = new Subject();
    			
    			if(jSubject.has("dis_codigo")) subject.acronym = jSubject.getString("dis_codigo"); // Monday is index 0
    			if(jSubject.has("ano_curricular")) subject.year = jSubject.getInt("ano_curricular");
    			if(jSubject.has("nome")) subject.namePt = jSubject.getString("nome");
    			if(jSubject.has("name")) subject.nameEn = jSubject.getString("name");
    			if(jSubject.has("periodo")) subject.semester = jSubject.getString("periodo");
    			
    			// add block to schedule
    			this.subjects.add(subject);
    		}
    		Log.e("JSON", "loaded disciplines");
    		return true;
    	}
    	Log.e("JSON", "disciplines not found");
    	return false;
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		StringBuilder url = new StringBuilder("https://www.fe.up.pt/si/disciplinas_geral.formview?");
		url.append("p_cad_codigo="+subjects.get(position).acronym);
		int secondYear = UIUtils.secondYearOfSchoolYear();
		int firstYear = secondYear -1;
		url.append("&p_ano_lectivo=" + firstYear +"/" + secondYear);
		url.append("&p_periodo=" +subjects.get(position).semester );
		Uri uri = Uri.parse( url.toString() );
		startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
	}
	
}