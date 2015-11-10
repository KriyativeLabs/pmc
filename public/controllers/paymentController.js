pmcApp.controller('paymentController', ['$scope', '$location','$modal','$timeout', '$log', 'apiService', 'cookieService', 'constantsService',
    function ($scope, $location,$modal,$timeout, $log, apiService, cookieService, constantsService) {

        $scope.getReceipts = function () {
            apiService.GET("/payments").then(function (response) {
                $scope.receipts = response.data.data;
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.getReceipts();

        var today = new Date();
        $scope.dt = today.toLocaleDateString('en-GB');

        $scope.open = function () {
            $timeout(function () {
                $scope.opened = true;
            });
        };

    }]);