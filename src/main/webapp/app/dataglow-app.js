var app = angular.module('dataglow', ['dataServices', 'ngRoute', 'dataglowControllers']);

app.constant('serverLocation', 'http://localhost:8081/dataglow-server/mvc');

app.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/reports', {
                templateUrl: 'parts/reports.html',
                controller: 'ReportsController'
            }).
            when('/artifacts', {
                templateUrl: 'parts/artifacts.html',
                controller: 'ArtifactsController'
            }).
            otherwise({
                redirectTo: '/reports'
            });
    }]);