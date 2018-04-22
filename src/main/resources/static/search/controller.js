(function(angular){

    var app = angular.module('app');

    app.controller('searchController', function($scope, $http){

        $scope.search = function(){
            //console.log('search ', $scope.searchUrl);
            $('#search-button').button('loading');

            $http.get('api/parse?url=' + encodeURI($scope.searchUrl))
                .then(function(response){
                    if (response.data && response.data.length > 0) {
                        $scope.result = response.data;
                    } else {
                        $scope.error = {description: 'No Image or Video Found'};
                    }
                })
                .catch(function(error){
                    //console.log('error:', JSON.stringify(error));
                    $scope.error = error.data;
                })
                .finally(function(){
                    $('#search-button').button('reset');
                });

        };

        $scope.closeAlert = function(){
            $scope.error = undefined;
        };

        $scope.$watch('keyupEvent', function(newVal, oldVal, scope){
            if (newVal && newVal.keyCode === 13) {
                scope.search();
            }
        });

    });


})(window.angular);