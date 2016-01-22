pmcApp.controller('customerController', ['$scope', '$compile', '$filter', '$location', '$modal', '$log', 'apiService', 'commonService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnBuilder',
    function ($scope, $compile, $filter, $location, $modal, $log, apiService, commonService, cookieService, constantsService, DTOptionsBuilder, DTColumnBuilder) {

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
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.dtOptions = DTOptionsBuilder.newOptions()
            //.withColumnFilter()
            //.withDOM('<"input-group"f>pitrl')
            // Active Responsive plugin
            .withOption('createdRow', createdRow)
            .withOption('responsive', true)
            .withDOM('tr')
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

        $scope.dtColumns = [
            DTColumnBuilder.newColumn('id').withTitle('Id').withClass('none'),
            DTColumnBuilder.newColumn('hNo').withTitle('H.No'),
            DTColumnBuilder.newColumn('name').withTitle('Name').withClass('all'),
            DTColumnBuilder.newColumn('mobile').withTitle('Mobile No'),
            DTColumnBuilder.newColumn('sbt').withTitle('STB No.'),
            DTColumnBuilder.newColumn('boxSerialNo').withTitle('Box Serial No.').withClass('none'),
            DTColumnBuilder.newColumn('balance').withTitle('Balance').withClass('all'),
            DTColumnBuilder.newColumn(null).withTitle('Action').withClass('all').notSortable().renderWith(actionsHtml)
        ];

        function actionsHtml(data, type, full, meta) {
            return '<button ng-disabled="'+(data.balanceAmount == 0)+'" class="btn btn-success btn-sm"' +
                                'style="padding:1px 10px !important;" ng-click="openReceipt('+data.id+')">Pay'+
                        '</button> &nbsp;'+
                        '<button class="btn btn-primary btn-sm" ng-hide="'+$scope.isAgent+'" ng-click="openUpdate('+data.id+')"'+
                                'style="padding:1px 10px !important;">Edit'+
                        '</button>';
        }

        function createdRow(row, data, dataIndex) {
            $compile(angular.element(row).contents())($scope);
        }

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

        connection.installationDate = commonService.getDateString($scope.dt);

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
        $scope.dt = connection.installationDate;

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
        connection.installationDate = commonService.getDateString($scope.dt);

        createObj.connections = [connection];

        apiService.PUT("/customers/" + $scope.id, createObj).then(function (response) {
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