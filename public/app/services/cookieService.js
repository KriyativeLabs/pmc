app.factory('cookieService',['$cookies', '$cookieStore', function($cookies, $cookieStore) {
        var setCookie = function(key,value){
            $cookieStore.put(key,value);
        };
        var getCookie = function(key) {
                return $cookieStore.get(key);
            };
        var removeCookie = function(key){
            return $cookieStore.remove(key);
        };
        var destroyCookie = function(){
            var cookies = $cookies.getAll();
            angular.forEach(cookies, function (v, k) {
                $cookies.remove(k);
            });
        };
         return {
                set:function(key,value) {return setCookie(key,value);},
                get:function(key) {return getCookie(key);},
                remove:function(key){return removeCookie(key);},
                destroy:function(){return destroyCookie();}
            };
    }]);

