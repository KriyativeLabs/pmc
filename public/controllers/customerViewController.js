pmcApp.controller('customerViewController', ['$scope', '$filter', '$location', '$modal', '$log', 'apiService', 'commonService', 'cookieService', 'constantsService',
    function ($scope, $filter, $location, $modal, $log, apiService, commonService, cookieService, constantsService) {

        var custId = $location.path().split(/[\s/]+/).pop();
        if (angular.isNumber(parseInt(custId))) {
            var getCustomerData = function () {
                apiService.GET("/customers/"+custId).then(function (result) {
                    console.log(result.data.data);
                    $scope.customer = result.data.data.customer;
                    $scope.connection = result.data.data.connection;

                    //----- get plan details --
                    apiService.GET("/plans/"+$scope.connection.planId).then(function (result) {
                        $scope.plan = result.data.data;
                    }, function (errorResponse) {
                        apiService.NOTIF_ERROR(errorResponse.data.message);
                        if (errorResponse.status != 200) {
                            console.log(errorResponse);
                        }
                    });
                    //payments
                    apiService.GET("/customers/"+custId+"/payments").then(function (result) {
                        $scope.payments = result.data.data;
                        console.log($scope.payments);
                    }, function (errorResponse) {
                        apiService.NOTIF_ERROR(errorResponse.data.message);
                        if (errorResponse.status != 200) {
                            console.log(errorResponse);
                        }
                    });
                }, function (errorResponse) {
                    apiService.NOTIF_ERROR(errorResponse.data.message);
                    if (errorResponse.status != 200) {
                        console.log(errorResponse);
                    }
                });
            };
            getCustomerData();
        }

    }]);

