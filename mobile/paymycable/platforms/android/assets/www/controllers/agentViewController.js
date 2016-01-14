pmcApp.controller('agentViewController', ['$scope', '$filter', '$location', '$modal', '$log', 'apiService', 'commonService', 'DTOptionsBuilder', 'DTColumnBuilder',
    function ($scope, $filter, $location, $modal, $log, apiService, commonService, DTOptionsBuilder, DTColumnBuilder) {

        var agentId = $location.path().split(/[\s/]+/).pop();
        if (angular.isNumber(parseInt(agentId))) {
            var getAgentData = function () {
                apiService.GET("/users/"+agentId).then(function (result) {
                    console.log(result.data.data);
                    $scope.agent = result.data.data;

                    //payments
                    apiService.GET("/users/"+agentId+"/payments/today").then(function (result) {
                        $scope.paymentstoday = result.data.data;
                    }, function (errorResponse) {
                        apiService.NOTIF_ERROR(errorResponse.data.message);
                        if (errorResponse.status != 200) {
                            console.log(errorResponse);
                        }
                    });

                    //payments old
                    apiService.GET("/users/"+agentId+"/payments").then(function (result) {
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
            getAgentData();
        }

        $scope.getTotal = function(){
            var total = 0;
            for(var i = 0; i < $scope.paymentstoday.length; i++){
                var receipt = $scope.paymentstoday[i];
                total += receipt.paidAmount;
            }
            return total;
        };

        $scope.dtOptions = DTOptionsBuilder.newOptions()
            .withOption('responsive', true)
            .withDOM('<"row"<"col-sm-6"i><"col-sm-6"p>>tr')
            .withPaginationType('full_numbers')
            .withDisplayLength(40)
            .withOption('language', {
                paginate: {
                    next: "",
                    previous: ""
                },
                search: "Search: ",
                lengthMenu: "_MENU_ records per page"
            });

        $scope.dtColumns = [
            DTColumnBuilder.newColumn('receiptNo').withTitle('Receipt No.'),
            DTColumnBuilder.newColumn('custName').withTitle('Customer Name').withClass('all'),
            DTColumnBuilder.newColumn('paidOn').withTitle('Paid On').withClass('all'),
            DTColumnBuilder.newColumn('amount').withTitle('Amount'),
            DTColumnBuilder.newColumn('remarks').withTitle('Remarks')
        ];



    }]);

