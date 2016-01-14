require.config({
    paths: {

        'angular': 'vendor/angular',
        'app': 'app',
        'coreModule':'coreModule'
    },
    shim: {
        'app': {
            deps: ['angular', 'coreModule']
        },
        'coreModule': {
            deps: ['angular']
        }
    }
});

require(['app'], function(){
    angular.bootstrap(document,['pmcApp']);
});
/*
    requirejs.onError = function (err) {
        console.log(err);
    };
    require([
        'jquery',
        'bootstrap',
        'angular',
        'angular-cache',
        'angular-cookies',
        'angular-resource',
        'angular-route',
        'angular-animate',
        'app',
        'cookieService',
        'httpInterceptor',
        'constantsService',
        'apiService',
        'authenticationController',
        'dashboardController',
        'jquery.app',
        'jquery.slim',
        'jquery.easy_pie',
        'jquery.sparkline',
        'jquery.flot',
        'jquery.flot.tooltip',
        'jquery.flot.spline',
        'jquery.flot.pie',
        'jquery.flot.resize',
        'jquery.flot.grow',
        'jquery.demo',
        'bootstrap_calendar',
        'boot.calendar.demo',
        'jquery.sortable',
        'app.plugin'

    ], function ($, bootstrap, angular) {
        angular.bootstrap(document, ['pmcApp']);
    });
})(requirejs);
*/