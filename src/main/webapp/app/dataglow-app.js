var app = angular.module('dataglow', ['dataServices']);

app.constant('serverLocation', 'http://localhost:8080/dataglow-server')

app.controller('MainCtrl', ['$scope', 'ReportsDataSource', function ($scope, ReportsDataSource) {
    $scope.hello = "world";

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
            periodUnits: null
        }
    }

    function startCreating() {
        $scope.isCreating = true;
        $scope.isEditing = false;
        resetCreateForm();
    }

    function createNewReport(newReport) {
        //$scope.reports.push(newReport);
        ReportsDataSource.save(newReport)
        resetCreateForm();
    }

    function stopCreation() {
        $scope.isCreating = false;
    }

    $scope.startCreating = startCreating;
    $scope.createNewReport = createNewReport;
    $scope.stopCreation = stopCreation;

}]);