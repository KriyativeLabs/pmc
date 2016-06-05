pmcApp.controller('planController', ['$scope', '$compile', '$filter', '$location', '$uibModal', '$log', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnBuilder',
    function ($scope, $compile, $filter, $location, $uibModal, $log, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnBuilder) {

        $scope.sNo = 1;
        $scope.isLoading=false;
        $scope.getPlans = function () {
            apiService.GET("/plans").then(function (response) {
                $scope.plans = response.data.data;
            }, function (errorResponse) {
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.deletePlan = function (id) {
            var userConfirmation = confirm("Are you sure you want to delete plan?");
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


        $scope.dtOptions = DTOptionsBuilder.newOptions()
            .withOption('createdRow', createdRow)
            .withOption('responsive', true)
            .withDOM('<"row"<"col-sm-6 m-xs"i>>tr')
            //.withPaginationType('full_numbers')
            .withDisplayLength(-1)
            .withOption('order', [2, 'asc'])
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
            DTColumnBuilder.newColumn('name').withTitle('Name').withClass('all'),
            DTColumnBuilder.newColumn('amount').withTitle('Price'),
            DTColumnBuilder.newColumn('noOfMonths').withTitle('No Of Months'),
            DTColumnBuilder.newColumn(null).withTitle('Action').withClass('all').notSortable().renderWith(actionsHtml)
        ];


        function actionsHtml(data, type, full, meta) {
            if ($scope.isAgent) {
                return "";
            } else {
                return '<button ng-click="openUpdate(' + data.id + ')"  ng-hide="' + $scope.isAgent + '" class="btn btn-primary btn-sm" style="padding:1px 17.5px !important;">Edit</button>' +
                    '<button ng-click="deletePlan(' + data.id + ')"  ng-hide="' + $scope.isAgent + '" class="btn btn-danger btn-sm" style="padding:1px 10px !important;">Delete</button>';
            }
        }

        function createdRow(row, data, dataIndex) {
            $compile(angular.element(row).contents())($scope);
        }


//############################################Modal###########################################
        $scope.open = function () {
            var modalInstance = $uibModal.open({
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
        $scope.openUpdate = function (planId) {
            var modalInstance = $uibModal.open({
                templateUrl: 'planCreate.html',
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
        $scope.isLoading=true;
        var createObj = {};
        createObj.name = $scope.name;
        createObj.amount = $scope.amount;
        createObj.noOfMonths = $scope.duration;
        createObj.companyId = -1;

        apiService.POST("/plans", createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $scope.isLoading=false;
            $uibModalInstance.close($scope.dt);
        }, function (errorResponse) {
            $scope.isLoading=false;
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };
};


var PlanUpdateCtrl = function ($scope, $uibModalInstance, $location, apiService, planId) {
    $scope.title = "Update";
    $scope.isLoading=true;
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
        $scope.isLoading=false;
    }, function (errorResponse) {
        apiService.NOTIF_ERROR(errorResponse.data.message);
        $scope.isLoading=false;
        if (errorResponse.status != 200) {
            if (errorResponse.status == 304)
                alert(errorResponse);
        }
    });


    $scope.planFunc = function () {
        $scope.isLoading=true;
        var createObj = {};
        createObj.id = parseInt(planId);
        createObj.name = $scope.name;
        createObj.amount = $scope.amount;
        createObj.noOfMonths = $scope.duration;
        createObj.companyId = -1;

        apiService.PUT("/plans/" + planId, createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $scope.isLoading=false;
            $uibModalInstance.close($scope.dt);
        }, function (errorResponse) {
            $scope.isLoading=false;
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };
};