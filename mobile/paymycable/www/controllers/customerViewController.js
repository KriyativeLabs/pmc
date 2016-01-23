pmcApp.controller('customerViewController', ['$scope', '$filter', '$location', '$modal', '$log', 'apiService', 'commonService', 'DTOptionsBuilder', 'DTColumnBuilder',
    function ($scope, $filter, $location, $modal, $log, apiService, commonService, DTOptionsBuilder, DTColumnBuilder) {

        $scope.displayPayments = true;
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
                        if($scope.payments.length == 0) {
                            $scope.displayPayments = false;
                        }

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
            DTColumnBuilder.newColumn('receiptNo').withTitle('Receipt No.').withClass('all'),
            DTColumnBuilder.newColumn('paidOn').withTitle('Paid On').withClass('all'),
            DTColumnBuilder.newColumn('amount').withTitle('Amount'),
            DTColumnBuilder.newColumn('remarks').withTitle('Remarks'),
            DTColumnBuilder.newColumn('agent').withTitle('Agent').withClass('all')
        ];

    }]);

