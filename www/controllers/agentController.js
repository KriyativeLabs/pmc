pmcApp.controller('agentController', ['$scope', '$compile', '$filter', '$location', '$uibModal', '$log', 'apiService', 'cookieService', 'constantsService', 'SweetAlert',
    function ($scope, $compile, $filter, $location, $uibModal, $log, apiService, cookieService, constantsService, SweetAlert) {

        $scope.isLoading = false;
        $scope.sNo = 1;
        $scope.progressbar.start();

        $scope.getAgents = function () {
            $scope.openLoader();
            apiService.GET("/users").then(function (response) {
                $scope.agents = response.data.data;
                $scope.agentsBackup = response.data.data;
                $scope.progressbar.complete();
                $scope.closeLoader();
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                $scope.progressbar.complete();
                $scope.closeLoader();
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.deleteAgent = function (id) {
            SweetAlert.swal({
                    title: "",
                    text: "Are You Sure? Want to delete agent?",
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
                        apiService.DELETE("/users/" + id).then(function (response) {
                            SweetAlert.swal("", "Deleted!", "success");
                            $scope.getAgents();
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

        $scope.changeData = function (search) {
            $scope.agents = $filter('filter')($scope.agentsBackup, search);
        };
        //############################################Modal###########################################
        $scope.open = function () {
            var modalInstance = $uibModal.open({
                templateUrl: 'agentModal.html',
                backdrop: 'static',
                controller: AgentCreateCtrl
            });

            modalInstance.result.then(function (selected) {
                $scope.getAgents();
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        //###########################################End##############################################

        //############################################Modal###########################################
        $scope.openUpdate = function (agentId, agentName, contactNo, email, loginId, accountType) {
            var modalInstance = $uibModal.open({
                templateUrl: 'agentModal.html',
                backdrop: 'static',
                controller: AgentUpdateCtrl,
                resolve: {
                    agentId: function () {
                        return agentId;
                    }
                }
            });

            modalInstance.result.then(function (selected) {
                $scope.getAgents();
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        //################End##########

    }]);

var AgentCreateCtrl = function ($scope, $uibModalInstance, $location, apiService) {
    $scope.title = "Create";

    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.createOrUpdate = function () {
        $scope.isLoading = true;
        var createObj = {};
        createObj.name = $scope.name;
        createObj.contactNo = parseInt($scope.contactNo);
        createObj.password = $scope.password;
        createObj.address = "";
        createObj.email = $scope.email;
        createObj.loginId = $scope.loginId;
        createObj.accountType = "AGENT";
        createObj.companyId = -1;
        createObj.status = true;

        apiService.POST("/users", createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $scope.isLoading = false;
            $uibModalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            $scope.isLoading = false;
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
            $scope.code = "";
        });
    };

};


var AgentUpdateCtrl = function ($scope, $uibModalInstance, $location, apiService, agentId) {
    $scope.title = "Update";
    $scope.isUpdate = true;
    $scope.isLoading = true;
    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
    apiService.GET("/users/" + agentId).then(function (response) {
        $scope.name = response.data.data.name;
        $scope.contactNo = response.data.data.contactNo;
        $scope.email = response.data.data.email;
        $scope.loginId = response.data.data.loginId;
        $scope.accountType = response.data.data.accountType;
        $scope.password = response.data.data.password;
        $scope.isLoading = false;
    }, function (errorResponse) {
        apiService.NOTIF_ERROR(errorResponse.data.message);
        $scope.isLoading = false;
        if (errorResponse.status != 200) {
            if (errorResponse.status == 304)
                alert(errorResponse);
        }
    });


    $scope.createOrUpdate = function () {
        $scope.isLoading = true;
        var createObj = {};
        createObj.id = parseInt(agentId);
        createObj.name = $scope.name;
        createObj.contactNo = parseInt($scope.contactNo);
        createObj.password = $scope.password;
        createObj.address = "";
        createObj.email = $scope.email;
        createObj.loginId = $scope.loginId;
        createObj.accountType = $scope.accountType;
        createObj.companyId = -1;
        createObj.status = true;

        apiService.PUT("/users/" + agentId, createObj).then(function (response) {
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