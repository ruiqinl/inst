(function(angular) {
    var app = angular.module('app');

    app.directive('media', function(){
        return {
            restrict: 'E',
            templateUrl: 'mediaDirective/template.html',
            controller: 'mediaController',
            scope: {
                media: '='
            }
        };
    });

    app.controller('mediaController', function($scope){
        $scope.play = function(event){
            var video = angular.element(event.target)[0];
            if (video.paused) {
                video.play();
            } else {
                video.pause();
            }
        };

        $scope.openInNewTab = function(url) {
            var tab = window.open(url, '_blank');
            tab.focus();
        };
    });


})(window.angular);