pmcApp.controller('agentController', ['$scope', '$compile', '$filter', '$location','$modal', '$log', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnBuilder',
    function ($scope,$compile, $filter, $location,$modal, $log, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnBuilder) {

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

        $scope.deleteAgent = function (id) {
            var userConfirmation = confirm("Are you sure you want to delete agent?");
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
            .withOption('createdRow', createdRow)
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
            DTColumnBuilder.newColumn('id').withTitle('Id').withClass('none'),
            DTColumnBuilder.newColumn('name').withTitle('Name').withClass('all'),
            DTColumnBuilder.newColumn('mobile').withTitle('Mobile No').withClass('all'),
            DTColumnBuilder.newColumn('email').withTitle('Email'),
            DTColumnBuilder.newColumn('loginId').withTitle('Login ID'),
            DTColumnBuilder.newColumn('accType').withTitle('Account Type'),
            DTColumnBuilder.newColumn(null).withTitle('Action').withClass('all').notSortable().renderWith(actionsHtml)
        ];


        function actionsHtml(data, type, full, meta) {
            return '<button ng-click="openUpdate('+data.id+')" class="btn btn-primary btn-sm" style="padding:1px 10px !important;">Edit</button>'+
            '<button ng-click="deleteAgent('+data.id+')" class="btn btn-danger btn-sm" style="padding:1px 10px !important;">Delete</button>';
        }

        function createdRow(row, data, dataIndex) {
            $compile(angular.element(row).contents())($scope);
        }



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
        createObj.password = $scope.password;
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


var AgentUpdateCtrl = function ($scope, $modalInstance, $location, apiService, agentId) {
    $scope.title = "Update";
    $scope.isUpdate = true;

    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
    apiService.GET("/users/" + agentId).then(function (response) {
                    
            $scope.name = response.data.data.name;
            $scope.contactNo = response.data.data.contactNo;
            $scope.email = response.data.data.email;
            $scope.loginId = response.data.data.loginId;
            $scope.accountType = response.data.data.accountType;
            $scope.password = response.data.data.password;
        
                }, function (errorResponse) {
                    apiService.NOTIF_ERROR(errorResponse.data.message);
                    if (errorResponse.status != 200) {
                        if (errorResponse.status == 304)
                            alert(errorResponse);
                    }
                });
    


    $scope.createOrUpdate = function () {
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
