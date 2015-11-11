pmcApp.controller('paymentController', ['$scope', '$location','$modal','$timeout', '$log', 'apiService', 'commonService',
    function ($scope, $location,$modal,$timeout, $log, apiService, commonService) {

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
        $scope.startDate = today.toLocaleDateString('en-GB');
        $scope.endDate = today.toLocaleDateString('en-GB');

        $scope.openStart = function () {
            $timeout(function () {
                $scope.openedStart = true;
            });
        };

        $scope.openEnd = function () {
            $timeout(function () {
                $scope.openedEnd = true;
            });
        };



        $scope.advHide = true;
        $scope.toggleadv = function(){
            $scope.advHide = !$scope.advHide;
        };

        $scope.advancedSearch = function(){
            var startdate = commonService.getDateString($scope.startDate);
            var enddate = commonService.getDateString($scope.endDate);
            apiService.GET("/payments/advanced?startdate="+startdate+"&enddate="+enddate).then(function (response) {
                $scope.receipts = response.data.data;
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });


        };


    }]);