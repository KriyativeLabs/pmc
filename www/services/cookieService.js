pmcApp.factory('cookieService',['$cookies','$window', function($cookies, $window) {

        var setCookie = function(key,value){
		 $window.localStorage.setItem(key, value); 
        };

        var getCookie = function(key) {
                return window.localStorage.getItem(key);
            };

        var removeCookie = function(key){
		window.localStorage.removeItem(key);
        };

        var destroyCookie = function(){
	    $window.localStorage.clear();	
            $window.location = "login.html";
        };

         return {
                set:function(key,value) {return setCookie(key,value);},
                get:function(key) {return getCookie(key);},
                remove:function(key){return removeCookie(key);},
                destroy:function(){return destroyCookie();}
            };
    }]);

