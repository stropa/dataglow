var dataSources = angular.module('dataServices', ['ngResource']);

dataSources.factory('ReportsDataSource', ['$resource', 'serverLocation', function ($resource, serverLocation) {
    return $resource(serverLocation + '/reports', {}, {})
}]);

dataSources.factory('ArtifactsDataSource', ['$resource', 'serverLocation', function($resource, serverLocation) {
    return $resource(serverLocation + "/artifacts", {}, {})
}]);