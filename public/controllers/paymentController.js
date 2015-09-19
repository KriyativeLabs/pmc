pmcApp.controller('paymentController', ['$scope', '$location', 'apiService', 'cookieService', 'constantsService',
    function ($scope, $location, apiService, cookieService, constantsService) {

        $scope.getCustomers = function () {
            apiService.GET("/customers").then(function (response) {
                console.log(response);
                $scope.customers = response.data.data;
            }, function (errorResponse) {
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        console.log($scope.customerssss);

        var query = $location.search().cust_id;
        if (!query) {
            query = 0;
        }
        console.log(query);


        $scope.recordPayment = function () {
            var createObj = {};
            createObj.customerId = $scope.cust_id;
            createObj.paidAmount = $scope.amount;
            createObj.discountedAmount = $scope.discount;
            createObj.emailId = $scope.email;
            createObj.city = $scope.city;
        };

    }]);