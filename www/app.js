var pmcApp = angular.module("pmcApp", ['ngCookies', 'ngResource', 'ngRoute','ui.bootstrap', 'chart.js','ui-notification', 'ngAnimate', 'ngFileSaver','toggle-switch','infinite-scroll', 'ngProgress', 'oitozero.ngSweetAlert']);

pmcApp.config(['$routeProvider','$compileProvider', function ($routeProvider,$compileProvider) {
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
    }).when("/settings", {
        templateUrl: "settings.html",
        controller: 'settingsController'
    }).otherwise({
        redirectTo: '/'
    });
}]);

pmcApp.run(function ($rootScope, $location, $cookies, $cookieStore) {
console.log("pmcApp intialised");
});
