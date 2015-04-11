package com.lenddo.data.models;

import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by joseph on 5/27/14.
 */
public class AndroidContact {
    private String name;
    private String phone;
    private String email;
    private String id;
    private int timesContacted;
    private int isStarred;
    private String lastContacted;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTimesContacted(int timesContacted) {
        this.timesContacted = timesContacted;
    }

    public int getTimesContacted() {
        return timesContacted;
    }

    public void setIsStarred(int isStarred) {
        this.isStarred = isStarred;
    }

    public int getIsStarred() {
        return isStarred;
    }

    public void setLastContacted(String lastContacted) {
        this.lastContacted = lastContacted;
    }

    public String getLastContacted() {
        return lastContacted;
    }

    public static class PhoneNumber {
        String phoneNumber;
        private int isStarred;
        private String type;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setIsStarred(int isStarred) {
            this.isStarred = isStarred;
        }

        public int getIsStarred() {
            return isStarred;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getTypeString(int type) {
            switch(type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                    return "custom";
                case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                    return "main";
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    return "mobile";
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    return "work";
                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                    return "fax_home";
                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                    return "fax_work";
                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                    return "fax_other";
                case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                    return "company_main";
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    return "home";
                default:
                    return "other";
            }
        }
    }

    ArrayList<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void addPhone(PhoneNumber phone) {
        phoneNumbers.add(phone);
    }

    public ArrayList<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }


}
