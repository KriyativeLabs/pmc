pmcApp.controller('agentController', ['$scope', '$filter', '$location','$modal', '$log', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnBuilder',
    function ($scope, $filter, $location,$modal, $log, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnBuilder) {

        $scope.sNo = 1;
        $scope.getAgents = function () {
            apiService.GET("/users").then(function (response) {
                $scope.agents = response.data.data;
                $scope.agentsBackup = response.data.data;
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.delete = function (id, name) {
            var userConfirmation = confirm("Are you sure you want to delete agent:" + name);
            if (userConfirmation) {
                apiService.DELETE("/users/" + id).then(function (response) {
                    apiService.NOTIF_SUCCESS(response.data.message);
                    $scope.getAgents();
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
            .withOption('responsive', true)
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

        $scope.dtColumns = [
            DTColumnBuilder.newColumn('sNo').withTitle('S.No'),
            DTColumnBuilder.newColumn('name').withTitle('Name').withClass('all'),
            DTColumnBuilder.newColumn('mobile').withTitle('Mobile No').withClass('all'),
            DTColumnBuilder.newColumn('email').withTitle('Email'),
            DTColumnBuilder.newColumn('loginId').withTitle('Login ID'),
            DTColumnBuilder.newColumn('accType').withTitle('Account Type'),
            DTColumnBuilder.newColumn('action').withTitle('Action').withClass('all')
        ];



        $scope.changeData = function (search) {
            $scope.agents = $filter('filter')($scope.agentsBackup, search);
        };
//############################################Modal###########################################
        $scope.open = function () {
            var modalInstance = $modal.open({
                templateUrl: 'agentModal.html',
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
            var modalInstance = $modal.open({
                templateUrl: 'agentModal.html',
                controller: AgentUpdateCtrl,
                resolve: {
                    agentId: function () {
                        return agentId;
                    },
                    agentName: function () {
                        return agentName;
                    },
                    agentContactNo: function () {
                        return contactNo;
                    },
                    agentEmail: function () {
                        return email;
                    },
                    agentLoginId: function () {
                        return loginId;
                    },
                    agentAccountType: function () {
                        return accountType;
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

var AgentCreateCtrl = function ($scope, $modalInstance, $location, apiService) {
    $scope.title = "Create";

    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.createOrUpdate = function () {
        var createObj = {};
        createObj.name = $scope.name;
        createObj.contactNo = parseInt($scope.contactNo);
        createObj.password = "";
        createObj.address = "";
        createObj.email = $scope.email;
        createObj.loginId = $scope.loginId;
        createObj.accountType = "AGENT";
        createObj.companyId = -1;

        apiService.POST("/users", createObj).then(function (response) {
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


var AgentUpdateCtrl = function ($scope, $modalInstance, $location, apiService, agentId,agentName,agentContactNo, agentEmail,agentLoginId, agentAccountType) {
    $scope.title = "Update";
    $scope.isUpdate = true;

    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.name = agentName;
    $scope.contactNo = agentContactNo;
    $scope.email = agentEmail;
    $scope.loginId = agentLoginId;
    $scope.accountType = agentAccountType;

    $scope.createOrUpdate = function () {
        var createObj = {};
        createObj.id = parseInt(agentId);
        createObj.name = $scope.name;
        createObj.contactNo = parseInt($scope.contactNo);
        createObj.password = "";
        createObj.address = "";
        createObj.email = $scope.email;
        createObj.loginId = $scope.loginId;
        createObj.accountType = $scope.accountType;
        createObj.companyId = -1;

        apiService.PUT("/users/" + agentId, createObj).then(function (response) {
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
