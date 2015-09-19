pmcApp.factory('cookieService',['$cookies','$window', function($cookies, $window) {

        var setCookie = function(key,value){
            $cookies.put(key,value);
        };

        var getCookie = function(key) {
                return $cookies.get(key);
            };

        var removeCookie = function(key){
            return $cookies.remove(key);
        };

        var destroyCookie = function(){
            var cookies = $cookies.getAll();
            angular.forEach(cookies, function (v, k) {
                console.log(k);
                $cookies.remove(k);
            });
            $window.location = "/login.html";
        };

         return {
                set:function(key,value) {return setCookie(key,value);},
                get:function(key) {return getCookie(key);},
                remove:function(key){return removeCookie(key);},
                destroy:function(){return destroyCookie();}
            };
    }]);

