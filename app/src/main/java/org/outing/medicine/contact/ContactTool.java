package org.outing.medicine.contact;

import org.outing.medicine.logic.AnContact;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ContactTool {
    private static final String CONTACT_SAVE_PATH = "contact_member";

    public static void saveAnContact(Context context, int location,
                                     AnContact contact) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                CONTACT_SAVE_PATH + location, Activity.MODE_PRIVATE).edit();
        prefs.putString("name", contact.getName());
        prefs.putString("phone", contact.getPhone());
        prefs.putString("relative",contact.getRelative());
        prefs.putString("icon_path", contact.getIconPath());
        prefs.commit();
    }

    public static AnContact getAnContact(Context context, int location) {
        SharedPreferences mPref = context.getSharedPreferences(
                CONTACT_SAVE_PATH + location, Activity.MODE_PRIVATE);
        AnContact contact = new AnContact();
        contact.setName(mPref.getString("name", ""));
        contact.setPhone(mPref.getString("phone", ""));
        contact.setRelative(mPref.getString("relative",""));
        contact.setIconPath(mPref.getString("icon_path", ""));
        return contact;
    }

}