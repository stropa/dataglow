angular.module('dataServices', ['ngResource'])
    .factory('ReportsDataSource', ['$resource', 'serverLocation', function($resource, serverLocation) {
        return $resource(serverLocation + '/reports', {}, {})
    }]);