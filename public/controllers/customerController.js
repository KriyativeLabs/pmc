pmcApp.controller('customerController', ['$scope', '$filter', '$location', '$modal', '$log', 'apiService', 'commonService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnDefBuilder',
    function ($scope, $filter, $location, $modal, $log, apiService, commonService, cookieService, constantsService, DTOptionsBuilder, DTColumnDefBuilder) {

//########################################Customers Page########################################
        var query = $location.search().query;
        if (!query) {
            query = "all";
        }

        var link = "/customers";
        if (query == "paid") {
            link = "/customers/paid";
        } else if (query == "unpaid") {
            link = "/customers/unpaid";
        } else {
            link = "/customers";
        }

        $scope.getCustomers = function () {
            apiService.GET(link).then(function (result) {
                console.log(result.data.data);
                $scope.customers = result.data.data;
                $scope.customersBackUp = result.data.data;
            },function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.dtOptions = DTOptionsBuilder.newOptions()
            //.withColumnFilter()
            //.withDOM('<"input-group"f>pitrl')
            .withDOM('<"row"<"col-sm-6"i><"col-sm-6"p>>tr')
            .withPaginationType('full_numbers')
            .withDisplayLength(40)
            .withOption('order', [4, 'desc'])
            .withOption('language', {
                paginate: {
                    next: "",
                    previous: ""
                },
                search: "Search: ",
                lengthMenu: "_MENU_ records per page"
            });

        $scope.changeData = function (search) {
            commonService.getResultFromLink("/customersearch?search=" + search).then(function (result) {
                $scope.customers = result.data.data;
                $scope.customersBackUp = result.data.data;
            });
        };
//#############################################################################################
        $scope.open = function () {

            var modalInstance = $modal.open({
                templateUrl: 'customerModal.html',
                controller: CustomerCreateCtrl
            });

            modalInstance.result.then(function (selected) {
                $scope.getCustomers();
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openUpdate = function (id) {

            var modalInstance = $modal.open({
                templateUrl: 'customerModal.html',
                controller: CustomerUpdateCtrl,
                resolve: {
                    id: function () {
                        return id;
                    }
                }
            });

            modalInstance.result.then(function (selected) {
                $scope.getCustomers();
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
    }]);

var CustomerCreateCtrl = function ($scope, $modalInstance, $timeout, apiService, commonService) {
    $scope.title = "Create";
    var today = new Date();
    $scope.dt = today.toLocaleDateString('en-GB');

    $scope.open = function () {
        $timeout(function () {
            $scope.opened = true;
        });
    };

    commonService.getAreas.then(function (result) {
        $scope.areas = result.data.data
    });
    commonService.getPlans.then(function (result) {
        $scope.plans = result.data.data
    });

    $scope.customerFunc = function () {
        var createObj = {};
        createObj.name = $scope.name;
        createObj.mobileNo = $scope.mobile_no;
        createObj.emailId = $scope.email;
        //createObj.city = $scope.city;
        createObj.balanceAmount = $scope.old_balance;
        createObj.areaId = parseInt($scope.area);
        createObj.address = $scope.address;

        var connection = {};
        connection.setupBoxId = $scope.sbt_no;

        connection.cafId = $scope.caf;
        connection.boxSerialNo = $scope.box_series;
        connection.status = $scope.status;
        connection.planId = parseInt($scope.plan);
        connection.discount = $scope.discount;
        connection.idProof = $scope.id_proof;
        if (Object.prototype.toString.call($scope.dt) === '[object Date]') {
            connection.installationDate = $scope.dt.getFullYear() + "-" + ($scope.dt.getMonth() + 1) + "-" + ($scope.dt.getDay() + 1);
        } else{
            connection.installationDate = $scope.dt;
        }

        createObj.connections = [connection];

        apiService.POST("/customers", createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $modalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };


    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};

var CustomerUpdateCtrl = function ($scope, $modalInstance, $timeout, apiService, commonService, id) {
    $scope.title = "Update";
    var today = new Date();
    $scope.dt = today.toLocaleDateString('en-GB');

    apiService.GET("/customers/" + id).then(function (response) {

        var customerData = response.data.data.customer;
        $scope.id = customerData.id;
        $scope.name = customerData.name;
        $scope.mobile_no = customerData.mobileNo;
        $scope.email = customerData.emailId;
        $scope.old_balance = customerData.balanceAmount;
        $scope.areaId = customerData.areaId;
        $scope.area = customerData.areaId;
        $scope.address = customerData.address;

        var connection = response.data.data.connection;
        $scope.sbt_no = connection.setupBoxId;
        $scope.caf = connection.cafId;
        $scope.box_series = connection.boxSerialNo;
        $scope.status = connection.status;
        $scope.planId = connection.planId;
        $scope.plan = connection.planId;
        $scope.discount = connection.discount;
        $scope.id_proof = connection.idProof;
        $scope.dt = connection.installationDate; //.getFullYear()+"-"+($scope.dt.getMonth()+1)+"-"+($scope.dt.getDay()+1);

    }, function (errorResponse) {
        apiService.NOTIF_ERROR(errorResponse.data.message);
        if (errorResponse.status != 200) {
            console.log(errorResponse);
        }
    });

    $scope.open = function () {
        $timeout(function () {
            $scope.opened = true;
        });
    };

    commonService.getAreas.then(function (result) {
        $scope.areas = result.data.data
    });
    commonService.getPlans.then(function (result) {
        $scope.plans = result.data.data
    });

    $scope.customerFunc = function () {
        var createObj = {};
        createObj.id = $scope.id;
        createObj.name = $scope.name;
        createObj.mobileNo = $scope.mobile_no;
        createObj.emailId = $scope.email;
        //createObj.city = $scope.city;
        createObj.balanceAmount = $scope.old_balance;
        createObj.areaId = parseInt($scope.area);
        createObj.address = $scope.address;

        var connection = {};
        connection.setupBoxId = $scope.sbt_no;
        connection.cafId = $scope.caf;
        connection.boxSerialNo = $scope.box_series;
        connection.status = $scope.status;
        connection.planId = parseInt($scope.plan);
        connection.discount = $scope.discount;
        connection.idProof = $scope.id_proof;
        $scope.dt = new Date($scope.dt);
        connection.installationDate = $scope.dt.getFullYear() + "-" + ($scope.dt.getMonth() + 1) + "-" + ($scope.dt.getDay() + 1);

        createObj.connections = [connection];

        apiService.PUT("/customers/"+$scope.id, createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $modalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };

    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};