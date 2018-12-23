package com.hapis.customer.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hapis.customer.R;
import com.hapis.customer.logger.LOG;
import com.hapis.customer.ui.BaseFragmentActivity;
import com.hapis.customer.ui.LoginActivity;
import com.hapis.customer.ui.SigupActivity;
import com.hapis.customer.ui.adapters.CountriesAdap;
import com.hapis.customer.ui.callback.CommonSearchCallBack;
import com.hapis.customer.ui.callback.SelectPreferredLocationCallBack;
import com.hapis.customer.ui.custom.InstantAutoCompleteTextView;
import com.hapis.customer.ui.custom.MaterialSpinner;
import com.hapis.customer.ui.custom.ShowHidePasswordEditText;
import com.hapis.customer.ui.custom.dialogplus.DialogPlus;
import com.hapis.customer.ui.custom.dialogplus.OnBackPressListener;
import com.hapis.customer.ui.custom.dialogplus.OnClickListener;
import com.hapis.customer.ui.custom.dialogplus.ViewHolder;
import com.hapis.customer.ui.custom.materialedittext.MaterialEditText;
import com.hapis.customer.ui.fragments.search.enterprise.enterprises.EnterpriseSearchCallBack;
import com.hapis.customer.ui.fragments.search.enterprise.enterprises.EnterpriseSearchDialogFragment;
import com.hapis.customer.ui.models.enterprise.EnterpriseRequest;
import com.hapis.customer.ui.models.enums.EnterpriseTypeEnum;
import com.hapis.customer.ui.models.enums.MasterDataUtils;
import com.hapis.customer.ui.utils.DialogIconCodes;
import com.hapis.customer.ui.utils.EditTextUtils;
import com.hapis.customer.ui.utils.EmailAccount;
import com.hapis.customer.ui.utils.GetDeviceEmailAccounts;
import com.hapis.customer.ui.utils.PermissionsUtils;
import com.hapis.customer.ui.utils.UserPermissionDeniedCallBack;
import com.hapis.customer.ui.view.BaseView;
import com.hapis.customer.ui.view.SignUpFragmentView;
import com.hapis.customer.ui.view.SignUpFragmentViewModal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpFrag extends BaseAbstractFragment<SignUpFragmentViewModal> implements
        GetDeviceEmailAccounts.FetchEmailAccounts, SignUpFragmentView, SelectPreferredLocationCallBack {

    public static final String TAG = SignUpFrag.class.getSimpleName();

    private MaterialSpinner prefix_spinner;
    private MaterialEditText select_preferred_location_edittext, select_enterprise_edittext, select_specialization_edittext, first_name_input_et,middle_name_input_et,last_name_input_et,mobile_et;

    private View v;
    private ShowHidePasswordEditText passwordET;
    private AppCompatButton signUpBttn;
    private List<String> countryCodesList;
    private CountriesAdap adapter;
    private AppCompatTextView countryFlagTv, countryISDCodeTv;
    private AppCompatImageView countryFlagIv;
    private LinearLayout countryCodeLay;
    private AppCompatTextView termsOfUse;
    private TextView signInTv2;
    private TextInputLayout emailInputLay;
    private InstantAutoCompleteTextView emailEt;

    private List<String> emailAccountList;

    private RelativeLayout select_preferred_location_rl, select_enterprise_rl, select_specialization_rl;
//    private AppCompatTextView versionTv, copyrightTv;

    private String countryIsdCode = "";


    @Override
    public void onFetchingEmailAccounts(List<EmailAccount> emailAccounts) {
        if (emailAccountList == null) {
            emailAccountList = new ArrayList<>();
        }
        for (EmailAccount account : emailAccounts) {
            emailAccountList.add(account.getName());
        }
        final ArrayAdapter<String> emailAdapter = new ArrayAdapter(getActivity(), R.layout.row_spn, emailAccountList);
        emailEt.setAdapter(emailAdapter);
    }

    public SignUpFrag() {
        // Required empty public constructor
    }

    @Override
    protected Class getViewModalClass() {
        return SignUpFragmentViewModal.class;
    }

    @Override
    protected BaseView getViewImpl() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        initViews();

        new TedPermission(getActivity())
                .setPermissionListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted() {
                        new GetDeviceEmailAccounts(getActivity(), SignUpFrag.this, GetDeviceEmailAccounts.GMAIL_ACCOUNT);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PermissionsUtils.onPermissionDenied(new UserPermissionDeniedCallBack() {
                                @Override
                                public void takeActionOnOnlyPermissionDenied() {
                                    return;
                                }

                                @Override
                                public void takeActionOnCheckedDontAskAndPermissionDenied() {
                                    return;
                                }

                                @Override
                                public void noAction() {
                                    return;
                                }
                            }, getActivity(), deniedPermissions, null);
                        }
                    }

                })
                .setPermissions(
                        Manifest.permission.GET_ACCOUNTS)
                .check();

        termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(), LoadWebPageActivity.class);
                intent.putExtra(LoadWebPageActivity.PAGE_TYPE, LoadWebPageActivity.TERMS_CONDITIONS);
                startActivity(intent);*/
            }
        });
        signInTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClicked();
            }
        });
        signUpBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SigupActivity) getActivity()).hideSoftKeyPad();
                mViewModal.validateRegistrationDetails(mSelectedLocation, selectedEnterpriseRequest, EditTextUtils.getText(select_specialization_edittext), EditTextUtils.getText(first_name_input_et), EditTextUtils.getText(last_name_input_et), EditTextUtils.getText(mobile_et), EditTextUtils.getText(emailEt),
                        EditTextUtils.getText(passwordET));
            }
        });

        passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((SigupActivity) getActivity()).hideSoftKeyPad();
                    mViewModal.validateRegistrationDetails(mSelectedLocation, selectedEnterpriseRequest, EditTextUtils.getText(select_specialization_edittext), EditTextUtils.getText(first_name_input_et), EditTextUtils.getText(last_name_input_et),EditTextUtils.getText(mobile_et), EditTextUtils.getText(emailEt),
                            EditTextUtils.getText(passwordET));
                    return true;
                }
                return false;
            }
        });

        countryCodeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SigupActivity) getActivity()).hideSoftKeyPad();
                final DialogPlus dialog = DialogPlus.newDialog(getActivity())
                        .setExpanded(false)
                        .setGravity(Gravity.CENTER)
                        .setContentHolder(new ViewHolder(R.layout.country_list_lay))
                        .setCancelable(true)
                        .setBackgroundColorResourceId(R.color.white)
                        .setOnBackPressListener(new OnBackPressListener() {
                            @Override
                            public void onBackPressed(DialogPlus dialog) {
                                dialog.dismiss();
                            }
                        })
                        .create(false);
                dialog.show();

                View dialogView = dialog.getHolderView();
                AppCompatEditText searchableET = dialogView.findViewById(R.id.searchable_et);
                AppCompatTextView noResultTv = dialogView.findViewById(R.id.no_result_tv);
