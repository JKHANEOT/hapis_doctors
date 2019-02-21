package com.hapis.customer.database.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.hapis.customer.HapisApplication;
import com.hapis.customer.R;
import com.hapis.customer.database.HapisDatabase;
import com.hapis.customer.database.daos.AddressDao;
import com.hapis.customer.database.daos.ApplicationProfileDao;
import com.hapis.customer.database.daos.UserProfileDao;
import com.hapis.customer.database.tables.ApplicationProfileTable;
import com.hapis.customer.database.tables.UserProfileTable;
import com.hapis.customer.exception.HapisException;
import com.hapis.customer.logger.LOG;
import com.hapis.customer.networking.RestCall;
import com.hapis.customer.networking.json.JSONAdaptor;
import com.hapis.customer.networking.util.RestConstants;
import com.hapis.customer.ui.models.ErrorMessage;
import com.hapis.customer.ui.models.ResponseStatus;
import com.hapis.customer.ui.models.users.LoginRequest;
import com.hapis.customer.ui.models.users.LoginResponse;
import com.hapis.customer.ui.models.users.UserListResponse;
import com.hapis.customer.ui.models.users.UserRequest;
import com.hapis.customer.ui.models.users.UserResponse;
import com.hapis.customer.ui.utils.AccessPreferences;
import com.hapis.customer.ui.utils.ApplicationConstants;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class UserProfileRepository {

    private final String TAG = UserProfileRepository.class.getName();

    private UserProfileDao userProfileDao;
    private ApplicationProfileDao applicationProfileDao;
    private AddressDao addressDao;

    public UserProfileRepository(){
        HapisDatabase database = HapisDatabase.getInstance(HapisApplication.getApplication());
        userProfileDao = database.getUserProfileDao();
        applicationProfileDao = database.getApplicationProfileDao();
        addressDao = database.getAddressDao();
    }

    public void insert(UserProfileTable userProfile){
        new InsertUserProfileAsyncTask(userProfileDao).execute(userProfile);
    }

    /*public void update(UserProfileTable userProfile){
        new UpdateUserProfileAsyncTask(userProfileDao).execute(userProfile);
    }*/

    public void delete(UserProfileTable userProfile){
        new DeleteUserProfileAsyncTask(userProfileDao).execute(userProfile);
    }

    public void deleteAllUserProfiles(){
        new DeleteAllUserProfileAsyncTask(userProfileDao).execute();
    }

    public void getAppProfileStatus(MutableLiveData<Integer> appStatus) {
        new GetAppProfileStatusAsyncTask(appStatus, applicationProfileDao).execute();
    }

    public UserProfileTable getUserProfile() {
        return userProfileDao.getUserProfileByUniqueId(AccessPreferences.get(HapisApplication.getApplication(), ApplicationConstants.LOGGED_IN_USER_GUID, null));
    }

    private static class GetAppProfileStatusAsyncTask extends AsyncTask<Void, Void, Integer> {

        private ApplicationProfileDao applicationProfileDao;
        private MutableLiveData<Integer> integerMutableLiveData;

        private GetAppProfileStatusAsyncTask(MutableLiveData<Integer> appStatus, ApplicationProfileDao userProfileDao){
            this.applicationProfileDao = userProfileDao;
            integerMutableLiveData = appStatus;
        }

        @Override
        protected Integer doInBackground(Void... notes) {

            Integer appStatus = applicationProfileDao.getAppProfileStatus();

            return appStatus;
        }

        @Override
        protected void onPostExecute(Integer appStatus) {
            super.onPostExecute(appStatus);

            if(appStatus == null || appStatus == 0){
                integerMutableLiveData.postValue(new Integer(ApplicationConstants.USER_NEW));
            }else{
                integerMutableLiveData.postValue(new Integer(appStatus));
            }
        }
    }

    public UserProfileTable getUserProfileByMobileNumber(String mobileNumber, String password) {
        return userProfileDao.getUserProfileByMobileNumber(mobileNumber, password);
    }

    public LiveData<UserProfileTable> getUserProfileByEmail(String email, String password) {
        return userProfileDao.getUserProfileByEmail(email, password);
    }

    private static class InsertUserProfileAsyncTask extends AsyncTask<UserProfileTable, Void, Void> {

        private UserProfileDao userProfileDao;

        private InsertUserProfileAsyncTask(UserProfileDao userProfileDao){
            this.userProfileDao = userProfileDao;
        }

        @Override
        protected Void doInBackground(UserProfileTable... notes) {

            userProfileDao.insert(notes[0]);

            return null;
        }
    }

    /*private static class UpdateUserProfileAsyncTask extends AsyncTask<UserProfileTable, Void, Void> {

        private UserProfileDao userProfileDao;

        private UpdateUserProfileAsyncTask(UserProfileDao userProfileDao){
            this.userProfileDao = userProfileDao;
        }

        @Override
        protected Void doInBackground(UserProfileTable... notes) {

            userProfileDao.update(notes[0]);

            return null;
        }
    }*/

    private static class DeleteUserProfileAsyncTask extends AsyncTask<UserProfileTable, Void, Void> {

        private UserProfileDao userProfileDao;

        private DeleteUserProfileAsyncTask(UserProfileDao userProfileDao){
            this.userProfileDao = userProfileDao;
        }

        @Override
        protected Void doInBackground(UserProfileTable... notes) {

            userProfileDao.delete(notes[0]);

            return null;
        }
    }

    private static class DeleteAllUserProfileAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserProfileDao userProfileDao;

        private DeleteAllUserProfileAsyncTask(UserProfileDao userProfileDao){
            this.userProfileDao = userProfileDao;
        }

        @Override
        protected Void doInBackground(Void... notes) {

            userProfileDao.deleteAllUserProfile();

            return null;
        }
    }

    public void doLogin(final MutableLiveData<LoginResponse> mutableLiveData, final String userName, final String enterpriseCode, final String password) {

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setMobileNo(userName);
        loginRequest.setEnterpriseCode(enterpriseCode);
        loginRequest.setPassword(password);


        RestCall restCall = new RestCall();
        restCall.setOnRestCallListener(new RestCall.RestCallListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                ResponseStatus responseStatus = new ResponseStatus();
                responseStatus.setStatusCode(ResponseStatus.FAILED);

                LoginResponse userModelResponse = new LoginResponse();
                userModelResponse.setStatus(responseStatus);

                mutableLiveData.postValue(userModelResponse);
            }

            @Override
            public void onResponse(RestCall.Result result, String response, List<ErrorMessage> errorMessages, String msg) {
                if (result == RestCall.Result.FAILED || result == RestCall.Result.EXCEPTION) {
                    ResponseStatus responseStatus = new ResponseStatus();
                    responseStatus.setStatusCode(ResponseStatus.FAILED);

                    LoginResponse userModelResponse = new LoginResponse();
                    userModelResponse.setStatus(responseStatus);

                    mutableLiveData.postValue(userModelResponse);
                } else {
                    try {
                        UserProfileTable userProfileTable = userProfileDao.getUserProfileByMobileNumber(userName, password);

                        if(userProfileTable != null && userProfileTable.getUniqueId() != null && userProfileTable.getUniqueId().length() > 0) {

                            ApplicationProfileTable applicationProfileTable = null;
                            List<ApplicationProfileTable> applicationProfileTables = applicationProfileDao.getAllApplicationProfileWithout();
                            if(applicationProfileTables != null && applicationProfileTables.size() > 0){
                                applicationProfileTable = applicationProfileTables.get(0);
                                applicationProfileTable.setAppStatus(ApplicationConstants.USER_LOGGED_IN);
                                applicationProfileDao.update(applicationProfileTable);
                            }else{
                                applicationProfileTable = new ApplicationProfileTable();
                                applicationProfileTable.setAppStatus(ApplicationConstants.USER_LOGGED_IN);
                                applicationProfileDao.insert(applicationProfileTable);
                            }

                            AccessPreferences.put(HapisApplication.getApplication(), ApplicationConstants.LOGGED_IN_USER_GUID, userProfileTable.getUniqueId());

                            userProfileTable.setLastLoginDate(new Date().getTime());
                            if (userProfileDao.getNumberOfRows(userProfileTable.getUniqueId()) > 0)
                                userProfileDao.update(userProfileTable);
                            else
                                userProfileDao.insert(userProfileTable);

                            ResponseStatus responseStatus = new ResponseStatus();
                            responseStatus.setStatusCode(ResponseStatus.SUCCESS);

                            LoginResponse userModelResponse = new LoginResponse();
                            userModelResponse.setStatus(responseStatus);

                            mutableLiveData.postValue(userModelResponse);
                        }else{
                            ApplicationProfileTable applicationProfileTable = new ApplicationProfileTable();
                            applicationProfileTable.setAppStatus(ApplicationConstants.USER_LOGGED_IN);
                            applicationProfileDao.insert(applicationProfileTable);

                            getUserDetails(mutableLiveData, userName, enterpriseCode, password);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ResponseStatus responseStatus = new ResponseStatus();
                        responseStatus.setStatusCode(ResponseStatus.FAILED);

                        LoginResponse userModelResponse = new LoginResponse();
                        userModelResponse.setStatus(responseStatus);

                        mutableLiveData.postValue(userModelResponse);
                    }
                }
            }
        });
        try {
            restCall.post(null, false, "Loading items",
                    HapisApplication.getApplication().getBackendUrl()+"9300"  + RestConstants.LOGIN_URL,
                    JSONAdaptor.toJSON(loginRequest));
        } catch (IOException e) {
            e.printStackTrace();
            ResponseStatus responseStatus = new ResponseStatus();
            responseStatus.setStatusCode(ResponseStatus.FAILED);

            LoginResponse userModelResponse = new LoginResponse();
            userModelResponse.setStatus(responseStatus);

            mutableLiveData.postValue(userModelResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseStatus responseStatus = new ResponseStatus();
            responseStatus.setStatusCode(ResponseStatus.FAILED);

            LoginResponse userModelResponse = new LoginResponse();
            userModelResponse.setStatus(responseStatus);

            mutableLiveData.postValue(userModelResponse);
        }
    }

    public void getUserDetails(final MutableLiveData<LoginResponse> mutableLiveData, final String userName, final String enterpriseCode, final String password) {

        RestCall restCall = new RestCall();
        restCall.setOnRestCallListener(new RestCall.RestCallListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                ResponseStatus responseStatus = new ResponseStatus();
                responseStatus.setStatusCode(ResponseStatus.FAILED);

                LoginResponse userModelResponse = new LoginResponse();
                userModelResponse.setStatus(responseStatus);

                mutableLiveData.postValue(userModelResponse);
            }

            @Override
            public void onResponse(RestCall.Result result, String response, List<ErrorMessage> errorMessages, String msg) {
                if (result == RestCall.Result.FAILED || result == RestCall.Result.EXCEPTION) {
                    ResponseStatus responseStatus = new ResponseStatus();
                    responseStatus.setStatusCode(ResponseStatus.FAILED);

                    LoginResponse userModelResponse = new LoginResponse();
                    userModelResponse.setStatus(responseStatus);

                    mutableLiveData.postValue(userModelResponse);
                } else {
                    try {

                        UserListResponse userListResponse = JSONAdaptor.fromJSON(response, UserListResponse.class);
                        if(userListResponse != null && userListResponse.getResults() != null && userListResponse.getResults().size() > 0){
                            boolean foundUser = false;
                            for(UserRequest userRequest : userListResponse.getResults()){
                                if(userRequest != null && userRequest.getMobileNo() != null && userRequest.getMobileNo().equals(userName) && userRequest.getPassword() != null && userRequest.getPassword().equals(password)){

                                    UserProfileTable userProfileTable = new UserProfileTable();

                                    userProfileTable.setUniqueId(userRequest.getUserCode());
                                    AccessPreferences.put(HapisApplication.getApplication(), ApplicationConstants.LOGGED_IN_USER_GUID, userProfileTable.getUniqueId());
                                    userProfileTable.setEnterpriseCode(userRequest.getEnterpriseCode());
                                    userProfileTable.setRoles(userRequest.getRoles());
                                    userProfileTable.setTitle(userRequest.getNamePrefix());
                                    userProfileTable.setFirstName(userRequest.getFirstName());
                                    userProfileTable.setMiddleName(userRequest.getMiddleName());
                                    userProfileTable.setLastName(userRequest.getLastName());
                                    userProfileTable.setMobileNumber(userRequest.getMobileNo());
                                    userProfileTable.setEmail(userRequest.getEmailAddress());
                                    userProfileTable.setPassword(userRequest.getPassword());

                                    userProfileTable.setProfileUpdatedDate(new Date().getTime());

                                    userProfileTable.setAgentCode(userRequest.getAgentCode());
                                    userProfileTable.setCustomerType(userRequest.getUserType());
                                    userProfileTable.setState(userRequest.getState());

                                    userProfileDao.insert(userProfileTable);

                                    foundUser = true;
                                    break;
                                }
                            }
                            if(!foundUser){
                                ResponseStatus responseStatus = new ResponseStatus();
                                responseStatus.setStatusCode(ResponseStatus.FAILED);

                                LoginResponse userModelResponse = new LoginResponse();
                                userModelResponse.setStatus(responseStatus);

                                mutableLiveData.postValue(userModelResponse);
                            }else{

                                UserProfileTable userProfileTable = userProfileDao.getUserProfileByMobileNumber(userName, password);

                                if(userProfileTable != null && userProfileTable.getUniqueId() != null && userProfileTable.getUniqueId().length() > 0) {
                                    applicationProfileDao.setAppProfileStatus(ApplicationConstants.USER_LOGGED_IN);
                                    AccessPreferences.put(HapisApplication.getApplication(), ApplicationConstants.LOGGED_IN_USER_GUID, userProfileTable.getUniqueId());

                                    userProfileTable.setLastLoginDate(new Date().getTime());
                                    if (userProfileDao.getNumberOfRows(userProfileTable.getUniqueId()) > 0)
                                        userProfileDao.update(userProfileTable);
                                    else
                                        userProfileDao.insert(userProfileTable);

                                    ResponseStatus responseStatus = new ResponseStatus();
                                    responseStatus.setStatusCode(ResponseStatus.SUCCESS);

                                    LoginResponse userModelResponse = new LoginResponse();
                                    userModelResponse.setStatus(responseStatus);

                                    mutableLiveData.postValue(userModelResponse);
                                }else{

                                    ResponseStatus responseStatus = new ResponseStatus();
                                    responseStatus.setStatusCode(ResponseStatus.FAILED);

                                    LoginResponse userModelResponse = new LoginResponse();
                                    userModelResponse.setStatus(responseStatus);

                                    mutableLiveData.postValue(userModelResponse);
                                }
                            }
                        }else{
                            ResponseStatus responseStatus = new ResponseStatus();
                            responseStatus.setStatusCode(ResponseStatus.FAILED);

                            LoginResponse userModelResponse = new LoginResponse();
                            userModelResponse.setStatus(responseStatus);

                            mutableLiveData.postValue(userModelResponse);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ResponseStatus responseStatus = new ResponseStatus();
                        responseStatus.setStatusCode(ResponseStatus.FAILED);

                        LoginResponse userModelResponse = new LoginResponse();
                        userModelResponse.setStatus(responseStatus);

                        mutableLiveData.postValue(userModelResponse);
                    }
                }
            }
        });
        try {
            restCall.get(null, false, "Loading items",
                    HapisApplication.getApplication().getBackendUrl()+"9300"  + RestConstants.getAllUsersByEnterpriseCode+enterpriseCode);
        } catch (IOException e) {
            e.printStackTrace();
            ResponseStatus responseStatus = new ResponseStatus();
            responseStatus.setStatusCode(ResponseStatus.FAILED);

            LoginResponse userModelResponse = new LoginResponse();
            userModelResponse.setStatus(responseStatus);

            mutableLiveData.postValue(userModelResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseStatus responseStatus = new ResponseStatus();
            responseStatus.setStatusCode(ResponseStatus.FAILED);

            LoginResponse userModelResponse = new LoginResponse();
            userModelResponse.setStatus(responseStatus);

            mutableLiveData.postValue(userModelResponse);
        }
    }

    public void doSignup(final MutableLiveData<UserResponse> mutableLiveData, final UserRequest userRequest) {

        RestCall restCall = new RestCall();
        restCall.setOnRestCallListener(new RestCall.RestCallListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                ResponseStatus responseStatus = new ResponseStatus();
                responseStatus.setStatusCode(ResponseStatus.FAILED);

                UserResponse userModelResponse = new UserResponse();
                userModelResponse.setStatus(responseStatus);

                mutableLiveData.postValue(userModelResponse);
            }

            @Override
            public void onResponse(RestCall.Result result, final String response, List<ErrorMessage> errorMessages, String msg) {
                try {
                    if (result == RestCall.Result.FAILED || result == RestCall.Result.EXCEPTION) {
                        UserResponse userModelResponse = JSONAdaptor.fromJSON(response, UserResponse.class);
                        if(userModelResponse != null && userModelResponse.getMessage() != null){
                            mutableLiveData.postValue(userModelResponse);
                        }else{
                            ResponseStatus responseStatus = new ResponseStatus();
                            responseStatus.setStatusCode(ResponseStatus.FAILED);

                            userModelResponse = new UserResponse();
                            userModelResponse.setStatus(responseStatus);

                            mutableLiveData.postValue(userModelResponse);
                        }
                    } else {
                        LOG.d(TAG, "" + response);

                        if(response != null && response.length() > 0){
                            UserResponse userModelResponse = JSONAdaptor.fromJSON(response, UserResponse.class);
                            if(userModelResponse != null && userModelResponse.getMessage() != null){
                                UserRequest signedUpResponse = userModelResponse.getMessage();
                                if(signedUpResponse != null){

                                    ApplicationProfileTable applicationProfileTable = new ApplicationProfileTable();
                                    applicationProfileTable.setAppStatus(ApplicationConstants.USER_SIGNED_UP);

                                    applicationProfileDao.insert(applicationProfileTable);

                                    UserProfileTable userProfileTable = new UserProfileTable();

                                    userProfileTable.setUniqueId(signedUpResponse.getUserCode());
                                    userProfileTable.setEnterpriseCode(signedUpResponse.getEnterpriseCode());
                                    userProfileTable.setRoles(signedUpResponse.getRoles());
                                    userProfileTable.setTitle(userRequest.getNamePrefix());
                                    userProfileTable.setFirstName(userRequest.getFirstName());
                                    userProfileTable.setMiddleName(userRequest.getMiddleName());
                                    userProfileTable.setLastName(userRequest.getLastName());
                                    userProfileTable.setMobileNumber(userRequest.getMobileNo());
                                    userProfileTable.setEmail(userRequest.getEmailAddress());
                                    userProfileTable.setPassword(userRequest.getPassword());

                                    userProfileTable.setProfileUpdatedDate(new Date().getTime());

                                    userProfileTable.setAgentCode(signedUpResponse.getAgentCode());
                                    userProfileTable.setCustomerType(signedUpResponse.getUserType());
                                    userProfileTable.setState(signedUpResponse.getState());

                                    userProfileDao.insert(userProfileTable);

                                    /*if(userModel.getAddress() != null){
                                        AddressTable addressTable = new AddressTable();

                                        addressTable.setFk_uniqueId(signedUpResponse.getCustomerCode());

                                        addressTable.setAddress(userModel.getAddress().getAddressLine1());
                                        addressTable.setLandmark(userModel.getAddress().getAddressLine2());
                                        addressTable.setCity(userModel.getAddress().getCity());
                                        addressTable.setState(userModel.getAddress().getStateCode());
                                        addressTable.setCountry(userModel.getAddress().getCountry());
                                        addressTable.setPinCode(userModel.getAddress().getPinCode());
                                        addressTable.setAddressType(userModel.getAddress().getAddressType());

                                        addressDao.insert(addressTable);
                                    }*/
                                }

                                mutableLiveData.postValue(userModelResponse);
                            }else{
                                ResponseStatus responseStatus = new ResponseStatus();
                                responseStatus.setStatusCode(ResponseStatus.FAILED);

                                userModelResponse = new UserResponse();
                                userModelResponse.setStatus(responseStatus);

                                mutableLiveData.postValue(userModelResponse);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseStatus responseStatus = new ResponseStatus();
                    responseStatus.setStatusCode(ResponseStatus.FAILED);

                    UserResponse userModelResponse = new UserResponse();
                    userModelResponse.setStatus(responseStatus);

                    mutableLiveData.postValue(userModelResponse);
                }
            }
        });
        try {
            restCall.post(HapisApplication.getApplication(), false, "Loading items",
                    HapisApplication.getApplication().getBackendUrl()+"9300" + RestConstants.REGISTER_USER_REQUEST_URL,
                    JSONAdaptor.toJSON(userRequest));
        } catch (IOException e) {
            e.printStackTrace();
            ResponseStatus responseStatus = new ResponseStatus();
            responseStatus.setStatusCode(ResponseStatus.FAILED);

            UserResponse userModelResponse = new UserResponse();
            userModelResponse.setStatus(responseStatus);

            mutableLiveData.postValue(userModelResponse);
        } catch (HapisException e) {
            e.printStackTrace();
            ResponseStatus responseStatus = new ResponseStatus();
            responseStatus.setStatusCode(ResponseStatus.FAILED);

            UserResponse userModelResponse = new UserResponse();
            userModelResponse.setStatus(responseStatus);

            mutableLiveData.postValue(userModelResponse);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseStatus responseStatus = new ResponseStatus();
            responseStatus.setStatusCode(ResponseStatus.FAILED);

            UserResponse userModelResponse = new UserResponse();
            userModelResponse.setStatus(responseStatus);

            mutableLiveData.postValue(userModelResponse);
        }
    }

    public class LogoutAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private MutableLiveData<Boolean> mutableLiveData;

        public LogoutAsyncTask(final MutableLiveData<Boolean> mutableLiveData){
            this.mutableLiveData = mutableLiveData;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            Boolean aBoolean = new Boolean(false);

            ApplicationProfileTable applicationProfileTable = null;
            List<ApplicationProfileTable> applicationProfileTables = applicationProfileDao.getAllApplicationProfileWithout();
            if(applicationProfileTables != null && applicationProfileTables.size() > 0){
                applicationProfileTable = applicationProfileTables.get(0);
                applicationProfileTable.setAppStatus(ApplicationConstants.USER_LOGGED_OUT);
                applicationProfileDao.update(applicationProfileTable);

                AccessPreferences.put(HapisApplication.getApplication(), ApplicationConstants.LOGGED_IN_USER_GUID, "");
                aBoolean = new Boolean(true);
                HapisDatabase database = HapisDatabase.getInstance(HapisApplication.getApplication());
                database.clearAllTables();
            }

            return aBoolean;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            mutableLiveData.postValue(aBoolean);
        }
    }

    public void loadUserProfileDetails(MutableLiveData<UserProfileTable> userProfileTableMutableLiveData) {
        new GetUserProfileAsyncTask(userProfileTableMutableLiveData, userProfileDao).execute();
    }

    private static class GetUserProfileAsyncTask extends AsyncTask<Void, Void, UserProfileTable> {

        private MutableLiveData<UserProfileTable> mUserProfileMutableLiveData;
        private UserProfileDao mUserProfileDao;

        private GetUserProfileAsyncTask(MutableLiveData<UserProfileTable> userProfileMutableLiveData, UserProfileDao userProfileDao){
            mUserProfileDao = userProfileDao;
            mUserProfileMutableLiveData = userProfileMutableLiveData;
        }

        @Override
        protected UserProfileTable doInBackground(Void... notes) {

            UserProfileTable userProfileTable = mUserProfileDao.getUserProfileByUniqueId(AccessPreferences.get(HapisApplication.getApplication(), ApplicationConstants.LOGGED_IN_USER_GUID, null));

            return userProfileTable;
        }

        @Override
        protected void onPostExecute(UserProfileTable userProfileTable) {
            super.onPostExecute(userProfileTable);

            if(userProfileTable != null){
                mUserProfileMutableLiveData.postValue(userProfileTable);
            }
        }
    }

    public void updateUserProfile(MutableLiveData<UserResponse> userProfileMutableLiveData, String firstName, String middleName, String lastName,
                                  String mobileNumber, String emailAddress, Integer gender, Date dateOfBirth){
        new UpdateUserProfileAsyncTask(userProfileMutableLiveData, userProfileDao, firstName, middleName, lastName, mobileNumber, emailAddress, gender, dateOfBirth).execute();
    }

    private static class UpdateUserProfileAsyncTask extends AsyncTask<Void, Void, UserRequest> {

        private MutableLiveData<UserResponse> mUserProfileMutableLiveData;
        private UserProfileDao mUserProfileDao;
        private String firstName;
        private String middleName;
        private String lastName;
        private String mobileNumber;
        private String emailAddress;
        private Integer gender;
        private Date dateOfBirth;
        private UserProfileTable userProfileTable;

        private UpdateUserProfileAsyncTask(MutableLiveData<UserResponse> userProfileMutableLiveData, UserProfileDao userProfileDao, String firstName, String middleName, String lastName,
                                           String mobileNumber, String emailAddress, Integer gender, Date dateOfBirth){
            mUserProfileDao = userProfileDao;
            mUserProfileMutableLiveData = userProfileMutableLiveData;
            this.firstName = firstName;

            this.gender = gender;
            this.middleName = middleName;
            this.lastName = lastName;
            this.mobileNumber = mobileNumber;
            this.emailAddress = emailAddress;
            this.dateOfBirth = dateOfBirth;
        }

        @Override
        protected UserRequest doInBackground(Void... notes) {
            userProfileTable = mUserProfileDao.getUserProfileByUniqueId(AccessPreferences.get(HapisApplication.getApplication(), ApplicationConstants.LOGGED_IN_USER_GUID, null));
            UserRequest userRequest = null;
            if(userProfileTable != null){
                userRequest = new UserRequest();

                userRequest.setFirstName(firstName);
                userProfileTable.setFirstName(firstName);

                userRequest.setMiddleName(middleName);
                userProfileTable.setMiddleName(middleName);

                if(lastName != null){
                    userRequest.setLastName(lastName);
                    userProfileTable.setLastName(lastName);
                }
                userRequest.setMobileNo(mobileNumber);
                userProfileTable.setMobileNumber(mobileNumber);

                if(emailAddress != null) {
                    userRequest.setEmailAddress(emailAddress);
                    userProfileTable.setEmail(emailAddress);
                }
                if(gender != null && gender.intValue() > 0) {
                    userRequest.setGender(gender);
                    userProfileTable.setGender(gender.intValue() == 1 ? "Male" : "Female");
                }
                if(dateOfBirth != null) {
                    userRequest.setDateOfBirth(dateOfBirth);
                    userProfileTable.setDateOfBirth(dateOfBirth.getTime());
                }
                if(userProfileTable.getEnterpriseCode() != null){
                    userRequest.setEnterpriseCode(userProfileTable.getEnterpriseCode());
                }
                if(userProfileTable.getRoles() != null){
                    userRequest.setRoles(userProfileTable.getRoles());
                }
                userRequest.setUserType(HapisApplication.getApplication().getResources().getInteger(R.integer.application_type));
                userRequest.setUserCode(userProfileTable.getUniqueId());
            }

            return userRequest;
        }

        @Override
        protected void onPostExecute(UserRequest userRequest) {
            super.onPostExecute(userRequest);

            updateUser(mUserProfileMutableLiveData, userRequest, mUserProfileDao, userProfileTable);
        }
    }

    private static void updateUser(final MutableLiveData<UserResponse> mutableLiveData, final UserRequest userRequest, final UserProfileDao userProfileDao, final UserProfileTable userProfileTable) {

        RestCall restCall = new RestCall();
        restCall.setOnRestCallListener(new RestCall.RestCallListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                ResponseStatus responseStatus = new ResponseStatus();
                responseStatus.setStatusCode(ResponseStatus.FAILED);

                UserResponse userModelResponse = new UserResponse();
                userModelResponse.setStatus(responseStatus);

                mutableLiveData.postValue(userModelResponse);
            }

            @Override
            public void onResponse(RestCall.Result result, String response, List<ErrorMessage> errorMessages, String msg) {
                if (result == RestCall.Result.FAILED || result == RestCall.Result.EXCEPTION) {
                    ResponseStatus responseStatus = new ResponseStatus();
                    responseStatus.setStatusCode(ResponseStatus.FAILED);

                    UserResponse userModelResponse = new UserResponse();
                    userModelResponse.setStatus(responseStatus);

                    mutableLiveData.postValue(userModelResponse);
                } else {
                    try {
                        if(response != null && response.length() > 0) {
                            UserResponse userResponse = JSONAdaptor.fromJSON(response, UserResponse.class);
                            if (userResponse != null) {

                                userProfileTable.setProfileUpdatedDate(new Date().getTime());
                                userProfileDao.update(userProfileTable);

                                mutableLiveData.postValue(userResponse);
                            }else{
                                ResponseStatus responseStatus = new ResponseStatus();
                                responseStatus.setStatusCode(ResponseStatus.FAILED);

                                UserResponse userModelResponse = new UserResponse();
                                userModelResponse.setStatus(responseStatus);

                                mutableLiveData.postValue(userModelResponse);
                            }
                        }else{
                            ResponseStatus responseStatus = new ResponseStatus();
                            responseStatus.setStatusCode(ResponseStatus.FAILED);

                            UserResponse userModelResponse = new UserResponse();
                            userModelResponse.setStatus(responseStatus);

                            mutableLiveData.postValue(userModelResponse);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ResponseStatus responseStatus = new ResponseStatus();
                        responseStatus.setStatusCode(ResponseStatus.FAILED);

                        UserResponse userModelResponse = new UserResponse();
                        userModelResponse.setStatus(responseStatus);

                        mutableLiveData.postValue(userModelResponse);
                    }
                }
            }
        });
        try {
            restCall.put(null, false, "Loading items",
                    HapisApplication.getApplication().getBackendUrl()+"9300" + RestConstants.UPDATE_USER_URL+userRequest.getUserCode(), JSONAdaptor.toJSON(userRequest));
        } catch (IOException e) {
            e.printStackTrace();
            ResponseStatus responseStatus = new ResponseStatus();
            responseStatus.setStatusCode(ResponseStatus.FAILED);

            UserResponse userModelResponse = new UserResponse();
            userModelResponse.setStatus(responseStatus);

            mutableLiveData.postValue(userModelResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseStatus responseStatus = new ResponseStatus();
            responseStatus.setStatusCode(ResponseStatus.FAILED);

            UserResponse userModelResponse = new UserResponse();
            userModelResponse.setStatus(responseStatus);

            mutableLiveData.postValue(userModelResponse);
        }
    }
}
