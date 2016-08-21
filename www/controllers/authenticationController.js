pmcApp.controller('authenticationController', ['$scope','$window', 'cookieService', 'apiService', 'constantsService',
    function($scope, $window, cookieService, apiService,constantsService) {
        console.log("authentication controller loaded");
        $scope.isError = false;
        $scope.error = "";
        $scope.isLoading = false;
        if(cookieService.get(constantsService.TOKEN)){
            $window.location.href = "index.html";
        }
        $scope.login = function(){
            $scope.isLoading = true;
            apiService.POST("/login?account_type="+constantsService.APP_TYPE,'{"loginId":"'+$scope.loginid+'","password":"'+$scope.password+'"}').then(function(response){
                //apiService.NOTIF_SUCCESS(response.data.message);
                cookieService.set(constantsService.TOKEN,response.data.data.token);
                cookieService.set(constantsService.USERNAME, response.data.data.name);
                cookieService.set(constantsService.COMPANY_NAME, response.data.data.company);
                cookieService.set(constantsService.ACC_TYPE, response.data.data.type);
                cookieService.set(constantsService.C_ID, response.data.data.cId);
                cookieService.set(constantsService.BULK_SMS, response.data.data.bSMS);
                cookieService.set(constantsService.BALANCE_REMINDER, response.data.data.balanceReminder);
                cookieService.set(constantsService.MSO, response.data.data.mso);
                $scope.isLoading = false;
                console.log(response.data.data);
                $window.location.href = "index.html";
            },function(errorResponse){
                $scope.isLoading = false;
		        console.log(errorResponse);
                if(errorResponse.status !=200){
                    $scope.isError = true;
                    $scope.error = "Login failed due to incorrect login and password!";
                    apiService.NOTIF_ERROR($scope.error);
                }
            });
        }
    }]);
