pmcApp.controller('mainController', ['$scope', '$location', '$window', '$rootScope', '$route', '$uibModal', '$log', 'apiService', 'cookieService', 'constantsService', 'ngProgressFactory',
    function ($scope, $location, $window, $rootScope, $route, $uibModal, $log, apiService, cookieService, constantsService, ngProgressFactory) {
        if (!cookieService.get(constantsService.TOKEN)) {
            $window.location.href = "login.html";
        }
        
        // OFFLINE CODE STARTS
        $rootScope.online = navigator.onLine;
        $window.addEventListener("offline", function () {
            $rootScope.$apply(function () {
                $rootScope.online = false;
            });
        }, false);
        $window.addEventListener("online", function () {
            $rootScope.$apply(function () {
                $rootScope.online = true;
            });
        }, false);
        // OFFLINE CODE ENDS

        $scope.isPLoading = false;
        $scope.isError = false;
        $scope.loader = false;
        $scope.progressbar = ngProgressFactory.createInstance();

        var modalInstance = $uibModal;
        $scope.openLoader = function () {
            $scope.progressbar.start();
            $scope.modalOpenInstance = modalInstance.open({
                templateUrl: 'loading.html',
                backdrop: 'static',
                windowClass: 'center-modal'

            });
        };
        $scope.closeLoader = function () {
            $scope.progressbar.complete();
            $scope.modalOpenInstance.close();
        };



        $scope.titleClass = "fa fa-pie-chart";
        $scope.title = "Dashboard";

        $scope.isActive = function (viewLocation) {
            if ($location.path().match('/dashboard')) {
                $scope.titleClass = "fa fa-pie-chart";
                $scope.title = "Dashboard";
            } else if ($location.path().match('/customers/')) {
                $scope.titleClass = "fa fa-user";
                $scope.title = "Customer Details";
            } else if ($location.path().match('/customers')) {
                $scope.titleClass = "fa fa-users";
                $scope.title = "Customers";
            } else if ($location.path().match('/payments')) {
                $scope.titleClass = "i i-stack2 icon";
                $scope.title = "Payments";
            } else if ($location.path().match('/areas')) {
                $scope.titleClass = "fa fa-map-marker";
                $scope.title = "Areas";
            } else if ($location.path().match('/plans')) {
                $scope.titleClass = "i i-tag2 icon";
                $scope.title = "Plans";
            } else if ($location.path().match('/agents/')) {
                $scope.titleClass = "fa fa-user";
                $scope.title = "Agent Details";
            } else if ($location.path().match('/agents')) {
                $scope.titleClass = "fa fa-user";
                $scope.title = "Agents";
            } else if ($location.path().match('/settings')) {
                $scope.titleClass = "fa fa-cog";
                $scope.title = "Settings";
            }

            return ($location.path().match(viewLocation));
        };

        $scope.reloadRoute = function () {
            $route.reload();
        }

        $scope.logout = function () {
            cookieService.destroy();
        };

        $scope.truncateName = function (name) {

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

        //        $scope.getNotifications();

        $scope.username = cookieService.get(constantsService.USERNAME).replace(/\b\w/g, function (txt) {
            return txt.toUpperCase();
        });

        $scope.isAgent = (cookieService.get(constantsService.ACC_TYPE) == "AGENT");

        $scope.bSmsEnable = (!(cookieService.get(constantsService.BULK_SMS) == "true") || $scope.isAgent);
        $scope.balanceReminder = !(cookieService.get(constantsService.BALANCE_REMINDER) == "true") || $scope.isAgent;

        $scope.mso = cookieService.get(constantsService.MSO);

        $scope.companyName = cookieService.get(constantsService.COMPANY_NAME).replace(/\b\w/g, function (txt) {
            return txt.toUpperCase();
        });

        $scope.isInvalidRemark = function (remark) {
            if (remark == "No Problems" | remark == "") {
                return true;
            } else {
                return false;
            }
        };


        //############################################Modal###########################################
        $scope.openReceipt = function (customerId) {
            var modalInstance = $uibModal.open({
                templateUrl: 'receiptModal.html',
                controller: PaymentReceiptCtrl,
                backdrop: 'static',
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
            var modalInstance = $uibModal.open({
                templateUrl: 'smsModal.html',
                backdrop: 'static',
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
            var modalInstance = $uibModal.open({
                templateUrl: 'changePasswordModal.html',
                backdrop: 'static',
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

var PaymentReceiptCtrl = function ($scope, $uibModalInstance, $timeout, $location, apiService, commonService, customerId) {

    $scope.discount = 0;
    $scope.remarks = "No Problems";
    $scope.isError = false;
    var customerData = {};
    if (customerId != 0) {
        $scope.isPLoading = true;
        apiService.GET("/customers/" + customerId).then(function (response) {
            customerData = response.data.data;
            $scope.id = customerData.id;
            $scope.name = customerData.name;
            $scope.houseNo = customerData.houseNo;
            $scope.pending_amount = customerData.balanceAmount;
            $scope.amount = customerData.balanceAmount;
            $scope.isPLoading = false;
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            $scope.isPLoading = false;
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });

    }

    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.recordPayment = function () {
        if ($scope.pending_amount < $scope.amount + $scope.discount) {
            $scope.isError = true;
        } else {
            $scope.isError = false;
            $scope.isPLoading = true;

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
                $scope.isPLoading = false;
                $uibModalInstance.close($scope.dt);
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                $scope.isPLoading = false;
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
                $scope.code = "";
            });
        }

    };

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };

    $scope.open = function () {
        $timeout(function () {
            $scope.opened = true;
        });
    };

    $scope.dateOptions = {
        maxDate: new Date(),
        //minDate: new Date(),
        startingDay: 1
    };

    $scope.today = function () {
        $scope.paidOn = new Date();
    };
    $scope.today();

};

var SmsCtrl = function ($scope, $uibModalInstance, $timeout, $location, apiService) {

    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
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
            $uibModalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
            $scope.code = "";
        });
    };
};

var PasswordChangeCtrl = function ($scope, $uibModalInstance, $timeout, $location, apiService) {
    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.updatePassword = function () {
        if ($scope.new_password == $scope.re_new_password) {
            var createObj = {};
            createObj.oldPassword = $scope.old_password;
            createObj.newPassword = $scope.new_password;

            apiService.POST("/users/changepassword ", createObj).then(function (response) {
                apiService.NOTIF_SUCCESS(response.data.message);
                $uibModalInstance.close($scope.dt);
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