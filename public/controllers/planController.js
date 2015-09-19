pmcApp.controller('planController', ['$scope', '$filter', '$location', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnDefBuilder',
    function ($scope, $filter, $location, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnDefBuilder) {

        $scope.sNo=1;
        $scope.getPlans = function(){
            apiService.GET("/plans").then(function(response){
                $scope.plans = response.data.data;
            },function(errorResponse){
                if(errorResponse.status !=200){
                   console.log(errorResponse);
                }
            });
        };


        var planId = $location.search().id;
        if (!planId) {
            $scope.isUpdate = false;
        } else{
            $scope.amount = parseInt($location.search().amount);
            $scope.name = $location.search().name;
            $scope.duration = parseInt($location.search().no);
            $scope.isUpdate = true;
        }

        $scope.updatePlan = function(){
            var createObj = {};
            createObj.id = parseInt(planId);
            createObj.name = $scope.name;
            createObj.amount = $scope.amount;
            createObj.noOfMonths = $scope.duration;
            createObj.companyId = -1;

            apiService.PUT("/plans/"+planId,createObj).then(function (response) {
                alert("Plan Successfully Updated!");
                $location.path("/plans");
            }, function (errorResponse) {
                alert(errorResponse.data.message)
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.createPlan = function(){
            var createObj = {};
            createObj.name = $scope.name;
            createObj.amount = $scope.amount;
            createObj.noOfMonths = $scope.duration;
            createObj.companyId = -1;

            apiService.POST("/plans",createObj).then(function (response) {
                alert("Plan Successfully Created!");
                $location.path("/plans");
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
                    alert("Area Successfully Deleted!");
                    $scope.getPlans();
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

    }]);