//                TypefaceHelper.getInstance().setTypeface(searchableET, TypefaceHelper.getFont(TypefaceHelper.FONT.LIGHT));
//                TypefaceHelper.getInstance().setTypeface(noResultTv, TypefaceHelper.getFont(TypefaceHelper.FONT.LIGHT));
                RecyclerView countryLv = dialogView.findViewById(R.id.country_lv);
                adapter = new CountriesAdap(getActivity(), countryCodesList, searchableET, noResultTv, dialog);
                countryLv.setAdapter(adapter);
                countryLv.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter.setOnItemClickListener(new CountriesAdap.CountryClickListener() {
                    @Override
                    public void onCountryClick(View view, int position, String[] countryStrArray) {
                        AppCompatTextView flagTv = view.findViewById(R.id.flag_tv);
                        AppCompatTextView countryTv = view.findViewById(R.id.country_name_tv);
                        countryFlagTv.setText(flagTv.getText());
                        String countryISOAlpha2Name = countryStrArray[1].trim().toLowerCase();
//                        countryFlagIv.setImageResource(getActivity().getResources().getIdentifier("drawable/" + countryISOAlpha2Name, null, getActivity().getPackageName()));
                        Glide.with(getActivity())
                                .load(getActivity().getResources().getIdentifier("drawable/" + countryISOAlpha2Name, null, getActivity().getPackageName()))
                                .apply(RequestOptions
                                        .noTransformation()
                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                        .centerInside()
                                        .circleCrop())
                                .into(countryFlagIv);
                        LOG.d("CountryISOCode", "" + countryStrArray[0]);
                        String ISOCode = countryStrArray[0];
                        countryISDCodeTv.setText("");
                        countryISDCodeTv.setText(countryISOAlpha2Name);
                        countryIsdCode = ISOCode;
                        countryISDCodeTv.invalidate();
                        ((SigupActivity) getActivity()).hideSoftKeyPad();
                        dialog.dismiss();
                    }
                });
            }
        });

