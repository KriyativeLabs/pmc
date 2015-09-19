pmcApp.config(function setUpConfig($httpProvider){
    var interceptorHttp = function ($q, $location,$window,cookieService) {
        return {
            request: function (config) {
                var splitUrl = config.url.split("/");
                console.log(config);
                if (splitUrl[splitUrl.length-1] !="login"){
                    if(!config.headers.Authorization){
                        cookieService.destroy();
                        $window.location.href = "/login.html";
                    }
                }
                return config;
            },

            response: function (result) {
                return result;
            },
            responseError: function (rejection) {
                if (rejection.status == 403 || rejection.status == 401) {
                    cookieService.destroy();
                    $window.location.href = "/login.html";
                }

                return $q.reject(rejection);
            }
        }
    };
    $httpProvider.interceptors.push(interceptorHttp);
});