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
            commonService.getResultFromLink(link).then(function(result){
                $scope.customers = result.data.data;
                $scope.customersBackUp = result.data.data;
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
            commonService.getResultFromLink("/customersearch?search="+search).then(function(result){
                $scope.customers = result.data.data;
                $scope.customersBackUp = result.data.data;
            });
        };
//#############################################################################################

//########################################Customers Create Page################################
 /*       $scope.today = function() {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.clear = function () {
            $scope.dt = null;
        };

        $scope.toggleMin = function() {
            $scope.minDate = $scope.minDate ? null : new Date();
        };
        $scope.toggleMin();
        $scope.maxDate = new Date(2020, 5, 22);

        $scope.open = function($event) {
            $scope.status.opened = true;
        };

        $scope.dateOptions = {
            formatYear: 'yy',
            startingDay: 1
        };


        $scope.format = "dd/MM/yyyy";

        $scope.status = {
            opened: false
        };

        $scope.getAreas = function() {
            apiService.GET("/areas").then(function (response) {
                $scope.areas = response.data.data;
                console.log($scope.areas);
            }, function (errorResponse) {
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        
        $scope.getPlans = function() {
            apiService.GET("/plans").then(function (response) {
                $scope.plans = response.data.data;
                console.log($scope.plans);
            }, function (errorResponse) {
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        //sbtNo: String, caf: String, boxSeries: String, status: String, planId: Int, discount: Int, idProof: String, installationDate: DateTime)
        //id: Option[Int], name: String, mobileNo: Long, emailId: String, address: String, areaId: Int, balanceAmount: Int, plans: List[CustomerPlan])
        $scope.createCustomer = function () {
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
            connection.installationDate = $scope.installation_date;

            createObj.connections = [connection];

            $scope.createErrorMsg = "Hello Error";
            apiService.POST("/customers",createObj).then(function (response) {
                alert(response.data.message);
                console.log(response.data.data);
            }, function (errorResponse) {
                alert(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        */
//#############################################################################################
        $scope.open = function () {

            var modalInstance = $modal.open({
                templateUrl: 'customerModal.html',
                controller: CustomerCreateCtrl
            });

            modalInstance.result.then(function (selected) {
                $scope.selected = selected;
            }, function () {

                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openUpdate = function () {

            var modalInstance = $modal.open({
                templateUrl: 'customerModal.html',
                controller: CustomerUpdateCtrl
            });

            modalInstance.result.then(function (selected) {
                $scope.selected = selected;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
    }]);

var CustomerCreateCtrl = function ($scope, $modalInstance, $timeout, apiService, commonService) {
    $scope.title = "Create";
    var today =  new Date();
    $scope.dt = today.getFullYear()+"-"+(today.getMonth()+1)+"-"+(today.getDay()+1);

    $scope.open = function() {
        $timeout(function() {
            $scope.opened = true;
        });
    };

    commonService.getAreas.then(function(result){$scope.areas = result.data.data});
    commonService.getPlans.then(function(result){$scope.plans = result.data.data});

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
        connection.installationDate = $scope.dt;

        createObj.connections = [connection];

        apiService.POST("/customers",createObj).then(function (response) {
            console.log(response.data.data);
            $scope.alerts = [];
            $scope.alerts.push({type: 'success', msg: "Customer Successfully Created!"});
            $location.path("/customers");
        }, function (errorResponse) {
            $scope.alerts = [];
            $scope.alerts.push({ type: 'danger', msg: errorResponse.data.message});
            if (errorResponse.status != 200) {
                console.log(errorResponse);
            }
            $scope.code = "";
        });
    };


    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};

var CustomerUpdateCtrl = function ($scope, $modalInstance, $timeout) {
    $scope.title = "Update";
    var today =  new Date();
    $scope.dt = today.toLocaleDateString('en-GB');

    $scope.open = function() {
        $timeout(function() {
            $scope.opened = true;
        });
    };


    $scope.ok = function () {
        $modalInstance.close($scope.dt);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};