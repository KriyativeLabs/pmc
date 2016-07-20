pmcApp.controller('agentViewController', ['$scope', '$filter', '$location', '$uibModal', '$log', 'apiService', 'commonService',
    function ($scope, $filter, $location, $uibModal, $log, apiService, commonService) {
        $scope.todayDisplay = true;
        $scope.oldDisplay = true;
        $scope.openLoader();
        var agentId = $location.path().split(/[\s/]+/).pop();
        if (angular.isNumber(parseInt(agentId))) {
            var getAgentData = function () {
                apiService.GET("/users/" + agentId).then(function (result) {
                    console.log(result.data.data);
                    $scope.agent = result.data.data;
                    //payments
                    apiService.GET("/users/" + agentId + "/payments/today").then(function (result) {
                        $scope.paymentstoday = result.data.data;
                        if ($scope.paymentstoday.length == 0) {
                            $scope.todayDisplay = false;
                        }
                    }, function (errorResponse) {
                        apiService.NOTIF_ERROR(errorResponse.data.message);
                        if (errorResponse.status != 200) {
                            console.log(errorResponse);
                        }
                    });
                    //payments old
                    apiService.GET("/users/" + agentId + "/payments").then(function (result) {
                        $scope.payments = result.data.data;
                        console.log($scope.payments);
                        if ($scope.payments.length == 0) {
                            $scope.oldDisplay = false;
                        }
                        $scope.closeLoader();
                    }, function (errorResponse) {
                        apiService.NOTIF_ERROR(errorResponse.data.message);
                        $scope.closeLoader();
                        if (errorResponse.status != 200) {
                            console.log(errorResponse);
                        }
                    });

                }, function (errorResponse) {
                    $scope.closeLoader();
                    apiService.NOTIF_ERROR(errorResponse.data.message);
                    if (errorResponse.status != 200) {
                        console.log(errorResponse);
                    }
                });
            };
            getAgentData();
        }

        $scope.getTotal = function () {
            var total = 0;
            for (var i = 0; i < $scope.paymentstoday.length; i++) {
                var receipt = $scope.paymentstoday[i];
                total += receipt.paidAmount;
            }
            return total;
        };
    }]);