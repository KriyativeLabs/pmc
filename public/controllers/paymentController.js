pmcApp.controller('paymentController', ['$scope', '$location','$modal', '$log', 'apiService', 'cookieService', 'constantsService',
    function ($scope, $location,$modal, $log, apiService, cookieService, constantsService) {

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

//############################################Modal###########################################
        $scope.open = function (id) {
            var modalInstance = $modal.open({
                templateUrl: 'receiptModal.html',
                controller: PaymentReceiptCtrl
            });

            modalInstance.result.then(function (selected) {
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
//###########################################End##############################################

    }]);

var PaymentReceiptCtrl = function ($scope, $modalInstance, $location, apiService) {
    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.createOrUpdate = function () {
        var createObj = {};
        createObj.customerId = $scope.cust_id;
        createObj.paidAmount = $scope.amount;
        createObj.paidOn = $scope.paidOn;
        createObj.discountedAmount = $scope.discount;
        createObj.remarks = $scope.remarks;
        createObj.companyId = -1;
        createObj.agentId = -1;

        apiService.POST("/payments", createObj).then(function (response) {
            console.log(response.data.data);
            $scope.alerts = [];
            $scope.alerts.push({type: 'success', msg: "Receipt Successfully Generated!"});
            $location.path("/payments");
        }, function (errorResponse) {
            $scope.alerts = [];
            $scope.alerts.push({ type: 'danger', msg: errorResponse.data.message});
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
            $scope.code = "";
        });
    };

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };
};