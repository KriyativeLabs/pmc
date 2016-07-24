pmcApp.controller('planController', ['$scope', '$compile', '$filter', '$location', '$uibModal', '$log', 'apiService', 'cookieService', 'constantsService',
                                     'SweetAlert',
    function ($scope, $compile, $filter, $location, $uibModal, $log, apiService, cookieService, constantsService, SweetAlert) {
        $scope.sNo = 1;
        $scope.isLoading = false;
        $scope.progressbar.start();

        $scope.getPlans = function () {
            $scope.openLoader();
            apiService.GET("/plans").then(function (response) {
                $scope.plans = response.data.data;
                $scope.progressbar.complete();
                $scope.closeLoader();
            }, function (errorResponse) {
                $scope.progressbar.complete();
                $scope.closeLoader();
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.deletePlan = function (id) {
            SweetAlert.swal({
                    title: "",
                    text: "Are You Sure? Want to delete plan?",
                    type: "warning",
                    //                    imageSize: '10x10',
                    showCancelButton: true,
                    confirmButtonColor: "#1AAE88",
                    confirmButtonText: "Yes",
                    cancelButtonText: "No",
                    //                    cancelButtonColor: "#DD6B55",   
                    closeOnConfirm: false,
                    closeOnCancel: true
                },
                function (isConfirm) {
                    if (isConfirm) {
                        apiService.DELETE("/plans/" + id).then(function (response) {
                            SweetAlert.swal("", "Deleted!", "success");
                            $scope.getPlans();
                        }, function (errorResponse) {
                            SweetAlert.swal("", errorResponse.data.message, "error");
                            if (errorResponse.status != 200) {
                                if (errorResponse.status == 304)
                                    alert(errorResponse);
                            }
                        });

                    }
                });
        };

        //############################################Modal###########################################
        $scope.open = function () {
            var modalInstance = $uibModal.open({
                templateUrl: 'planCreate.html',
                backdrop: 'static',
                controller: PlanCreateCtrl
            });
            modalInstance.result.then(function (selected) {
                $scope.getPlans();
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        //###########################################End##############################################

        //############################################Modal###########################################
        $scope.openUpdate = function (planId) {
            var modalInstance = $uibModal.open({
                templateUrl: 'planCreate.html',
                backdrop: 'static',
                controller: PlanUpdateCtrl,
                resolve: {
                    planId: function () {
                        return planId;
                    }
                }
            });

            modalInstance.result.then(function (selected) {
                $scope.getPlans();
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        //################End##########

    }]);


var PlanCreateCtrl = function ($scope, $uibModalInstance, $location, apiService) {
    $scope.title = "Create";

    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.planFunc = function () {
        $scope.isLoading = true;
        var createObj = {};
        createObj.name = $scope.name;
        createObj.amount = $scope.amount;
        createObj.noOfMonths = $scope.duration;
        createObj.companyId = -1;

        apiService.POST("/plans", createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $scope.isLoading = false;
            $uibModalInstance.close($scope.dt);
        }, function (errorResponse) {
            $scope.isLoading = false;
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };
};


var PlanUpdateCtrl = function ($scope, $uibModalInstance, $location, apiService, planId) {
    $scope.title = "Update";
    $scope.isLoading = true;
    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    apiService.GET("/plans/" + planId).then(function (response) {
        $scope.name = response.data.data.name;
        $scope.amount = response.data.data.amount;
        $scope.duration = response.data.data.noOfMonths;
        $scope.isLoading = false;
    }, function (errorResponse) {
        apiService.NOTIF_ERROR(errorResponse.data.message);
        $scope.isLoading = false;
        if (errorResponse.status != 200) {
            if (errorResponse.status == 304)
                alert(errorResponse);
        }
    });


    $scope.planFunc = function () {
        $scope.isLoading = true;
        var createObj = {};
        createObj.id = parseInt(planId);
        createObj.name = $scope.name;
        createObj.amount = $scope.amount;
        createObj.noOfMonths = $scope.duration;
        createObj.companyId = -1;

        apiService.PUT("/plans/" + planId, createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $scope.isLoading = false;
            $uibModalInstance.close($scope.dt);
        }, function (errorResponse) {
            $scope.isLoading = false;
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };
};