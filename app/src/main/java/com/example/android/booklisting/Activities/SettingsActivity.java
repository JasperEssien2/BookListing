package com.example.android.booklisting.Activities;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.booklisting.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getWindow().setBackgroundDrawableResource(R.color.colorPreferenceBackground);
        //getWindow().getP
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            Preference maxResult = findPreference(getString(R.string.max_result_key));
            bindPreferenceSummaryToValue(maxResult);

            Preference orderBy = findPreference(getString(R.string.order_by_array_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference filter = findPreference(getString(R.string.filter_list_key));
            bindPreferenceSummaryToValue(filter);

            Preference printType = findPreference(getString(R.string.print_type_list_key));
            bindPreferenceSummaryToValue(printType);

            Preference matureRating = findPreference(getString(R.string.mature_rating_list_key));
            bindPreferenceSummaryToValue(matureRating);

            Preference restrictByDownloadAvail = findPreference(getString(R.string.download_available_key));
            bindPreferenceSummaryToValue(restrictByDownloadAvail, false);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();


            if(preference instanceof ListPreference){
                ListPreference orderByListPreference = (ListPreference)preference;
                int index = orderByListPreference.findIndexOfValue(stringValue);
                CharSequence[] label = orderByListPreference.getEntries();
                preference.setSummary(label[index]);

            }else if(preference instanceof EditTextPreference && preference != null){
//                final int MIN_VALUE = 10;
//                final int MAX_VALUE = 40;
//                InputFilter inputFilter = new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                        try {
//                            // Remove the string out of destination that is to be replaced
//                            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
//                            // Add the new string in
//                            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
//                            //int input = Integer.parseInt(dest.toString() + source.toString());
//                            int input = Integer.parseInt(newVal);
//                            if (isInRange(MIN_VALUE, MAX_VALUE, input))
//                                return null;
//                        } catch (NumberFormatException nfe) { }
//                        return "";
//                    }
//                };
//                InputFilter[] inputFilters = {inputFilter};

                if(Integer.parseInt(stringValue) > 40){
                    Toast.makeText(getActivity(),
                            getString(R.string.max_result_value_high), Toast.LENGTH_SHORT).show();
                    stringValue = "40";
//                    ((EditTextPreference) preference).getEditText().setFilters(inputFilters);
//                    ((EditTextPreference) preference).getEditText().setText("40");
//                    preference.setDefaultValue(stringValue);
                }
                preference.setSummary(stringValue);

            } else if(preference instanceof SwitchPreference){
                SwitchPreference switchPreference = (SwitchPreference) preference;
                switchPreference.setDefaultValue(R.string.download_available_switch_off);

                if(Boolean.parseBoolean(stringValue))
                    preference.setSummary(R.string.download_available_switch_on);
                else
                    preference.setSummary(R.string.download_available_switch_off);
            }

            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference){
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    preference.getContext());
            String preferenceString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        private void bindPreferenceSummaryToValue(Preference preference, boolean val){
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    preference.getContext());
            boolean preferenceString = sharedPreferences.getBoolean(preference.getKey(), false);
            onPreferenceChange(preference, preferenceString);
        }

//        private boolean isInRange(int a, int b, int c) {
//            return b > a ? c >= a && c <= b : c >= b && c <= a;
//        }
    }
}
