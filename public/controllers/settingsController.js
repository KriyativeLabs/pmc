pmcApp.controller('settingsController', ['$scope', '$filter', '$location', 'apiService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnDefBuilder',
    function ($scope, $filter, $location, apiService, cookieService, constantsService, DTOptionsBuilder, DTColumnDefBuilder) {

        $scope.sNo = 1;
        $scope.getPlans = function () {
            apiService.GET("/plans").then(function (response) {
                console.log(response);
                $scope.plans = response.data.data;
            }, function (errorResponse) {
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.updatePassword = function () {
            if ($scope.new_password == $scope.re_new_password) {
                var createObj = {};
                createObj.oldPassword = $scope.old_password;
                createObj.newPassword = $scope.new_password;

                apiService.POST("/users/changepassword ", createObj).then(function (response) {
                    console.log(response.data.data);
                    alert("Password Successfully updated!");
                    $location.path("/dashboard");
                }, function (errorResponse) {
                    alert(errorResponse.data.message);
                    if (errorResponse.status != 200) {
                        console.log(errorResponse);
                    }
                });
            } else {
                alert("New password entered is not matching");
            }
        };
    }]);