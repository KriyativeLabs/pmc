pmcApp.controller('areaController', ['$scope','$compile', '$filter', '$location', '$route', '$uibModal', '$log', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnBuilder',
    function ($scope, $compile, $filter, $location, $route, $uibModal, $log, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnBuilder) {
        $scope.sNo = 1;

        $scope.isLoading = false;

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

        $scope.deleteArea = function (id) {
            var userConfirmation = confirm("Are you sure you want to delete area?");
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
            .withOption('createdRow', createdRow)
            .withOption('responsive', true)
            .withOption('stateSave', true)
            .withDOM('<"row"<"col-sm-12 m-xs"i>>tr')
            .withPaginationType('full_numbers')
            .withDisplayLength(-1)
            .withOption('language', {
                paginate: {
                    next: "",
                    previous: ""
                },
                search: "Search: ",
                lengthMenu: "_MENU_ records per page"
            });

        $scope.dtColumns = [
            DTColumnBuilder.newColumn('sNo').withTitle('S.No'),
            DTColumnBuilder.newColumn('id').withTitle('Id').withClass('none'),
            DTColumnBuilder.newColumn('code').withTitle('Code'),
            DTColumnBuilder.newColumn('name').withTitle('Name').withClass('all'),
            DTColumnBuilder.newColumn(null).withTitle('Action').withClass('all').notSortable().renderWith(actionsHtml)
        ];

        function actionsHtml(data, type, full, meta) {
            return '<button ng-click="openUpdate('+data.id+')" ng-hide="'+$scope.isAgent+'" class="btn btn-primary btn-sm" style="padding:1px 17.5px !important;">Edit</button>'+
                '<button ng-click="deleteArea('+data.id+')" ng-hide="'+$scope.isAgent+'" class="btn btn-danger btn-sm" style="padding:1px 10px !important;">Delete</button>';
        }

        function createdRow(row, data, dataIndex) {
            $compile(angular.element(row).contents())($scope);
        }


        $scope.changeData = function (search) {
            $scope.areas = $filter('filter')($scope.areasBackup, search);
        };

//############################################Modal###########################################
        $scope.open = function () {
            var modalInstance = $uibModal.open({
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
        $scope.openUpdate = function (areaId) {
            var modalInstance = $uibModal.open({
                templateUrl: 'areaCreate.html',
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