pmcApp.controller('customerViewController', ['$scope', '$filter', '$location', '$uibModal', '$log', 'apiService', 'commonService','constantsService',
    function ($scope, $filter, $location, $uibModal, $log, apiService, commonService,constantsService) {

        $scope.isLoading = false;
        $scope.displayPayments = true;
        $scope.sbtname = constantsService.SBT_NAME;
        $scope.boxseriesname = constantsService.BOX_SERIES;
        $scope.cafname = constantsService.CAF;
        $scope.cheader = constantsService.C_CON_HEADER;
        $scope.openLoader();
        
        var custId = $location.path().split(/[\s/]+/).pop();
        $scope.openLoader();
        if (angular.isNumber(parseInt(custId))) {
            var getCustomerData = function () {
                apiService.GET("/customers/" + custId).then(function (result) {
                    console.log(result.data.data);
                    var custRes = result.data.data;
                    //----- get plan details --
                    apiService.GET("/plans").then(function (result) {
                        $scope.plans = result.data.data;
                        $scope.customer = custRes.customer;
                        $scope.hideRemider = ($scope.balanceReminder || $scope.customer.balanceAmount == 0);
                        $scope.connections = custRes.connections;
                    }, function (errorResponse) {
                        apiService.NOTIF_ERROR(errorResponse.data.message);
                        if (errorResponse.status != 200) {
                            console.log(errorResponse);
                        }
                    });

                    //payments
                    apiService.GET("/customers/" + custId + "/payments").then(function (result) {
                        $scope.payments = result.data.data;
                        if ($scope.payments.length == 0) {
                            $scope.displayPayments = false;
                        }
                        console.log($scope.payments);
                    }, function (errorResponse) {
                        apiService.NOTIF_ERROR(errorResponse.data.message);
                        if (errorResponse.status != 200) {
                            console.log(errorResponse);
                        }
                    });
                    
                    apiService.GET("/credits/" + custId).then(function (result) {
                        $scope.credits = result.data.data;
                        if ($scope.credits.length == 0) {
                            $scope.displayCredits = false;
                        } else {
                            $scope.displayCredits = true;
                        }
                        console.log($scope.credits);
                        $scope.closeLoader();
                    }, function (errorResponse) {
                        $scope.closeLoader();
                        apiService.NOTIF_ERROR(errorResponse.data.message);
                        if (errorResponse.status != 200) {
                            console.log(errorResponse);
                        }
                    });
                    
                }, function (errorResponse) {
                    $scope.closeLoader();
                    apiService.NOTIF_ERROR(errorResponse.data.message);
                    if (errorResponse.status != 200) {
                        console.log(errorResponse);
                    }
                });
            };
            getCustomerData();
        }
        
        $scope.activate = funtion(id){
            $scope.progressbar.start();
            $scope.isLoading = true;
            apiService.POST("/connections/" + id + "/activate", {}).then(function (result) {
                $scope.progressbar.complete();
                $scope.isLoading = false;
                apiService.NOTIF_SUCCESS(result.data.message)
            }, function (errorResponse) {
                $scope.progressbar.complete();
                $scope.isLoading = false;
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        
        $scope.showActive = funtion(con){
            if(con.msoStatus) {
                if(con.msoStatus == "ACTIVE") {
                    return false;
                } else (con.msoStatus == "IN_ACTIVE") {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        
        $scope.deactivate = funtion(id){
            $scope.progressbar.start();
            $scope.isLoading = true;
            apiService.POST("/connections/" + id + "/deactivate", {}).then(function (result) {
                $scope.progressbar.complete();
                $scope.isLoading = false;
                apiService.NOTIF_SUCCESS(result.data.message)
            }, function (errorResponse) {
                $scope.progressbar.complete();
                $scope.isLoading = false;
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        
        $scope.balanceAlert = function () {
            $scope.progressbar.start();
            $scope.openLoader();
            apiService.GET("/customers/" + custId + "/balance_reminder").then(function (result) {
                $scope.progressbar.complete();
                $scope.closeLoader();
                apiService.NOTIF_SUCCESS("Balance Reminder Sent Successfully");
            }, function (errorResponse) {
                $scope.progressbar.complete();
                $scope.closeLoader();
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        }
        $scope.connectionPlan = function (planId) {
            var plan = {};
            angular.forEach($scope.plans, function (value, key) {
                if (value.id == planId) {
                    plan = value;
                }
            });
            console.log(plan.name);
            return plan.name + "-" + plan.amount;
        };

    }]);