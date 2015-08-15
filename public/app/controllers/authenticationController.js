app.controller('authenticationController', ['$scope','$window', 'cookieService', 'apiService', 'constantsService',
    function($scope, $window, cookieService, apiService,constantsService) {
        $scope.loginid="krupa_login_1";
        $scope.password="password1";
        $scope.isError = false;
        $scope.error = "";
        //cookieService.remove(constantsService.TOKEN);
        if(cookieService.get(constantsService.TOKEN)){
            $window.location.href = "/views/layout";
            //$location.path("/layout");

        }
        $scope.login = function(){
            apiService.POST("/login",'{"loginId":"'+$scope.loginid+'","password":"'+$scope.password+'"}').then(function(response){
                cookieService.set(constantsService.TOKEN,response.data.data.token);
                cookieService.set(constantsService.USERNAME, response.data.data.name);
                cookieService.set(constantsService.COMPANY_NAME, response.data.data.company);
                $window.location.href = "/views/layout";
                //$location.path("/layout");
            },function(errorRespose){
                if(errorRespose.status !=200){
                    $scope.isError = true;
                    $scope.error = "Login failed due to incorrect login and password!"
                }
            });

        }
    }]);