//        AccessPreferences.put(getActivity(), ApplicationConstants.IS_USER_LOGGED_IN, ApplicationConstants.USER_NEW);

        return v;
    }

    private String selectedPrefix;

    AdapterView.OnItemSelectedListener prefixItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedPrefix = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @SuppressLint("RestrictedApi")
    private void initViews() {

        select_preferred_location_rl = v.findViewById(R.id.select_preferred_location_rl);
        select_preferred_location_edittext = v.findViewById(R.id.select_preferred_location_edittext);

        select_preferred_location_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectLocationFragment();
            }
        });

        select_enterprise_rl = v.findViewById(R.id.select_enterprise_rl);
        select_enterprise_edittext = v.findViewById(R.id.select_enterprise_edittext);

        select_enterprise_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedLocation != null && mSelectedLocation.size() > 0 && mSelectedLocation.size() == 3) {
                    ((SigupActivity) getActivity()).showProgressDialog(getActivity(), getResources().getString(R.string.please_wait));
                    mViewModal.getEnterprisesBy(EnterpriseTypeEnum.HOSPITAL.code(), mSelectedLocation.get(2));
                }else{
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.please_select_preferred_location), Toast.LENGTH_SHORT).show();
                }
            }
        });

        select_specialization_rl = v.findViewById(R.id.select_specialization_rl);
        select_specialization_edittext = v.findViewById(R.id.select_specialization_edittext);

        select_specialization_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonSearchDialogFragment dialog =
                        CommonSearchDialogFragment.newInstance((BaseFragmentActivity)getActivity(), new CommonSearchCallBack() {
                            @Override
                            public void updateSelectedValue(String selectedValue) {
                                select_specialization_edittext.setText(selectedValue);
                            }
                        }, MasterDataUtils.getSpecialisation(), "Specialisation");
                dialog.setCancelable(false);
                dialog.show(getActivity().getSupportFragmentManager(), SelectPreferredLocationDialogFragment.TAG);
            }
        });

        prefix_spinner = (MaterialSpinner)v.findViewById(R.id.prefix_spinner);

        ArrayAdapter<String> prefixAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.prefix_array));
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefix_spinner.setAdapter(prefixAdapter);
        prefix_spinner.setOnItemSelectedListener(prefixItemSelectedListener);

        first_name_input_et = (MaterialEditText)v.findViewById(R.id.first_name_input_et);
        middle_name_input_et = (MaterialEditText)v.findViewById(R.id.middle_name_input_et);
        last_name_input_et = (MaterialEditText)v.findViewById(R.id.last_name_input_et);

        emailEt = v.findViewById(R.id.email_et);
        mobile_et = v.findViewById(R.id.mobile_et);
        passwordET = v.findViewById(R.id.pswd_et);
        passwordET.setFilters(EditTextUtils.getSpaceFilterWithMaxLength(getResources().getInteger(R.integer.PASSWORD_LENGTH_MAX)));

        termsOfUse = v.findViewById(R.id.terms_of_use_tv);
        signInTv2 = v.findViewById(R.id.sign_in_tv2);
        signUpBttn = v.findViewById(R.id.sign_up_bttn);

        countryCodesList = Arrays.asList(getResources().getStringArray(R.array.CountryCodes));

        countryCodeLay = v.findViewById(R.id.country_lay);
        countryFlagTv = v.findViewById(R.id.flag_tv);
        countryFlagIv = v.findViewById(R.id.flag_iv);
        countryISDCodeTv = v.findViewById(R.id.country_mobile_code_tv);
        //countryFlagTv.setText(localeToEmoji("IN"));
        String countryID = ((SigupActivity) getActivity()).getCountryID();
        /*countryFlagIv.setImageResource(getActivity().getResources().getIdentifier("drawable/"
                + countryID, null, getActivity().getPackageName()));*/
        Glide.with(getActivity())
                .load(getActivity().getResources().getIdentifier("drawable/" + countryID, null, getActivity().getPackageName()))
                .apply(RequestOptions
                        .noTransformation()
                        .centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .circleCrop())
                .into(countryFlagIv);
        countryISDCodeTv.setText(countryID/*"+" + ((SigupActivity) getActivity()).getCountryDialCode()*/);
        countryIsdCode = ((SigupActivity) getActivity()).getCountryDialCode();
    }

    @Override
    public void validateScreenFields(String errorMsg) {
        if(errorMsg == null || (errorMsg != null && errorMsg.length() == 0)){
            ((SigupActivity)getActivity()).showProgressDialog(getActivity(), getResources().getString(R.string.please_wait_progress_msg));
            mViewModal.doSignup(selectedEnterpriseRequest.getEnterpriseCode(), EditTextUtils.getText(select_specialization_edittext), selectedPrefix, EditTextUtils.getText(first_name_input_et), EditTextUtils.getText(middle_name_input_et),
                    EditTextUtils.getText(last_name_input_et), EditTextUtils.getText(mobile_et),
                    EditTextUtils.getText(emailEt), EditTextUtils.getText(passwordET));
        }else{
            ((SigupActivity)getActivity()).showError(errorMsg, null, null, null, DialogIconCodes.DIALOG_FAILED.getIconCode());
        }
    }

    @Override
    public void signInClicked() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        try {
            getActivity().finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void SignupRequestFailed(String errorMsg) {
        if(errorMsg != null && errorMsg.length() > 0){
            ((SigupActivity)getActivity()).dismissProgressDialog();
            ((SigupActivity)getActivity()).showError(errorMsg, null, null, null, DialogIconCodes.DIALOG_FAILED.getIconCode());
        }
    }

    @Override
    public void SignupRequestSuccess(String msg) {
        ((SigupActivity)getActivity()).dismissProgressDialog();
        if(msg != null && msg.length() > 0){

            OnClickListener onClickListener = new OnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    switch (view.getId()){
                        case R.id.positive_btn:
                        {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            try {
                                getActivity().finishAffinity();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            };

            ((SigupActivity)getActivity()).showError(msg, onClickListener, getResources().getString(R.string.ok), null, DialogIconCodes.DIALOG_SUCCESS.getIconCode());
        }
    }

    private List<String> mSelectedLocation;

    public void showSelectLocationFragment() {

        new LoadLocationDetailsFromFile().execute();
    }

    @Override
    public void updateSelectedLocation(List<String> selectedLocation) {
        mSelectedLocation = selectedLocation;
        if(mSelectedLocation != null && mSelectedLocation.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mSelectedLocation.get(2)+"-");
            stringBuilder.append(mSelectedLocation.get(1)+"-");
            stringBuilder.append(mSelectedLocation.get(0));

            select_preferred_location_edittext.setText(stringBuilder.toString());
        }
    }

    class LoadLocationDetailsFromFile extends AsyncTask<Void, Void, Map<String, Map<String, List<String>>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ((SigupActivity)getActivity()).showProgressDialog(getActivity(), "Loading location please wait..");
        }

        @Override
        protected Map<String, Map<String, List<String>>> doInBackground(Void... voids) {

            Map<String, Map<String, List<String>>> locationList = new HashMap<>();

            List<String> city = new ArrayList<>();
            city.add("Bangalore");
            city.add("Mysore");

            HashMap<String, List<String>> state = new HashMap<>();
            state.put("Karnataka", city);

            locationList.put("India", state);

            return locationList;
        }

        @Override
        protected void onPostExecute(Map<String, Map<String, List<String>>> locationList) {
            super.onPostExecute(locationList);

            ((SigupActivity)getActivity()).dismissProgressDialog();

            SelectPreferredLocationDialogFragment dialog =
                    SelectPreferredLocationDialogFragment.newInstance(SignUpFrag.this, locationList, mSelectedLocation);
            dialog.setCancelable(false);
            dialog.show(getActivity().getSupportFragmentManager(), SelectPreferredLocationDialogFragment.TAG);
        }
    }

    private EnterpriseRequest selectedEnterpriseRequest;

    @Override
    public void updateEnterpriseByTypeAndCity(List<EnterpriseRequest> enterpriseRequestList) {

        EnterpriseSearchDialogFragment dialog =
                EnterpriseSearchDialogFragment.newInstance((BaseFragmentActivity)getActivity(), new EnterpriseSearchCallBack() {
                    @Override
                    public void updateSelectedValue(EnterpriseRequest enterpriseRequest) {
                        selectedEnterpriseRequest = enterpriseRequest;
                        select_enterprise_edittext.setText(selectedEnterpriseRequest.getEnterpriseName());
                    }
                }, enterpriseRequestList, "Hospital");
        dialog.setCancelable(false);
        dialog.show(getActivity().getSupportFragmentManager(), SelectPreferredLocationDialogFragment.TAG);

        ((SigupActivity)getActivity()).dismissProgressDialog();
    }
}
