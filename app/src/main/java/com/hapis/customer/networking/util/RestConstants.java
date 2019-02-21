package com.hapis.customer.networking.util;

public interface RestConstants {

    // New APIs Header Rest Constants
    String CONTENT_HEADER_VALUE = "application/json";

    int DES_KEY_LENGTH = 16;

    String TRUST_STORE_PASSWORD = "e@s0fTech";

    String USER_AGENT_HEADER = "User-Agent";
    String TRANSPORT_KEY_HEADER = "Trans-Key";
    String REST_AUTHORIZATION_HEADER = "Authorization";
    String REST_AUTHORIZATION_FORMAT = "Bearer %s";
    String CHARSET_HEADER = "Accept-Charset";
    String CONTENT_TYPE_HEADER = "Content-Type";
    String CONTENT_LENGTH_HEADER = "Content-Length";
    String APPLICATION_TYPE_HEADER = "Application-Type";
    String DEVICE_ID_HEADER = "Device-Id";
    String APPLICATION_VERSION_HEADER = "Application-Version";
    String CONTENT_USER_AGENT = "User-Agent";
    String JWT_HEADER = "JWTHeader";


    String UPLOAD_CRASH_LOG_URL = "/Hapis-Module-Customer/rest/common/errorLogUpload";


    String LOGIN_URL                                    = "/users/login";
    String REGISTER_USER_REQUEST_URL                    = "/users";
    String UPDATE_USER_URL                              = "/users/";

    String getEnterprisesByEnterpriseTypeAndCity        = "/enterprises/";/*ENTERPRISE_TYPE_INDIVIDUAL(2) OR ENTERPRISE_TYPE_HOSPITAL/CityName(Bangalore)*//*GET*/
    String getUserEnterprisesByMobileNumber             = "/users/getUserEnterprisesBy/";/*{mobileNo}*/
    String GET_DOCTORS_BY_ENTERPRISECODE_SPECIALIZATION = "/appointments/getDoctors/";/*enterpriseCode/specialization*//*GET*/
    String getAvailableAppointments                     = "/appointments/getAvailableAppointments/";/*{doctorCode}/{requestedDate}/{enterpriseCode}*//*GET*/
    String CREATE_APPOINTMENTS                          = "/appointments";
    String getAppointmentsByCustomer                    = "/appointments/getByCustomerCode/";/*{customerCode}/{status- (601 - Booked, 602-Canceled, 603-Closed)}*/
    String getAppointment                               = "/appointments/";/*{appointmentCode}*/
    String updateAppointment                            = "/appointments/updateAppointment";
    String getEnterpriseByEnterpriseCode                = "/enterprises/";/*{enterpriseCode}*/
    String getUserByCode                                = "/users/";/*{userCode}*/
    String getAppointmentsByDoctor                      = "/appointments/getByDoctor/";/*{doctorCode}/{hospitalCode}/{appointmentDate}*/
    String getAllUsersByEnterpriseCode                  = "/users/enterpriseCode/";/*{enterpriseCode}*/
}
