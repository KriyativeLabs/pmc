app.factory('apiService',['$http','cookieService','constantsService', function($http,cookieService,constantsService) {
        var apiURL = 'http://localhost:9000/api/v1';
        var doRequest = function(path, method,data) {
            var authToken = cookieService.get(constantsService.TOKEN);
            if(authToken) {
                $http.defaults.headers.common.Authorization = authToken;
            }
            $http.defaults.headers.contentType = 'application/json';
            if(method == 'POST' || method == 'PUT') {
                return $http({
                    method: method,
                    url: apiURL + path,
                    /*
                    headers:{
                        'Content-Type':'application/json'
                    },*/
                    data: data
                });
            } else if(method == 'GET' || method == 'DELETE') {
                return $http({
                    method: method,
                    url: apiURL + path/*,
                    headers:{
                        'Content-Type':'application/json'
                    }*/
                });
            }
            };
            return {
                POST:function(path,data) {return doRequest(path,"POST",data);},
                PUT:function(path,data) {return doRequest(path,"PUT",data);},
                GET:function(path){return doRequest(path,"GET","");},
                DELETE:function(path){return doRequest(path,"DELETE","");}
            };
    }]);

