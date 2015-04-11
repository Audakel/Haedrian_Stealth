package com.lenddo.sdk.models;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.lenddo.sdk.core.FieldPreProcessListener;
import com.lenddo.widget.address.models.Address;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FormDataCollector {

    private static final String TAG = FormDataCollector.class.getName();
    private String lastName;
    private String firstName;
    private String email;
    private int birthDay;
    private HashMap<String, Object> fields = new HashMap<String, Object>();
    private String userId;
    private AuthorizationStatus authorizationStatus;
    private String partnerScriptId;
    private String middleName;
    private String employerName;
    private String mobilePhone;
    private String homePhone;
    private String startEmploymentDate;
    private String endEmploymentDate;
    private String motherFirstName;
    private String motherMiddleName;
    private String motherLastName;
    private String universityName;
    private Address address = new Address();

    public int getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(int birthDay) {
        this.birthDay = birthDay;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    private int birthMonth;
    private int birthYear;

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setDateOfBirth(DatePicker dateOfBirth) {
        this.birthDay = dateOfBirth.getDayOfMonth();
        this.birthMonth = dateOfBirth.getMonth();
        this.birthYear = dateOfBirth.getYear();
    }

    public void setDateOfBirth(String dateOfBirth) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        try {
            Date date =sdf.parse(dateOfBirth);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            this.birthDay = c.get(Calendar.DAY_OF_MONTH);
            this.birthMonth = c.get(Calendar.MONTH) + 1;
            this.birthYear = c.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void validate() {
    }

    public void putField(String key, String value) {
        fields.put(key, value);
    }

    public void putField(String key, int value) {
        fields.put(key, value);
    }

    public void putField(String key, long value) {
        fields.put(key, value);
    }

    public void putField(String key, boolean value) {
        fields.put(key, value);
    }

    public Object getField(String key) {
        return fields.get(key);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public HashMap<String, Object> getFields() {
        return fields;
    }

    public void setAuthorizationStatus(AuthorizationStatus authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }

    public AuthorizationStatus getAuthorizationStatus() {
        return authorizationStatus;
    }


    public Object autoNull(Object value) {
        if (value == null) return JSONObject.NULL;
        return value;
    }

    public String toVerificationData() {
        try {
            JSONObject verificationData = new JSONObject();
            JSONObject name = new JSONObject();
            name.put("last", autoNull(getLastName()));
            name.put("middle", autoNull(getMiddleName()));
            name.put("first", autoNull(getFirstName()));
            verificationData.put("name", name);

            if (getBirthDay() == 0) {
                verificationData.put("date_of_birth", JSONObject.NULL);
            } else {
                verificationData.put("date_of_birth", getBirthYear() + "-" + getBirthMonth() + "-" + getBirthDay());
            }
            verificationData.put("employer", getEmployerName());

            JSONObject phone = new JSONObject();
            phone.put("mobile", autoNull(getMobilePhone()));
            phone.put("home", autoNull(getHomePhone()));
            verificationData.put("phone", phone);

            JSONObject employmentPeriod = new JSONObject();
            employmentPeriod.put("start_date", autoNull(getStartEmploymentDate()));
            employmentPeriod.put("end_date", autoNull(getEndEmploymentDate()));
            verificationData.put("employment_period", employmentPeriod);

            JSONObject mothersMaidenName = new JSONObject();

            mothersMaidenName.put("first", autoNull(getMotherFirstName()));
            mothersMaidenName.put("middle", autoNull(getMotherMiddleName()));
            mothersMaidenName.put("last", autoNull(getMotherLastName()));
            verificationData.put("mothers_maiden_name", mothersMaidenName);

            verificationData.put("university", autoNull(getUniversityName()));
            verificationData.put("email", autoNull(getEmail()));

            if (address!=null) {
                JSONObject addressObj = new JSONObject();
                addressObj.put("line_1", autoNull(address.getHouseNumber() + ", " + address.getStreet()));
                addressObj.put("line_2", autoNull(address.getBarangay()));
                addressObj.put("city", autoNull(address.getCity()));
                addressObj.put("country_code", autoNull(address.getCountryCode()));
                addressObj.put("postal_code", autoNull(address.getPostalCode()));
                addressObj.put("administrative_division", autoNull(address.getProvince()));
                if (address.getLatLng()!=null) {
                    addressObj.put("latitude", address.getLatLng().latitude);
                    addressObj.put("longitude", address.getLatLng().longitude);
                }
                verificationData.put("address", addressObj);
            }

            return verificationData.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toVerificationJson() {
        try {
            JSONObject verificationData = new JSONObject();

            verificationData.put("lastname", autoNull(getLastName()));
            verificationData.put("middlename", autoNull(getMiddleName()));
            verificationData.put("firstname", autoNull(getFirstName()));

            if (getBirthDay() == 0) {
                verificationData.put("birthdate", JSONObject.NULL);
            } else {
                verificationData.put("birthdate", getBirthYear() + "-" + getBirthMonth() + "-" + getBirthDay());
            }
            verificationData.put("employer", getEmployerName());

            verificationData.put("mobilephone", autoNull(getMobilePhone()));
            verificationData.put("homephone", autoNull(getHomePhone()));

            verificationData.put("employment_startdate", autoNull(getStartEmploymentDate()));
            verificationData.put("employment_enddate", autoNull(getEndEmploymentDate()));

            verificationData.put("mother_firstname", autoNull(getMotherFirstName()));
            verificationData.put("mother_middlename", autoNull(getMotherMiddleName()));
            verificationData.put("mother_lastname", autoNull(getMotherLastName()));
            verificationData.put("university", autoNull(getUniversityName()));
            verificationData.put("email", autoNull(getEmail()));

            if (address!=null) {
                verificationData.put("address_line_1", autoNull(address.getHouseNumber() + ", " + address.getStreet()));
                verificationData.put("address_line_2", autoNull(address.getBarangay()));
                verificationData.put("address_city", autoNull(address.getCity()));
                verificationData.put("address_country_code", autoNull(address.getCountryCode()));
                verificationData.put("address_postal_code", autoNull(address.getPostalCode()));
                verificationData.put("address_administrative_division", autoNull(address.getProvince()));
                if (address.getLatLng()!=null) {
                    verificationData.put("address_latitude", address.getLatLng().latitude);
                    verificationData.put("address_longitude", address.getLatLng().longitude);
                }
            }

            return verificationData.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toJson() {

        JSONObject rawForm = new JSONObject();

        try {
            rawForm.put("c_id", getUserId());
            rawForm.put("ps_id", getPartnerScriptId());
            rawForm.put("first_name", getFirstName());
            rawForm.put("middle_name", getMiddleName());
            rawForm.put("email", getEmail());
            rawForm.put("last_name", getLastName());
            rawForm.put("mobile_number", getMobilePhone());
            rawForm.put("employment_start_date", getStartEmploymentDate());
            rawForm.put("employment_end_date", getEndEmploymentDate());
            rawForm.put("mother_first_name", getMotherFirstName());
            rawForm.put("mother_middle_name", getMotherMiddleName());
            rawForm.put("mother_last_name", getMotherLastName());
            rawForm.put("university", getUniversityName());

            JSONObject dateOfBirth = new JSONObject();
            dateOfBirth.put("day", getBirthDay());
            dateOfBirth.put("month", getBirthMonth());
            dateOfBirth.put("year", getBirthYear());
            rawForm.put("birthdate", dateOfBirth);

            //custom fields
            for (String key : getFields().keySet()) {
                rawForm.put(key, getField(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rawForm.toString();

    }

    public void evaluateGroup(ViewGroup group, ArrayList<View> collectFields) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                collectFields.add(view);
            } else if (view instanceof Spinner) {
                collectFields.add(view);
            } else if (view instanceof ViewGroup) {
                evaluateGroup((ViewGroup) view, collectFields);
            }
        }
    }

    public void collect(Activity sampleActivity, int formContainer) {
        collect(sampleActivity,formContainer, null);
    }

    public void collect(Activity sampleActivity, int formContainer, FieldPreProcessListener listener) {
        ViewGroup group = (ViewGroup) sampleActivity.findViewById(formContainer);
        collect(listener, group);
    }

    public void collect(FieldPreProcessListener listener, ViewGroup group) {
        ArrayList<View> collectFields = new ArrayList<View>();
        evaluateGroup(group, collectFields);
        for (View v : collectFields) {
            Object tag = v.getTag();
            Object value;
            if (listener != null) {
                if (listener.process(this, v)) continue;
            }
            if (tag != null && tag instanceof String) {
                String name = tag.toString();

                String stringValue = null;
                if (v instanceof EditText) {
                    EditText editText = (EditText) v;
                    stringValue = editText.getText().toString();
                    putField(name, stringValue);
                } else if (v instanceof Spinner) {
                    Spinner spinner = (Spinner) v;
                    stringValue = spinner.getSelectedItem().toString();
                    putField(name, stringValue);
                }
                Log.d(TAG, "processed " + name);
            }
        }
    }

    public String getPartnerScriptId() {
        return partnerScriptId;
    }

    public void setPartnerScriptId(String partnerScriptId) {
        this.partnerScriptId = partnerScriptId;
    }

    public String getMiddleName() {
        return middleName;
    }


    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEmployerName() {
        return employerName;
    }


    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }


    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getHomePhone() {
        return homePhone;
    }


    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getStartEmploymentDate() {
        return startEmploymentDate;
    }


    public void setStartEmploymentDate(String startEmploymentDate) {
        this.startEmploymentDate = startEmploymentDate;
    }

    public String getEndEmploymentDate() {
        return endEmploymentDate;
    }

    public void setEndEmploymentDate(String endEmploymentDate) {
        this.endEmploymentDate = endEmploymentDate;
    }

    public String getMotherFirstName() {
        return motherFirstName;
    }

    public void setMotherFirstName(String motherFirstName) {
        this.motherFirstName = motherFirstName;
    }

    public String getMotherMiddleName() {
        return motherMiddleName;
    }

    public void setMotherMiddleName(String motherMiddleName) {
        this.motherMiddleName = motherMiddleName;
    }

    public String getMotherLastName() {
        return motherLastName;
    }

    public void setMotherLastName(String motherLastName) {
        this.motherLastName = motherLastName;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public void setAddress(com.lenddo.widget.address.models.Address address) {
        this.address = address;
    }

    public com.lenddo.widget.address.models.Address getAddress() {
        return address;
    }

    public String getHouseNumber() {
        return address.getHouseNumber();
    }

    public void setHouseNumber(String houseNumber) {
        this.address.setHouseNumber(houseNumber);
    }

    public String getStreet() {
        return this.address.getStreet();
    }

    public void setStreet(String street) {
        this.address.setStreet(street);
    }

    public String getBarangay() {
        return this.address.getBarangay();
    }

    public void setBarangay(String barangay) {
        this.address.setBarangay(barangay);
    }

    public Object getProvince() {
        return address.getProvince();
    }

    public void setProvince(String province) {
        address.setProvince(province);
    }
}
