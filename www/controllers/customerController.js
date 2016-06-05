pmcApp.controller('customerController', ['$scope', '$compile', '$filter', '$location', '$uibModal', '$log', 'apiService', 'commonService', 'cookieService', 'constantsService', 'DTOptionsBuilder', 'DTColumnBuilder', 'FileSaver', 'Blob',
    function ($scope, $compile, $filter, $location, $uibModal, $log, apiService, commonService, cookieService, constantsService, DTOptionsBuilder, DTColumnBuilder, FileSaver, Blob) {

        //########################################Customers Page########################################
        var first = true;
        $scope.switchStatus = true;
        var query = $location.search().query;
        if (!query) {
            query = "all";
        }
        $scope.isLoading = false;
        var pageSize = 20;
        var pageNo = 1;
        $scope.from = 1;
        $scope.to = 20;

        var isPaid = "all";
        if (query == "paid") {
            isPaid = "true";
        } else if (query == "unpaid") {
            isPaid = "false";
        }

        $scope.$watch('switchStatus', function () {
            if (first) {
                first = false;
            } else {
                $scope.getCustomers();
            }
        });

        var q = "";
        var link = "/customers?isPaid=all";
        var countLink = "/customers/count?isPaid=all";
        if (query == "paid") {
            link = "/customers?isPaid=true";
            countLink = "/customers/count?isPaid=true";
        } else if (query == "unpaid") {
            link = "/customers?isPaid=false";
            countLink = "/customers/count?isPaid=false";
        } else {
            link = "/customers?isPaid=all";
            countLink = "/customers/count?isPaid=all";
        }

        var setTo = function (number) {
            if ($scope.to > number) {
                $scope.to = number;
            }
        };

        $scope.getCustomers = function () {
            var li = finalLink;
            if ($scope.switchStatus) {
                li = li + "&active=true";
            } else {
                li = li + "&active=false";
            }
            $scope.getCustomersCount();
            apiService.GET(li).then(function (result) {
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

        $scope.getCustomersCount = function () {
            apiService.GET(countLink).then(function (result) {
                $scope.customersCount = result.data.data.count;
                setTo($scope.customersCount);
                $scope.noOfPages = $scope.customersCount / pageSize;
                if ($scope.customersCount % pageSize > 0) {
                    $scope.noOfPages = $scope.noOfPages + 1;
                }
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        $scope.getCustomersCount();

        $scope.download = function () {
            var dLink = "/customers/download?isPaid=" + isPaid + "&q=" + q;
            apiService.DOWNLOAD(dLink).then(function (result) {

                var data = new Blob([result.data], {type: result.headers('Content-Type')});
                FileSaver.saveAs(data, result.headers("filename"));
                console.log(result);
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };


        $scope.loadNext = function (buttonId) {
            if (buttonId == -2) {
                finalLink = link + "&pageNo=1&pageSize=" + pageSize;
            } else if (buttonId == -1) {
                if (pageNo - 1 > 0) {
                    pageNo = pageNo - 1;
                } else {
                    pageNo = 1;
                }
                finalLink = link + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
            } else if (buttonId == 1) {
                if (pageNo + 1 <= $scope.noOfPages) {
                    pageNo = pageNo + 1;
                } else {
                    pageNo = $scope.noOfPages;
                }
                finalLink = link + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
            } else {
                pageNo = $scope.noOfPages;
                finalLink = link + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
            }

            $scope.from = (pageSize * pageNo) - pageSize + 1;
            $scope.to = pageSize * pageNo;

            $scope.getCustomers();
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
            .withOption('order', [6, 'desc'])
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
            return '<button ng-disabled="' + (data.balanceAmount == 0) + '" class="btn btn-success btn-sm"' +
                'style="padding:1px 11px !important;" ng-click="openReceipt(' + data.id + ')">Pay' +
                '</button> &nbsp;' +
                '<button class="btn btn-primary btn-sm" ng-hide="' + $scope.isAgent + '" ng-click="openUpdate(' + data.id + ')"' +
                'style="padding:1px 10px !important;">Edit' +
                '</button>';
        }

        $scope.stbString = function (cons, isSbt) {
            var result = "";
            angular.forEach(cons, function (value, key) {
                if (result != "") {
                    result = result + ",";
                }
                if (isSbt) {
                    result = result + value.setupBoxId;
                } else {
                    result = result + value.boxSerialNo;
                }
            });
            return result;
        };


        function createdRow(row, data, dataIndex) {
            $compile(angular.element(row).contents())($scope);
        }

        $scope.changeData = function (search) {
            q = search;
            commonService.getResultFromLink("/customers?q=" + search).then(function (result) {
                $scope.customers = result.data.data;
                $scope.customersBackUp = result.data.data;
            });
        };
        //#############################################################################################
        $scope.open = function () {

            var modalInstance = $uibModal.open({
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

            var modalInstance = $uibModal.open({
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

var CustomerCreateCtrl = function ($scope, $uibModalInstance, $timeout, apiService, commonService) {
    $scope.title = "Create";
//    var today = new Date();
//    $scope.dt = today.toLocaleDateString('en-GB');

    $scope.open = function (con) {
        $timeout(function () {
            con.opened = true;
        });
    };

    $scope.getAreas = function () {
        apiService.GET("/areas").then(function (result) {
            $scope.areas = result.data.data;
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };

    $scope.getAreas();

    $scope.getPlans = function () {
        apiService.GET("/plans").then(function (result) {
            $scope.plans = result.data.data
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };
    $scope.getPlans();

    $scope.customerFunc = function () {
        $scope.isLoading = true;
        var createObj = {};
        createObj.name = $scope.name;
        createObj.mobileNo = $scope.mobile_no;
        createObj.emailId = $scope.email;
        //createObj.city = $scope.city;
        createObj.balanceAmount = $scope.old_balance;
        createObj.areaId = parseInt($scope.area);
        createObj.address = $scope.address;

        var connections = [];
        angular.forEach($scope.cons, function (value, key) {
            connections.push({
                setupBoxId: value.sbt_no,
                cafId: value.caf,
                boxSerialNo: value.box_series,
                status: value.status,
                planId: parseInt(value.plan),
                discount: value.discount,
                idProof: value.id_proof,
                installationDate: commonService.getDateString(value.dt)
            });
        });

        createObj.connections = connections;

        apiService.POST("/customers", createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $scope.isLoading = false;
            $uibModalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            $scope.isLoading = false;
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };

    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
    $scope.cons = [];
    $scope.conCount = 1;
    $scope.cons.push({
        title: 'Connection - ' + ($scope.conCount),
        dt: new Date(),
        opened: false
    });

    $scope.add = function () {
        $scope.cons.push({
            title: 'Connection - ' + ($scope.conCount + 1),
            dt: new Date(),
            opened: false
        });
        $scope.conCount = $scope.conCount + 1;
    };

    $scope.remove = function (index) {
        $scope.cons.splice(index, 1);
    };

    $scope.dateOptions = {
        //maxDate: new Date(),
        //minDate: new Date(),
        startingDay: 1
    };
};

var CustomerUpdateCtrl = function ($scope, $uibModalInstance, $timeout, apiService, commonService, id) {
    $scope.title = "Update";
    //var today = new Date();
    //$scope.dt = today.toLocaleDateString('en-GB');

    $scope.cons = [];
    $scope.conCount = 0;
    $scope.isLoading = true;
    $scope.add = function () {
        $scope.cons.push({
            title: 'Connection - ' + ($scope.conCount + 1),
            dt: new Date(),
            opened: false
        });
        $scope.conCount = $scope.conCount + 1;
    };

    $scope.remove = function (index) {
        $scope.cons.splice(index, 1);
    };

    $scope.getAreas = function () {
        $scope.isLoading = true;
        apiService.GET("/areas").then(function (result) {
            $scope.isLoading = false;
            $scope.areas = result.data.data;
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            $scope.isLoading = false;
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };

    $scope.getAreas();

    $scope.getPlans = function () {
        $scope.isLoading = true;
        apiService.GET("/plans").then(function (result) {
            $scope.plans = result.data.data
            $scope.isLoading = false;
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            $scope.isLoading = false;
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };
    $scope.getPlans();


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

        var connections = response.data.data.connections;
        angular.forEach(connections, function (value, key) {
            $scope.conCount = $scope.conCount + 1;
            $scope.cons.push({
                title: 'Connection - ' + ($scope.conCount),
                opened: false,
                sbt_no: value.setupBoxId,
                caf: value.cafId,
                box_series: value.boxSerialNo,
                status: value.status,
                planId: value.planId,
                plan: value.planId,
                discount: value.discount,
                id_proof: value.idProof,
                dt: new Date(value.installationDate)
            });
            $scope.isLoading = false;
        });

    }, function (errorResponse) {
        apiService.NOTIF_ERROR(errorResponse.data.message);
        $scope.isLoading = false;
        if (errorResponse.status != 200) {
            console.log(errorResponse);
        }
    });

    $scope.open = function (con) {
        $timeout(function () {
            con.opened = true;
        });
    };

    $scope.customerFunc = function () {
        $scope.isLoading = true;
        var createObj = {};
        createObj.id = $scope.id;
        createObj.name = $scope.name;
        createObj.mobileNo = $scope.mobile_no;
        createObj.emailId = $scope.email;
        //createObj.city = $scope.city;
        createObj.balanceAmount = $scope.old_balance;
        createObj.areaId = parseInt($scope.area);
        createObj.address = $scope.address;

        var connections = [];
        angular.forEach($scope.cons, function (value, key) {
            connections.push({
                setupBoxId: value.sbt_no,
                cafId: value.caf,
                boxSerialNo: value.box_series,
                status: value.status,
                planId: parseInt(value.plan),
                discount: value.discount,
                idProof: value.id_proof,
                installationDate: commonService.getDateString(value.dt)
            });
        });
        createObj.connections = connections;

        apiService.PUT("/customers/" + $scope.id, createObj).then(function (response) {
            apiService.NOTIF_SUCCESS(response.data.message);
            $uibModalInstance.close($scope.dt);
        }, function (errorResponse) {
            apiService.NOTIF_ERROR(errorResponse.data.message);
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
        });
    };

    $scope.ok = function () {
        $uibModalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.dateOptions = {
        //maxDate: new Date(),
        //minDate: new Date(),
        startingDay: 1
    };
};
