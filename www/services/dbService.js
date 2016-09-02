pmcApp.factory('dbService', ['$webSql', function ($webSql) {
    var db = $webSql.openDatabase("pmc", "1.0", "PMC App Database", 2 * 1024 * 1024);

    var init = function () {
        db.transaction(function (tx) {
            //MONTHLY STATS
            tx.executeSql("CREATE TABLE IF NOT EXISTS monthly_stats (month text, collected integer, pending integer)");

            //AGENT STATS
            tx.executeSql("CREATE TABLE IF NOT EXISTS agent_stats (id integer primary key, agent_name text, collected integer)");

            //CUSTOMERS
            tx.executeSql("CREATE TABLE IF NOT EXISTS customers (id integer primary key, name text, house_no text, address text, balance_amount integer, mobile_no text, connections text)");

            //PAYMENTS
            tx.executeSql("CREATE TABLE IF NOT EXISTS payments (id integer primary key, agent_details text, customer_details text, paid_amount integer, paidOn text, receipt_no text, remarks text)");

            //AREAS
            tx.executeSql("CREATE TABLE IF NOT EXISTS areas (id integer primary key, name text, code text)");

            //Plans
            tx.executeSql("CREATE TABLE IF NOT EXISTS plans (id integer primary key, name text, amount integer, no_of_months integer)");

            //Agents
            tx.executeSql("CREATE TABLE IF NOT EXISTS users (id integer primary key, name text, email text, contact_no text, login_id text, address text)");

        });
    };

    init();

    var monthlyStatsInsert = function (data) {
        db.transaction(function (tx) {
            tx.executeSql("")
            angular.forEach(data, function (row) {
                tx.executeSql("INSERT INTO monthly_stats (month, collected, pending) VALUES (?,?, ?)", [row.month, row.collectedAmount, row.closingBalance]);
            });
        });
    };


    return {
        set: function (key, value) {
            return setCookie(key, value);
        },
        get: function (key) {
            return getCookie(key);
        },
        remove: function (key) {
            return removeCookie(key);
        },
        destroy: function () {
            return destroyCookie();
        }
    };
    }]);