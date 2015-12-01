pmcApp.directive("limitTo", [function() {
    return {
        restrict: "A",
        link: function(scope, elem, attrs) {
            var limit = parseInt(attrs.limitTo);
            angular.element(elem).on("keydown", function() {
            if (this.value.length > limit){
                //alert(this.value.length);
                this.value = this.value.substring(0, limit);
                //alert(this.value.length);
            }
            });
        }
    }
}]);
