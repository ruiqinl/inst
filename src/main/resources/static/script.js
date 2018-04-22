(function(angular){

    var app = angular.module('app', ['ngRoute', 'ngAnimate', 'ngSanitize', 'ui.bootstrap']);

    app.config(function($routeProvider){
        $routeProvider
            .when('/', {
                templateUrl: 'search/template.html',
                controller: 'searchController'
            })
            .otherwise({redirectTo:'/'});
    });

})(window.angular);