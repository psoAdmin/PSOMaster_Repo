var module = angular.module("PSOMaster", ['ngResource','ngRoute', 'ngSanitize','ng-fusioncharts','ngFlash','ngAnimate', 'ui.bootstrap','treasure-overlay-spinner','AuthServices']);//,'chart.js'

module.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/login', {
		templateUrl : 'views/Login.jsp',
		controller : 'LoginController'
	})
	.when('/dashboard', {
		templateUrl : 'views/Dashboard.jsp',
		controller : 'DashboardController',
        requiresAuthentication: true,
        permissions: ["ADMIN","TEST"]
	})
	.when('/orderMaster', {
		templateUrl : 'views/OrderMaster.jsp',
		controller : 'OrderMasterController',
        requiresAuthentication: true,
        permissions: ["ADMIN"]
	})
	.when('/getOrder/:orderID', {
		templateUrl : 'views/OrderMaster.jsp',
		controller : 'OrderMasterController',
        requiresAuthentication: true
	})
	.when('/updateOrder', {
		templateUrl : 'views/updateOrder.jsp',
		controller : 'UpdateOrderController',
        requiresAuthentication: true,
        permissions: ["ADMIN"]
	})
	.when('/updateOrder/:orderID', {
		templateUrl : 'views/updateOrder.jsp',
		controller : 'UpdateOrderController',
        requiresAuthentication: true,
        permissions: ["ADMIN"]
	})
	.when('/sendEmail', {
		templateUrl : 'views/sendEmail.jsp',
		controller : 'SendEmailController',
        requiresAuthentication: true
	})
	.when('/demo', {
		templateUrl : 'views/demo.jsp',
		controller : 'SendEmailController'
	})
	.when('/StuckOrderDetails/:orderStatus', {
		templateUrl : 'views/StuckOrderDetails.jsp',
		controller : 'StuckOrderDetailsController',
        requiresAuthentication: true
	})
	.when('/manageGroup', {
		templateUrl : 'views/admin/ManageGroup.jsp',
		controller : 'ManageGroupController'
	})
	.when('/manageUser', {
		templateUrl : 'views/admin/ManageUser.jsp',
		controller : 'ManageUserController'
	})
	.otherwise({
		redirectTo : '/login'
	});
} ]);


module.run(run);

run.$inject = ['$rootScope', '$location', 'Auth'];
function run ($rootScope, $location, Auth) {
	 $rootScope.spinner = {
		        active: false,
		        on: function () {
		          this.active = true;
		        },
		        off: function () {
		          this.active = false;
		        }
		      };
	 
	 Auth.init();
     
    $rootScope.$on('$routeChangeStart', function (event, next) {
        if (!Auth.checkPermissionForView(next)){
            event.preventDefault();
            $location.path("/login");
        }
    });
}



module.directive('permission', ['Auth', function(Auth) {
   return {
       restrict: 'A',
       scope: {
          permission: '='
       },
 
       link: function (scope, elem, attrs) {
            scope.$watch(Auth.isLoggedIn, function() {
                if (Auth.userHasPermission(scope.permission)) {
                    elem.show();
                } else {
                    elem.hide();
                }
            });                
       }
   }
}]);