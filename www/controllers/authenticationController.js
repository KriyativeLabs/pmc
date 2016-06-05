pmcApp.controller('authenticationController', ['$scope','$window', 'cookieService', 'apiService', 'constantsService',
    function($scope, $window, cookieService, apiService,constantsService) {
        console.log("authentication controller loaded");
        //$scope.loginid="krupa_login_1";
        //$scope.password="Surya@123";
        $scope.isError = false;
        $scope.error = "";
        if(cookieService.get(constantsService.TOKEN)){
            $window.location.href = "index.html";
            //$location.path("/dashboard");
           // $scope.$apply();
        }
        $scope.login = function(){
            apiService.POST("/login?account_type=internet",'{"loginId":"'+$scope.loginid+'","password":"'+$scope.password+'"}').then(function(response){
                apiService.NOTIF_SUCCESS(response.data.message);
                cookieService.set(constantsService.TOKEN,response.data.data.token);
                cookieService.set(constantsService.USERNAME, response.data.data.name);
                cookieService.set(constantsService.COMPANY_NAME, response.data.data.company);
                cookieService.set(constantsService.ACC_TYPE, response.data.data.type);
                $window.location.href = "index.html";
            },function(errorRespose){
                if(errorRespose.status !=200){
                    alert(errorRespose.status);
                    $scope.isError = true;
                    $scope.error = "Login failed due to incorrect login and password!"
                    apiService.NOTIF_ERROR($scope.error);
                }
            });

        }
    }]);
