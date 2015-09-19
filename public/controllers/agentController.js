pmcApp.controller('agentController', ['$scope', '$filter', '$location', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnDefBuilder',
    function ($scope, $filter, $location, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnDefBuilder) {

        $scope.sNo = 1;
        $scope.getAgents = function () {
            apiService.GET("/users").then(function (response) {
                console.log(response);
                $scope.agents = response.data.data;
                $scope.agentsBackup = response.data.data;
            }, function (errorResponse) {
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        
        var agentId = $location.search().id;
        if (!agentId) {
            $scope.isCreate = true;
        } else{
            $scope.code = $location.search().code;
            $scope.name = $location.search().name;
            $scope.isCreate = false;
        }
        
        $scope.updateAgent = function () {
            var createObj = {};
            createObj.id = parseInt(agentId);
            createObj.code = $scope.code;
            createObj.name = $scope.name;
            createObj.city = "N/A";
            createObj.companyId = -1;
            createObj.idSequence = 0;

            apiService.PUT("/users/"+agentId, createObj).then(function (response) {
                console.log(response.data.data);
                alert("Agent Successfully Updated!");
                $location.path("/agents");
            }, function (errorResponse) {
                alert(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        
        $scope.createAgent = function () {
            var createObj = {};
            createObj.code = $scope.code;
            createObj.name = $scope.name;
            createObj.city = "N/A";
            createObj.companyId = -1;
            createObj.idSequence = 0;

            apiService.POST("/users", createObj).then(function (response) {
                console.log(response.data.data);
                alert("Agent Successfully Created!");
                $location.path("/agents");
            }, function (errorResponse) {
                alert(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
                $scope.code = "";
            });
        };

        $scope.delete = function (id, name) {
            var userConfirmation = confirm("Are you sure you want to delete agent:" + name);
            if (userConfirmation) {
                apiService.DELETE("/users/" + id).then(function (response) {
                    alert("Agent Successfully Deleted!");
                    $scope.getAgents();
                }, function (errorResponse) {
                    console.log(errorResponse);
                    alert(errorResponse.data.message);
                    if (errorResponse.status != 200) {
                        if (errorResponse.status == 304)
                            alert(errorResponse);
                    }
                });
            }
        };
        
        $scope.dtOptions = DTOptionsBuilder.newOptions()
            //.withColumnFilter()
            //.withDOM('<"input-group"f>pitrl')
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
            $scope.agents = $filter('filter')($scope.agentsBackup, search);
        };

     }]);