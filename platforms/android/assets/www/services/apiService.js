pmcApp.factory('apiService', ['$http', 'cookieService', 'constantsService', 'Notification', function ($http, cookieService, constantsService, Notification) {
    var apiURL = 'http://paymyinternet.in/api/v1';
    var doRequest = function (path, method, data) {
        var authToken = cookieService.get(constantsService.TOKEN);
        if (authToken) {
            $http.defaults.headers.common.Authorization = authToken;
        }
        $http.defaults.headers.contentType = 'application/json';
        if (method == 'POST' || method == 'PUT') {
            return $http({
                method: method,
                url: apiURL + path,
                /*
                 headers:{
                 'Content-Type':'application/json'
                 },*/
                data: data
            });
        } else if (method == 'GET' || method == 'DELETE') {
            return $http({
                method: method,
                url: apiURL + path/*,
                 headers:{
                 'Content-Type':'application/json'
                 }*/
            });
        }
    };

    var errorTime = function (msg) {
        Notification.error({message: msg, delay: 10000, positionY: 'top', positionX: 'center'});
    };

    var successTime = function (msg) {
        Notification.success({message: msg, delay: 5000, positionY: 'top', positionX: 'center'});
    };

    return {
        POST: function (path, data) {
            return doRequest(path, "POST", data);
        },
        PUT: function (path, data) {
            return doRequest(path, "PUT", data);
        },
        GET: function (path) {
            return doRequest(path, "GET", "");
        },
        DELETE: function (path) {
            return doRequest(path, "DELETE", "");
        },
        NOTIF_SUCCESS: function (msg) {
            return successTime(msg);
        },
        NOTIF_ERROR: function (msg) {
            return errorTime(msg);
        }
    };
}]);

