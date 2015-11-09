pmcApp.controller('planController', ['$scope', '$filter', '$location', '$modal', '$log', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnDefBuilder',
    function ($scope, $filter, $location, $modal, $log, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnDefBuilder) {

        $scope.sNo = 1;
        $scope.getPlans = function () {
            apiService.GET("/plans").then(function (response) {
                $scope.plans = response.data.data;
            }, function (errorResponse) {
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.delete = function (id, name) {
            var userConfirmation = confirm("Are you sure you want to delete plan:" + name);
            if (userConfirmation) {
                apiService.DELETE("/plans/" + id).then(function (response) {
                    apiService.NOTIF_SUCCESS(response.data.message);
                    $scope.getPlans();
                }, function (errorResponse) {
                    apiService.NOTIF_ERROR(errorResponse.data.message);
                    if (errorResponse.status != 200) {
                        if (errorResponse.status == 304)
                            alert(errorResponse);
                    }
                });
            }
        };
//############################################Modal###########################################
        $scope.open = function () {
            var modalInstance = $modal.open({
                templateUrl: 'planCreate.html',
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
        $scope.openUpdate = function (planId, planName, planAmount, planNoOfMonths) {
            var modalInstance = $modal.open({
                templateUrl: 'planCreate.html',
                controller: PlanUpdateCtrl,
                resolve: {
                    planId: function () {
                        return planId;
                    },
                    planName: function () {
                        return planName;
                    },
                    planAmount: function () {
                        return planAmount;
                    },
                    planNoOfMonths: function () {
                        return planNoOfMonths;
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


var PlanCreateCtrl = function ($scope, $modalInstance, $location, apiService) {
    $scope.title = "Create";

    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.planFunc = function () {
        var createObj = {};
        createObj.name = $scope.name;
        createObj.amount = $scope.amount;
        createObj.noOfMonths = $scope.duration;
        createObj.companyId = -1;

        apiService.POST("/plans", createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $modalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };
};


var PlanUpdateCtrl = function ($scope, $modalInstance, $location, apiService, planId, planName, planAmount, planNoOfMonths) {
    $scope.title = "Update";
    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
    $scope.name = planName;
    $scope.amount = planAmount;
    $scope.duration = planNoOfMonths;

    $scope.planFunc = function () {
        var createObj = {};
        createObj.id = parseInt(planId);
        createObj.name = $scope.name;
        createObj.amount = $scope.amount;
        createObj.noOfMonths = $scope.duration;
        createObj.companyId = -1;

        apiService.PUT("/plans/" + planId, createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $modalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };
};