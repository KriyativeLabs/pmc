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

    var convertDate = function(dt){
        if(angular.isDate(dt)){
            return dt.getFullYear() + "-" + (dt.getMonth() + 1) + "-" + dt.getDate();
        } else{
            var dateParts = dt.split("/");
            var ddt = new Date(dateParts[2],dateParts[1]-1, dateParts[0]);
            return ddt.getFullYear() + "-" + (ddt.getMonth() + 1) + "-" + ddt.getDate();
        }
    };
    return {
        getAreas: getEntities("/areas"),
        getPlans: getEntities("/plans"),
        getResultFromLink:function(link) {return getEntities(link);},
        getDateString:function(dt){return convertDate(dt);}
    };
}]);

