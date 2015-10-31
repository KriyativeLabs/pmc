pmcApp.controller('dashboardController', ['$scope','$window', 'apiService','cookieService','constantsService',
    function($scope,$window, apiService,cookieService,constantsService) {
        $scope.getDashboardData = function(){
            apiService.GET("/dashboarddata").then(function(response){
                console.log(response);
                $scope.unpaidCustomers = response.data.data.unpaidCustomers;
                $scope.paidCustomers = response.data.data.totalCustomers - response.data.data.unpaidCustomers;
                $scope.balanceAmount = response.data.data.balanceAmount;
                $scope.amountCollected = response.data.data.amountCollected;
            },function(errorResponse){      
                if(errorResponse.status !=200){
                    console.log(errorResponse);
                }
            });
        };
        $scope.getDashboardData();
        
        $scope.username=cookieService.get(constantsService.USERNAME);//.replace(/\b\w/g, function (txt) { return txt.toUpperCase(); });
        $scope.companyName=cookieService.get(constantsService.COMPANY_NAME);//.replace(/\b\w/g, function (txt) { return txt.toUpperCase(); });
    }]);