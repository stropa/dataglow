var controllers = angular.module('dataglowControllers', ['ngResource']);

controllers.controller('ReportsController', ['$scope', 'ReportsDataSource', function ($scope, ReportsDataSource) {

    /*$scope.reports = [
     {name: "merchant_daily_counts", query:"select * from foo", period: "1", periodUnits: "DAYS", lastBuildTime: "21/12/2014 00:01"},
     {name: "merchant_minute_counts", query:"select * from bar", period: "1", periodUnits: "MINUTES", lastBuildTime: "21/12/2014 14:01"}
     ];*/


    $scope.reports = ReportsDataSource.query();

    $scope.isEditing = false;
    $scope.isCreating = false;

    function resetCreateForm() {
        $scope.newReport = {
            name: 'new_report',
            period: null,
            periodUnits: null,
            useCache: false,
            analyzeAll: false,
            maxCacheSize: -1
        }
    }

    function startCreating() {
        $scope.isCreating = true;
        $scope.isEditing = false;
        resetCreateForm();
    }

    function createNewReport(newReport) {
        //$scope.reports.push(newReport);
        ReportsDataSource.save(newReport, function success() {
            $scope.reports = ReportsDataSource.query();
        });
        resetCreateForm();
    }

    function stopCreation() {
        $scope.isCreating = false;
    }

    $scope.startCreating = startCreating;
    $scope.createNewReport = createNewReport;
    $scope.stopCreation = stopCreation;

}]);

controllers.controller('ArtifactsController', ['$scope', 'ArtifactsDataSource', function ($scope, ArtifactsDataSource) {

    $scope.artifacts = ArtifactsDataSource.query();

    function showChartForArtifact(artifact) {
        console.log("GOTCHA!!!");
        showChart(artifact);
    }

    $scope.showChartForArtifact = showChartForArtifact;

}]);

controllers.controller('SeriesController', ['$scope', '$http', 'CubesDataSource', 'SeriesDataSource', function ($scope, $http, CubesDataSource, SeriesDataSource) {

    function cubeSelected() {

        $scope.cubeDescription = CubesDataSource.get({}, {id: $scope.cubeId});
        $scope.cubeDescription.$promise.then(function (cubeDescription) {
            $scope.totalAxes = cubeDescription.dimensions.length;
        });

        $scope.dimensions = CubesDataSource.get({}, {id: $scope.cubeId + "/axes"});
        $scope.dimensions.$promise.then(function (dims) {
            var i = 0;
            axes = [];
            for (axe in dims.axes) {
                axes[i] = dims.axes[axe];
                i++;
            }
            $scope.axes = axes;
        });
    }

    $scope.cubeSelected = cubeSelected;

    $scope.cubes = CubesDataSource.query();

    // forCubeCoordinates

    function loadPoints() {
        $scope.points = SeriesDataSource.loadForCubeCoordinates({
                cubeName: $scope.cubeDescription.name,
                axe0: $scope.axeSelection_0,
                axe1: $scope.axeSelection_1,
                axe2: $scope.axeSelection_2,
                aggregate: $scope.aggregateSelected,
                dateFrom: $scope.dateFrom,
                dateTo: $scope.dateTo
            },
            {path: "forCubeCoordinates"}
        );
        $scope.points.$promise.then(function (points) {
            showChart(points);
        })
    }

    $scope.loadPoints = loadPoints;

}]);

controllers.controller('AuthenticationController',

    function ($rootScope, $scope, $http, $location) {

        var authenticate = function (credentials, callback) {

            var headers = credentials ? {
                authorization: "Basic "
                + btoa(credentials.username + ":" + credentials.password)
            } : {};

            $http.get('mvc/user', {headers: headers}).success(function (data) {
                if (data.name) {
                    $rootScope.authenticated = true;
                    $rootScope.authorities = data.authorities;
                } else {
                    $rootScope.authenticated = false;
                }
                callback && callback();
            }).error(function () {
                $rootScope.authenticated = false;
                callback && callback();
            });

        };

        authenticate();
        $rootScope.authenticate = authenticate;
        $scope.credentials = {};
        $scope.login = function () {
            authenticate($scope.credentials, function () {
                if ($rootScope.authenticated) {
                    $location.path("/");
                    $scope.error = false;
                } else {
                    $location.path("/login");
                    $scope.error = true;
                }
            });
        };

        $rootScope.hasPermission = function(permission) {
            result = false;
            if($rootScope.authorities) {
                $rootScope.authorities.forEach(function(el) {
                    debugger;
                    if (el.authority == permission ) {
                        result = true;
                    }
                });
            }
            return result;
        }
    });

controllers.controller('LandingPageController',
    function($rootScope, $scope, $location) {
        $rootScope.authenticate();
        $scope.showLogin = !$rootScope.authenticated;
    }
);
