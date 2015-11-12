var pmcApp = angular.module("pmcApp", ['ngCookies', 'ngResource', 'ngRoute','ui.bootstrap','datatables', 'chart.js','ui-notification']);

pmcApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when("/login", {
        templateUrl: 'login_old.html',
        controller: 'authenticationController'
    }).when("/", {
        templateUrl: 'dashboard.html',
        controller: 'dashboardController'
    }).when("/dashboard", {
        templateUrl: 'dashboard.html',
        controller: 'dashboardController'
    }).when("/index.html", {
        templateUrl: 'dashboard.html',
        controller: 'dashboardController'
    }).when("/customers", {
        templateUrl: "customers.html",
        controller: 'customerController'
    }).when("/customers/:id", {
        templateUrl: "customer-view.html",
        controller: 'customerViewController'
    }).when("/plans", {
        templateUrl: "plans.html",
        controller: 'planController'
    }).when("/payments", {
        templateUrl: "payments.html",
        controller: 'paymentController'
    }).when("/payments/receipt", {
        templateUrl: "record_payment.html",
        controller: 'paymentController'
    }).when("/areas", {
        templateUrl: "areas.html",
        controller: 'areaController'
    }).when("/agents", {
        templateUrl: "agents.html",
        controller: 'agentController'
    }).when("/agents/:id", {
        templateUrl: "agent-view.html",
        controller: 'agentViewController'
    }).when("/change_password", {
        templateUrl: "change_password.html",
        controller: 'settingsController'
    }).otherwise({
        redirectTo: '/'
    });
    
}]);

pmcApp.config([
        "$locationProvider", function($locationProvider) {
            return $locationProvider.html5Mode({
                enabled: true,
                requireBase: false
            }).hashPrefix("!"); // enable the new HTML5 routing and history API
            // return $locationProvider.html5Mode(true).hashPrefix("!"); // enable the new HTML5 routing and history API
        }
    ]);


pmcApp.run(function ($rootScope, $location, $cookies, $cookieStore) {
console.log("pmcApp intialised");
});
