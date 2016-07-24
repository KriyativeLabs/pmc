pmcApp.controller('areaController', ['$scope', '$compile', '$filter', '$location', '$route', '$uibModal', '$log', 'apiService', 'cookieService', 'constantsService', 'SweetAlert',
    function ($scope, $compile, $filter, $location, $route, $uibModal, $log, apiService, cookieService, constantsService, SweetAlert) {
        $scope.sNo = 1;
        $scope.isLoading = false;
        $scope.progressbar.start();

        $scope.getAreas = function () {
            $scope.openLoader();
            apiService.GET("/areas").then(function (response) {
                $scope.areas = response.data.data;
                $scope.areasBackup = response.data.data;
                $scope.progressbar.complete();
                $scope.closeLoader();
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                $scope.closeLoader();
                $scope.progressbar.complete();
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.deleteArea = function (id) {
            SweetAlert.swal({
                    title: "",
                    text: "Are You Sure? Want to delete area?",
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
                        apiService.DELETE("/areas/" + id).then(function (response) {
                            SweetAlert.swal("", "Deleted!", "success");
                            $scope.getAreas();
                        }, function (errorResponse) {
                            SweetAlert.swal("", errorResponse.data.message, "error");
                            if (errorResponse.status != 200) {
                                if (errorResponse.status == 304)
                                    apiService.NOTIF_ERROR(errorResponse.data.message);
                            }
                        });
                    }
                });
        };
        
        $scope.changeData = function (search) {
            $scope.areas = $filter('filter')($scope.areasBackup, search);
        };

        //############################################Modal###########################################
        $scope.open = function () {
            var modalInstance = $uibModal.open({
                templateUrl: 'areaCreate.html',
                backdrop: 'static',
                controller: AreaCreateCtrl
            });

            modalInstance.result.then(function (selected) {
                $scope.areas = $scope.getAreas();
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        //###########################################End##############################################

        //############################################Modal###########################################
        $scope.openUpdate = function (areaId) {
            var modalInstance = $uibModal.open({
                templateUrl: 'areaCreate.html',
                backdrop: 'static',
                controller: AreaUpdateCtrl,
                resolve: {
                    areaId: function () {
                        return areaId;
                    }
                }
            });

            modalInstance.result.then(function (selected) {
                $scope.areas = $scope.getAreas();
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        //################End##########
    }]);

var AreaCreateCtrl = function ($scope, $uibModalInstance, $location, apiService) {
    $scope.title = "Create";

    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.areaFunc = function () {
        $scope.isLoading = true;
        var createObj = {};
        createObj.code = $scope.code;
        createObj.name = $scope.name;
        createObj.city = "N/A";
        createObj.companyId = -1;
        createObj.idSequence = 0;
        apiService.POST("/areas", createObj).then(function (response) {
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


var AreaUpdateCtrl = function ($scope, $uibModalInstance, $location, apiService, areaId) {
    $scope.title = "Update";
    $scope.isLoading = true;
    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    apiService.GET("/areas/" + areaId).then(function (response) {
        $scope.code = response.data.data.code;
        $scope.name = response.data.data.name;
        $scope.isLoading = false;
    }, function (errorResponse) {
        apiService.NOTIF_ERROR(errorResponse.data.message);
        $scope.isLoading = false;
        if (errorResponse.status != 200) {
            if (errorResponse.status == 304)
                apiService.NOTIF_ERROR(errorResponse.data.message);
        }
    });

    $scope.areaFunc = function () {
        $scope.isLoading = true;
        var createObj = {};
        createObj.id = parseInt(areaId);
        createObj.code = $scope.code;
        createObj.name = $scope.name;
        createObj.city = "N/A";
        createObj.companyId = -1;
        createObj.idSequence = 0;

        apiService.PUT("/areas/" + areaId, createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $scope.isLoading = false;
            $uibModalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            $scope.isLoading = false;
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };

};