pmcApp.service('constantsService',function() {
        var token = 'token';
        var username = 'user_name';
        var apiUrl = "";
        var companyName = 'company_name';
        var accountType = 'account_type';
    return {
        TOKEN:token,
        USERNAME:username,
        API_URL:apiUrl,
        COMPANY_NAME:companyName,
        ACC_TYPE:accountType
    }
});