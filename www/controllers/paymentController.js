pmcApp.controller('paymentController', ['$scope', '$location','$uibModal','$timeout', '$log', 'apiService', 'commonService', 'DTOptionsBuilder', 'DTColumnBuilder',
    function ($scope, $location,$uibModal,$timeout, $log, apiService, commonService, DTOptionsBuilder, DTColumnBuilder) {
        $scope.progressbar.start();
        $scope.getReceipts = function () {
            apiService.GET("/payments").then(function (response) {
                $scope.receipts = response.data.data;
                $scope.setTotal();
                $scope.progressbar.complete();
            }, function (errorResponse) {
                $scope.progressbar.complete();
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
            var startdate = commonService.getDateString($scope.startDate);
            var enddate = commonService.getDateString($scope.endDate);
            apiService.GET("/payments/advanced?startdate="+startdate+"&enddate="+enddate).then(function (response) {
                $scope.receipts = response.data.data;
                $scope.setTotal();
                $scope.progressbar.complete();
            }, function (errorResponse) {
                $scope.progressbar.complete();
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });


        };

        $scope.dtOptions = DTOptionsBuilder.newOptions()
            .withOption('responsive', true)
            .withDOM('<"row"<"col-sm-12 m-xs"i>>tr')
            //.withPaginationType('full_numbers')
            .withDisplayLength(-1)
            .withOption('order', [2, 'desc'])
            .withOption('language', {
                paginate: {
                    next: "",
                    previous: ""
                },
                search: "Search: ",
                lengthMenu: "_MENU_ records per page"
            });

        $scope.dtColumns = [
            DTColumnBuilder.newColumn('receiptNo').withTitle('Receipt No').withClass('all'),
            DTColumnBuilder.newColumn('customerDetails').withTitle('Customer Name').withClass('all'),
            DTColumnBuilder.newColumn('paidOn').withTitle('Date'),
            DTColumnBuilder.newColumn('paidAmount').withTitle('Amount').withClass('all'),
            DTColumnBuilder.newColumn('remarks').withTitle('Remarks'),
            DTColumnBuilder.newColumn('agentDetails').withTitle('Agent')
        ];

        $scope.dateOptions = {
            maxDate: new Date(),
            //minDate: new Date(),
            startingDay: 1
        };

    }]);