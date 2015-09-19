pmcApp.controller('areaController', ['$scope','$window', '$filter', '$location', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnDefBuilder',
    function ($scope,$window, $filter, $location, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnDefBuilder) {
        $scope.alerts =[];
        $scope.sNo = 1;
        $scope.getAreas = function () {
            apiService.GET("/areas").then(function (response) {
                console.log(response);
                $scope.areas = response.data.data;
                $scope.areasBackup = response.data.data;
            }, function (errorResponse) {
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        
        var areaId = $location.search().id;
        if (!areaId) {
            $scope.isCreate = true;
        } else{
            $scope.code = $location.search().code;
            $scope.name = $location.search().name;
            $scope.isCreate = false;
        }
        
        $scope.updateArea = function () {
            var createObj = {};
            createObj.id = parseInt(areaId);
            createObj.code = $scope.code;
            createObj.name = $scope.name;
            createObj.city = "N/A";
            createObj.companyId = -1;
            createObj.idSequence = 0;

            apiService.PUT("/areas/"+areaId, createObj).then(function (response) {
                console.log(response.data.data);
                alert("Area Successfully Updated!");
                $location.path("/areas");
            }, function (errorResponse) {
                alert(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        
        $scope.createArea = function () {
            var createObj = {};
            createObj.code = $scope.code;
            createObj.name = $scope.name;
            createObj.city = "N/A";
            createObj.companyId = -1;
            createObj.idSequence = 0;

            apiService.POST("/areas", createObj).then(function (response) {
                console.log(response.data.data);
                $scope.alerts = [];
                $scope.alerts.push({type: 'success', msg:"Area Successfully Created!"});
                $location.path("/areas");
            }, function (errorResponse) {
                $scope.alerts = [];
                $scope.alerts.push({ type: 'danger',msg: errorResponse.data.message});
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
                $scope.code = "";
            });
        };

        $scope.delete = function (id, name) {
            var userConfirmation = confirm("Are you sure you want to delete area:" + name);
            if (userConfirmation) {
                apiService.DELETE("/areas/" + id).then(function (response) {
                    alert("Area Successfully Deleted!");
                    $scope.getAreas();
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
            $scope.areas = $filter('filter')($scope.areasBackup, search);
        };

        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        };

     }]);