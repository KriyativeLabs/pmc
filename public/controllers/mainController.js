pmcApp.controller('mainController', ['$scope', '$location','$modal', '$log', 'apiService', 'cookieService', 'constantsService',
    function ($scope, $location,$modal, $log, apiService, cookieService, constantsService) {
        $scope.username = cookieService.get(constantsService.USERNAME).replace(/\b\w/g, function (txt) {
            return txt.toUpperCase();
        });

        $scope.companyName = cookieService.get(constantsService.COMPANY_NAME).replace(/\b\w/g, function (txt) {
            return txt.toUpperCase();
        });

        $scope.isActive = function (viewLocation) {
            return ($location.path().match(viewLocation));
        };

        $scope.logout = function(){
            console.log("Hello Logout");
            cookieService.destroy();
            $location.path("/login");
        };
        
//############################################Modal###########################################
        $scope.openReciept = function(id) {
            var modalInstance = $modal.open({
                templateUrl: 'receiptModal.html',
                controller: PaymentReceiptCtrl,
                resolve: {
                    customerId: function () {
                        return id;
                    }
                }
            });

            modalInstance.result.then(function (selected) {
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
//###########################################End##############################################
    }]);
var PaymentReceiptCtrl = function ($scope, $modalInstance, $location, apiService, customerId) {

    var customerData = {};
    if (customerId != 0) {
        apiService.GET("/customers/" + customerId).then(function (response) {
            console.log(response.data.data);
            customerData = response.data.data;
        }, function (errorResponse) {
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
        
    }
    
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
