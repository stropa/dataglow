var dataSources = angular.module('dataServices', ['ngResource']);

dataSources.factory('ReportsDataSource', ['$resource', 'serverLocation', function ($resource, serverLocation) {
    return $resource(serverLocation + '/reports', {}, {})
}]);

dataSources.factory('ArtifactsDataSource', ['$resource', 'serverLocation', function ($resource, serverLocation) {
    return $resource(serverLocation + "/artifacts", {}, {})
}]);

dataSources.factory('CubesDataSource', ['$resource', 'serverLocation', function ($resource, serverLocation) {
    return $resource(serverLocation + "/cubes/:id",
        {id: "@id"},
        {})
}]);

dataSources.factory('SeriesDataSource', ['$resource', 'serverLocation', function ($resource, serverLocation) {
    return $resource(serverLocation + "/series/:path",
        {path: "@path"},
        {
            loadForCubeCoordinates: {method: 'POST', isArray: true}
        })
}]);
