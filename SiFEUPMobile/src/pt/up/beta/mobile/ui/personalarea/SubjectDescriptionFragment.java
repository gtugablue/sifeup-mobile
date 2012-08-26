package pt.up.beta.mobile.ui.personalarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraProvider;
import pt.up.beta.mobile.content.tables.SubjectsTable;
import pt.up.beta.mobile.datatypes.Subject;
import pt.up.beta.mobile.datatypes.Subject.Book;
import pt.up.beta.mobile.datatypes.Subject.EvaluationComponent;
import pt.up.beta.mobile.datatypes.Subject.Software;
import pt.up.beta.mobile.datatypes.Subject.Teacher;
import pt.up.beta.mobile.datatypes.SubjectFiles;
import pt.up.beta.mobile.datatypes.SubjectFiles.File;
import pt.up.beta.mobile.datatypes.SubjectFiles.Folder;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.sifeup.SubjectUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.dialogs.DownloaderFragment;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import pt.up.beta.mobile.ui.webclient.WebviewActivity;
import pt.up.beta.mobile.ui.webclient.WebviewFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

import external.com.google.android.apps.iosched.util.UIUtils;

public class SubjectDescriptionFragment extends BaseFragment implements
		OnPageChangeListener, ResponseCommand, LoaderCallbacks<Cursor> {

	public final static String SUBJECT_CODE = "pt.up.fe.mobile.ui.studentarea.SUBJECT_CODE";
	public final static String SUBJECT_YEAR = "pt.up.fe.mobile.ui.studentarea.SUBJECT_YEAR";
	public final static String SUBJECT_PERIOD = "pt.up.fe.mobile.ui.studentarea.SUBJECT_PERIOD";

	private String code;
	private String year;
	private String period;
	private Subject subject;
	private SubjectFiles subjectFiles;
	/** */
	private PagerSubjectAdapter pagerAdapter;

	/** */
	private LayoutInflater layoutInflater;

	/** */
	private ViewPager viewPager;

	/** */
	private TabPageIndicator indicator;

	private int currentPage = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		code = args.get(SUBJECT_CODE).toString();
		year = args.get(SUBJECT_YEAR).toString();
		period = args.get(SUBJECT_PERIOD).toString();
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layoutInflater = inflater;
		View root = inflater.inflate(R.layout.subject_description,
				getParentContainer(), true);
		viewPager = (ViewPager) root.findViewById(R.id.pager_subject);
		viewPager.setAdapter(new PagerSubjectAdapter());
		// Find the indicator from the layout
		indicator = (TabPageIndicator) root
				.findViewById(R.id.indicator_subject);
		// Create our custom adapter to supply pages to the viewpager.
		pagerAdapter = new PagerSubjectAdapter();
		viewPager.setAdapter(pagerAdapter);
		indicator.setViewPager(viewPager);
		// Set the indicator as the pageChangeListener
		indicator.setOnPageChangeListener(this);
		return getParentContainer();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// task = SubjectUtils.getSubjectReply(code, year, period, this);
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.subject_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_subject_schedule) {
			Intent i = new Intent(getActivity(), ScheduleActivity.class);
			i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
					ScheduleFragment.SCHEDULE_UC);
			i.putExtra(ScheduleFragment.SCHEDULE_CODE, code);
			i.putExtra(
					Intent.EXTRA_TITLE,
					getString(R.string.title_schedule_arg,
							subject != null ? subject.getNamePt() : code));
			startActivity(i);
			return true;
		}
		if (item.getItemId() == R.id.menu_go_to_subject_sigarra) {
			Intent i = new Intent(getActivity(), WebviewActivity.class);
			i.putExtra(WebviewFragment.URL_INTENT,
					SifeupAPI.getSubjectSigarraUrl(code, year, period));
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			goLogin();
			return;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		if (subject == null) {
			subject = (Subject) results[0];
			String title = subject.getNamePt();
			if (!UIUtils.isLocalePortuguese()
					&& subject.getNameEn().trim().length() > 0)
				title = subject.getNameEn();
			getSherlockActivity().getSupportActionBar().setTitle(title);
			SubjectUtils.getSubjectContentReply(code, year, period, this);
			return;
		}
		if (subjectFiles == null) {
			subjectFiles = (SubjectFiles) results[0];
			pagerAdapter.notifyDataSetChanged();
			// Start at a custom position
			indicator.setCurrentItem(0);
			indicator.notifyDataSetChanged();
			showMainScreen();
		}
	}

	/**
	 * Pager Subject Adapter
	 * 
	 * @author Ângela Igreja
	 * 
	 */
	class PagerSubjectAdapter extends PagerAdapter implements TitleProvider {
		@Override
		public String getTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.objectives);
			case 1:
				return getString(R.string.content);
			case 2:
				return getString(R.string.teachers);
			case 3:
				return getString(R.string.bibliography);
			case 4:
				return getString(R.string.software);
			case 5:
				return getString(R.string.metodology);
			case 6:
				return getString(R.string.evaluation);
			case 7:
				return getString(R.string.admission_exams);
			case 8:
				return getString(R.string.final_grade);
			case 9:
				return getString(R.string.special_evaluation);
			case 10:
				return getString(R.string.improvement_classification);
			case 11:
				return getString(R.string.comments);
			case 12:
				return getString(R.string.subject_content);
			}

			return "";
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);

		}

		@Override
		public int getCount() {
			if (subject == null || subjectFiles == null)
				return 0;
			return 13;
		}

		@Override
		public Object instantiateItem(final View collection, int position) {
			View root = null;
			switch (position) {
			case 0: {
				if (subject.getContent() != null
						&& !subject.getContent().equals("")) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					final TextView text = (TextView) root
							.findViewById(R.id.content);
					text.setText(subject.getContent());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 1: {
				if (subject.getObjectives() != null
						&& !subject.getObjectives().equals("")) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					final TextView text = (TextView) root
							.findViewById(R.id.content);
					text.setText(subject.getObjectives());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 2: {
				if (subject.getTeachers() != null
						&& subject.getTeachers().size() != 0) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					ListView list = (ListView) root
							.findViewById(R.id.generic_list);
					String[] from = new String[] { "name" };
					int[] to = new int[] { R.id.teacher_name };
					// prepare the list of all records
					List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

					for (Teacher t : subject.getTeachers()) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(from[0], t.getName());
						fillMaps.add(map);
					}

					SimpleAdapter adapter = new SimpleAdapter(getActivity(),
							fillMaps, R.layout.list_item_subject_teacher, from,
							to);
					list.setAdapter(adapter);
					list.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int pos, long id) {
							Teacher b = subject.getTeachers().get(pos);
							Intent i = new Intent(getActivity(),
									ProfileActivity.class);
							i.putExtra(ProfileActivity.PROFILE_CODE,
									b.getCode());
							i.putExtra(ProfileActivity.PROFILE_TYPE,
									ProfileActivity.PROFILE_EMPLOYEE);
							i.putExtra(Intent.EXTRA_TITLE, b.getName());
							startActivity(i);

						}
					});
				} else
					root = getEmptyScreen(getString(R.string.no_data));

				break;
			}
			case 3: {
				if (subject.getBibliography() != null
						&& subject.getBibliography().size() != 0) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					ListView listBooks = (ListView) root
							.findViewById(R.id.generic_list);
					final String[] from = new String[] { "typeDescription",
							"authors", "title", "link", "isbn" };

					final int[] to = new int[] { R.id.typeDescription,
							R.id.authors, R.id.title, R.id.link, R.id.isbn };
					// prepare the list of all records
					final List<HashMap<String, String>> fillMapsBooks = new ArrayList<HashMap<String, String>>();

					for (Book b : subject.getBibliography()) {
						final HashMap<String, String> map = new HashMap<String, String>();
						map.put(from[0], b.getTypeDescription());
						map.put(from[1], b.getAuthors());
						map.put(from[2], b.getTitle());
						map.put(from[3], b.getLink());
						map.put(from[4], b.getIsbn());
						fillMapsBooks.add(map);
					}
					final SimpleAdapter adapterBooks = new SimpleAdapter(
							getActivity(), fillMapsBooks,
							R.layout.list_item_subject_book, from, to);
					listBooks.setAdapter(adapterBooks);
					listBooks.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int pos, long id) {
							Book b = subject.getBibliography().get(pos);
							if (b.getLink() == null)
								return;
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(b.getLink()));
							startActivity(i);

						}
					});
				} else
					root = getEmptyScreen(getString(R.string.no_data));

				break;
			}
			case 4: {
				if (subject.getSoftware() != null
						&& subject.getSoftware().size() != 0) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					ListView listSoftware = (ListView) root
							.findViewById(R.id.generic_list);
					String[] fromSoftware = new String[] { "name",
							"description" };
					int[] toSoftware = new int[] { R.id.name, R.id.description };
					// prepare the list of all records
					List<HashMap<String, String>> fillMapsSoftware = new ArrayList<HashMap<String, String>>();

					for (Software s : subject.getSoftware()) {
						HashMap<String, String> map = new HashMap<String, String>();

						map.put(fromSoftware[0], s.getName());
						map.put(fromSoftware[1], s.getDescription());
						fillMapsSoftware.add(map);
					}
					SimpleAdapter adapterSoftware = new SimpleAdapter(
							getActivity(), fillMapsSoftware,
							R.layout.list_item_subject_software, fromSoftware,
							toSoftware);
					listSoftware.setAdapter(adapterSoftware);
				} else
					root = getEmptyScreen(getString(R.string.no_data));

				break;
			}
			case 5: {
				if (subject.getMetodology() != null
						&& !subject.getMetodology().equals("")) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView metodologyText = (TextView) root
							.findViewById(R.id.content);
					metodologyText.setText(subject.getMetodology());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 6: {
				if (subject.getSoftware() != null
						&& subject.getSoftware().size() != 0) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					final ListView listEvaluation = (ListView) root
							.findViewById(R.id.generic_list);

					final String[] from = new String[] { "description",
							"typeDesc" };
					final int[] to = new int[] { R.id.description,
							R.id.typeDesc };
					// prepare the list of all records
					final List<HashMap<String, String>> fillMapsEvaluation = new ArrayList<HashMap<String, String>>();

					for (EvaluationComponent e : subject.getEvaluation()) {
						final HashMap<String, String> map = new HashMap<String, String>();

						map.put(from[0], e.getDescription());
						map.put(from[0], e.getTypeDesc());
						fillMapsEvaluation.add(map);

					}
					final SimpleAdapter adapterEvaluation = new SimpleAdapter(
							getActivity(), fillMapsEvaluation,
							R.layout.list_item_subject_evaluation_component,
							from, to);
					listEvaluation.setAdapter(adapterEvaluation);
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 7: {
				if (subject.getFrequenceCond() != null
						&& !subject.getFrequenceCond().equals("")) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView admissionExamsText = (TextView) root
							.findViewById(R.id.content);
					admissionExamsText.setText(subject.getFrequenceCond());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 8: {
				if (subject.getEvaluationFormula() != null
						&& !subject.getEvaluationFormula().equals("")) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView finalGradeText = (TextView) root
							.findViewById(R.id.content);
					finalGradeText.setText(subject.getEvaluationFormula());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 9: {
				if (subject.getEvaluationProc() != null
						&& !subject.getEvaluationProc().equals("")) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView specialEvaluationText = (TextView) root
							.findViewById(R.id.content);
					specialEvaluationText.setText(subject.getEvaluationProc());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 10: {
				if (subject.getImprovementProc() != null
						&& !subject.getImprovementProc().equals("")) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView improvementClassificationText = (TextView) root
							.findViewById(R.id.content);
					improvementClassificationText.setText(subject
							.getImprovementProc());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 11: {
				if (subject.getObservations() != null
						&& !subject.getObservations().equals("")) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView commentsText = (TextView) root
							.findViewById(R.id.content);
					commentsText.setText(subject.getObservations());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 12: {
				if (subjectFiles != null
						&& (subjectFiles.getCurrentFolder().getFolders().size() != 0 || subjectFiles
								.getCurrentFolder().getFiles().size() != 0)) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					root.setTag(getString(R.string.subject_content));
					final ListView list = (ListView) root
							.findViewById(R.id.generic_list);
					list.setAdapter(getSubjectContentAdapter());
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> list, View item,
								int position, long id) {
							if (position >= subjectFiles.getCurrentFolder()
									.getFolders().size()) {
								// launch download;
								File toDownload = subjectFiles
										.getCurrentFolder()
										.getFiles()
										.get(position
												- subjectFiles
														.getCurrentFolder()
														.getFolders().size());
								if (toDownload.getUrl() == null
										|| toDownload.getUrl().trim().length() == 0) {
									DownloaderFragment
											.newInstance(
													toDownload.getName(),
													SifeupAPI
															.getSubjectFileContents(Integer
																	.toString(toDownload
																			.getCode())),
													toDownload.getFilename(),
													null, toDownload.getSize())
											.show(getFragmentManager(),
													"Downloader");
								} else {
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse(toDownload.getUrl()));
									startActivity(i);
								}
								return;
							}
							subjectFiles.setCurrentFolder(subjectFiles
									.getCurrentFolder().getFolders()
									.get(position));
							View contents = viewPager
									.findViewWithTag(getString(R.string.subject_content));
							((ListView) contents
									.findViewById(R.id.generic_list))
									.setAdapter(getSubjectContentAdapter());

						}
					});
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}

			}
			if (root == null)
				root = getEmptyScreen(getString(R.string.no_data));
			((ViewPager) collection).addView(root, 0);
			return root;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

	}

	private ListAdapter getSubjectContentAdapter() {
		String[] from = new String[] { "name" };
		int[] to = new int[] { R.id.folder_name };

		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

		for (Folder f : subjectFiles.getCurrentFolder().getFolders()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[0], f.getName());
			fillMaps.add(map);
		}
		for (File f : subjectFiles.getCurrentFolder().getFiles()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[0], f.getName());
			fillMaps.add(map);
		}
		return new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_folder, from, to);
	}

	public void onBackPressed() {
		if (currentPage == 12
				&& subjectFiles.getCurrentFolder().getParent() != null) {
			subjectFiles.setCurrentFolder(subjectFiles.getCurrentFolder()
					.getParent());
			View contents = viewPager
					.findViewWithTag(getString(R.string.subject_content));
			((ListView) contents.findViewById(R.id.generic_list))
					.setAdapter(getSubjectContentAdapter());
		} else {
			if (getActivity() != null)
				getActivity().finish();
		}
	}

	// Unused
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// Unused
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int page) {
		currentPage = page;
	}

	protected void onRepeat() {
		showLoadingScreen();
		if (subject == null) {
			task = SubjectUtils.getSubjectReply(code, year, period, this);
			return;
		}
		if (subjectFiles == null) {
			SubjectUtils.getSubjectContentReply(code, year, period, this);
			return;
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new CursorLoader(getActivity(),
				SigarraProvider.CONTENT_SUBJECTS_URI, new String[] {
						SubjectsTable.COLUMN_CONTENT,
						SubjectsTable.COLUMN_FILES },
				SubjectsTable.COLUMN_USER_CODE + "=? AND "
						+ SubjectsTable.COLUMN_CODE + "=? AND "
						+ SubjectsTable.COLUMN_PERIOD + "=? AND "
						+ SubjectsTable.COLUMN_YEAR + "=?", new String[] {
						SessionManager.getInstance(getActivity())
								.getLoginCode(), code, period, year }, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try {
			if (cursor.moveToFirst()) {
				subject = new Subject().JSONSubject(cursor.getString(cursor
						.getColumnIndex(SubjectsTable.COLUMN_CONTENT)));
				String title = subject.getNamePt();
				if (!UIUtils.isLocalePortuguese()
						&& !TextUtils.isEmpty(subject.getNameEn().trim()))
					title = subject.getNameEn();
				getSherlockActivity().getSupportActionBar().setTitle(title);
				subjectFiles = new SubjectFiles().JSONSubjectContent(cursor
						.getString(cursor
								.getColumnIndex(SubjectsTable.COLUMN_CONTENT)));
				pagerAdapter.notifyDataSetChanged();
				// Start at a custom position
				indicator.setCurrentItem(0);
				indicator.notifyDataSetChanged();
				showMainScreen();
			} else
				showEmptyScreen(getString(R.string.general_error));
		} catch (JSONException e) {
			e.printStackTrace();
			showEmptyScreen(getString(R.string.general_error));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
