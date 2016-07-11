pmcApp.controller('paymentController', ['$scope', '$location','$uibModal','$timeout', '$log', 'apiService', 'commonService', 'DTOptionsBuilder', 'DTColumnBuilder',
    function ($scope, $location,$uibModal,$timeout, $log, apiService, commonService, DTOptionsBuilder, DTColumnBuilder) {
        $scope.progressbar.start();
        var pageNo = 1;
        var pageSize = 30;
        
        $scope.loading = false;
        $scope.disableScroll = false;
        
        $scope.receipts = [];
        $scope.getReceipts = function () {
            $scope.loading = true;
            apiService.GET("/payments?pageNo="+ pageNo + "&pageSize="+ pageSize).then(function (response) {
                $scope.receipts = $scope.receipts.concat(response.data.data);
                $scope.setTotal();
                $scope.progressbar.complete();
                $scope.loading = false;
            }, function (errorResponse) {
                $scope.progressbar.complete();
                $scope.loading = false;
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        $scope.total = 0;

        $scope.setTotal = function(){
            $scope.total = 0;
            for(var i = 0; i < $scope.receipts.length; i++){
                var receipt = $scope.receipts[i];
                $scope.total += receipt.paidAmount;
            }
        };

        $scope.getReceipts();

        
        $scope.isInvalid = function(remark){
            if(remark == "No Problems" | remark == ""){
                return true;
            } else {
                return false;
            }
        };
        
        $scope.loadNext = function(){
            if (!$scope.loading && !$scope.disableScroll) {
                pageNo = pageNo + 1;
                
                $scope.getReceipts();
            }
        };
        
        var today = new Date();
        $scope.startDate = today;
        $scope.endDate = today;

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
            $scope.progressbar.start();
            $scope.disableScroll = true;
            var startdate = commonService.getDateString($scope.startDate);
            var enddate = commonService.getDateString($scope.endDate);
            apiService.GET("/payments/advanced?startdate="+startdate+"&enddate="+enddate).then(function (response) {
                $scope.receipts = response.data.data;
                pageNo = 1;
                $scope.setTotal();
                $scope.progressbar.complete();
            }, function (errorResponse) {
                pageNo = 1;
                $scope.progressbar.complete();
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        $scope.dateOptions = {
            maxDate: new Date(),
            //minDate: new Date(),
            startingDay: 1
        };

    }]);