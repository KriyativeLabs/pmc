pmcApp.controller('authenticationController', ['$scope','$window', 'cookieService', 'apiService', 'constantsService',
    function($scope, $window, cookieService, apiService,constantsService) {
        console.log("authentication controller loaded");
        $scope.loginid="krupa_login_1";
        $scope.password="password1";
        $scope.isError = false;
        $scope.error = "";
        cookieService.remove(constantsService.TOKEN);
        if(cookieService.get(constantsService.TOKEN)){
            $window.location.href = "/";
            //$location.path("/dashboard");
           // $scope.$apply();
        }
        $scope.login = function(){
            apiService.POST("/login",'{"loginId":"'+$scope.loginid+'","password":"'+$scope.password+'"}').then(function(response){
                cookieService.set(constantsService.TOKEN,response.data.data.token);
                console.log(response.data.data.token);
                cookieService.set(constantsService.USERNAME, response.data.data.name);
                cookieService.set(constantsService.COMPANY_NAME, response.data.data.company);
                console.log(response.data.data);
                $window.location.href = "/";
               // $location.path("/dashboard");
                //$scope.$apply();
            },function(errorRespose){
                if(errorRespose.status !=200){
                    $scope.isError = true;
                    $scope.error = "Login failed due to incorrect login and password!"
                }
            });

        }
    }]);