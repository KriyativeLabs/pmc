pmcApp.directive('customerInfo', function() {
    return {
        restrict: 'E',
        scope: {
            info: '='
        },
        templateUrl: '/app/directives/customerInfo.html'
    };
});