pmcApp.controller('areaController', ['$scope', '$filter', '$location', '$route', '$modal', '$log', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnDefBuilder',
    function ($scope, $filter, $location, $route, $modal, $log, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnDefBuilder) {
        $scope.sNo = 1;
        $scope.getAreas = function () {
            apiService.GET("/areas").then(function (response) {
                $scope.areas = response.data.data;
                $scope.areasBackup = response.data.data;
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        var areaId = $location.search().id;
        if (!areaId) {
            $scope.isCreate = true;
        } else {
            $scope.code = $location.search().code;
            $scope.name = $location.search().name;
            $scope.isCreate = false;
        }

        $scope.deleteArea = function (id, name) {
            var userConfirmation = confirm("Are you sure you want to delete area:" + name);
            if (userConfirmation) {
                apiService.DELETE("/areas/" + id).then(function (response) {
                    apiService.NOTIF_SUCCESS(response.data.message);
                    $scope.getAreas();
                }, function (errorResponse) {
                    apiService.NOTIF_ERROR(errorResponse.data.message);
                    if (errorResponse.status != 200) {
                        if (errorResponse.status == 304)
                            apiService.NOTIF_ERROR(errorResponse.data.message);
                    }
                });
            }
        };

        $scope.dtOptions = DTOptionsBuilder.newOptions()
            //.withColumnFilter()
            //.withDOM('<"input-group"f>pitrl')
            .withOption('responsive', true)
            .withOption('stateSave', true)
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


        $scope.changeData = function (search) {
            $scope.areas = $filter('filter')($scope.areasBackup, search);
        };

//############################################Modal###########################################
        $scope.open = function () {
            var modalInstance = $modal.open({
                templateUrl: 'areaCreate.html',
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
        $scope.openUpdate = function (areaId, areaName, areaCode) {
            var modalInstance = $modal.open({
                templateUrl: 'areaCreate.html',
                controller: AreaUpdateCtrl,
                resolve: {
                    areaId: function () {
                        return areaId;
                    },
                    areaName: function () {
                        return areaName;
                    },
                    areaCode: function () {
                        return areaCode;
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

var AreaCreateCtrl = function ($scope, $modalInstance, $location, apiService) {
    $scope.title = "Create";

    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.areaFunc = function () {
        var createObj = {};
        createObj.code = $scope.code;
        createObj.name = $scope.name;
        createObj.city = "N/A";
        createObj.companyId = -1;
        createObj.idSequence = 0;
        apiService.POST("/areas", createObj).then(function (response) {
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


var AreaUpdateCtrl = function ($scope, $modalInstance, $location, apiService, areaId, areaName, areaCode) {
    $scope.title = "Update";

    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
    $scope.code = areaCode;
    $scope.name = areaName;

    $scope.areaFunc = function () {
        var createObj = {};
        createObj.id = parseInt(areaId);
        createObj.code = $scope.code;
        createObj.name = $scope.name;
        createObj.city = "N/A";
        createObj.companyId = -1;
        createObj.idSequence = 0;

        apiService.PUT("/areas/" + areaId, createObj).then(function (response) {
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