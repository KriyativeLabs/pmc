pmcApp.controller('settingsController', ['$scope', '$location', '$uibModal', '$timeout', '$log', 'cookieService', 'apiService', 'constantsService', 'SweetAlert',
    function ($scope, $location, $uibModal, $timeout, $log, cookieService, apiService, constantsService, SweetAlert) {
        var companyId = cookieService.get(constantsService.C_ID);

        $scope.progressbar.start();
        $scope.openLoader();
        $scope.getSettings = function (id) {
            $scope.loading = true;
            $scope.loader = true;
            apiService.GET("/companies/" + id).then(function (response) {

                console.log(response);
                $scope.progressbar.complete();
                $scope.closeLoader();
                $scope.loading = false;
                $scope.loader = false;
                $scope.name = response.data.data.name;
                $scope.address = response.data.data.address;
                $scope.contactNo = response.data.data.contactNo;
                $scope.lastBillGeneratedOn = response.data.data.lastBillGeneratedOn;
                $scope.owner = response.data.data.owner;
                $scope.smsCount = response.data.data.smsCount;
                $scope.smsEnabled = response.data.data.smsEnabled;

                $scope.subscriptionSMS = response.data.data.subscriptionSMS;
                $scope.balanceReminders = response.data.data.balanceReminders;
                $scope.paymentSms = response.data.data.paymentSMS;
                $scope.bulkSms = response.data.data.bulkSMS;
            }, function (errorResponse) {
                $scope.progressbar.complete();
                $scope.closeLoader();
                $scope.loading = false;
                $scope.loader = false;
                SweetAlert.swal("", "Something Went Wrong!\n Please Try Again", "error");
                console.log(errorResponse);
            });
        };
        $scope.getSettings(companyId);

        $scope.update = function () {
            $scope.isLoading = true;
            
            var createObj = {};
            createObj.id = parseInt(companyId);
            createObj.name = $scope.name;
            createObj.address = $scope.address;
            createObj.contactNo = $scope.contactNo;
            createObj.owner = $scope.owner;
            createObj.smsEnabled = $scope.smsEnabled;
            createObj.subscriptionSMS = $scope.subscriptionSMS;
            createObj.balanceReminders = $scope.balanceReminders;
            createObj.paymentSMS = $scope.paymentSms;
            createObj.bulkSMS = $scope.bulkSms;
            createObj.smsCount = 0;
            createObj.receiptNo = 0;
            createObj.isCableNetwork = true;
            createObj.pricePerCustomer = 2;
            createObj.msoType = "UNKNOWN";

            apiService.PUT("/companies/"+companyId, createObj).then(function (response) {
                $scope.isLoading = false;
                $scope.getSettings(companyId);
                SweetAlert.swal({
                    title: "",
                    text: response.data.message+"\n"+ "Please Logout to reflect new Settings!",
                    type: "warning",
                    //                    imageSize: '10x10',
                    showCancelButton: true,
                    confirmButtonColor: "#1AAE88",
                    confirmButtonText: "Logout",
                    cancelButtonText: "No",
                    //                    cancelButtonColor: "#DD6B55",   
                    closeOnConfirm: false,
                    closeOnCancel: true
                },
                function (isConfirm) {
                    if (isConfirm) {
                        $scope.logout();
                    }
                });
                
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                $scope.isLoading = false;
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
    }]);