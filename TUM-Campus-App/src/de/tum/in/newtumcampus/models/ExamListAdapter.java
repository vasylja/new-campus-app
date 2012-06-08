package de.tum.in.newtumcampus.models;

import java.util.List;

import de.tum.in.newtumcampus.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/** Custom UI adapter for a list of exams.
 * 
 * @author Vincenz Doelle
 * @review Daniel G. Mayr
 * @review Thomas Behrens */
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		// find and init UI
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.grades_listview, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.name);
			holder.tvGrade = (TextView) convertView.findViewById(R.id.grade);
			holder.tvDetails1 = (TextView) convertView.findViewById(R.id.tv1);
			holder.tvDetails2 = (TextView) convertView.findViewById(R.id.tv2);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// fill UI with data
		Exam exam = exams.get(position);
		if (exam != null) {
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
		TextView tvName;
		TextView tvGrade;
		TextView tvDetails1;
		TextView tvDetails2;
	}
}
