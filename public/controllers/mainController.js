pmcApp.controller('mainController', ['$scope', '$location', 'apiService', 'cookieService', 'constantsService',
    function ($scope, $location, apiService, cookieService, constantsService) {
        $scope.username = cookieService.get(constantsService.USERNAME).replace(/\b\w/g, function (txt) {
            return txt.toUpperCase();
        });

        $scope.companyName = cookieService.get(constantsService.COMPANY_NAME).replace(/\b\w/g, function (txt) {
            return txt.toUpperCase();
        });

        $scope.isActive = function (viewLocation) {
            return ($location.path().match(viewLocation));
        };

        $scope.getCustomers = function(){
            apiService.GET("/customers").then(function(response){
                $scope.customers = response.data.data;
                $scope.customersBackUp = response.data.data;
            },function(errorResponse){
                if(errorResponse.status !=200){
                   console.log(errorResponse);
                }
            });
        };
        
        $scope.customerssss = $scope.getCustomers();

        $scope.logout = function(){
            console.log("Hello Logout");
            cookieService.destroy();
            //$location.path("/login");
        };

    }]);