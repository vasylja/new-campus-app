package de.tum.in.newtumcampus.models;

import java.util.List;

import de.tum.in.newtumcampus.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Custom UI adapter for a list of exams.
 * 
 * @author Vincenz Doelle
 * @review Daniel G. Mayr
 * @review Thomas Behrens
 */
public class ExamListAdapter extends BaseAdapter {
	private static List<Exam> exams;

	private final LayoutInflater mInflater;

	private final Context context;

	public ExamListAdapter(Context context, List<Exam> results) {
		exams = results;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return exams.size();
	}

	@Override
	public Object getItem(int position) {
		return exams.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	private String[] semester;


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		semester = new String[getCount()];	
		
		// find and init UI
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.grades_listview, null);
			holder = new ViewHolder();
			holder.tvTest = (TextView) convertView.findViewById(R.id.test);
			holder.tvSemester = (TextView) convertView.findViewById(R.id.semester);
			holder.tvName = (TextView) convertView.findViewById(R.id.name);
			holder.tvGrade = (TextView) convertView.findViewById(R.id.grade);
			holder.tvDetails1 = (TextView) convertView.findViewById(R.id.tv1);
			holder.tvDetails2 = (TextView) convertView.findViewById(R.id.tv2);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		setSemesterString();
		// fill UI with data
		Exam exam = exams.get(position);
		if (exam != null) {
			holder.tvTest.setText(""+position);
			if (getSemesterCheck(position)) {
				holder.tvSemester.setText(cutSemester(exam.getSemester()));
			}
			holder.tvName.setText(exam.getCourse());
			holder.tvGrade.setText(exam.getGrade());
			holder.tvDetails1.setText(context.getString(R.string.date) + ": " + exam.getDate() + ", "
					+ context.getString(R.string.semester) + ": " + exam.getSemester());
			holder.tvDetails2.setText(context.getString(R.string.examiner) + ": " + exam.getExaminer() + ", "
					+ context.getString(R.string.mode) + ": " + exam.getModus());
		}

		return convertView;
	}

	static class ViewHolder {
		TextView tvTest;
		TextView tvSemester;
		TextView tvName;
		TextView tvGrade;
		TextView tvDetails1;
		TextView tvDetails2;
	}

	/**
	 * @author Florian Schulz
	 * @solves examSemester check 
	 * TODO Review Vasyl
	 */
	public boolean getSemesterCheck(int position) {
		if (position == 0){
			return true;
		} else if (semester[position].equals(semester[++position]) && (position <= getCount()-1)) {
			return false;
		} else {
			return true;
		}
	}
	
	public void setSemesterString(){
		for(int i = 0; i < exams.size()-1; i++){
			semester[i] = exams.get(i).getSemester();
		}
	}
	
	public String getSemesterString(){
		String a = "";
		for(int i = 0; i < exams.size()-1; i++){
			a = a + "i="+i+" "+semester[i] + " ";
		}
		return a;
	}

	/**
	 * @author Florian Schulz
	 * @solves Semesterstyle = "Summer semester 2012"
	 * @param semester
	 *            TODO Review Vasyl
	 */
	public String cutSemester(String semester) {
		String semesterName = context.getString(R.string.semester_n);
		int yearNumber = 0;
		if (semester.length() == 3) {
			try {
				yearNumber = Integer.parseInt(semester.substring(0, 2));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String year = " 20" + semester.substring(0, 2);
			if (semester.endsWith("W")) {
				semesterName = context.getString(R.string.semester_w);
				year = year.concat("/" + (++yearNumber));
				semester = semesterName.concat(year);
			} else if (semester.endsWith("S")) {
				semesterName = context.getString(R.string.semester_s);
				semester = semesterName.concat(year);
			}
			return semester;
		} else {
			// No Value
			return semester;
		}
	}
}
