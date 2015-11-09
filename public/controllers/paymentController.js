pmcApp.controller('paymentController', ['$scope', '$location','$modal', '$log', 'apiService', 'cookieService', 'constantsService',
    function ($scope, $location,$modal, $log, apiService, cookieService, constantsService) {

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

    }]);