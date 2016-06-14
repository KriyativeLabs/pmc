pmcApp.config(function setUpConfig($httpProvider){
    var interceptorHttp = function ($q, $location,$window,cookieService) {
        return {
            request: function (config) {
                var splitUrl = config.url.split("/");
                if (splitUrl[splitUrl.length-1] !="login" && !splitUrl[splitUrl.length-1].indexOf(".html") > 0){
                    if(!config.headers.Authorization){
                        cookieService.destroy();
                        $window.location.href = "login.html";
                    }
                }
                return config;
            },

            response: function (result) {
                return result;
            },
            responseError: function (rejection) {
                if(rejection.status == 0){
		    //alert(rejection.config.url);
                    //apiService.NOTIF_ERROR("Unable to reach our servers. Please check your internet connection!");
                    //Notification.error({message: "Unable to reach our servers. Please check your internet connection!", delay: 10000});
                    alert("Unable to reach our servers. Please check your internet connection!");
                    window.stop();
                }
                if (rejection.status == 403 || rejection.status == 401) {
                    cookieService.destroy();
                    $window.location.href = "login.html";
                }

                return $q.reject(rejection);
            }
        }
    };
    $httpProvider.interceptors.push(interceptorHttp);
});
