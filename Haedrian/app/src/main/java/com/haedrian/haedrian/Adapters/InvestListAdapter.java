package com.haedrian.haedrian.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haedrian.haedrian.DummyContent.InvestProjectObject;
import com.haedrian.haedrian.DummyContent.ProjectContent;
import com.haedrian.haedrian.R;

import java.util.ArrayList;

/**
 * Created by hbll-rm on 2/26/2015.
 */
public class InvestListAdapter extends RecyclerView.Adapter<InvestListAdapter.MyViewHolder> {
    private final LayoutInflater inflater;
    ArrayList<InvestProjectObject> listOfProjects;
    Context mContext;

    public InvestListAdapter(Context context) {
        listOfProjects = new ArrayList<>();
        mContext = context;

        inflater = LayoutInflater.from(mContext);

        // Set up all the dummy views with project content
        for (int i = 0; i < ProjectContent.projectTitle.length; i++) {
            String mProjectTitle = ProjectContent.projectTitle[i];
            String mPersonName = ProjectContent.personName[i];
            String mLocation = ProjectContent.projectLocation[i];
            String mProjectDescription = ProjectContent.projectDescription[i];
            int mFundingGoal = ProjectContent.fundingGoal[i];
            int mCurrentAmountRaised = ProjectContent.currentAmountRaised[i];
            int mdaysLeft = ProjectContent.daysLeft[i];
            int mDrawableImage = ProjectContent.projectImage[i];

            InvestProjectObject newProject = new InvestProjectObject(
                    mProjectTitle, mPersonName, mLocation, mProjectDescription,
                    mFundingGoal, mCurrentAmountRaised, mdaysLeft, mDrawableImage
            );

            listOfProjects.add(newProject);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.view_invest_list, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        InvestProjectObject project = listOfProjects.get(position);

        myViewHolder.titleView.setText(project.getProjectTitle());
        myViewHolder.locationView.setText(project.getLocation());
        myViewHolder.goalView.setText(String.valueOf(project.getFundingGoal()));
        myViewHolder.daysLeftView.setText(String.valueOf(project.getDaysLeft()));
        myViewHolder.raisedView.setText(String.valueOf(project.getCurrentAmountRaised()));
        myViewHolder.projectPicture.setImageResource(project.getDrawableImage());
    }

    @Override
    public int getItemCount() {
        return listOfProjects.size();
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.view_invest_list, parent, false);
        TextView titleView = (TextView) row.findViewById(R.id.titleView);
        TextView raisedView = (TextView) row.findViewById(R.id.raisedView);
        TextView goalView = (TextView) row.findViewById(R.id.goalRaisView);
        TextView daysLeftView = (TextView) row.findViewById(R.id.daysLeftGoal);
        TextView locationView = (TextView) row.findViewById(R.id.locationView);

        InvestProjectObject project = listOfProjects.get(position);
        titleView.setText(project.getProjectTitle());
        raisedView.setText(project.getCurrentAmountRaised());
        goalView.setText(project.getFundingGoal());
        daysLeftView.setText(project.getDaysLeft());
        locationView.setText(project.getLocation());

        return row;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        TextView raisedView;
        TextView goalView;
        TextView daysLeftView;
        TextView locationView;
        ImageView projectPicture;

        public MyViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.titleView);
            raisedView = (TextView) itemView.findViewById(R.id.raisedView);
            goalView = (TextView) itemView.findViewById(R.id.goalRaisView);
            daysLeftView = (TextView) itemView.findViewById(R.id.daysLeftGoal);
            locationView = (TextView) itemView.findViewById(R.id.locationView);
            projectPicture = (ImageView) itemView.findViewById(R.id.projectImageView);

        }
    }
}
