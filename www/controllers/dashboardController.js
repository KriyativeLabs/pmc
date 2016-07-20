pmcApp.controller('dashboardController', ['$scope', '$filter', 'apiService', 'cookieService', 'constantsService',
    function ($scope, $filter, apiService, cookieService, constantsService) {

        var monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        $scope.openLoader();
        $scope.title = "Dashboard";
        $scope.progressbar.start();
        $scope.loader = true;
        $scope.doughnutlabels = [];
        $scope.doughnutdata = [];
        $scope.doughnutcolors = [
            {
                "fillColor": "rgba(227,50,68,0.8)",
                "strokeColor": "rgba(227,50,68,0.8)",
                "pointColor": "rgba(220,220,220,1)",
                "pointStrokeColor": "#fff",
                "pointHighlightFill": "#fff",
                "pointHighlightStroke": "rgba(151,187,205,0.8)"
            },
            {
                "fillColor": "rgb(23,123,187)",
                "strokeColor": "rgb(23,123,187)",
                "pointColor": "rgba(220,220,220,1)",
                "pointStrokeColor": "#fff",
                "pointHighlightFill": "#fff",
                "pointHighlightStroke": "rgba(151,187,205,0.8)"
            }
        ];

        $scope.barcolors = [
            {
                "fillColor": "rgba(227,50,68,0.8)",
                "strokeColor": "rgb(227,50,68)",
                "pointColor": "rgba(220,220,220,1)",
                "pointStrokeColor": "#fff",
                "pointHighlightFill": "#fff",
                "pointHighlightStroke": "rgba(151,187,205,0.8)"
            },
            {
                "fillColor": "rgb(23,123,187)",
                "strokeColor": "rgb(23,123,187)",
                "pointColor": "rgba(220,220,220,1)",
                "pointStrokeColor": "#fff",
                "pointHighlightFill": "#fff",
                "pointHighlightStroke": "rgba(151,187,205,0.8)"
            }
        ];

        $scope.barlabels = [];
        $scope.barseries = ['Closing Balance', 'Collected'];

        $scope.bardata = [
            [],
            []
        ];

        $scope.getDashboardData = function () {
            
            apiService.GET("/dashboarddata").then(function (response) {
                console.log(response);
                $scope.unpaidCustomers = response.data.data.unpaidCustomers;
                $scope.paidCustomers = response.data.data.totalCustomers - response.data.data.unpaidCustomers;
                $scope.balanceAmount = response.data.data.balanceAmount;
                $scope.amountCollected = response.data.data.amountCollected;
                
                apiService.GET("/agent_stats").then(function (response) {
                    var responseData = response.data.data;
                    $scope.doughnutlabels.push("Balance Amount");
                    $scope.doughnutdata.push($scope.balanceAmount);
                    for (var key in responseData) {
                        if (responseData.hasOwnProperty(key)) {
                            $scope.doughnutlabels.push(key);
                            $scope.doughnutdata.push(responseData[key]);
                        }
                    }
                   $scope.closeLoader();
                }, function (errorResponse) {
                    apiService.NOTIF_ERROR(errorResponse.data.message);
                    $scope.closeLoader();
                    if (errorResponse.status != 200) {
                        console.log(errorResponse);
                    }
                });
            }, function (errorResponse) {
                apiService.NOTIF_ERROR(errorResponse.data.message);
                $scope.closeLoader();
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };

        $scope.getDashboardData();
        
        var monthlyStats = function () {
            apiService.GET("/company_stats").then(function (response) {
                var responseData = response.data.data;
                for (i = responseData.length; i > 0; i--) {
                    var date = new Date(responseData[i - 1].month);
                    $scope.barlabels.push(monthNames[date.getMonth()] + "'" + date.getFullYear());
                    $scope.bardata[0].push(responseData[i - 1].closingBalance);
                    $scope.bardata[1].push(responseData[i - 1].collectedAmount);
                }

                if(responseData.length == 0){
                    var dateDummy = new Date();
                    $scope.barlabels.push(monthNames[dateDummy.getMonth()-1] + "'" + dateDummy.getFullYear());
                    $scope.bardata[0].push(0);
                    $scope.bardata[1].push(0);
                }
                $scope.progressbar.complete();
            }, function (errorResponse) {
                $scope.progressbar.complete();
                apiService.NOTIF_ERROR(errorResponse.data.message);
                if (errorResponse.status != 200) {
                    console.log(errorResponse);
                }
            });
        };
        monthlyStats();

        $scope.username = cookieService.get(constantsService.USERNAME);
        $scope.companyName = cookieService.get(constantsService.COMPANY_NAME);
    }]);