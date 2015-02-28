package com.haedrian.haedrian.DummyContent;

/**
 * Created by hbll-rm on 2/26/2015.
 */
public class InvestProjectObject {
    private String mProjectTitle, mPersonName, mLocation, mProjectDescription;
    private int mFundingGoal;
    private int mCurrentAmountRaised;



    private int mDaysLeft;
    private int mDrawableImage;




    public InvestProjectObject(String projectTitle, String personName, String location, String projectDescription,
                               int fundingGoal, int currentAmountRaised, int daysLeft,  int drawableImage)
    {
        mProjectTitle = projectTitle;
        mPersonName = personName;
        mLocation = location;
        mProjectDescription = projectDescription;
        mFundingGoal = fundingGoal;
        mCurrentAmountRaised = currentAmountRaised;
        mDrawableImage = drawableImage;
    }


    public String getProjectTitle() {
        return mProjectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        mProjectTitle = projectTitle;
    }

    public String getPersonName() {
        return mPersonName;
    }

    public void setPersonName(String personName) {
        mPersonName = personName;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getProjectDescription() {
        return mProjectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        mProjectDescription = projectDescription;
    }

    public int getFundingGoal() {
        return mFundingGoal;
    }

    public void setFundingGoal(int fundingGoal) {
        mFundingGoal = fundingGoal;
    }

    public int getCurrentAmountRaised() {
        return mCurrentAmountRaised;
    }

    public void setCurrentAmountRaised(int currentAmountRaised) {
        mCurrentAmountRaised = currentAmountRaised;
    }

    public int getDrawableImage() {
        return mDrawableImage;
    }

    public void setDrawableImage(int drawableImage) {
        mDrawableImage = drawableImage;
    }

    public int getDaysLeft() {
        return mDaysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        mDaysLeft = daysLeft;
    }








}
