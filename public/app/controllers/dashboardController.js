app.controller('dashboardController', ['$scope','$window', 'apiService','cookieService','constantsService',
    function($scope,$window, apiService,cookieService,constantsService) {
        $scope.unpaidCustomers=0;
        $scope.collectableAmount=0;
        $scope.templateurl = '/customer';
        $scope.$apply();
        $scope.getUnpaidCustomers = function(){
            apiService.GET("/customers/unpaid").then(function(response){
                console.log(response);
                $scope.unpaidCustomers = response.data.data.length;
            },function(errorResponse){
                if(errorResponse.status !=200){
                   console.log(errorResponse);
                }
            });
        };

        $scope.getUnpaidCustomers();

        $scope.paidCustomers=0;
        $scope.getPaidCustomers = function(){
            apiService.GET("/customers/paid").then(function(response){
                console.log(response);
                $scope.paidCustomers = response.data.data.length;

            },function(errorResponse){
                if(errorResponse.status !=200){
                    console.log(errorResponse);
                }
            });
        };
        console.log(cookieService.get(constantsService.TOKEN));
        $scope.getPaidCustomers();

        $scope.username=cookieService.get(constantsService.USERNAME).replace(/\b\w/g, function (txt) { return txt.toUpperCase(); });
        $scope.companyName=cookieService.get(constantsService.COMPANY_NAME).replace(/\b\w/g, function (txt) { return txt.toUpperCase(); });
    }]);