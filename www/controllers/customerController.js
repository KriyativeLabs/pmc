pmcApp.controller('customerController', ['$scope', '$compile', '$filter', '$location', '$uibModal', '$log', 'apiService',
    'commonService', 'cookieService', 'constantsService', 'FileSaver', 'Blob',
    function ($scope, $compile, $filter, $location, $uibModal, $log, apiService, commonService, cookieService, constantsService,
        FileSaver, Blob) {

        //########################################Customers Page########################################
        var first = true;
        $scope.switchStatus = true;
        $scope.loader = false;
        $scope.openLoader();
        var query = $location.search().query;
        if (!query) {
            query = "all";
        }
        var pageSize = 20;
        var pageNo = 1;

        var isPaid = "all";
        if (query == "paid") {
            isPaid = "true";
        } else if (query == "unpaid") {
            isPaid = "false";
        }
        $scope.customers = [];
        $scope.loading = false;
        $scope.disableScroll = false;

        $scope.$watch('switchStatus', function () {
            if (first) {
                first = false;
            } else {
                $scope.customers = [];
                pageNo = 1;
                $scope.loadNext();
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

        $scope.isInvalidPhoneNo = function (mobileNo) {
            if ((mobileNo + '').length != 10) {
                return true;
            }
            if (!angular.isNumber(mobileNo)) {
                return true;
            }
            return false;
        }

        $scope.getCustomers = function () {
            $scope.loader = true;
            var li = finalLink;
            if ($scope.switchStatus) {
                li = li + "&active=true";
            } else {
                li = li + "&active=false";
            }
            $scope.getCustomersCount();
            $scope.progressbar.start();

            apiService.GET(li).then(function (result) {
                //                console.log(result.data.data);
                $scope.customers = $scope.customers.concat(result.data.data);
                $scope.loading = false;
                $scope.progressbar.complete();
                $scope.loader = false;
                $scope.closeLoader();
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                $scope.loading = false;
                $scope.loader = false;
                $scope.progressbar.complete();
                $scope.closeLoader();
                if (errorResponse.status != 200) {
                    //                    console.log(errorResponse);
                }
            });
        };

        $scope.getCustomersCount = function () {
            apiService.GET(countLink).then(function (result) {
                $scope.customersCount = result.data.data.count;
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
                var data = new Blob([result.data], {
                    type: result.headers('Content-Type')
                });
                FileSaver.saveAs(data, result.headers("filename"));
                //                console.log(result);
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.loadNext = function () {
            console.log($scope.disableScroll);
            if (!$scope.loading && !$scope.disableScroll) {
                //                console.log("Loading");
                $scope.loading = true;
                finalLink = link + "&pageNo=" + pageNo + "&pageSize=20";
                $scope.getCustomers();
                pageNo = pageNo + 1;
            }
        };

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

        $scope.changeData = function (search) {
            q = search;
            pageNo = 1;
            if (search) {
                $scope.disableScroll = true;
                $scope.loader = true;
                commonService.getResultFromLink("/customers?q=" + search).then(function (result) {
                    $scope.customers = result.data.data;
                    $scope.loader = false;
                });
            } else {
                $scope.customers = [];
                $scope.disableScroll = false;
                $scope.loadNext();
            }
        };
        //#############################################################################################
        $scope.open = function () {

            var modalInstance = $uibModal.open({
                templateUrl: 'customerModal.html',
                backdrop: 'static',
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
                backdrop: 'static',
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

var CustomerCreateCtrl = function ($scope, $uibModalInstance, $timeout, apiService, commonService, constantsService) {
    $scope.title = "Create";
    $scope.sbtname = constantsService.SBT_NAME;
    $scope.boxseriesname = constantsService.BOX_SERIES;
    $scope.cafname = constantsService.CAF;
    $scope.cheader = constantsService.C_CON_HEADER;

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
        createObj.companyId = -1;
        
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
                companyId: -1,
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
        title: 'Connection#' + ($scope.conCount),
        dt: new Date(),
        opened: false
    });

    $scope.add = function () {
        $scope.cons.push({
            title: 'Connection#' + ($scope.conCount + 1),
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

var CustomerUpdateCtrl = function ($scope, $uibModalInstance, $timeout, apiService, commonService, constantsService, id) {
    $scope.title = "Update";
    $scope.sbtname = constantsService.SBT_NAME;
    $scope.boxseriesname = constantsService.BOX_SERIES;
    $scope.cafname = constantsService.CAF;
    $scope.cheader = constantsService.C_CON_HEADER;
    //var today = new Date();
    //$scope.dt = today.toLocaleDateString('en-GB');

    $scope.cons = [];
    $scope.conCount = 0;
    $scope.isLoading = true;
    $scope.add = function () {
        $scope.cons.push({
            title: 'Connection#' + ($scope.conCount + 1),
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
        var customerData = response.data.data;
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
                title: 'Connection#' + ($scope.conCount),
                opened: false,
                id: value.id,
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
        createObj.companyId = -1;

        var connections = [];
        angular.forEach($scope.cons, function (value, key) {
            connections.push({
                id:value.id,
                setupBoxId: value.sbt_no,
                cafId: value.caf,
                boxSerialNo: value.box_series,
                status: value.status,
                planId: parseInt(value.plan),
                discount: value.discount,
                idProof: value.id_proof,
                companyId: -1,
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