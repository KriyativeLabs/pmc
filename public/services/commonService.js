pmcApp.factory('commonService', ['apiService', function (apiService) {


    var getEntities = function (link) {
        return apiService.GET(link);/*.then(function (response) {
            return response.data.data;
        }, function (errorResponse) {
            if (errorResponse.status != 200) {
                return errorResponse.message;
            }
        });*/
    };
    return {
        getAreas: getEntities("/areas"),
        getPlans: getEntities("/plans"),
        getResultFromLink:function(link) {return getEntities(link);}
    };
}]);

