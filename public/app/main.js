
(function(requirejs){
    'use_strict';
    requirejs.config({
        packages:['controllers','directives','services'],
        shim:{
            'jquery': {exports:'jquery'},
            'bootstrap': {deps: ['jquery']},
            'jquery.app':{deps:['jquery']},
            'jquery.slim':{deps:['jquery']},
            'jquery.easy_pie':{deps:['jquery']},
            'jquery.sparkline':{deps:['jquery']},
            'jquery.flot':{deps:['jquery']},
            'jquery.flot.tooltip':{deps:['jquery','jquery.flot']},
            'jquery.flot.spline':{deps:['jquery','jquery.flot','jquery.easy_pie']},
            'jquery.flot.pie':{deps:['jquery','jquery.flot','jquery.flot.tooltip','jquery.flot.spline']},
            'jquery.flot.resize':{deps:['jquery','jquery.flot','jquery.flot.pie']},
            'jquery.flot.grow':{deps:['jquery','jquery.flot','jquery.flot.pie']},
            'jquery.demo':{deps:['jquery','jquery.flot','jquery.flot.pie']},
            'bootstrap_calendar':{deps:['bootstrap']},
            'boot.calendar.demo':{deps:['jquery','bootstrap','bootstrap_calendar']},
            'jquery.sortable':{deps:['jquery']},
            'app.plugin':{deps:['jquery','jquery.easy_pie']},

            'angular': {
                deps: ['bootstrap'],
                exports: 'angular'
            },
            'angular-cache': {deps: ['angular']},
            'angular-cookies': {deps: ['angular-cache']},
            'angular-resource': {deps: ['angular-cookies']},
            'angular-route': {deps: ['angular-resource']},
            'app':{deps:['angular']},
            'cookieService':{deps:['app']},
            'httpInterceptor':{deps:['app','cookieService']},
            'constantsService':{deps:['app']},
            'apiService':{deps:['app','cookieService','constantsService']},
            'authenticationController':{deps:['app','apiService','cookieService','constantsService']},
            'dashboardController':{deps:['app','apiService','cookieService','constantsService']}
        },
        paths: {
            'requirejs': 'node_modules/requirejs/require',
            'jquery': '../js/jquery.min',
            'bootstrap': '../js/bootstrap',
            'jquery.app': '../js/app',
            'jquery.slim': '../js/slimscroll/jquery.slimscroll.min',
            'jquery.easy_pie': '../js/charts/easypiechart/jquery.easy-pie-chart',
            'jquery.sparkline': '../js/charts/sparkline/jquery.sparkline.min',
            'jquery.flot': '../js/charts/flot/jquery.flot.min',
            'jquery.flot.tooltip': '../js/charts/flot/jquery.flot.tooltip.min',
            'jquery.flot.spline': '../js/charts/flot/jquery.flot.spline',
            'jquery.flot.pie': '../js/charts/flot/jquery.flot.pie.min',
            'jquery.flot.resize': '../js/charts/flot/jquery.flot.resize',
            'jquery.flot.grow': '../js/charts/flot/jquery.flot.grow',
            'jquery.demo': '../js/charts/flot/demo',
            'bootstrap_calendar': '../js/calendar/bootstrap_calendar',
            'boot.calendar.demo': '../js/calendar/demo',
            'jquery.sortable': '../js/sortable/jquery.sortable',
            'app.plugin': '../js/app.plugin',
            'angular': 'node_modules/angular/angular.min',
            'angular-cache': 'node_modules/angular-cache/dist/angular-cache.min',
            'angular-cookies': 'node_modules/angular-cookies/angular-cookies.min',
            'angular-resource': 'node_modules/angular-resource/angular-resource.min',
            'angular-route': 'node_modules/angular-route/angular-route.min',
            'app': 'app',
            'cookieService': 'services/cookieService',
            'httpInterceptor': 'interceptors/httpInterceptor',
            'constantsService': 'services/constantsService',
            'apiService': 'services/apiService',
            'authenticationController': 'controllers/authenticationController',
            'dashboardController': 'controllers/dashboardController'
            // 'jquery-ui':'../assets/js/core/jquery.min'
        }

    });

    requirejs.onError = function (err) {
        console.log(err);
    };
    require(['jquery',
        'bootstrap',
        'angular',
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
        'app.plugin',
        'angular-cache',
        'angular-cookies',
        'angular-resource',
        'angular-route',
        'app',
        'cookieService',
        'httpInterceptor',
        'constantsService',
        'apiService',
        'authenticationController',
        'dashboardController'
    ], function ($, bootstrap, angular) {
        angular.bootstrap(document, ['pmcApp']);
    });
})(requirejs);

/*


require.config({
    paths:{
        'angular':'node_modules/angular/angular',
        'jQuery':'/assets/js/core/jquery.min'
        //'app':'myPmc',
        //'controllers':'controllers/authenticationController.js'
    },
    shim:{

        'angular':{ 'exports':'angular'},
        'jQuery':{'exports':'jQuery'}

    }
});
/*
require(['jQuery', 'angular', 'app'] , function ($, angular, app) {
    $(function () {
        angular.bootstrap(document, ['app']);
    });
});


require(['jQuery', 'angular', 'app'] , function ($, angular, app) {
        angular.bootstrap(document, ['pmcApp']);
});

(function () {
    App.initHelpers('slick');
});
 */
