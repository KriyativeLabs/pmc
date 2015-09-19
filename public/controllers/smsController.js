pmcApp.controller('smsController', ['$scope', '$filter', '$location', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnDefBuilder',
    function ($scope, $filter, $location, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnDefBuilder) {

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

        $scope.charsChange = function(){
            //$scope.charsLeft = $scope.charsLeft - $scope.smsData.size;
        };

        


     }]);