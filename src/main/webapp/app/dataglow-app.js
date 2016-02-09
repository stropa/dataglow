var app = angular.module('dataglow', ['dataServices', 'ngRoute', 'dataglowControllers']);

app.constant('serverLocation', '/dataglow/mvc');

app.config(['$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {
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
            when('/login', {
                templateUrl: 'parts/login.html',
                controller: 'AuthenticationController'
            }).
            when('/', {
                templateUrl: 'parts/welcome.html',
                controller: 'AuthenticationController'
            }).
            otherwise({
                redirectTo: '/'
            });

        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    }
]);