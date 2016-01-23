pmcApp.controller('mainController', ['$scope', '$location', '$modal', '$log', 'apiService', 'cookieService', 'constantsService',
    function ($scope, $location, $modal, $log, apiService, cookieService, constantsService) {

        $scope.titleClass = "i i-chart icon";
        $scope.title = "Dashboard";
        $scope.isActive = function (viewLocation) {
            if ($location.path().match('/dashboard')) {
                $scope.titleClass = "i i-chart icon";
                $scope.title = "Dashboard";
            } else if ($location.path().match('/customers')) {
                $scope.titleClass = "i i-users2 icon";
                $scope.title = "Customers";
            } else if ($location.path().match('/payments')) {
                $scope.titleClass = "i i-stack2 icon";
                $scope.title = "Payments";
            } else if ($location.path().match('/areas')) {
                $scope.titleClass = "i i-pin icon";
                $scope.title = "Areas";
            } else if ($location.path().match('/plans')) {
                $scope.titleClass = "i i-tag2 icon";
                $scope.title = "Plans";
            } else if ($location.path().match('/agents')) {
                $scope.titleClass = "i i-user2 icon";
                $scope.title = "Agents";
            }

            return ($location.path().match(viewLocation));
        };


        $scope.logout = function () {
            cookieService.destroy();
        };

        $scope.getNotifications = function () {
            apiService.GET("/notifications").then(function (response) {
                $scope.notifications = response.data.data;
                console.log($scope.notifications);
                $scope.notifCount = $scope.notifications.length;

            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.getNotifications();

        $scope.username = cookieService.get(constantsService.USERNAME).replace(/\b\w/g, function (txt) {
            return txt.toUpperCase();
        });

        $scope.companyName = cookieService.get(constantsService.COMPANY_NAME).replace(/\b\w/g, function (txt) {
            return txt.toUpperCase();
        });

        $scope.isAgent = (cookieService.get(constantsService.ACC_TYPE) == "AGENT");


//############################################Modal###########################################
        $scope.openReceipt = function (customerId) {
            var modalInstance = $modal.open({
                templateUrl: 'receiptModal.html',
                controller: PaymentReceiptCtrl,
                resolve: {
                    customerId: function () {
                        return customerId;
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

//############################################SMS Modal###########################################
        $scope.openSms = function () {
            var modalInstance = $modal.open({
                templateUrl: 'smsModal.html',
                controller: SmsCtrl
            });

            modalInstance.result.then(function (selected) {
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
//###########################################End##############################################


//############################################SMS Modal###########################################
        $scope.openChangePass = function () {
            var modalInstance = $modal.open({
                templateUrl: 'changePasswordModal.html',
                controller: PasswordChangeCtrl
            });

            modalInstance.result.then(function (selected) {
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
//###########################################End##############################################
    }]);

var PaymentReceiptCtrl = function ($scope, $modalInstance, $timeout, $location, apiService, commonService, customerId) {

    $scope.discount = 0;
    $scope.remarks = "No Problems";

    var customerData = {};
    if (customerId != 0) {
        apiService.GET("/customers/" + customerId).then(function (response) {
            customerData = response.data.data;
            $scope.id = customerData.customer.id;
            $scope.name = customerData.customer.name;
            $scope.houseNo = customerData.customer.houseNo;
            $scope.pending_amount = customerData.customer.balanceAmount;
            $scope.amount = customerData.customer.balanceAmount;

        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
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

    $scope.recordPayment = function () {
        var createObj = {};
        createObj.customerId = $scope.id;
        createObj.paidAmount = $scope.amount;
        createObj.paidOn = commonService.getDateString($scope.paidOn);
        createObj.receiptNo = "";
        createObj.remarks = $scope.remarks;
        createObj.discountedAmount = $scope.discount;
        createObj.companyId = -1;
        createObj.agentId = -1;

        apiService.POST("/payments", createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $modalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
            $scope.code = "";
        });
    };

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };

    var today = new Date();
    $scope.paidOn = today.toLocaleDateString('en-GB');

    $scope.open = function () {
        $timeout(function () {
            $scope.opened = true;
        });
    };
};

var SmsCtrl = function ($scope, $modalInstance, $timeout, $location, apiService) {

    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.sendSms = function () {
        console.log("Sms");
        var createObj = {};
        if ($scope.smsType != null) {
            createObj.smsType = $scope.smsType;
        } else {
            createObj.smsType = "ALL";
        }
        createObj.message = $scope.smsData;

        apiService.POST("/sms", createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $modalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
            $scope.code = "";
        });
    };
};

var PasswordChangeCtrl = function ($scope, $modalInstance, $timeout, $location, apiService) {
    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.updatePassword = function () {
        if ($scope.new_password == $scope.re_new_password) {
            var createObj = {};
            createObj.oldPassword = $scope.old_password;
            createObj.newPassword = $scope.new_password;

            apiService.POST("/users/changepassword ", createObj).then(function (response) {
                apiService.NOTIF_SUCCESS(response.data.message);
                $modalInstance.close($scope.dt);
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        } else {
            apiService.NOTIF_ERROR("New password entered is not matching");
        }
    };
};
