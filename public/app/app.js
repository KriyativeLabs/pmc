var app = "";
(function () {
    'use strict';
    app = angular.module("pmcApp",['ngCookies','ngResource','ngRoute']);

    app.config(['$routeProvider', function($routeProvider){
        console.log($routeProvider);
        $routeProvider.when("/views/login",{
            templateUrl:"login.html",
            controller:'authenticationController'
        //    controllerAs:"ac"
        }).when("/views/layout",{
            templateUrl:"layout.html",
            controller:'dashboardController'
        //    controllerAs:"dc"
        }).when("/views/customer", {
            templateUrl:"customers.html"
        }).otherwise({
            redirectTo: '/views/login'
        });
        //$locationProvider.html5Mode(true);
    }]);
}());

app.run(function($rootScope, $location, $cookies, $cookieStore) {
});


