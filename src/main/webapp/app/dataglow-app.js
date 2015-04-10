var app = angular.module('dataglow', ['dataServices', 'ngRoute', 'dataglowControllers']);

app.constant('serverLocation', 'http://localhost:8987/dataglow/mvc');

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
            when('/data', {
                templateUrl: 'parts/series.html',
                controller: 'SeriesController'
            }).
            otherwise({
                redirectTo: '/reports'
            });
    }]);